package com.google.gwt.sample.showcase.client.content.cell;

public class Functions {

  private Functions() {}
  
  static <T> Function<T, T> identity() {
    return new Function<T, T>() {
      public T apply(T input) {
        return input;
      }};
  }
}
