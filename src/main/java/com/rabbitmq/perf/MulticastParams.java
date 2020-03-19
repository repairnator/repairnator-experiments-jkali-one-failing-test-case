// Copyright (c) 2007-Present Pivotal Software, Inc.  All rights reserved.
//
// This software, the RabbitMQ Java client library, is triple-licensed under the
// Mozilla Public License 1.1 ("MPL"), the GNU General Public License version 2
// ("GPL") and the Apache License version 2 ("ASL"). For the MPL, please see
// LICENSE-MPL-RabbitMQ. For the GPL, please see LICENSE-GPL2.  For the ASL,
// please see LICENSE-APACHE2.
//
// This software is distributed on an "AS IS" basis, WITHOUT WARRANTY OF ANY KIND,
// either express or implied. See the LICENSE file for specific language governing
// rights and limitations of this software.
//
// If you have any questions regarding licensing, please contact us at
// info@rabbitmq.com.

package com.rabbitmq.perf;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ShutdownSignalException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class MulticastParams {
    private long confirm = -1;
    private int confirmTimeout = 30;
    private int consumerCount = 1;
    private int producerCount = 1;
    private int consumerChannelCount = 1;
    private int producerChannelCount = 1;
    private int consumerTxSize = 0;
    private int producerTxSize = 0;
    private int channelPrefetch = 0;
    private int consumerPrefetch = 0;
    private int minMsgSize = 0;

    private int timeLimit = 0;
    private float producerRateLimit = 0.0f;
    private float consumerRateLimit = 0.0f;
    private int producerMsgCount = 0;
    private int consumerMsgCount = 0;
    private boolean consumerSlowStart = false;

    private String exchangeName = "direct";
    private String exchangeType = "direct";
    private List<String> queueNames = new ArrayList<>();
    private String routingKey = null;
    private boolean randomRoutingKey = false;
    private boolean skipBindingQueues = false;

    private List<?> flags = new ArrayList<>();

    private int multiAckEvery = 0;
    private boolean autoAck = false;
    private boolean autoDelete = true;

    private List<String> bodyFiles = new ArrayList<>();
    private String bodyContentType = null;

    private boolean predeclared = false;
    private boolean useMillis = false;

    private Map<String, Object> queueArguments = null;

    private int consumerLatencyInMicroseconds = 0;

    private String queuePattern = null;
    private int queueSequenceFrom = -1;
    private int queueSequenceTo = -1;

    private Map<String, Object> messageProperties = null;

    private TopologyHandler topologyHandler;

    private int heartbeatSenderThreads = -1;

    private int routingKeyCacheSize = 0;
    private boolean exclusive = false;

    public void setExchangeType(String exchangeType) {
        this.exchangeType = exchangeType;
    }

    public void setExchangeName(String exchangeName) {
        this.exchangeName = exchangeName;
    }

    public void setQueueNames(List<String> queueNames) {
        if(queueNames == null) {
            this.queueNames = new ArrayList<>();
        } else {
            this.queueNames = new ArrayList<>(queueNames);
        }
    }

    public void setRoutingKey(String routingKey) {
        this.routingKey = routingKey;
    }

    public void setRandomRoutingKey(boolean randomRoutingKey) {
        this.randomRoutingKey = randomRoutingKey;
    }
    
    public void setSkipBindingQueues(boolean skipBindingQueues) {
    	this.skipBindingQueues = skipBindingQueues;
    }

    public void setProducerRateLimit(float producerRateLimit) {
        this.producerRateLimit = producerRateLimit;
    }

    public void setProducerCount(int producerCount) {
        this.producerCount = producerCount;
    }

    public void setProducerChannelCount(int producerChannelCount) {
        this.producerChannelCount = producerChannelCount;
    }

    public void setConsumerRateLimit(float consumerRateLimit) {
        this.consumerRateLimit = consumerRateLimit;
    }

    public void setConsumerCount(int consumerCount) {
        this.consumerCount = consumerCount;
    }

    public void setConsumerChannelCount(int consumerChannelCount) {
        this.consumerChannelCount = consumerChannelCount;
    }
    
    public void setConsumerSlowStart(boolean slowStart) {
        this.consumerSlowStart = slowStart;
    }

    public void setProducerTxSize(int producerTxSize) {
        this.producerTxSize = producerTxSize;
    }

    public void setConsumerTxSize(int consumerTxSize) {
        this.consumerTxSize = consumerTxSize;
    }

    public void setConfirm(long confirm) {
        this.confirm = confirm;
    }

    public void setConfirmTimeout(int confirmTimeout) {
        this.confirmTimeout = confirmTimeout;
    }

    public void setAutoAck(boolean autoAck) {
        this.autoAck = autoAck;
    }

    public void setMultiAckEvery(int multiAckEvery) {
        this.multiAckEvery = multiAckEvery;
    }

    public void setChannelPrefetch(int channelPrefetch) {
        this.channelPrefetch = channelPrefetch;
    }

    public void setConsumerPrefetch(int consumerPrefetch) {
        this.consumerPrefetch = consumerPrefetch;
    }

    public void setMinMsgSize(int minMsgSize) {
        this.minMsgSize = minMsgSize;
    }

    public void setTimeLimit(int timeLimit) {
        this.timeLimit = timeLimit;
    }

    public void setUseMillis(boolean useMillis) {
        this.useMillis = useMillis;
    }

    public void setProducerMsgCount(int producerMsgCount) {
        this.producerMsgCount = producerMsgCount;
    }

    public void setConsumerMsgCount(int consumerMsgCount) {
        this.consumerMsgCount = consumerMsgCount;
    }

    public void setMsgCount(int msgCount) {
        setProducerMsgCount(msgCount);
        setConsumerMsgCount(msgCount);
    }

    public void setFlags(List<?> flags) {
        this.flags = flags;
    }

    public void setAutoDelete(boolean autoDelete) {
        this.autoDelete = autoDelete;
    }

    public void setPredeclared(boolean predeclared) {
        this.predeclared = predeclared;
    }

    public void setQueueArguments(Map<String, Object> queueArguments) {
        this.queueArguments = queueArguments;
    }

    public void setConsumerLatencyInMicroseconds(int consumerLatencyInMicroseconds) {
        this.consumerLatencyInMicroseconds = consumerLatencyInMicroseconds;
    }

    public void setMessageProperties(Map<String, Object> messageProperties) {
        this.messageProperties = messageProperties;
    }

    public int getConsumerCount() {
        return consumerCount;
    }

    public int getConsumerChannelCount() {
        return consumerChannelCount;
    }
    
    public boolean getConsumerSlowStart() {
        return consumerSlowStart;
    }

    public int getConsumerThreadCount() {
        return consumerCount * consumerChannelCount;
    }

    public int getProducerCount() {
        return producerCount;
    }

    public int getProducerChannelCount() {
        return producerChannelCount;
    }

    public int getProducerThreadCount() {
        return producerCount * producerChannelCount;
    }

    public int getMinMsgSize() {
        return minMsgSize;
    }

    public String getRoutingKey() {
        return routingKey;
    }

    public boolean getRandomRoutingKey() {
        return randomRoutingKey;
    }
    
    public boolean getSkipBindingQueues() {
    	return skipBindingQueues;
    }

    public void setBodyFiles(List<String> bodyFiles) {
        if (bodyFiles == null) {
            this.bodyFiles = new ArrayList<>();
        } else {
            this.bodyFiles = new ArrayList<>(bodyFiles);
        }
    }

    public void setBodyContentType(String bodyContentType) {
        this.bodyContentType = bodyContentType;
    }

    public void setQueuePattern(String queuePattern) {
        this.queuePattern = queuePattern;
    }

    public String getQueuePattern() {
        return queuePattern;
    }

    public void setQueueSequenceFrom(int queueSequenceFrom) {
        this.queueSequenceFrom = queueSequenceFrom;
    }

    public int getQueueSequenceFrom() {
        return queueSequenceFrom;
    }

    public void setQueueSequenceTo(int queueSequenceTo) {
        this.queueSequenceTo = queueSequenceTo;
    }

    public int getQueueSequenceTo() {
        return queueSequenceTo;
    }

    public void setHeartbeatSenderThreads(int heartbeatSenderThreads) {
        this.heartbeatSenderThreads = heartbeatSenderThreads;
    }

    public int getHeartbeatSenderThreads() {
        return heartbeatSenderThreads <= 0 ? producerCount + consumerCount : this.heartbeatSenderThreads;
    }

    public int getTimeLimit() {
        return timeLimit;
    }

    public int getProducerMsgCount() {
        return producerMsgCount;
    }

    public int getConsumerMsgCount() {
        return consumerMsgCount;
    }

    public void setRoutingKeyCacheSize(int routingKeyCacheSize) {
        this.routingKeyCacheSize = routingKeyCacheSize;
    }

    public Producer createProducer(Connection connection, Stats stats, MulticastSet.CompletionHandler completionHandler) throws IOException {
        Channel channel = connection.createChannel();
        if (producerTxSize > 0) channel.txSelect();
        if (confirm >= 0) channel.confirmSelect();
        if (!predeclared || !exchangeExists(connection, exchangeName)) {
            channel.exchangeDeclare(exchangeName, exchangeType);
        }
        MessageBodySource messageBodySource;
        TimestampProvider tsp;
        if (bodyFiles.size() > 0) {
            tsp = new TimestampProvider(useMillis, true);
            messageBodySource = new LocalFilesMessageBodySource(bodyFiles, bodyContentType);
        } else {
            tsp = new TimestampProvider(useMillis, false);
            messageBodySource = new TimeSequenceMessageBodySource(tsp, minMsgSize);
        }
        final Producer producer = new Producer(channel, exchangeName, this.topologyHandler.getRoutingKey(),
                                               randomRoutingKey, flags, producerTxSize,
                                               producerRateLimit, producerMsgCount,
                                               confirm, confirmTimeout, messageBodySource,
                                               tsp, stats, messageProperties, completionHandler, this.routingKeyCacheSize);
        channel.addReturnListener(producer);
        channel.addConfirmListener(producer);
        this.topologyHandler.next();
        return producer;
    }

    public Consumer createConsumer(Connection connection, Stats stats, MulticastSet.CompletionHandler completionHandler) throws IOException {
        Channel channel = connection.createChannel();
        if (consumerTxSize > 0) channel.txSelect();
        List<String> generatedQueueNames = this.topologyHandler.configureQueuesForClient(connection);
        if (consumerPrefetch > 0) channel.basicQos(consumerPrefetch);
        if (channelPrefetch > 0) channel.basicQos(channelPrefetch, true);

        boolean timestampInHeader;
        if (bodyFiles.size() > 0) {
            timestampInHeader = true;
        } else {
            timestampInHeader = false;
        }
        TimestampProvider tsp = new TimestampProvider(useMillis, timestampInHeader);
        Consumer consumer = new Consumer(channel, this.topologyHandler.getRoutingKey(), generatedQueueNames,
                                         consumerTxSize, autoAck, multiAckEvery,
                                         stats, consumerRateLimit, consumerMsgCount,
                                         consumerLatencyInMicroseconds, tsp, completionHandler);
        this.topologyHandler.next();
        return consumer;
    }

    public List<String> configureAllQueues(Connection connection) throws IOException {
        return this.topologyHandler.configureAllQueues(connection);
    }

    public void init() {
        if (this.queuePattern == null) {
            this.topologyHandler = new FixedQueuesTopologyHandler(this, this.routingKey, this.queueNames);
        } else {
            this.topologyHandler = new SequenceTopologyHandler(this, this.queueSequenceFrom, this.queueSequenceTo, this.queuePattern);
        }

    }

    public void resetTopologyHandler() {
        this.topologyHandler.reset();
    }

    private static boolean exchangeExists(Connection connection, final String exchangeName) throws IOException {
        if ("".equals(exchangeName)) {
            // NB: default exchange always exists
            return true;
        } else {
            return exists(connection, ch -> ch.exchangeDeclarePassive(exchangeName));
        }
    }

    private static boolean queueExists(Connection connection, final String queueName) throws IOException {
        return queueName != null && exists(connection, ch -> ch.queueDeclarePassive(queueName));
    }

    public boolean hasLimit() {
        return this.timeLimit > 0 || this.consumerMsgCount > 0 || this.producerCount > 0;
    }

    public void setExclusive(boolean exclusive) {
        this.exclusive = exclusive;
    }

    public boolean isExclusive() {
        return exclusive;
    }

    private interface Checker {
        void check(Channel ch) throws IOException;
    }

    private static boolean exists(Connection connection, Checker checker) throws IOException {
        try {
            Channel ch = connection.createChannel();
            checker.check(ch);
            ch.abort();
            return true;
        }
        catch (IOException e) {
            ShutdownSignalException sse = (ShutdownSignalException) e.getCause();
            if (!sse.isHardError()) {
                AMQP.Channel.Close closeMethod = (AMQP.Channel.Close) sse.getReason();
                if (closeMethod.getReplyCode() == AMQP.NOT_FOUND) {
                    return false;
                }
            }
            throw e;
        }
    }

    /**
     * Contract to handle the creation and configuration of resources.
     * E.g. creation of queues, binding exchange to queues.
     */
    interface TopologyHandler {

        /**
         * Get the current routing key
         * @return
         */
        String getRoutingKey();

        /**
         * Configure the queues for the current client (e.g. consumer or producer)
         * @param connection
         * @return the configured queues names (can be server-generated names)
         * @throws IOException
         */
        List<String> configureQueuesForClient(Connection connection) throws IOException;

        /**
         * Configure all the queues for this run
         * @param connection
         * @return the configured queues names (can be server-generated names)
         * @throws IOException
         */
        List<String> configureAllQueues(Connection connection) throws IOException;

        /**
         * Move the cursor forward.
         * Should be called when the configuration (queues and routing key)
         * is required for the next client (consumer or producer).
         */
        void next();

        /**
         * Reset the {@link TopologyHandler}.
         * Typically reset the cursor. To call e.g. when a new set of
         * clients need to configured.
         */
        void reset();

    }

    /**
     * Support class that contains queue configuration.
     */
    static abstract class TopologyHandlerSupport {

        protected final MulticastParams params;

        protected TopologyHandlerSupport(MulticastParams params) {
            this.params = params;
        }

        protected List<String> configureQueues(Connection connection, List<String> queues, Runnable afterQueueConfigurationCallback) throws IOException {
            Channel channel = connection.createChannel();
            if (!params.predeclared || !exchangeExists(connection, params.exchangeName)) {
                channel.exchangeDeclare(params.exchangeName, params.exchangeType);
            }

            // To ensure we get at-least 1 default queue:
            // (don't declare any queues when --predeclared is passed,
            // otherwise unwanted server-named queues without consumers will pile up.
            // see https://github.com/rabbitmq/rabbitmq-perf-test/issues/25 and
            // https://github.com/rabbitmq/rabbitmq-perf-test/issues/43)
            if (!params.predeclared && queues.isEmpty()) {
                queues = Collections.singletonList("");
            }

            List<String> generatedQueueNames = new ArrayList<>();
            for (String qName : queues) {
                if (!params.predeclared || !queueExists(connection, qName)) {
                    qName = channel.queueDeclare(qName,
                        params.flags.contains("persistent"),
                        params.isExclusive(),
                        params.autoDelete,
                        params.queueArguments).getQueue();
                }
                generatedQueueNames.add(qName);
                // skipping binding to default exchange,
                // as it's not possible to explicitly bind to it.
                if (!"".equals(params.exchangeName) && !"amq.default".equals(params.exchangeName) && !params.skipBindingQueues) {
                    channel.queueBind(qName, params.exchangeName, params.topologyHandler.getRoutingKey());
                }
                afterQueueConfigurationCallback.run();
            }
            channel.abort();

            return generatedQueueNames;
        }

    }

    /**
     * {@link TopologyHandler} implementation that contains a list of a queues and a fixed routing key.
     */
    static class FixedQueuesTopologyHandler extends TopologyHandlerSupport implements TopologyHandler {

        final String routingKey;

        final List<String> queueNames;

        FixedQueuesTopologyHandler(MulticastParams params, String routingKey, List<String> queueNames) {
            super(params);
            if (routingKey == null) {
                this.routingKey = UUID.randomUUID().toString();
            } else {
                this.routingKey = routingKey;
            }
            this.queueNames = queueNames == null ? new ArrayList<>() : queueNames;
        }

        @Override
        public String getRoutingKey() {
            return routingKey;
        }

        @Override
        public List<String> configureQueuesForClient(Connection connection) throws IOException {
            return configureQueues(connection, this.queueNames, () -> {});
        }

        @Override
        public List<String> configureAllQueues(Connection connection) throws IOException {
            if (shouldConfigureQueues() && !this.params.isExclusive()) {
                return configureQueues(connection, this.queueNames, () -> {});
            }
            return null;
        }

        public boolean shouldConfigureQueues() {
            // if no consumer, no queue has been configured and
            // some queues are specified, we have to configure the queues and their bindings
            return this.params.consumerCount == 0 && !(queueNames.size() == 0);
        }

        @Override
        public void next() {
            // NO OP
        }

        @Override
        public void reset() {
            // NO OP
        }
    }

    /**
     * {@link TopologyHandler} meant to use a sequence of queues and routing keys.
     * E.g. <code>perf-test-1</code>, <code>perf-test-2</code>, etc.
     * The routing key has the same value as the current queue.
     */
    static class SequenceTopologyHandler extends TopologyHandlerSupport implements TopologyHandler {

        final List<String> queues;
        int index = 0;

        public SequenceTopologyHandler(MulticastParams params, int from, int to, String queuePattern) {
            super(params);
            queues = new ArrayList<>(to - from + 1);
            for (int i = from; i <= to; i++) {
                queues.add(String.format(queuePattern, i));
            }
        }

        @Override
        public String getRoutingKey() {
            return this.getQueueNamesForClient().get(0);
        }

        @Override
        public List<String> configureQueuesForClient(Connection connection) throws IOException {
            if (this.params.isExclusive()) {
                // if queues are exclusive, we create them for each consumer connection
                return configureQueues(connection, getQueueNamesForClient(), () -> {});
            } else {
                // just returns the name of the current queue, no need to create it,
                // it's been configured already
                return getQueueNamesForClient();
            }

        }

        @Override
        public List<String> configureAllQueues(Connection connection) throws IOException {
            // if queues are exclusive, we'll create them for each consumer connection
            if (this.params.isExclusive()) {
                return null;
            } else {
                return configureQueues(connection, getQueueNames(), () -> this.next());
            }
        }

        protected List<String> getQueueNames() {
            return Collections.unmodifiableList(queues);
        }

        protected List<String> getQueueNamesForClient() {
            return Collections.singletonList(queues.get(index % queues.size()));
        }

        @Override
        public void next() {
            index++;
        }

        @Override
        public void reset() {
            index = 0;
        }
    }
}
