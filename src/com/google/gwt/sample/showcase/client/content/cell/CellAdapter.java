package com.google.gwt.sample.showcase.client.content.cell;

import java.util.HashSet;
import java.util.Set;

import javax.annotation.Nullable;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.cell.client.Cell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;

/**
 * An implementation of {@link Cell} that delegates all calls to a cell of a different type.
 *
 * @param <F> the value type of this cell
 * @param <T> the value type of the delegate cell
 */
public class CellAdapter<F, T> extends AbstractCell<F> {

  private final Function<? super F, ? extends T> transform;
  private final Cell<T> cell;

  /**
   * Constructor for {@link CellAdapter}.
   * @param cell the delegate cell
   * @param transform a function that translates values from this cell's type to the delegate's type
   */
  @SuppressWarnings("unchecked")
  public CellAdapter(
      Cell<T> cell, 
      @Nullable Function<? super F, ? extends T> transform,
      @Nullable Set<String> events) {
    super(combine(cell.getConsumedEvents(), events));
    this.transform = 
        (Function<? super F, ? extends T>) 
            (transform != null ? transform : Functions.<T>identity());
    this.cell = cell;
  }

  @Override 
  public boolean dependsOnSelection() {
    return cell.dependsOnSelection();
  }

  @Override 
  public boolean handlesSelection() {
    return cell.handlesSelection();
  }

  @Override 
  public boolean isEditing(Context context, Element parent, F value) {
    return cell.isEditing(context, parent, transform.apply(value));
  }

  /**
   * Handle a browser event that took place within the cell. 
   * See {@link Cell#onBrowserEvent}.
   */
  @Override 
  public void onBrowserEvent(
      Context context, 
      Element parent, 
      final F value, 
      NativeEvent event, 
      final ValueUpdater<F> valueUpdater) {
    cell.onBrowserEvent(
        context, parent, transform.apply(value), event, new ValueUpdater<T>() {
      public void update(T ignored) {
        if (valueUpdater != null) {
          valueUpdater.update(value);
        }
      }
    });
  }

  @Override 
  public void render(Context context, F value, SafeHtmlBuilder sb) {
    cell.render(context, transform.apply(value), sb);
  }

  @Override 
  public boolean resetFocus(Context context, Element parent, F value) {
    return cell.resetFocus(context, parent, transform.apply(value));
  }

  @Override 
  public void setValue(Context context, Element parent, F value) {
    cell.setValue(context, parent, transform.apply(value));
  }

  static Set<String> combine(
      @Nullable Set<String> set1, @Nullable Set<String> set2) {
    Set<String> result = new HashSet<>();
    if (set1 != null) {
      result.addAll(set1);
    }
    if (set2 != null) {
      result.addAll(set2);
    }
    return result;
  }
}
