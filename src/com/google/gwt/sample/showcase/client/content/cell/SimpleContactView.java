package com.google.gwt.sample.showcase.client.content.cell;

import com.google.gwt.cell.client.Cell;
import com.google.gwt.cell.client.Cell.Context;
import com.google.gwt.dom.client.Document;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.sample.showcase.client.content.cell.ContactDatabase.ContactInfo;
import com.google.gwt.user.client.ui.Widget;

/**
 * A simple widget that uses a Cell to render its content from a ContactInfo.
 */
class SimpleContactView extends Widget {

  private final Cell<ContactInfo> cell;

  SimpleContactView(Cell<ContactInfo> cell) {
    this.cell = cell;
    
    setElement(Document.get().createDivElement());
  }
  
  void setContact(ContactInfo contact) {
    Context cellContext = new Context(0, 0, contact); // contact is its own key

    SafeHtmlBuilder htmlBuilder = new SafeHtmlBuilder();
    cell.render(cellContext, contact, htmlBuilder);

    getElement().setInnerSafeHtml(htmlBuilder.toSafeHtml());
  }
}
