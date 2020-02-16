package org.yamcs.web.websocket;

import java.io.IOException;
import java.nio.channels.ClosedChannelException;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yamcs.ConfigurationException;
import org.yamcs.YamcsServer;
import org.yamcs.api.ws.WSConstants;
import org.yamcs.protobuf.Yamcs.ProtoDataType;
import org.yamcs.security.User;
import org.yamcs.web.HttpRequestHandler;
import org.yamcs.web.HttpRequestInfo;
import org.yamcs.web.HttpServer;

import com.google.protobuf.Message;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.WriteBufferWaterMark;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler.HandshakeComplete;
import io.netty.util.AttributeKey;

/**
 * Class for text/binary websocket handling
 */
public class WebSocketFrameHandler extends SimpleChannelInboundHandler<WebSocketFrame> {

    public static final String WEBSOCKET_PATH = "_websocket";
    public static final AttributeKey<HttpRequestInfo> CTX_HTTP_REQUEST_INFO = AttributeKey.valueOf("httpRequestInfo");

    private static final Logger log = LoggerFactory.getLogger(WebSocketFrameHandler.class);

    private ChannelHandlerContext ctx;

    // these two are valid after the socket has been upgraded and they are practical final
    private Channel channel;
    private WebSocketClient processorClient;

    private WebSocketDecoder decoder;
    private WebSocketEncoder encoder;

    private int dataSeqCount = -1;
    private int droppedWrites = 0; // Consecutive dropped writes used to free up resources

    // Basic info about the original http request that lead to the websocket upgrade
    private HttpRequestInfo originalRequestInfo;

    // Provides access to the various resources served through this websocket
    private Map<String, AbstractWebSocketResource> resourcesByName = new HashMap<>();

    // after how many consecutive dropped writes will the connection be closed
    private int connectionCloseNumDroppedMsg;

    private WriteBufferWaterMark writeBufferWaterMark;

    public WebSocketFrameHandler(HttpRequestInfo originalRequestInfo, int connectionCloseNumDroppedMsg,
            WriteBufferWaterMark writeBufferWaterMark) {
        this.originalRequestInfo = originalRequestInfo;
        this.connectionCloseNumDroppedMsg = connectionCloseNumDroppedMsg;
        this.writeBufferWaterMark = writeBufferWaterMark;
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        this.ctx = ctx;
        channel = ctx.channel();
        channel.config().setWriteBufferWaterMark(writeBufferWaterMark);
        // Try to use an application name as provided by the client. For browsers (since overriding
        // websocket headers is not supported) this will be the browser's standard user-agent string
        String applicationName;
        if (originalRequestInfo.getHeaders().contains(HttpHeaderNames.USER_AGENT)) {
            applicationName = originalRequestInfo.getHeaders().get(HttpHeaderNames.USER_AGENT);
        } else {
            applicationName = "Unknown (" + channel.remoteAddress() + ")";
        }

        String yamcsInstance = originalRequestInfo.getYamcsInstance();
        User user = originalRequestInfo.getUser();
        processorClient = new WebSocketClient(yamcsInstance, this, applicationName, user);
        HttpServer httpServer = YamcsServer.getGlobalService(HttpServer.class);
        if (httpServer != null) { // Can happen in junit when not using yamcs.yaml
            for (WebSocketResourceProvider provider : httpServer.getWebSocketResourceProviders()) {
                AbstractWebSocketResource resource = provider.createForClient(processorClient);
                processorClient.registerResource(provider.getRoute(), resource);
            }
        }
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof HandshakeComplete) {
            log.info("{} {} {}", originalRequestInfo.getMethod(), originalRequestInfo.getUri(),
                    HttpResponseStatus.SWITCHING_PROTOCOLS.code());

            HandshakeComplete handshakeEvt = (HandshakeComplete) evt;
            String subprotocol = handshakeEvt.selectedSubprotocol();

            // TODO We should allow subprotocol to be null, but currently this triggers
            // autodetection rather than defaulting to json. So warn for now to give
            // clients time to upgrade.
            if (subprotocol == null) {
                log.warn("Detected a WebSocket client that does not specify "
                        + "'Sec-WebSocket-Protocol: protobuf' "
                        + "or 'Sec-WebSocket-Protocol: json'. Defaulting to deprecated frame detection.");
            }

            // TODO eventually uncomment below comment. But first the deprecated incoming frame
            // detection in channelRead0 should be removed.
            if (/*subprotocol == null ||*/ "json".equals(subprotocol)) {
                decoder = new JsonDecoder();
                encoder = new JsonEncoder();
            } else if ("protobuf".equals(subprotocol)) {
                decoder = new ProtobufDecoder();
                encoder = new ProtobufEncoder(ctx);
            }

            // After upgrade, no further HTTP messages will be received
            ctx.pipeline().remove(HttpRequestHandler.class);

            // Send data with server-assigned connection state (clientId, instance, processor)
            // TODO remove the if-condition once frame detection is removed from channelRead0.
            // The 'if' was added to prevent sending an early json message to an old gpb client
            if (subprotocol != null) {
                processorClient.sendConnectionInfo();
            }
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, WebSocketFrame frame) throws Exception {
        try {
            try {
                log.debug("Received frame {}", frame);

                // TODO this type of frame detection is deprecated. Use the new mechanism
                // via a Sec-WebSocket-Protocol header directly on the upgrade request.
                if (encoder == null) {
                    if (frame instanceof TextWebSocketFrame) {
                        decoder = new JsonDecoder();
                        encoder = new JsonEncoder();
                    } else if (frame instanceof BinaryWebSocketFrame) {
                        decoder = new ProtobufDecoder();
                        encoder = new ProtobufEncoder(ctx);
                    }
                }

                ByteBuf binary = frame.content();
                if (binary != null) {
                    if (log.isTraceEnabled()) {
                        log.debug("WebSocket data: {}", frame);
                    }
                    WebSocketDecodeContext msg = decoder.decodeMessage(binary);

                    AbstractWebSocketResource resource = resourcesByName.get(msg.getResource());
                    if (resource != null) {
                        WebSocketReply reply = resource.processRequest(msg, decoder);
                        if (reply != null) {
                            sendReply(reply);
                        }
                    } else {
                        throw new WebSocketException(msg.getRequestId(),
                                "Invalid message (unsupported resource: '" + msg.getResource() + "')");
                    }
                }
            } catch (WebSocketException e) {
                log.debug("Returning nominal exception back to the client: {}", e.getMessage());
                sendException(e);
            }
        } catch (Exception e) {
            log.error("Internal Server Error while handling incoming web socket frame", e);
            try { // Gut-shot, at least try to inform the client
                  // TODO should do our best to return a better requestId here
                sendException(new WebSocketException(WSConstants.NO_REQUEST_ID, "Internal Server Error"));
            } catch (Exception e2) { // Oh well, we tried.
                log.warn("Could not inform client of earlier Internal Server Error due to additional exception " + e2,
                        e2);
            }
        }
    }

