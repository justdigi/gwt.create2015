package com.google.gwt.sample.showcase.client.content.cell;

import java.util.HashSet;
import java.util.Set;

import com.google.gwt.cell.client.Cell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.dom.client.BrowserEvents;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.user.client.ui.HasScrolling;

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
          Context context,
          Element parent, 
          T value, 
          NativeEvent event,
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
  
  /**
   * Wrap the given cell so that if
   * {@link Cell#resetFocus(com.google.gwt.cell.client.Cell.Context, Element, Object)} is
   * called on it, the wrapped cell will focus itself, but will also make sure that the
   * browser window doesn't automatically scroll the cell into view. This is handy for
   * cells that should respond to keyboard commands even when out of view.
   *
   * <p>Note that refocusing on top-level cells will happen automatically when a cell widget
   * is redrawn. One surprising cause of a redraws is that HasDataPresenter optimizes extensions
   * to a short list by just redoing the whole list. See
   * {@link com.google.gwt.user.cellview.client.HasDataPresenter#REDRAW_THRESHOLD} for what
   * can trigger redraws.
   */
  public static <T> Cell<T> makeFocusableWithoutScrolling(
      Cell<T> cellToWrap, final HasScrolling scrollable) {
    return new CellAdapter<T, T>(cellToWrap, null, null) {
      @Override 
      public boolean resetFocus(Context context, Element parent, T value) {
        int x = scrollable.getHorizontalScrollPosition();
        int y = scrollable.getVerticalScrollPosition();
        parent.focus();
        scrollable.setHorizontalScrollPosition(x);
        scrollable.setVerticalScrollPosition(y);
        return true;
      }
    };
  }
}
