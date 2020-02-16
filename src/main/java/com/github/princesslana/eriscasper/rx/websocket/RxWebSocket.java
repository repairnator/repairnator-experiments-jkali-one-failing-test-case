package com.github.princesslana.eriscasper.rx.websocket;

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import java.util.Optional;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RxWebSocket {

  // https://tools.ietf.org/html/rfc6455#section-7.4
  private static final int NORMAL_CLOSURE = 1000;

  private static final Logger LOG = LoggerFactory.getLogger(RxWebSocket.class);

  private final OkHttpClient http;

  private WebSocket ws;

  public RxWebSocket(OkHttpClient http) {
    this.http = http;
  }

  public Observable<RxWebSocketEvent> connect(String url) {
    return Observable.<RxWebSocketEvent>create(
            em -> {
              Request rq = new Request.Builder().url(url).build();

              ws = http.newWebSocket(rq, new Listener(em));
            })
        .doOnNext(e -> LOG.trace("Received: {}.", e))
        .doOnError(e -> LOG.warn("Error: {}.", e));
  }

  public Completable send(String text) {
    return Completable.fromAction(() -> ws.send(text))
        .doOnComplete(() -> LOG.trace("Sent: {}.", text));
  }

  private static class Listener extends WebSocketListener {
    private final ObservableEmitter<RxWebSocketEvent> em;

    private Listener(ObservableEmitter<RxWebSocketEvent> em) {
      this.em = em;
    }

    @Override
    public void onClosed(WebSocket ws, int code, String reason) {
      em.onNext(ClosedTuple.of(ws, code, reason));
      em.onComplete();
    }

    @Override
    public void onClosing(WebSocket ws, int code, String reason) {
      em.onNext(ClosingTuple.of(ws, code, reason));
      ws.close(NORMAL_CLOSURE, null);
    }

    @Override
    public void onFailure(WebSocket ws, Throwable t, Response response) {
      em.onNext(FailureTuple.of(ws, t, Optional.ofNullable(response)));
      em.onError(t);
    }

    @Override
    public void onMessage(WebSocket ws, okio.ByteString bytes) {
      em.onNext(ByteStringMessageTuple.of(ws, bytes));
    }

    @Override
    public void onMessage(WebSocket ws, String text) {
      em.onNext(StringMessageTuple.of(ws, text));
    }

    @Override
    public void onOpen(WebSocket ws, Response response) {
      em.onNext(OpenTuple.of(ws, response));
    }
  }
}
