package com.google.gwt.sample.showcase.client.content.cell;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.BrowserEvents;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.HasKeyDownHandlers;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.sample.showcase.client.Settings;
import com.google.gwt.sample.showcase.client.content.cell.ContactDatabase.ContactInfo;
import com.google.gwt.user.cellview.client.AbstractHasData.DefaultKeyboardSelectionHandler;
import com.google.gwt.user.cellview.client.CellList;
import com.google.gwt.user.cellview.client.LoadingStateChangeEvent;
import com.google.gwt.user.cellview.client.LoadingStateChangeEvent.LoadingState;
import com.google.gwt.view.client.CellPreviewEvent;

class CustomKeyboardHandler extends DefaultKeyboardSelectionHandler<ContactInfo> {

  interface SelectableWidget extends HasKeyDownHandlers {
    void selectWidget();
  }

  private static final int PAGE_INCREMENT = 11;
  private final CellList<ContactInfo> cellList;
  private final SelectableWidget widgetAboveList;
  private boolean isEndRequestPending = false;
  
  public CustomKeyboardHandler(
      CellList<ContactInfo> cellList, SelectableWidget widgetAboveList) {
    super(cellList);
    this.cellList = cellList;
    this.widgetAboveList = widgetAboveList;
    
    cellList.addLoadingStateChangeHandler(new LoadingStateChangeEvent.Handler() {
      @Override
      public void onLoadingStateChanged(LoadingStateChangeEvent event) {
        onLoadingChange(event.getLoadingState());
      }
    });

    widgetAboveList.addKeyDownHandler(new KeyDownHandler() {
      @Override
      public void onKeyDown(KeyDownEvent event) {
        switch (event.getNativeKeyCode()) {
          case KeyCodes.KEY_DOWN:
          case KeyCodes.KEY_J:
            setCurrentRow(0);
            event.preventDefault();
            break;
        }
      }
    });
  }

  @Override
  public void onCellPreview(CellPreviewEvent<ContactInfo> event) {
    if (Settings.get().getKeyHandling()) {
      NativeEvent nativeEvent = event.getNativeEvent();

      if (nativeEvent.getType() == BrowserEvents.KEYDOWN) { // A key was pushed down
        if (nativeEvent.getShiftKey() || nativeEvent.getAltKey()
            || nativeEvent.getCtrlKey() || nativeEvent.getMetaKey()) {
          // Ignore if a modifier key is down
          return;
        }

        switch (nativeEvent.getKeyCode()) {
          case KeyCodes.KEY_DOWN:  // The down arrow key
          case KeyCodes.KEY_J:
            setCurrentRow(cellList.getKeyboardSelectedRow() + 1);
            cancelEvent(event);
            break;
          case KeyCodes.KEY_UP:  // The up arrow key
          case KeyCodes.KEY_K:
            if (cellList.getKeyboardSelectedRow() < 1) {
              goAboveList(event.getValue());
            } else {
              setCurrentRow(cellList.getKeyboardSelectedRow() - 1);
            }
            cancelEvent(event);
            break;
          case KeyCodes.KEY_PAGEDOWN:
          case KeyCodes.KEY_SPACE:
            setCurrentRow(cellList.getKeyboardSelectedRow() + PAGE_INCREMENT);
            cancelEvent(event);
            break;
          case KeyCodes.KEY_PAGEUP:
            setCurrentRow(cellList.getKeyboardSelectedRow() - PAGE_INCREMENT);
            cancelEvent(event);
            break;
          case KeyCodes.KEY_HOME:
            setCurrentRow(cellList.getPageStart());
            cancelEvent(event);
            break;
          case KeyCodes.KEY_END:
            goToVeryEnd();
            cancelEvent(event);
            break;
        }

        // Bypass the default handler (super-class) for all keydown events.
        // For keys not handled here, let the browser handle them.
        return;
      }
    }

    // Should get here only if event was not handled above.  Send the event to
    // the default handler.
    super.onCellPreview(event);
  }
  
  void setCurrentRow(int row) {
    cellList.setKeyboardSelectedRow(row);
    
    // Read the current row index back, because the cellList will have clipped
    // it to a valid row if we tried to set it to a row that doesn't exist.
    int newRow = cellList.getKeyboardSelectedRow();
    
    // Scroll the row into view?
    // If you need custom scrolling, e.g. because of fixed elements on the page.
  }

  // Re-implement DefaultKeyboardSelectionHandler.handledEvent because that's
  // package-private.
  void cancelEvent(CellPreviewEvent<ContactInfo> event) {
    event.setCanceled(true);
    event.getNativeEvent().preventDefault();
  }

  void goAboveList(ContactInfo keyboardSelectedValue) {
    cellList.getSelectionModel().setSelected(keyboardSelectedValue, false);

    // Unselecting the row (above) will steal the focus (doesn't really need to,
    // but does), and do this deferred.  Defer select here to come after that.
    Scheduler.get().scheduleDeferred(new ScheduledCommand() {
      @Override
      public void execute() {
        widgetAboveList.selectWidget();
      }
    });
  }

  // The async data loading broke the end key.  The default handler only goes to
  // the end of what's loaded.  To fix it, update the page size to request more
  // data, then wait until it's loaded before selecting the last row.
  void goToVeryEnd() {
    int totalRows = cellList.getRowCount();
    int pageStart = cellList.getPageStart();
    int pageSize = cellList.getPageSize();
    
    if (cellList.isRowCountExact() && totalRows > pageStart + pageSize) {
      isEndRequestPending = true;
      cellList.setPageSize(totalRows - pageStart);
    } else {
      // Just go to the end of what's rendered
      setCurrentRow(cellList.getVisibleItemCount());
    }
  }
  
  void onLoadingChange(LoadingState newState) {
    if (isEndRequestPending && newState == LoadingState.LOADED) {
      // Loading change events are sent just before any new rows are rendered.
      // Defer execution to let them render.
      Scheduler.get().scheduleDeferred(new ScheduledCommand() {
        @Override
        public void execute() {
          int totalRows = cellList.getRowCount();
          int pageStart = cellList.getPageStart();
          int pageSize = cellList.getPageSize();
          int renderedRows = cellList.getVisibleItemCount();
          
          if (pageStart + pageSize < totalRows) {
            // Either more rows got added, or the page size got shrunk.  Fix it.
            cellList.setPageSize(totalRows - pageStart);
          } else if (renderedRows == totalRows - pageStart ) {
            isEndRequestPending = false;
            setCurrentRow(renderedRows - 1);
          } else {
            // keep waiting
          }
        }
      });
    }
  }
}
