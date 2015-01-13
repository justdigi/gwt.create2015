package com.google.gwt.sample.showcase.client.content.cell;

import java.util.ArrayList;
import java.util.Collections;
import java.util.logging.Logger;

import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.FontWeight;
import com.google.gwt.dom.client.Style.TextAlign;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.sample.showcase.client.content.cell.ContactDatabase.ContactInfo;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.view.client.AbstractDataProvider;
import com.google.gwt.view.client.HasData;

public class AsyncContactProvider extends AbstractDataProvider<ContactInfo> {

  private static final Logger logger = 
      Logger.getLogger(AsyncContactProvider.class.getName());

  private final ArrayList<ContactInfo> contacts = new ArrayList<ContactInfo>();
  private final Label loadingStatus = new Label("Loading...");
  private int requestCount;

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
    requestCount++;
    Timer timer = new Timer() {
      int rc = requestCount;
      @Override
      public void run() {
        int size = contacts.size();
        if (size > 0) {
          // Do not push data if the data set is empty.
          updateRowData(display, 0, contacts);
        }
        loadingStatus.setVisible(false);
        logger.info("...RPC " + rc);
      }
    };
    // Simulate the delay incurred by a remote procedure call.
    loadingStatus.setVisible(true);
    loadingStatus.setText("RPC " + requestCount + "...");
    logger.info("RPC " + requestCount + "...");
    timer.schedule(requestCount == 1 ? 4000 : 2000);
  }

  public void refresh() {
    updateRowData(0, contacts);
  }

  public void reset() {
    requestCount = 0;
    updateRowData(0, Collections.<ContactInfo> emptyList());
  }
  
  public void add(final ContactInfo contact) {
    contacts.add(contact);
    int start = contacts.size() - 1;
    updateRowData(start, contacts.subList(start, start + 1));
    updateRowCount(contacts.size(), true);
  }

  public void remove(ContactInfo contact) {
    int index = contacts.indexOf(contact);
    if (index == -1) {
      return;
    }
    contacts.remove(index);        
    updateRowData(index, contacts.subList(index, contacts.size()));
    updateRowCount(contacts.size(), true);
  }
}
