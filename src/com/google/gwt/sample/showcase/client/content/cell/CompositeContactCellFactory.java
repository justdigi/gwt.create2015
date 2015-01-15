package com.google.gwt.sample.showcase.client.content.cell;

import java.util.Arrays;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.cell.client.Cell;
import com.google.gwt.cell.client.CompositeCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.HasCell;
import com.google.gwt.cell.client.ImageResourceCell;
import com.google.gwt.cell.client.SafeHtmlCell;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.dom.client.BrowserEvents;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.EventTarget;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.sample.showcase.client.content.cell.ContactDatabase.ContactInfo;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.ImageResourceRenderer;

class CompositeContactCellFactory {

  static Cell<ContactInfo> create(
      final CwCellList.Images images) {
    return new CompositeCell<ContactInfo>(Arrays.asList(
        createContactIcon(images), 
        createNameAndAddress(),
        createMailTo(),
        createStar(images)));
  }

  private static HasCell<ContactInfo, ImageResource> createContactIcon(
      final CwCellList.Images images) {
    return HasCells.forCellWithConstantValue(
        new ImageResourceCell(), images.contact());
  }

  private static HasCell<ContactInfo, SafeHtml> createNameAndAddress() {
    return HasCells.forAdaptedCell(
        new SafeHtmlCell(), 
        new Function<ContactInfo, SafeHtml>() {
          public SafeHtml apply(ContactInfo contact) {
            return new SafeHtmlBuilder()
                .appendEscaped(contact.getFullName())
                .appendHtmlConstant("<br>")
                .appendEscaped(contact.getAddress())
                .toSafeHtml();
          }
        });
  }

  private static HasCell<ContactInfo, Boolean> createStar(
      final CwCellList.Images images) {
    return new HasCell<ContactInfo, Boolean>() {
      @Override
      public Cell<Boolean> getCell() {
        return new AbstractCell<Boolean>(BrowserEvents.CLICK) {

          private ImageResourceRenderer renderer = new ImageResourceRenderer();

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
            if (BrowserEvents.CLICK.equals(event.getType())) {
              // Ignore clicks that occur outside of the outermost element.
              EventTarget eventTarget = event.getEventTarget();
              if (parent.getFirstChildElement().isOrHasChild(Element.as(eventTarget))) {
                boolean newValue = !value;
                valueUpdater.update(newValue);
                SafeHtmlBuilder sb = new SafeHtmlBuilder();
                render(context, newValue, sb);
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
      }
    };
  }
  
  private static HasCell<ContactInfo, ContactInfo> createMailTo() {
    Cell<ContactInfo> mailToIcon = 
        Cells.adaptWithConstantValue(new TextCell(), "@");
    return HasCells.forCell(Cells.makeClickable(
        mailToIcon,
        new Cells.Receiver<ContactInfo>() {
          public void accept(ContactInfo contact) {
            Window.open(
                "https://mail.google.com/mail/u/0/" 
                    + "?view=cm&fs=1&tf=1&source=mailto&to=" 
                    + contact.getFirstName() + "." + contact.getLastName() 
                    + "@gmail.com",
                "_blank",
                null);
          }
        }));
  }
}
