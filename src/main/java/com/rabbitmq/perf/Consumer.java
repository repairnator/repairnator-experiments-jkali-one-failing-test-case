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

import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import com.rabbitmq.client.ShutdownSignalException;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BiFunction;

public class Consumer extends ProducerConsumerBase implements Runnable {

    private ConsumerImpl                q;
    private final Channel               channel;
    private final String                id;
    private final List<String>          queueNames;
    private final int                   txSize;
    private final boolean               autoAck;
    private final int                   multiAckEvery;
    private final Stats                 stats;
    private final int                   msgLimit;
    private final Map<String, String>   consumerTagBranchMap = Collections.synchronizedMap(new HashMap<String, String>());
    private final ConsumerLatency       consumerLatency;
    private final BiFunction<BasicProperties, byte[], Long> timestampExtractor;
    private final TimestampProvider     timestampProvider;
    private final MulticastSet.CompletionHandler completionHandler;
    private final AtomicBoolean completed = new AtomicBoolean(false);

    public Consumer(Channel channel, String id,
                    List<String> queueNames, int txSize, boolean autoAck,
                    int multiAckEvery, Stats stats, float rateLimit, int msgLimit,
                    int consumerLatencyInMicroSeconds,
                    TimestampProvider timestampProvider,
                    MulticastSet.CompletionHandler completionHandler) {

        this.channel           = channel;
        this.id                = id;
        this.queueNames        = queueNames;
        this.rateLimit         = rateLimit;
        this.txSize            = txSize;
        this.autoAck           = autoAck;
        this.multiAckEvery     = multiAckEvery;
        this.stats             = stats;
        this.msgLimit          = msgLimit;
        this.timestampProvider = timestampProvider;
        this.completionHandler = completionHandler;

        if (consumerLatencyInMicroSeconds <= 0) {
            this.consumerLatency = new NoWaitConsumerLatency();
        } else if (consumerLatencyInMicroSeconds >= 1000) {
            this.consumerLatency = new ThreadSleepConsumerLatency(consumerLatencyInMicroSeconds / 1000);
        } else {
            this.consumerLatency = new BusyWaitConsumerLatency(consumerLatencyInMicroSeconds * 1000);
        }

        if (timestampProvider.isTimestampInHeader()) {
            this.timestampExtractor = (properties, body) -> {
                    Object timestamp = properties.getHeaders().get(Producer.TIMESTAMP_HEADER);
                    return timestamp == null ? Long.MAX_VALUE : (Long) timestamp;
            };
        } else {
            this.timestampExtractor = (properties, body) -> {
                DataInputStream d = new DataInputStream(new ByteArrayInputStream(body));
                try {
                    d.readInt();
                    return d.readLong();
                } catch (IOException e) {
                    throw new RuntimeException("Error while extracting timestamp from body");
                }

            };
        }
    }

    public void run() {
        try {
            q = new ConsumerImpl(channel);
            for (String qName : queueNames) {
                String tag = channel.basicConsume(qName, autoAck, q);
                consumerTagBranchMap.put(tag, qName);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ShutdownSignalException e) {
            throw new RuntimeException(e);
        }
    }

    private class ConsumerImpl extends DefaultConsumer {
        long now;
        int totalMsgCount = 0;

        private ConsumerImpl(Channel channel) {
            super(channel);
            lastStatsTime = now = System.currentTimeMillis();
            msgCount = 0;
        }

        @Override
        public void handleDelivery(String consumerTag, Envelope envelope, BasicProperties properties, byte[] body) throws IOException {
            totalMsgCount++;
            msgCount++;

            if (msgLimit == 0 || msgCount <= msgLimit) {
                long messageTimestamp = timestampExtractor.apply(properties, body);
                long nowTimestamp = timestampProvider.getCurrentTime();

                if (!autoAck) {
                    if (multiAckEvery == 0) {
                        channel.basicAck(envelope.getDeliveryTag(), false);
                    } else if (totalMsgCount % multiAckEvery == 0) {
                        channel.basicAck(envelope.getDeliveryTag(), true);
                    }
                }

                if (txSize != 0 && totalMsgCount % txSize == 0) {
                    channel.txCommit();
                }

                long diff_time = timestampProvider.getDifference(nowTimestamp, messageTimestamp);
                stats.handleRecv(id.equals(envelope.getRoutingKey()) ? diff_time : 0L);

                now = System.currentTimeMillis();
                if (rateLimit > 0.0f) {
                    delay(now);
                }
                consumerLatency.simulateLatency();
            }
            if (msgLimit != 0 && msgCount >= msgLimit) { // NB: not quite the inverse of above
                countDown();
            }
        }

        @Override
        public void handleShutdownSignal(String consumerTag, ShutdownSignalException sig) {
            countDown();
        }

        @Override
        public void handleCancel(String consumerTag) throws IOException {
            System.out.printf("Consumer cancelled by broker for tag: %s", consumerTag);
            if (consumerTagBranchMap.containsKey(consumerTag)) {
                String qName = consumerTagBranchMap.get(consumerTag);
                System.out.printf("Re-consuming. Queue: %s for Tag: %s", qName, consumerTag);
                channel.basicConsume(qName, autoAck, q);
            } else {
                System.out.printf("Could not find queue for consumer tag: %s", consumerTag);
            }
        }
    }

    private void countDown() {
        if (completed.compareAndSet(false, true)) {
            completionHandler.countDown();
        }
    }

    private interface ConsumerLatency {

        void simulateLatency();

    }

    private static class NoWaitConsumerLatency implements ConsumerLatency {

        @Override
        public void simulateLatency() {
            // NO OP
        }

    }

    private static class ThreadSleepConsumerLatency implements ConsumerLatency {

        private final int waitTime;

        private ThreadSleepConsumerLatency(int waitTime) {
            this.waitTime = waitTime;
        }

        @Override
        public void simulateLatency() {
            try {
                Thread.sleep(waitTime);
            } catch (InterruptedException e) {
                throw new RuntimeException("Exception while simulating latency", e);
            }
        }
    }

    // from https://stackoverflow.com/a/11499351
    private static class BusyWaitConsumerLatency implements ConsumerLatency {

        private final long delay;

        private BusyWaitConsumerLatency(long delay) {
            this.delay = delay;
        }

        @Override
        public void simulateLatency() {
            long start = System.nanoTime();
            while(System.nanoTime() - start < delay);
        }
    }

}
