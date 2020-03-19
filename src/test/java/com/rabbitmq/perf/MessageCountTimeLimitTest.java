// Copyright (c) 2018-Present Pivotal Software, Inc.  All rights reserved.
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

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.Envelope;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.awaitility.Awaitility.waitAtMost;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.junit.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

/**
 *
 */
public class MessageCountTimeLimitTest {

    @Mock
    ConnectionFactory cf;
    @Mock
    Connection c;
    @Mock
    Channel ch;
    @Mock
    Stats stats;

    MulticastParams params;

    ExecutorService executorService;

    MulticastSet.ThreadingHandler th;

    AtomicBoolean testIsDone;

    volatile long testDurationInMs;

    static Stream<Arguments> producerCountArguments() {
        return Stream.of(
            Arguments.of(1, 1),
            Arguments.of(10, 1),
            Arguments.of(1, 10),
            Arguments.of(2, 5)
        );
    }

    static Stream<Arguments> consumerCountArguments() {
        return Stream.of(
            Arguments.of(1, 1),
            Arguments.of(10, 1),
            Arguments.of(1, 10),
            Arguments.of(2, 5)
        );
    }

    @BeforeEach
    public void init() throws Exception {
        initMocks(this);

        when(cf.newConnection(anyString())).thenReturn(c);
        when(c.createChannel()).thenReturn(ch);

        testIsDone = new AtomicBoolean(false);
        executorService = Executors.newCachedThreadPool();
        th = new MulticastSet.DefaultThreadingHandler();
        testDurationInMs = -1;
        params = new MulticastParams();
        params.setPredeclared(true);
    }

    @AfterEach
    public void tearDown() {
        executorService.shutdownNow();
        th.shutdown();
    }

    @Test
    public void noLimit() throws Exception {
        countsAndTimeLimit(0, 0, 0);
        MulticastSet multicastSet = getMulticastSet();

        CountDownLatch publishedLatch = new CountDownLatch(1000);
        doAnswer(invocation -> {
            publishedLatch.countDown();
            return null;
        }).when(ch).basicPublish(anyString(), anyString(),
            anyBoolean(), eq(false),
            any(), any());

        run(multicastSet);

        assertThat("1000 messages should have been published by now",
            publishedLatch.await(5, TimeUnit.SECONDS), is(true));

        assertThat(testIsDone.get(), is(false));
        // only the configuration connection has been closed
        // so the test is still running in the background
        verify(c, times(1)).close();
    }

    // --time 5
    @Test
    public void timeLimit() {
        countsAndTimeLimit(0, 0, 5);
        MulticastSet multicastSet = getMulticastSet();

        run(multicastSet);

        waitAtMost(10, TimeUnit.SECONDS).until(() -> testIsDone.get(), is(true));
        assertThat(testDurationInMs, greaterThanOrEqualTo(5000L));
    }

    // -y 1 --pmessages 10 -x n -X m
    @ParameterizedTest
    @MethodSource("producerCountArguments")
    public void producerCount(int producersCount, int channelsCount) throws Exception {
        int messagesCount = 10;
        countsAndTimeLimit(messagesCount, 0, 0);
        params.setProducerCount(producersCount);
        params.setProducerChannelCount(channelsCount);
        MulticastSet multicastSet = getMulticastSet();

        run(multicastSet);

        int messagesTotal = producersCount * channelsCount * messagesCount;

        CountDownLatch publishedLatch = new CountDownLatch(messagesTotal);
        doAnswer(invocation -> {
            publishedLatch.countDown();
            return null;
        }).when(ch).basicPublish(anyString(), anyString(),
            anyBoolean(), anyBoolean(),
            any(), any());

        assertThat(messagesTotal + " messages should have been published by now",
            publishedLatch.await(10, TimeUnit.SECONDS), is(true));
        waitAtMost(5, TimeUnit.SECONDS).until(() -> testIsDone.get(), is(true));
        verify(ch, times(messagesTotal))
            .basicPublish(anyString(), anyString(),
                anyBoolean(), anyBoolean(),
                any(), any(byte[].class)
            );
    }

    // --cmessages 10 -y n -Y m
    @ParameterizedTest
    @MethodSource("consumerCountArguments")
    public void consumerCount(int consumersCount, int channelsCount) throws Exception {
        int messagesCount = 10;
        countsAndTimeLimit(0, messagesCount, 0);
        params.setConsumerCount(consumersCount);
        params.setConsumerChannelCount(channelsCount);
        params.setQueueNames(asList("queue"));
        MulticastSet multicastSet = getMulticastSet();

        CountDownLatch consumersLatch = new CountDownLatch(consumersCount * channelsCount);
        AtomicInteger consumerTagCounter = new AtomicInteger(0);
        ArgumentCaptor<Consumer> consumerArgumentCaptor = ArgumentCaptor.forClass(Consumer.class);
        doAnswer(invocation -> {
            consumersLatch.countDown();
            return consumerTagCounter.getAndIncrement() + "";
        }).when(ch).basicConsume(anyString(), anyBoolean(), consumerArgumentCaptor.capture());

        run(multicastSet);

        assertThat(consumersCount * channelsCount + " consumer(s) should have been registered by now",
            consumersLatch.await(5, TimeUnit.SECONDS), is(true));

        waitAtMost(5, TimeUnit.SECONDS).until(() -> consumerArgumentCaptor.getAllValues(), hasSize(consumersCount * channelsCount));

        for (Consumer consumer : consumerArgumentCaptor.getAllValues()) {
            sendMessagesToConsumer(messagesCount, consumer);
        }

        waitAtMost(5, TimeUnit.SECONDS).until(() -> testIsDone.get(), is(true));
    }

