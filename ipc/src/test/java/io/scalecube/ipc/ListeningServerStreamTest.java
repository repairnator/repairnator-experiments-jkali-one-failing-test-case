package io.scalecube.ipc;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import io.scalecube.ipc.Event.Topic;
import io.scalecube.transport.Address;

import org.junit.Before;
import org.junit.Test;

import rx.observers.AssertableSubscriber;
import rx.subjects.BehaviorSubject;
import rx.subjects.Subject;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class ListeningServerStreamTest {

  private static final Duration TIMEOUT = Duration.ofMillis(3000);
  private static final long TIMEOUT_MILLIS = TIMEOUT.toMillis();

  private ListeningServerStream serverStream;

  @Before
  public void setUp() {
    serverStream = ListeningServerStream.newListeningServerStream().withListenAddress("localhost");
  }

  @Test
  public void testBindWithDefaults() {
    ListeningServerStream serverStream =
        ListeningServerStream.newListeningServerStream().withListenAddress("localhost");
    assertEquals("127.0.0.1:5801", serverStream.bindAwait().toString());
  }

  @Test
  public void testServerStreamBindsOnAvailablePort() throws Exception {
    Address address1 = serverStream.withPort(5555).bindAwait();
    Address address2 = serverStream.withPort(5555).bindAwait();
    Address address3 = serverStream.withPort(5555).bindAwait();
    assertEquals("127.0.0.1:5555", address1.toString());
    assertEquals("127.0.0.1:5556", address2.toString());
    assertEquals("127.0.0.1:5557", address3.toString());
  }

  @Test
  public void testServerStreamBindsThenUnbinds() throws Exception {
    String expectedAddress = "127.0.0.1:5801";
    ListeningServerStream serverStream =
        ListeningServerStream.newListeningServerStream().withListenAddress("localhost");

    try {
      assertEquals(expectedAddress, serverStream.bindAwait().toString());
    } finally {
      serverStream.close();
    }

    // check you can bind on same port after previous close
    serverStream = ListeningServerStream.newListeningServerStream().withListenAddress("localhost");
    try {
      assertEquals(expectedAddress, serverStream.bindAwait().toString());
    } finally {
      serverStream.close();
    }
  }

  @Test
  public void testServerStreamOnClose() throws Exception {
    AtomicBoolean onCloseBoolean = new AtomicBoolean();
    serverStream.listenClose(aVoid -> onCloseBoolean.set(true));
    serverStream.close();
    assertTrue(onCloseBoolean.get());
  }

  @Test
  public void testBranchingAtBind() {
    // check default bind
    String localhost = serverStream.withListenAddress("localhost").withPort(4444).bindAwait().toString();
    assertEquals("127.0.0.1:4444", localhost);

    try {
      // say you don't want autoincrement and try bind
      serverStream.withListenAddress("localhost").withPort(4444).withPortAutoIncrement(false).bindAwait();
      fail("Expected BindException here");
    } catch (Exception e) {
      assertTrue(e.getMessage(), e.getMessage().contains("Address already in use"));
    }
  }

  @Test
  public void testServerStreamRemotePartyClosed() throws Exception {
    Subject<Event, Event> serverStreamSubject = BehaviorSubject.create();
    serverStream.listen().subscribe(serverStreamSubject);
    AssertableSubscriber<Event> serverStreamSubscriber = serverStreamSubject.test();

    Address address = serverStream.bindAwait();

    ClientStream clientStream = ClientStream.newClientStream();
    clientStream.send(address, ServiceMessage.withQualifier("q/test").build());

    List<Event> events =
        serverStreamSubscriber.awaitValueCount(2, TIMEOUT_MILLIS, TimeUnit.MILLISECONDS).getOnNextEvents();
    assertEquals(Topic.ChannelContextSubscribed, events.get(0).getTopic());
    assertEquals(Topic.ReadSuccess, events.get(1).getTopic());

    // close remote party and receive corresp events
    BehaviorSubject<Event> channelInactiveSubject = BehaviorSubject.create();
    serverStream.listenChannelContextClosed().subscribe(channelInactiveSubject);

    // close connector channel at client stream
    clientStream.close();

    // await a bit
    TimeUnit.SECONDS.sleep(3);

    // assert that serverStream received event about closed client connector channel
    Event event = channelInactiveSubject.test().getOnNextEvents().get(0);
    assertEquals(Topic.ChannelContextClosed, event.getTopic());
    assertFalse("Must not have error at this point", event.hasError());
  }
}
