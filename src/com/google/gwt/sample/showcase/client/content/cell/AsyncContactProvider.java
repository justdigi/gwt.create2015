package com.google.gwt.sample.showcase.client.content.cell;

import java.util.ArrayList;

import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.FontWeight;
import com.google.gwt.dom.client.Style.TextAlign;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.sample.showcase.client.content.cell.ContactDatabase.ContactInfo;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.AbstractDataProvider;
import com.google.gwt.view.client.HasData;

public class AsyncContactProvider extends AbstractDataProvider<ContactInfo> {

  private final ArrayList<ContactInfo> contacts = new ArrayList<ContactInfo>();
  private final Widget loadingStatus = new Label("Loading...");

  public AsyncContactProvider() {
    installLoadingIndicator();
  }

  private void installLoadingIndicator() {
    Style style = loadingStatus.getElement().getStyle();
    style.setFontSize(2, Unit.EM);
    style.setFontWeight(FontWeight.BOLD);
    style.setBackgroundColor("yellow");
    style.setTextAlign(TextAlign.CENTER);
    RootLayoutPanel.get().add(loadingStatus);
    RootLayoutPanel.get().setWidgetTopHeight(loadingStatus, 0, Unit.PX, 3, Unit.EM);
  }

  @Override
  protected void onRangeChanged(final HasData<ContactInfo> display) {
    Timer timer = new Timer() {
      @Override
      public void run() {
        int size = contacts.size();
        if (size > 0) {
          // Do not push data if the data set is empty.
          updateRowData(display, 0, contacts);
        }
        loadingStatus.setVisible(false);
      }
    };
    loadingStatus.setVisible(true);
    timer.schedule(1000);
  }

  public void refresh() {
    updateRowData(0, contacts);
  }

  public void add(final ContactInfo contact) {
    Timer timer = new Timer() {
      @Override
      public void run() {
        contacts.add(contact);
        int start = contacts.size() - 1;
        updateRowData(start, contacts.subList(start, start + 1));
        updateRowCount(contacts.size(), true);
        loadingStatus.setVisible(false);
      }
    };
    loadingStatus.setVisible(true);
    timer.schedule(1000);
  }

  public void remove(ContactInfo contact) {
    final int index = contacts.indexOf(contact);
    if (index == -1) {
      return;
    }
    Timer timer = new Timer() {
      @Override
      public void run() {        
        contacts.remove(index);        
        updateRowData(index, contacts.subList(index, contacts.size()));
        updateRowCount(contacts.size(), true);
        loadingStatus.setVisible(false);
      }
    };
    loadingStatus.setVisible(true);
    timer.schedule(1000);
  }
}
