package com.google.gwt.sample.showcase.client.content.cell;

import com.google.gwt.cell.client.Cell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.HasCell;

public class HasCells {

  private HasCells() {}
  
  public static <F, T> HasCell<F, T> forCellWithConstantValue(
      final Cell<T> cell, final T value) {
    return new HasCell<F, T>() {
      @Override
      public Cell<T> getCell() {
        return cell;
      }

      @Override
      public FieldUpdater<F, T> getFieldUpdater() {
        return null;
      }

      @Override
      public T getValue(F ignored) {
        return value;
      }
    };
  }
  
  public static <F, T> HasCell<F, T> forCellAndFunction(
      final Cell<T> cell, final Function<F, T> fn) {
    return new HasCell<F, T>() {
      @Override
      public Cell<T> getCell() {
        return cell;
      }

      @Override
      public FieldUpdater<F, T> getFieldUpdater() {
        return null;
      }

      @Override
      public T getValue(F input) {
        return fn.apply(input);
      }
    };
  }

}
