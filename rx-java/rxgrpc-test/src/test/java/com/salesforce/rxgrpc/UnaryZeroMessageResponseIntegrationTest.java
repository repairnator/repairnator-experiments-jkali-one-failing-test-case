/*
 *  Copyright (c) 2017, salesforce.com, inc.
 *  All rights reserved.
 *  Licensed under the BSD 3-Clause license.
 *  For full license text, see LICENSE.txt file in the repo root  or https://opensource.org/licenses/BSD-3-Clause
 */

package com.salesforce.rxgrpc;

import com.salesforce.grpc.testing.contrib.NettyGrpcServerRule;
import io.grpc.*;
import io.grpc.stub.StreamObserver;
import io.reactivex.Flowable;
import io.reactivex.Single;
import io.reactivex.observers.TestObserver;
import io.reactivex.subscribers.TestSubscriber;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

@SuppressWarnings("Duplicates")
public class UnaryZeroMessageResponseIntegrationTest {
    @Rule
    public NettyGrpcServerRule serverRule = new NettyGrpcServerRule();

    @Rule
    public UnhandledRxJavaErrorRule errorRule = new UnhandledRxJavaErrorRule().autoVerifyNoError();

    private static class MissingUnaryResponseService extends GreeterGrpc.GreeterImplBase {
        @Override
        public void sayHello(HelloRequest request, StreamObserver<HelloResponse> responseObserver) {
            responseObserver.onCompleted();
        }

        @Override
        public StreamObserver<HelloRequest> sayHelloReqStream(StreamObserver<HelloResponse> responseObserver) {
            return new StreamObserver<HelloRequest>() {
                @Override
                public void onNext(HelloRequest helloRequest) {
                    responseObserver.onCompleted();
                }

                @Override
                public void onError(Throwable throwable) {

                }

                @Override
                public void onCompleted() {

                }
            };
        }
    }

    @Test
    public void zeroMessageResponseOneToOne() {
        serverRule.getServiceRegistry().addService(new MissingUnaryResponseService());

        RxGreeterGrpc.RxGreeterStub stub = RxGreeterGrpc.newRxStub(serverRule.getChannel());
        Single<HelloRequest> req = Single.just(HelloRequest.newBuilder().setName("rxjava").build());
        Single<HelloResponse> resp = req.compose(stub::sayHello);

        TestObserver<String> testObserver = resp.map(HelloResponse::getMessage).test();
        testObserver.awaitTerminalEvent(3, TimeUnit.SECONDS);
        testObserver.assertError(StatusRuntimeException.class);
        testObserver.assertError(t -> ((StatusRuntimeException) t).getStatus().getCode() == Status.CANCELLED.getCode());
    }

    @Test
    public void zeroMessageResponseManyToOne() {
        serverRule.getServiceRegistry().addService(new MissingUnaryResponseService());

        RxGreeterGrpc.RxGreeterStub stub = RxGreeterGrpc.newRxStub(serverRule.getChannel());
        Flowable<HelloRequest> req = Flowable.just(
                HelloRequest.newBuilder().setName("a").build(),
                HelloRequest.newBuilder().setName("b").build(),
                HelloRequest.newBuilder().setName("c").build());

        Single<HelloResponse> resp = req.as(stub::sayHelloReqStream);

        TestObserver<String> testObserver = resp.map(HelloResponse::getMessage).test();
        testObserver.awaitTerminalEvent(3, TimeUnit.SECONDS);
        testObserver.assertError(StatusRuntimeException.class);
        testObserver.assertError(t -> ((StatusRuntimeException) t).getStatus().getCode() == Status.CANCELLED.getCode());
    }
}
