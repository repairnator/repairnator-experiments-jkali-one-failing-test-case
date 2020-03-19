/*
 *  Copyright (c) 2017, salesforce.com, inc.
 *  All rights reserved.
 *  Licensed under the BSD 3-Clause license.
 *  For full license text, see LICENSE.txt file in the repo root  or https://opensource.org/licenses/BSD-3-Clause
 */

package com.salesforce.servicelibs;

import com.google.protobuf.Empty;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import jline.console.ConsoleReader;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static com.salesforce.servicelibs.ConsoleUtil.*;

/**
 * Demonstrates building a gRPC streaming client using Reactor and Reactive-Grpc.
 */
public final class ReactorChatClient {
    private static final int PORT = 9999;

    private ReactorChatClient() { }

    public static void main(String[] args) throws Exception {
        // Connect to the sever
        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", PORT).usePlaintext().build();
        ReactorChatGrpc.ReactorChatStub stub = ReactorChatGrpc.newReactorStub(channel);

        CountDownLatch done = new CountDownLatch(1);
        ConsoleReader console = new ConsoleReader();

        // Prompt the user for their name
        console.println("Press ctrl+D to quit");
        String author = console.readLine("Who are you? > ");
        stub.postMessage(toMessage(author, author + " joined.")).subscribe();

        // Subscribe to incoming messages
        Disposable chatSubscription = stub.getMessages(Mono.just(Empty.getDefaultInstance())).subscribe(
            message -> {
                // Don't re-print our own messages
                if (!message.getAuthor().equals(author)) {
                    printLine(console, message.getAuthor(), message.getMessage());
                }
            },
            throwable -> {
                printLine(console, "ERROR", throwable.getMessage());
                done.countDown();
            },
            done::countDown
        );

        // Publish outgoing messages
        Flux.fromIterable(new ConsoleIterator(console, author + " > "))
            .map(msg -> toMessage(author, msg))
            .flatMap(stub::postMessage)
            .subscribe(
                empty -> { },
                throwable -> {
                    printLine(console, "ERROR", throwable.getMessage());
                    done.countDown();
                },
                done::countDown
            );

        // Wait for a signal to exit, then clean up
        done.await();
        stub.postMessage(toMessage(author, author + " left.")).subscribe();
        chatSubscription.dispose();
        channel.shutdown();
        channel.awaitTermination(1, TimeUnit.SECONDS);
        console.getTerminal().restore();
    }

    private static Mono<ChatProto.ChatMessage> toMessage(String author, String message) {
        return Mono.just(
            ChatProto.ChatMessage.newBuilder()
                .setAuthor(author)
                .setMessage(message)
                .build()
        );
    }
}
