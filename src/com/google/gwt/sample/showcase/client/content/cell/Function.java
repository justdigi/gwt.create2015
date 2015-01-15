package com.google.gwt.sample.showcase.client.content.cell;

import javax.annotation.Nullable;

interface Function<F, T> {
  @Nullable T apply(@Nullable F input);  
}