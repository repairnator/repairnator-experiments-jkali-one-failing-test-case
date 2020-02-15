/*
 * Copyright (C) 2017-2017 DataStax Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.datastax.oss.driver.internal.core.adminrequest;

import com.datastax.oss.driver.api.core.ProtocolVersion;
import com.datastax.oss.driver.api.core.type.codec.TypeCodec;
import com.datastax.oss.driver.api.core.type.codec.TypeCodecs;
import com.datastax.oss.driver.internal.core.util.concurrent.CompletableFutures;
import com.datastax.oss.protocol.internal.response.result.ColumnSpec;
import com.datastax.oss.protocol.internal.response.result.Rows;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.AbstractIterator;
import com.google.common.collect.ImmutableMap;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletionStage;

public class AdminResult implements Iterable<AdminResult.Row> {

  private final Queue<List<ByteBuffer>> data;
  private final Map<String, ColumnSpec> columnSpecs;
  private final AdminRequestHandler nextHandler;
  private final ProtocolVersion protocolVersion;

  public AdminResult(Rows rows, AdminRequestHandler nextHandler, ProtocolVersion protocolVersion) {
    this.data = rows.getData();

    ImmutableMap.Builder<String, ColumnSpec> columnSpecsBuilder = ImmutableMap.builder();
    for (ColumnSpec spec : rows.getMetadata().columnSpecs) {
      columnSpecsBuilder.put(spec.name, spec);
    }
    // Admin queries are simple selects only, so there are no duplicate names (if that ever
    // changes, build() will fail and we'll have to do things differently)
    this.columnSpecs = columnSpecsBuilder.build();

    this.nextHandler = nextHandler;
    this.protocolVersion = protocolVersion;
  }

  /** This consumes the result's data and can be called only once. */
  @Override
  public Iterator<Row> iterator() {
    return new AbstractIterator<Row>() {
      @Override
      protected Row computeNext() {
        List<ByteBuffer> rowData = data.poll();
        return (rowData == null) ? endOfData() : new Row(columnSpecs, rowData, protocolVersion);
      }
    };
  }

  public boolean hasNextPage() {
    return nextHandler != null;
  }

  public CompletionStage<AdminResult> nextPage() {
    return (nextHandler == null)
        ? CompletableFutures.failedFuture(
            new AssertionError("No next page, use hasNextPage() before you call this method"))
        : nextHandler.start();
  }

  public static class Row {
    private static final TypeCodec<Map<String, String>> MAP_OF_TEXT_TO_TEXT =
        TypeCodecs.mapOf(TypeCodecs.TEXT, TypeCodecs.TEXT);
    private static final TypeCodec<Map<String, ByteBuffer>> MAP_OF_TEXT_TO_BLOB =
        TypeCodecs.mapOf(TypeCodecs.TEXT, TypeCodecs.BLOB);
    private static final TypeCodec<Map<UUID, ByteBuffer>> MAP_OF_UUID_TO_BLOB =
        TypeCodecs.mapOf(TypeCodecs.UUID, TypeCodecs.BLOB);

    @VisibleForTesting
    static final TypeCodec<List<String>> LIST_OF_TEXT = TypeCodecs.listOf(TypeCodecs.TEXT);

    private static final TypeCodec<Set<String>> SET_OF_TEXT = TypeCodecs.setOf(TypeCodecs.TEXT);

    private final Map<String, ColumnSpec> columnSpecs;
    private final List<ByteBuffer> data;
    private final ProtocolVersion protocolVersion;

    private Row(
        Map<String, ColumnSpec> columnSpecs,
        List<ByteBuffer> data,
        ProtocolVersion protocolVersion) {
      this.columnSpecs = columnSpecs;
      this.data = data;
      this.protocolVersion = protocolVersion;
    }

    public Boolean getBoolean(String columnName) {
      return get(columnName, TypeCodecs.BOOLEAN);
    }

    public Integer getInteger(String columnName) {
      return get(columnName, TypeCodecs.INT);
    }

    public Double getDouble(String columnName) {
      return get(columnName, TypeCodecs.DOUBLE);
    }

    public String getString(String columnName) {
      return get(columnName, TypeCodecs.TEXT);
    }

    public UUID getUuid(String columnName) {
      return get(columnName, TypeCodecs.UUID);
    }

    public ByteBuffer getByteBuffer(String columnName) {
      return get(columnName, TypeCodecs.BLOB);
    }

    public InetAddress getInetAddress(String columnName) {
      return get(columnName, TypeCodecs.INET);
    }

    public List<String> getListOfString(String columnName) {
      return get(columnName, LIST_OF_TEXT);
    }

    public Set<String> getSetOfString(String columnName) {
      return get(columnName, SET_OF_TEXT);
    }

    public Map<String, String> getMapOfStringToString(String columnName) {
      return get(columnName, MAP_OF_TEXT_TO_TEXT);
    }

    public Map<String, ByteBuffer> getMapOfStringToByteBuffer(String columnName) {
      return get(columnName, MAP_OF_TEXT_TO_BLOB);
    }

    public Map<UUID, ByteBuffer> getMapOfUuidToByteBuffer(String columnName) {
      return get(columnName, MAP_OF_UUID_TO_BLOB);
    }

    private <T> T get(String columnName, TypeCodec<T> codec) {
      // Minimal checks here: this is for internal use, so the caller should know what they're
      // doing
      if (!columnSpecs.containsKey(columnName)) {
        return null;
      } else {
        int index = columnSpecs.get(columnName).index;
        return codec.decode(data.get(index), protocolVersion);
      }
    }
  }
}