    // --time 5 -x 1 --pmessages 10 -y 1 --cmessages 10
    @Test
    public void timeLimitTakesPrecedenceOverCounts() throws Exception {
        int nbMessages = 10;
        countsAndTimeLimit(nbMessages, nbMessages, 5);
        params.setQueueNames(asList("queue"));
        MulticastSet multicastSet = getMulticastSet();

        CountDownLatch publishedLatch = new CountDownLatch(nbMessages);
        doAnswer(invocation -> {
            publishedLatch.countDown();
            return null;
        }).when(ch).basicPublish(anyString(), anyString(),
            anyBoolean(), eq(false),
            any(), any());

        CountDownLatch consumersLatch = new CountDownLatch(1);
        AtomicInteger consumerTagCounter = new AtomicInteger(0);
        ArgumentCaptor<Consumer> consumerArgumentCaptor = ArgumentCaptor.forClass(Consumer.class);
        doAnswer(invocation -> {
            consumersLatch.countDown();
            return consumerTagCounter.getAndIncrement() + "";
        }).when(ch).basicConsume(anyString(), anyBoolean(), consumerArgumentCaptor.capture());

        run(multicastSet);

        assertThat("1 consumer should have been registered by now",
            consumersLatch.await(5, TimeUnit.SECONDS), is(true));
        assertThat(consumerArgumentCaptor.getValue(), notNullValue());
        sendMessagesToConsumer(nbMessages / 2, consumerArgumentCaptor.getValue());

        assertThat(nbMessages + " messages should have been published by now",
            publishedLatch.await(5, TimeUnit.SECONDS), is(true));

        assertThat(testIsDone.get(), is(false));

        waitAtMost(10, TimeUnit.SECONDS).until(() -> testIsDone.get(), is(true));
        assertThat(testDurationInMs, greaterThanOrEqualTo(5000L));
    }

    // -x 0 -y 1
    @Test
    public void consumerOnlyDoesNotStop() throws Exception {
        countsAndTimeLimit(0, 0, 0);
        params.setQueueNames(asList("queue"));
        params.setProducerCount(0);
        params.setConsumerCount(1);

        MulticastSet multicastSet = getMulticastSet();

        CountDownLatch consumersLatch = new CountDownLatch(1);
        AtomicInteger consumerTagCounter = new AtomicInteger(0);
        ArgumentCaptor<Consumer> consumerArgumentCaptor = ArgumentCaptor.forClass(Consumer.class);
        doAnswer(invocation -> {
            consumersLatch.countDown();
            return consumerTagCounter.getAndIncrement() + "";
        }).when(ch).basicConsume(anyString(), anyBoolean(), consumerArgumentCaptor.capture());

        run(multicastSet);

        assertThat("1 consumer should have been registered by now",
            consumersLatch.await(5, TimeUnit.SECONDS), is(true));
        assertThat(consumerArgumentCaptor.getValue(), notNullValue());

        assertThat(testIsDone.get(), is(false));
        // only the configuration connection has been closed
        // so the test is still running in the background
        verify(c, times(1)).close();
    }

    // -x 0 -y 1
    @Test
    public void producerOnlyDoesNotStop() throws Exception {
        countsAndTimeLimit(0, 0, 0);
        params.setProducerCount(1);
        params.setConsumerCount(0);

        MulticastSet multicastSet = getMulticastSet();

        CountDownLatch publishedLatch = new CountDownLatch(1000);
        doAnswer(invocation -> {
            publishedLatch.countDown();
            return null;
        }).when(ch).basicPublish(anyString(), anyString(),
            anyBoolean(), eq(false),
            any(), any());

        run(multicastSet);

        assertThat("1000 messages should have been published by now",
            publishedLatch.await(5, TimeUnit.SECONDS), is(true));
        assertThat(testIsDone.get(), is(false));
        // only the configuration connection has been closed
        // so the test is still running in the background
        verify(c, times(1)).close();
    }

    // producer only test doesn't stop immediately

    private void sendMessagesToConsumer(int messagesCount, Consumer consumer) {
        IntStream.range(0, messagesCount).forEach(i -> {
            executorService.submit(() -> {
                try {
                    consumer.handleDelivery(
                        "",
                        new Envelope(1, false, "", ""),
                        null,
                        new byte[20]
                    );
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        });
    }

    private void countsAndTimeLimit(int pmc, int cmc, int time) {
        params.setProducerMsgCount(pmc);
        params.setConsumerMsgCount(cmc);
        params.setTimeLimit(time);
    }

    private MulticastSet getMulticastSet() {
        MulticastSet set = new MulticastSet(
            stats, cf, params, singletonList("amqp://localhost"),
            PerfTest.getCompletionHandler(params)
        );
        set.setThreadingHandler(th);
        return set;
    }

    private void run(MulticastSet multicastSet) {
        executorService.submit(() -> {
            try {
                long start = System.nanoTime();
                multicastSet.run();
                testDurationInMs = (System.nanoTime() - start) / 1_000_000;
                testIsDone.set(true);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }
}
