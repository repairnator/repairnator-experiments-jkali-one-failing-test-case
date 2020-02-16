package com.github.princesslana.eriscasper.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.princesslana.eriscasper.BotToken;
import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.ratelimiter.RateLimiterRegistry;
import io.github.resilience4j.ratelimiter.operator.RateLimiterOperator;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.Callable;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Routes {

  private static final Logger LOG = LoggerFactory.getLogger(Routes.class);

  private static final MediaType MEDIA_TYPE_JSON = MediaType.parse("application/json");

  private final RateLimiterRegistry rateLimiterRegistry = RateLimiterRegistry.ofDefaults();

  private final BotToken token;

  private final OkHttpClient client;
  private final ObjectMapper jackson;

  public Routes(BotToken token, OkHttpClient client, ObjectMapper jackson) {
    this.token = token;
    this.client = client;
    this.jackson = jackson;
  }

  public <O> Single<O> execute(Route<Void, O> route) {
    return execute(route, null);
  }

  public <I, O> Single<O> execute(Route<I, O> route, I data) {
    Function<Request.Builder, Request.Builder> addAuthorizationHeader =
        b -> b.header("Authorization", "Bot " + token.unwrap());

    return Single.just(route)
        .map(r -> r.newRequestBuilder(data))
        .map(addAuthorizationHeader)
        .map(Request.Builder::build)
        .flatMap(rq -> executeRequest(route, rq))
        .lift(RateLimiterOperator.of(getRateLimiter(route)));
  }

  private <O> Single<O> executeRequest(Route<?, O> route, Request rq) {
    Callable<Response> execute =
        () -> {
          LOG.debug("Executing: {}...", route);
          return client.newCall(rq).execute();
        };

    Consumer<Response> close =
        r -> {
          r.close();
          LOG.debug("Closed: {}.", r);
        };

    Consumer<Response> updateRateLimit =
        r -> {
          String remainingHeader = r.header("X-RateLimit-Remaining");
          String resetHeader = r.header("X-RateLimit-Reset");

          try {
            int remaining = Integer.parseInt(remainingHeader, 10);
            Instant until = Instant.ofEpochSecond(Long.parseLong(resetHeader));

            RateLimiter rl = getRateLimiter(route);

            rl.changeLimitForPeriod(remaining);
            rl.changeTimeoutDuration(Duration.between(Instant.now(), until));
          } catch (IllegalArgumentException e) {
            // we use a little EAFP here (https://docs.python.org/3/glossary.html#term-eafp)
            // If the headers are absent or not numeric or the values passed into the rate limiter
            // are invalid we may end up here
            LOG.debug(
                "Could not update rate limit to {}/{} ({})",
                remainingHeader,
                resetHeader,
                e.getMessage());
          }
        };

    // Single#using does not work here, as it performs the close operation immediately upon emitting
    // the response, because once it's received the response it knows it can receive no more.
    //
    // Observable#using closes the response at a more appropriate time. My feeling is that this is
    // wrong - we're tricking rxjava into thinking there is something coming next so as to delay the
    // close.
    return Observable.using(execute, Observable::just, close)
        .subscribeOn(Schedulers.io())
        .doOnNext(r -> LOG.debug("Done: {} -> {}.", route, r))
        .doOnNext(updateRateLimit)
        .flatMapSingle(
            r ->
                r.isSuccessful()
                    ? Single.just(r)
                    : Single.error(new IllegalStateException("Unexpected response: " + r)))
        .doOnError(e -> LOG.warn("Error: {} - {}.", route, e))
        .map(route.getResponseHandler())
        .firstOrError();
  }

  private RateLimiter getRateLimiter(Route<?, ?> r) {
    return rateLimiterRegistry.rateLimiter(r.getPath());
  }
}