    /**
     * this is called when the client closes abruptly the connection
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.warn("Will close channel due to error", cause);
        ctx.close();
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        if (processorClient != null) {
            log.info("Channel {} closed", ctx.channel().remoteAddress());
            processorClient.quit();
        }
    }

    void addResource(String name, AbstractWebSocketResource resource) {
        if (resourcesByName.containsKey(name)) {
            throw new ConfigurationException("A resource named '" + name + "' is already being served");
        }
        resourcesByName.put(name, resource);
    }

    private WebSocketEncoder getEncoder() {
        if (encoder == null) {
            log.debug("WebSocket frame encoding is not specified. Encoding in JSON by default");
            return new JsonEncoder();
        }
        return encoder;
    }

    public void sendReply(WebSocketReply reply) throws IOException {
        if (!channel.isOpen()) {
            throw new IOException("Channel not open");
        }
        if (!channel.isWritable()) {
            log.warn("Dropping reply message because channel is not writable");
            return;
        }

        WebSocketFrame frame = getEncoder().encodeReply(reply);
        channel.writeAndFlush(frame);
    }

    private void sendException(WebSocketException e) throws IOException {
        WebSocketFrame frame = getEncoder().encodeException(e);
        channel.writeAndFlush(frame);
    }

    /**
     * Sends actual data over the web socket. If the channel is not or no longer writable, the message is dropped. We do
     * not want to block the calling thread (because that will be a processor thread).
     * 
     * The websocket clients will know when the messages have been dropped from the sequence count.
     */
    public <T extends Message> void sendData(ProtoDataType dataType, T data) throws IOException {
        dataSeqCount++;
        if (!channel.isOpen()) {
            throw new ClosedChannelException();
        }

        if (!channel.isWritable()) {
            log.warn("Dropping {} message for client [id={}, username={}] because channel is not or no longer writable",
                    dataType, processorClient.getClientId(), processorClient.getUsername());
            droppedWrites++;

            if (droppedWrites >= connectionCloseNumDroppedMsg) {
                log.warn("Too many ({}) dropped messages for client [id={}, username={}]. Forcing disconnect",
                        droppedWrites, processorClient.getClientId(), processorClient.getUsername());
                ctx.close();
            }
            return;
        }
        droppedWrites = 0;
        WebSocketFrame frame = getEncoder().encodeData(dataSeqCount, dataType, data);
        channel.writeAndFlush(frame);
    }

    public Channel getChannel() {
        return channel;
    }

}
