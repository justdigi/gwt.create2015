package com.google.gwt.sample.showcase.client.content.cell;

import java.util.HashSet;
import java.util.Set;

import com.google.gwt.cell.client.Cell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.dom.client.BrowserEvents;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.KeyCodes;

public class Cells {

  private Cells() {}
  
  public static <F, T> Cell<F> adapt(Cell<T> cell, Function<F, T> transform) {
    return new CellAdapter<F, T>(cell, transform, null);
  }   
  
  public static <F, T> Cell<F> adaptWithConstantValue(
      Cell<T> cell, final T value) {
    return new CellAdapter<F, T>(
        cell, 
        new Function<F, T>() {
          @Override
          public T apply(F input) {
            return value;
          }
        }, 
        null);
  }   
  
  public static <T> Cell<T> makeClickable(
      Cell<T> cell, final Receiver<T> clickReceiver) {
    Set<String> events = new HashSet<>();
    events.add(BrowserEvents.CLICK);
    events.add(BrowserEvents.KEYDOWN);
    return new CellAdapter<T, T>(cell, null, events) {
      @Override
      public void onBrowserEvent(
          Context context, Element parent, T value, NativeEvent event,
          ValueUpdater<T> valueUpdater) {
        super.onBrowserEvent(context, parent, value, event, valueUpdater);
        if (event.getType().equals(BrowserEvents.CLICK)
            || (event.getType().equals(BrowserEvents.KEYDOWN)
                && event.getKeyCode() == KeyCodes.KEY_ENTER)) {
          clickReceiver.accept(value);
        }
      }
    };
  }   
}
