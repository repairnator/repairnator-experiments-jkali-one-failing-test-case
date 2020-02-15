package de.felix_klauke.doctrin.client.connection;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.reactivex.Observable;
import io.reactivex.Scheduler;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Felix Klauke <fklauke@itemis.de>
 */
public class DoctrinClientConnectionImpl extends SimpleChannelInboundHandler<JSONObject> implements DoctrinClientConnection {

    private final ExecutorService executorService = Executors.newCachedThreadPool(new ThreadFactory() {
        private static final String PREFIX = "Doctrin Publisher Worker #";
        private final AtomicInteger threadId = new AtomicInteger(0);

        @Override
        public Thread newThread(Runnable r) {
            Thread thread = new Thread(r);
            thread.setName(PREFIX + threadId.getAndIncrement());
            return thread;
        }
    });

    /**
     * The rx scheduler worker.
     */
    private final Scheduler scheduler;

    private final Logger logger = LoggerFactory.getLogger(DoctrinClientConnectionImpl.class);

    /**
     * The publish subject that will emit all the messages.
     */
    private final PublishSubject<JSONObject> publishSubject = PublishSubject.create();

    /**
     * The connected subject that will emit whenever the connection state changes.
     */
    private final PublishSubject<Boolean> connectedSubject = PublishSubject.create();

    private Channel lastChannel;

    public DoctrinClientConnectionImpl(Channel lastChannel) {
        this.lastChannel = lastChannel;

        scheduler = Schedulers.from(executorService);
    }

    @Override
    public Observable<JSONObject> getMessages() {
        return publishSubject;
    }

    @Override
    public Observable<Boolean> getConnected() {
        return connectedSubject;
    }

    @Override
    public Observable<Boolean> sendMessage(JSONObject jsonObject) {
        Future<ChannelFuture> channelFuture = executorService.submit(() -> {
            logger.debug("Sending object to server: {}.", jsonObject);
            return lastChannel.writeAndFlush(jsonObject);
        });

        Observable<Boolean> observable = Observable.create(observableEmitter -> {
            ChannelFuture channelFuture1 = channelFuture.get();

            channelFuture1.await();

            if (!channelFuture1.isSuccess()) {
                observableEmitter.onError(channelFuture1.cause());
                return;
            }

            observableEmitter.onNext(channelFuture1.isSuccess());
            observableEmitter.onComplete();
        });

        return observable.observeOn(scheduler).subscribeOn(scheduler);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        connectedSubject.onNext(true);
        lastChannel = ctx.channel();
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        connectedSubject.onNext(false);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        connectedSubject.onError(cause);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, JSONObject msg) {
        logger.debug("Received raw message: {}.", msg);
        lastChannel = ctx.channel();
        publishSubject.onNext(msg);
    }
}
