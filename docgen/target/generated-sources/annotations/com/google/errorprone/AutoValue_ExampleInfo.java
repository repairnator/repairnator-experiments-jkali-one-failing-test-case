
package com.google.errorprone;

import javax.annotation.processing.Generated;

@Generated("com.google.auto.value.processor.AutoValueProcessor")
 final class AutoValue_ExampleInfo extends ExampleInfo {

  private final ExampleInfo.ExampleKind type;
  private final String checkerClass;
  private final String name;
  private final String code;

  AutoValue_ExampleInfo(
      ExampleInfo.ExampleKind type,
      String checkerClass,
      String name,
      String code) {
    if (type == null) {
      throw new NullPointerException("Null type");
    }
    this.type = type;
    if (checkerClass == null) {
      throw new NullPointerException("Null checkerClass");
    }
    this.checkerClass = checkerClass;
    if (name == null) {
      throw new NullPointerException("Null name");
    }
    this.name = name;
    if (code == null) {
      throw new NullPointerException("Null code");
    }
    this.code = code;
  }

  @Override
  public ExampleInfo.ExampleKind type() {
    return type;
  }

  @Override
  public String checkerClass() {
    return checkerClass;
  }

  @Override
  public String name() {
    return name;
  }

  @Override
  public String code() {
    return code;
  }

  @Override
  public String toString() {
    return "ExampleInfo{"
         + "type=" + type + ", "
         + "checkerClass=" + checkerClass + ", "
         + "name=" + name + ", "
         + "code=" + code
        + "}";
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }
    if (o instanceof ExampleInfo) {
      ExampleInfo that = (ExampleInfo) o;
      return (this.type.equals(that.type()))
           && (this.checkerClass.equals(that.checkerClass()))
           && (this.name.equals(that.name()))
           && (this.code.equals(that.code()));
    }
    return false;
  }

  @Override
  public int hashCode() {
    int h = 1;
    h *= 1000003;
    h ^= this.type.hashCode();
    h *= 1000003;
    h ^= this.checkerClass.hashCode();
    h *= 1000003;
    h ^= this.name.hashCode();
    h *= 1000003;
    h ^= this.code.hashCode();
    return h;
  }

}
