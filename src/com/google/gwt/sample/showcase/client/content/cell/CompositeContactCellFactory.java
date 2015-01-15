package com.google.gwt.sample.showcase.client.content.cell;

import java.util.ArrayList;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.cell.client.Cell;
import com.google.gwt.cell.client.CompositeCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.HasCell;
import com.google.gwt.cell.client.ImageResourceCell;
import com.google.gwt.cell.client.SafeHtmlCell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.dom.client.BrowserEvents;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.EventTarget;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.sample.showcase.client.content.cell.ContactDatabase.ContactInfo;
import com.google.gwt.user.client.ui.ImageResourceRenderer;

class CompositeContactCellFactory {

  static Cell<ContactInfo> create(
      final CwCellList.Images images) {
    ArrayList<HasCell<ContactInfo, ?>> hasCells = 
        new ArrayList<HasCell<ContactInfo, ?>>();
    hasCells.add(new HasCell<ContactInfo, ImageResource>() {
      @Override
      public Cell<ImageResource> getCell() {
        return new ImageResourceCell();
      }

      @Override
      public FieldUpdater<ContactInfo, ImageResource> getFieldUpdater() {
        return null;
      }

      @Override
      public ImageResource getValue(ContactInfo contact) {
        return images.contact();
      }});
    hasCells.add(new HasCell<ContactInfo, SafeHtml>() {
      @Override
      public Cell<SafeHtml> getCell() {
        return new SafeHtmlCell();
      }

      @Override
      public FieldUpdater<ContactInfo, SafeHtml> getFieldUpdater() {
        return null;
      }

      @Override
      public SafeHtml getValue(ContactInfo contact) {
        return new SafeHtmlBuilder()
            .appendEscaped(contact.getFullName())
            .appendHtmlConstant("<br>")
            .appendEscaped(contact.getAddress())
            .toSafeHtml();
      }});
    hasCells.add(new HasCell<ContactInfo, Boolean>() {
      @Override
      public Cell<Boolean> getCell() {
        return new AbstractCell<Boolean>(BrowserEvents.CLICK) {

          private ImageResourceRenderer renderer =
              new ImageResourceRenderer();

          @Override
          public void render(Cell.Context context, Boolean value,
              SafeHtmlBuilder sb) {
            if (value != null) {
              sb.append(renderer.render(
                  value ? images.star() : images.starOutline()));
            }            
          }
          
          @Override
          public void onBrowserEvent(Cell.Context context,
              Element parent, Boolean value, NativeEvent event,
              ValueUpdater<Boolean> valueUpdater) {
            // Let AbstractCell handle the keydown event.
            super.onBrowserEvent(context, parent, value, event, valueUpdater);

            // Handle the click event.
            if ("click".equals(event.getType())) {
              // Ignore clicks that occur outside of the outermost element.
              EventTarget eventTarget = event.getEventTarget();
              if (parent.getFirstChildElement().isOrHasChild(Element.as(eventTarget))) {
                valueUpdater.update(!value);
                SafeHtmlBuilder sb = new SafeHtmlBuilder();
                render(context, !value, sb);
                parent.setInnerSafeHtml(sb.toSafeHtml());
              }
            }
          }
        };
      }

      @Override
      public FieldUpdater<ContactInfo, Boolean> getFieldUpdater() {
        return new FieldUpdater<ContactInfo, Boolean>() {

          @Override
          public void update(int index, ContactInfo contact, Boolean value) {
            contact.setStarred(value);
          }
        };
      }

      @Override
      public Boolean getValue(ContactInfo contact) {
        return contact.isStarred();
      }});
    return new CompositeCell<ContactInfo>(hasCells);
  }
  
//  public static <T> Cell<T> makeClickable(Cell<T> cell, final Receiver<T> clickReceiver) {
//    Set<String> events = new HashSet<>();
//    events.add(BrowserEvents.CLICK);
//    events.add(BrowserEvents.KEYDOWN);
//    return new ForwardingCell<T>(cell, events) {
//      @Override
//      public void onBrowserEvent(Context context, Element parent, T value, NativeEvent event,
//          ValueUpdater<T> valueUpdater) {
//        super.onBrowserEvent(context, parent, value, event, valueUpdater);
//        if (event.getType().equals(BrowserEvents.CLICK)
//            || (event.getType().equals(BrowserEvents.KEYDOWN)
//                && event.getKeyCode() == KeyCodes.KEY_ENTER)) {
//          clickReceiver.accept(value);
//        }
//      }
//    };
//  }
//  
//  interface Receiver<T> {
//    void accept(T value);
//  }
//  
//  public static abstract class ForwardingCell<T> extends AbstractCell<T> {
//
//    private final Cell<T> cell;
//
//    /**
//     * Build an instance that wraps the given {@code cell}. The consumed events
//     * of the result cell will include the consumed events of {@code cell} plus
//     * any additional events specified by the optional {@code events} parameter.
//     *
//     * @param cell the cell to be wrapped
//     * @param events optional set of events to be consumed
//     */
//    protected ForwardingCell(Cell<T> cell, @Nullable Set<String> events) {
//      super(combine(cell.getConsumedEvents(), events));
//      this.cell = cell;
//    }
//
//    /**
//     * Build an instance that wraps the given {@code cell}. The consumed events
//     * of the result cell will be the same as the consumed events of {@code cell}.
//     *
//     * @param cell the cell to be wrapped
//     */
//    protected ForwardingCell(Cell<T> cell) {
//      this(cell, null);
//    }
//
//    @Override
//    public void render(Cell.Context context, T data, SafeHtmlBuilder safeHtmlBuilder) {
//      cell.render(context, data, safeHtmlBuilder);
//    }
//
//    @Override
//    public boolean dependsOnSelection() {
//      return cell.dependsOnSelection();
//    }
//
//    @Override
//    public boolean handlesSelection() {
//      return cell.handlesSelection();
//    }
//
//    @Override
//    public boolean isEditing(Context context, Element parent, T value) {
//      return cell.isEditing(context, parent, value);
//    }
//
//    @Override
//    public void onBrowserEvent(
//        Context context, Element parent, T value, NativeEvent event, ValueUpdater<T> valueUpdater) {
//      cell.onBrowserEvent(context, parent, value, event, valueUpdater);
//    }
//
//    @Override
//    public boolean resetFocus(Context context, Element parent, T value) {
//      return cell.resetFocus(context, parent, value);
//    }
//
//    @Override
//    public void setValue(Context context, Element parent, T value) {
//      cell.setValue(context, parent, value);
//    }
//
//    @Override
//    public String toString() {
//      return cell.toString();
//    }
//
//    @VisibleForTesting
//    static Set<String> combine(@Nullable Set<String> set1, @Nullable Set<String> set2) {
//      Set<String> result = new HashSet<>();
//      if (set1 != null) {
//        result.addAll(set1);
//      }
//      if (set2 != null) {
//        result.addAll(set2);
//      }
//      return result;
//    }
//  }
}
