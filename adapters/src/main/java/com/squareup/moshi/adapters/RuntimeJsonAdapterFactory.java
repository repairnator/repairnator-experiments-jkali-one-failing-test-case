package com.squareup.moshi.adapters;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.JsonDataException;
import com.squareup.moshi.JsonReader;
import com.squareup.moshi.JsonWriter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import javax.annotation.CheckReturnValue;

/**
 * A JsonAdapter factory for polymorphic types. This is useful when the type is not known before
 * deserializing the JSON. This factory's adapters expect JSON in the format of a JSON object with a
 * key whose value is a label that determines the type to which to map the JSON object.
 */
public final class RuntimeJsonAdapterFactory<T> implements JsonAdapter.Factory {
  final Class<T> baseType;
  final String labelKey;
  final Map<String, Type> labelToType = new LinkedHashMap<>();

  /**
   * @param baseType The base type for which this factory will create adapters.
   * @param labelKey The key in the JSON object whose value determines the type to which to map the
   * JSON object.
   */
  @CheckReturnValue
  public static <T> RuntimeJsonAdapterFactory<T> of(Class<T> baseType, String labelKey) {
    if (baseType == null) throw new NullPointerException("baseType == null");
    if (labelKey == null) throw new NullPointerException("labelKey == null");
    return new RuntimeJsonAdapterFactory<>(baseType, labelKey);
  }

  RuntimeJsonAdapterFactory(Class<T> baseType, String labelKey) {
    this.baseType = baseType;
    this.labelKey = labelKey;
  }

  /**
   * Register the subtype that can be created based on the label. When deserializing, if a label
   * that was not registered is found, a JsonDataException will be thrown. When serializing, if a
   * type that was not registered is used, an IllegalArgumentException will be thrown.
   */
  public RuntimeJsonAdapterFactory<T> registerSubtype(Class<? extends T> subtype, String label) {
    if (subtype == null) throw new NullPointerException("subtype == null");
    if (label == null) throw new NullPointerException("label == null");
    if (labelToType.containsKey(label) || labelToType.containsValue(subtype)) {
      throw new IllegalArgumentException("Subtypes and labels must be unique.");
    }
    labelToType.put(label, subtype);
    return this;
  }

  @Override
  public JsonAdapter<?> create(Type type, Set<? extends Annotation> annotations, Moshi moshi) {
    if (Types.getRawType(type) != baseType || !annotations.isEmpty()) {
      return null;
    }
    int size = labelToType.size();
    Map<Type, JsonAdapter<Object>> typeToAdapter = new LinkedHashMap<>(size);
    Map<String, JsonAdapter<Object>> labelToAdapter = new LinkedHashMap<>(size);
    for (Map.Entry<String, Type> entry : labelToType.entrySet()) {
      String label = entry.getKey();
      Type typeValue = entry.getValue();
      JsonAdapter<Object> adapter = moshi.adapter(typeValue);
      labelToAdapter.put(label, adapter);
      typeToAdapter.put(typeValue, adapter);
    }
    return new RuntimeJsonAdapter(labelKey, labelToAdapter, typeToAdapter);
  }

  static final class RuntimeJsonAdapter extends JsonAdapter<Object> {
    final String labelKey;
    final Map<String, JsonAdapter<Object>> labelToAdapter;
    final Map<Type, JsonAdapter<Object>> typeToAdapter;

    RuntimeJsonAdapter(
        String labelKey,
        Map<String, JsonAdapter<Object>> labelToAdapter,
        Map<Type, JsonAdapter<Object>> typeToAdapter) {
      this.labelKey = labelKey;
      this.labelToAdapter = labelToAdapter;
      this.typeToAdapter = typeToAdapter;
    }

    @Override public Object fromJson(JsonReader reader) throws IOException {
      JsonReader.Token peekedToken = reader.peek();
      if (peekedToken != JsonReader.Token.BEGIN_OBJECT) {
        throw new JsonDataException("Expected BEGIN_OBJECT but was " + peekedToken
            + " at path " + reader.getPath());
      }
      Object jsonValue = reader.readJsonValue();
      Map<String, Object> jsonObject = (Map<String, Object>) jsonValue;
      Object label = jsonObject.get(labelKey);
      if (label == null) {
        throw new JsonDataException("Missing label for " + labelKey);
      }
      if (!(label instanceof String)) {
        throw new JsonDataException("Label for '"
            + labelKey
            + "' must be a string but was "
            + label
            + ", a "
            + label.getClass());
      }
      JsonAdapter<Object> adapter = labelToAdapter.get(label);
      if (adapter == null) {
        throw new JsonDataException("Expected one of "
            + labelToAdapter.keySet()
            + " for key '"
            + labelKey
            + "' but found '"
            + label
            + "'. Register a subtype for this label.");
      }
      return adapter.fromJsonValue(jsonValue);
    }

    @Override public void toJson(JsonWriter writer, Object value) throws IOException {
      Class<?> type = value.getClass();
      JsonAdapter<Object> adapter = typeToAdapter.get(type);
      if (adapter == null) {
        throw new IllegalArgumentException("Expected one of "
            + typeToAdapter.keySet()
            + " but found "
            + value
            + ", a "
            + value.getClass()
            + ". Register this subtype.");
      }
      adapter.toJson(writer, value);
    }
  }
}
