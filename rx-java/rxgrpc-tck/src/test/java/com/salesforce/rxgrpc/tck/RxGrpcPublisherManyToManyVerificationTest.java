/*
 *  Copyright (c) 2017, salesforce.com, inc.
 *  All rights reserved.
 *  Licensed under the BSD 3-Clause license.
 *  For full license text, see LICENSE.txt file in the repo root  or https://opensource.org/licenses/BSD-3-Clause
 */

package com.salesforce.rxgrpc.tck;

import com.google.common.util.concurrent.MoreExecutors;
import io.grpc.ManagedChannel;
import io.grpc.Server;
import io.grpc.inprocess.InProcessChannelBuilder;
import io.grpc.inprocess.InProcessServerBuilder;
import io.reactivex.Flowable;
import io.reactivex.schedulers.Schedulers;
import org.reactivestreams.Publisher;
import org.reactivestreams.tck.PublisherVerification;
import org.reactivestreams.tck.TestEnvironment;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * Publisher tests from the Reactive Streams Technology Compatibility Kit.
 * https://github.com/reactive-streams/reactive-streams-jvm/tree/master/tck
 */
@SuppressWarnings("Duplicates")
@Test(timeOut = 3000)
public class RxGrpcPublisherManyToManyVerificationTest extends PublisherVerification<Message> {
    public static final long DEFAULT_TIMEOUT_MILLIS = 500L;
    public static final long PUBLISHER_REFERENCE_CLEANUP_TIMEOUT_MILLIS = 500L;

    public RxGrpcPublisherManyToManyVerificationTest() {
        super(new TestEnvironment(DEFAULT_TIMEOUT_MILLIS, DEFAULT_TIMEOUT_MILLIS), PUBLISHER_REFERENCE_CLEANUP_TIMEOUT_MILLIS);
    }

    private static Server server;
    private static ManagedChannel channel;

    @BeforeClass
    public static void setup() throws Exception {
        System.out.println("RxGrpcPublisherManyToManyVerificationTest");
        server = InProcessServerBuilder.forName("RxGrpcPublisherManyToManyVerificationTest").addService(new TckService()).build().start();
        channel = InProcessChannelBuilder.forName("RxGrpcPublisherManyToManyVerificationTest").usePlaintext().build();
    }

    @AfterClass
    public static void tearDown() throws Exception {
        channel.shutdown();
        server.shutdown();

        server = null;
        channel = null;
    }

    @Override
    public Publisher<Message> createPublisher(long elements) {
        RxTckGrpc.RxTckStub stub = RxTckGrpc.newRxStub(channel);
        Flowable<Message> request = Flowable.range(0, (int)elements).map(this::toMessage);
        Publisher<Message> publisher = request.compose(stub::manyToMany).observeOn(Schedulers.from(MoreExecutors.directExecutor()));

        return publisher;
    }

    @Override
    public Publisher<Message> createFailedPublisher() {
        RxTckGrpc.RxTckStub stub = RxTckGrpc.newRxStub(channel);
        Flowable<Message> request = Flowable.just(toMessage(TckService.KABOOM));
        Publisher<Message> publisher = request.compose(stub::manyToMany);

        return publisher;
    }

    private Message toMessage(int i) {
        return Message.newBuilder().setNumber(i).build();
    }
}
