package com.google.gwt.sample.showcase.client.content.cell;

import java.util.Set;

import javax.annotation.Nullable;

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
 *
 * @author johnjo@google.com (John Jones)
 */
public class TranslatingCell<F, T> implements Cell<F> {

  interface Function<F, T> {
    @Nullable T apply(@Nullable F input);
  }
  
  private final Function<? super F, ? extends T> translate;
  private final Cell<T> delegate;

  /**
   * Constructor for {@link TranslatingCell}.
   *
   * @param translate a function that translates values from this cell's type to the delegate's type
   * @param delegate the delegate cell
   */
  public TranslatingCell(Function<? super F, ? extends T> translate, Cell<T> delegate) {
    this.translate = translate;
    this.delegate = delegate;
  }

  @Override public boolean dependsOnSelection() {
    return delegate.dependsOnSelection();
  }

  @Override public Set<String> getConsumedEvents() {
    return delegate.getConsumedEvents();
  }

  @Override public boolean handlesSelection() {
    return delegate.handlesSelection();
  }

  @Override public boolean isEditing(Context context, Element parent, F value) {
    return delegate.isEditing(context, parent, translate.apply(value));
  }

  /**
   * Handle a browser event that took place within the cell.  See {@link Cell#onBrowserEvent}.
   *
   * <p>This implementation always passes a {@code null} {@link ValueUpdater} to the delegate
   * because this method's {@code valueUpdater} parameter is not compatible with the delegate.
   * If we ever need a value updater here, an alternate constructor can be added to this class
   * to accept a helper that announces an update of type {@code T} when the delegate announces
   * an update of type {@code D}.
   */
  @Override public void onBrowserEvent(
      Context context, Element parent, F value, NativeEvent event, ValueUpdater<F> valueUpdater) {
    delegate.onBrowserEvent(context, parent, translate.apply(value), event, null);
  }

  @Override public void render(Context context, F value, SafeHtmlBuilder sb) {
    delegate.render(context, translate.apply(value), sb);
  }

  @Override public boolean resetFocus(Context context, Element parent, F value) {
    return delegate.resetFocus(context, parent, translate.apply(value));
  }

  @Override public void setValue(Context context, Element parent, F value) {
    delegate.setValue(context, parent, translate.apply(value));
  }
}
