package com.github.princesslana.eriscasper.rx;

import io.reactivex.Maybe;
import java.util.Optional;

public class Maybes {
  private Maybes() {}

  public static <T> Maybe<T> fromNullable(T v) {
    return v == null ? Maybe.empty() : Maybe.just(v);
  }

  public static <T> Maybe<T> fromOptional(Optional<T> op) {
    return op.map(Maybe::just).orElse(Maybe.empty());
  }
}
