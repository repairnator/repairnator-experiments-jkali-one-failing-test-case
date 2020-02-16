/*
 * Copyright 2018 The OpenTracing Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package io.opentracing.contrib.hazelcast;

import com.hazelcast.core.ExecutionCallback;
import com.hazelcast.core.ICompletableFuture;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class TracingCompletableFuture<V> implements ICompletableFuture<V> {

  private final ICompletableFuture<V> future;
  private final Map<String, String> spanContextMap;
  private final boolean traceWithActiveSpanOnly;

  public TracingCompletableFuture(ICompletableFuture<V> future,
      boolean traceWithActiveSpanOnly, Map<String, String> spanContextMap) {
    this.future = future;
    this.spanContextMap = spanContextMap;
    this.traceWithActiveSpanOnly = traceWithActiveSpanOnly;
    future.andThen(new TracingExecutionCallback<>(new NoopExecutionCallback<>(),
        traceWithActiveSpanOnly, spanContextMap));
  }

  @Override
  public void andThen(ExecutionCallback<V> callback) {
    future
        .andThen(new TracingExecutionCallback<>(callback, traceWithActiveSpanOnly, spanContextMap));
  }

  @Override
  public void andThen(ExecutionCallback<V> callback,
      Executor executor) {
    future
        .andThen(new TracingExecutionCallback<>(callback, traceWithActiveSpanOnly, spanContextMap),
            executor);
  }

  @Override
  public boolean cancel(boolean mayInterruptIfRunning) {
    return future.cancel(mayInterruptIfRunning);
  }

  @Override
  public boolean isCancelled() {
    return future.isCancelled();
  }

  @Override
  public boolean isDone() {
    return future.isDone();
  }

  @Override
  public V get() throws InterruptedException, ExecutionException {
    return future.get();
  }

  @Override
  public V get(long timeout, TimeUnit unit)
      throws InterruptedException, ExecutionException, TimeoutException {
    return future.get(timeout, unit);
  }

}
