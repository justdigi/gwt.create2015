package com.google.gwt.sample.showcase.client.content.cell;

/**
 * Inspired by the Guava API.
 */
interface Receiver<T> {
  void accept(T value);
}