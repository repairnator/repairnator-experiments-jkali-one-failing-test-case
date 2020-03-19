/*
 *  Copyright (c) 2017, salesforce.com, inc.
 *  All rights reserved.
 *  Licensed under the BSD 3-Clause license.
 *  For full license text, see LICENSE.txt file in the repo root  or https://opensource.org/licenses/BSD-3-Clause
 */

package com.salesforce.reactorgrpc.tck;

import io.grpc.ManagedChannel;
import io.grpc.Server;
import io.grpc.inprocess.InProcessChannelBuilder;
import io.grpc.inprocess.InProcessServerBuilder;
import org.reactivestreams.Publisher;
import org.reactivestreams.tck.PublisherVerification;
import org.reactivestreams.tck.TestEnvironment;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import reactor.core.publisher.Mono;

/**
 * Publisher tests from the Reactive Streams Technology Compatibility Kit.
 * https://github.com/reactive-streams/reactive-streams-jvm/tree/master/tck
 */
@SuppressWarnings("Duplicates")
@Test(timeOut = 3000)
public class ReactorGrpcPublisherOneToOneVerificationTest extends PublisherVerification<Message> {
    public static final long DEFAULT_TIMEOUT_MILLIS = 500L;
    public static final long PUBLISHER_REFERENCE_CLEANUP_TIMEOUT_MILLIS = 500L;

    public ReactorGrpcPublisherOneToOneVerificationTest() {
        super(new TestEnvironment(DEFAULT_TIMEOUT_MILLIS, DEFAULT_TIMEOUT_MILLIS), PUBLISHER_REFERENCE_CLEANUP_TIMEOUT_MILLIS);
    }

    private static Server server;
    private static ManagedChannel channel;

    @BeforeClass
    public static void setup() throws Exception {
        System.out.println("ReactorGrpcPublisherOneToOneVerificationTest");
        server = InProcessServerBuilder.forName("ReactorGrpcPublisherOneToOneVerificationTest").addService(new TckService()).build().start();
        channel = InProcessChannelBuilder.forName("ReactorGrpcPublisherOneToOneVerificationTest").usePlaintext().build();
    }

    @AfterClass
    public static void tearDown() throws Exception {
        channel.shutdown();
        server.shutdown();

        server = null;
        channel = null;
    }

    @Override
    public long maxElementsFromPublisher() {
        return 1;
    }

    @Override
    public Publisher<Message> createPublisher(long elements) {
        ReactorTckGrpc.ReactorTckStub stub = ReactorTckGrpc.newReactorStub(channel);
        Mono<Message> request = Mono.just(toMessage((int) elements));
        Mono<Message> publisher = stub.oneToOne(request);

        return publisher.flux();
    }

    @Override
    public Publisher<Message> createFailedPublisher() {
        ReactorTckGrpc.ReactorTckStub stub = ReactorTckGrpc.newReactorStub(channel);
        Mono<Message> request = Mono.just(toMessage(TckService.KABOOM));
        Mono<Message> publisher = stub.oneToOne(request);

        return publisher.flux();
    }

    private Message toMessage(int i) {
        return Message.newBuilder().setNumber(i).build();
    }
}
