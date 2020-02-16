package com.squareup.moshi.adapters;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.JsonDataException;
import com.squareup.moshi.JsonReader;
import com.squareup.moshi.Moshi;
import java.io.IOException;
import java.util.Collections;
import java.util.Map;
import okio.Buffer;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;

@SuppressWarnings("CheckReturnValue")
public final class RuntimeJsonAdapterFactoryTest {
  @Test public void fromJson() throws IOException {
    Moshi moshi = new Moshi.Builder()
        .add(RuntimeJsonAdapterFactory.of(Message.class, "type")
            .registerSubtype(Success.class, "success")
            .registerSubtype(Error.class, "error"))
        .build();
    JsonAdapter<Message> adapter = moshi.adapter(Message.class);

    assertThat(adapter.fromJson("{\"type\":\"success\",\"value\":\"Okay!\"}"))
        .isEqualTo(new Success("Okay!"));
    assertThat(adapter.fromJson("{\"type\":\"error\",\"error_logs\":{\"order\":66}}"))
        .isEqualTo(new Error(Collections.<String, Object>singletonMap("order", 66d)));
  }

  @Test public void toJson() {
    Moshi moshi = new Moshi.Builder()
        .add(RuntimeJsonAdapterFactory.of(Message.class, "type")
            .registerSubtype(Success.class, "success")
            .registerSubtype(Error.class, "error"))
        .build();
    JsonAdapter<Message> adapter = moshi.adapter(Message.class);

    assertThat(adapter.toJson(new Success("Okay!")))
        .isEqualTo("{\"value\":\"Okay!\"}");
    assertThat(adapter.toJson(new Error(Collections.<String, Object>singletonMap("order", 66))))
        .isEqualTo("{\"error_logs\":{\"order\":66}}");
  }

  @Test public void unregisteredLabelValue() throws IOException {
    Moshi moshi = new Moshi.Builder()
        .add(RuntimeJsonAdapterFactory.of(Message.class, "type")
            .registerSubtype(Success.class, "success")
            .registerSubtype(Error.class, "error"))
        .build();
    JsonAdapter<Message> adapter = moshi.adapter(Message.class);

    JsonReader reader =
        JsonReader.of(new Buffer().writeUtf8("{\"type\":\"data\",\"value\":\"Okay!\"}"));
    try {
      adapter.fromJson(reader);
      fail();
    } catch (JsonDataException expected) {
      assertThat(expected).hasMessage("Expected one of [success, error] for key 'type' but found"
          + " 'data'. Register a subtype for this label.");
    }
    assertThat(reader.peek()).isEqualTo(JsonReader.Token.END_DOCUMENT);
  }

  @Test public void unregisteredSubtype() {
    Moshi moshi = new Moshi.Builder()
        .add(RuntimeJsonAdapterFactory.of(Message.class, "type")
            .registerSubtype(Success.class, "success")
            .registerSubtype(Error.class, "error"))
        .build();
    JsonAdapter<Message> adapter = moshi.adapter(Message.class);

    try {
      adapter.toJson(new EmptyMessage());
    } catch (IllegalArgumentException expected) {
      assertThat(expected).hasMessage("Expected one of [class"
          + " com.squareup.moshi.adapters.RuntimeJsonAdapterFactoryTest$Success, class"
          + " com.squareup.moshi.adapters.RuntimeJsonAdapterFactoryTest$Error] but found"
          + " com.squareup.moshi.adapters.RuntimeJsonAdapterFactoryTest$EmptyMessage@65e579dc, a"
          + " class com.squareup.moshi.adapters.RuntimeJsonAdapterFactoryTest$EmptyMessage."
          + " Register this subtype.");
    }
  }

  @Test public void nonStringLabelValue() throws IOException {
    Moshi moshi = new Moshi.Builder()
        .add(RuntimeJsonAdapterFactory.of(Message.class, "type")
            .registerSubtype(Success.class, "success")
            .registerSubtype(Error.class, "error"))
        .build();
    JsonAdapter<Message> adapter = moshi.adapter(Message.class);

    try {
      adapter.fromJson("{\"type\":{},\"value\":\"Okay!\"}");
      fail();
    } catch (JsonDataException expected) {
      assertThat(expected).hasMessage("Label for 'type' must be a string but was {},"
          + " a class com.squareup.moshi.LinkedHashTreeMap");
    }
  }

  @Test public void nonObjectDoesNotConsume() throws IOException {
    Moshi moshi = new Moshi.Builder()
        .add(RuntimeJsonAdapterFactory.of(Message.class, "type")
            .registerSubtype(Success.class, "success")
            .registerSubtype(Error.class, "error"))
        .build();
    JsonAdapter<Message> adapter = moshi.adapter(Message.class);

    JsonReader reader = JsonReader.of(new Buffer().writeUtf8("\"Failure\""));
    try {
      adapter.fromJson(reader);
      fail();
    } catch (JsonDataException expected) {
      assertThat(expected).hasMessage("Expected BEGIN_OBJECT but was STRING at path $");
    }
    assertThat(reader.nextString()).isEqualTo("Failure");
    assertThat(reader.peek()).isEqualTo(JsonReader.Token.END_DOCUMENT);
  }

  @Test public void uniqueSubtypes() {
    RuntimeJsonAdapterFactory<Message> factory =
        RuntimeJsonAdapterFactory.of(Message.class, "type")
            .registerSubtype(Success.class, "success");
    try {
      factory.registerSubtype(Success.class, "data");
      fail();
    } catch (IllegalArgumentException expected) {
      assertThat(expected).hasMessage("Subtypes and labels must be unique.");
    }
  }

  @Test public void uniqueLabels() {
    RuntimeJsonAdapterFactory<Message> factory =
        RuntimeJsonAdapterFactory.of(Message.class, "type")
            .registerSubtype(Success.class, "data");
    try {
      factory.registerSubtype(Error.class, "data");
      fail();
    } catch (IllegalArgumentException expected) {
      assertThat(expected).hasMessage("Subtypes and labels must be unique.");
    }
  }

  interface Message {
  }

  static final class Success implements Message {
    final String value;

    Success(String value) {
      this.value = value;
    }

    @Override public boolean equals(Object o) {
      if (this == o) return true;
      if (!(o instanceof Success)) return false;
      Success success = (Success) o;
      return value.equals(success.value);
    }

    @Override public int hashCode() {
      return value.hashCode();
    }
  }

  static final class Error implements Message {
    final Map<String, Object> error_logs;

    Error(Map<String, Object> error_logs) {
      this.error_logs = error_logs;
    }

    @Override public boolean equals(Object o) {
      if (this == o) return true;
      if (!(o instanceof Error)) return false;
      Error error = (Error) o;
      return error_logs.equals(error.error_logs);
    }

    @Override public int hashCode() {
      return error_logs.hashCode();
    }
  }

  static final class EmptyMessage implements Message {
  }
}
