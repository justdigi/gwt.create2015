package com.google.gwt.sample.showcase.client.content.cell;

import com.google.gwt.dom.client.BrowserEvents;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.sample.showcase.client.content.cell.ContactDatabase.ContactInfo;
import com.google.gwt.user.cellview.client.AbstractHasData.DefaultKeyboardSelectionHandler;
import com.google.gwt.user.cellview.client.CellList;
import com.google.gwt.view.client.CellPreviewEvent;

class PreviewHandler<T extends ContactInfo> extends DefaultKeyboardSelectionHandler<T> {

  private static final int PAGE_INCREMENT = 9;
  
  private final CellList<T> cellList;
  
  public PreviewHandler(CellList<T> cellList) {
    super(cellList);
    this.cellList = cellList;
  }

  @Override
  public void onCellPreview(CellPreviewEvent<T> event) {
    NativeEvent nativeEvent = event.getNativeEvent();
    
    switch (nativeEvent.getType()) {
      case BrowserEvents.KEYDOWN:  // A key has been pushed down
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
            setCurrentRow(cellList.getKeyboardSelectedRow() - 1);
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
            // Just eat this key.  Don't want to trigger RPCs for all data.
            cancelEvent(event);
            break;
        }
        
        // Bypass the default handler (super-class) for all keydown events.
        // For keys not handled here, let the browser handle them (e.g. spacebar to scroll).
        return;
    }

    // Should get here only if event was not handled above.  Send the event to the default handler.
    super.onCellPreview(event);
  }
  
  void setCurrentRow(int row) {
    cellList.setKeyboardSelectedRow(row);
    
    // Read the current row index back from the cellList, because it will have clipped it
    // to a valid row if we tried to set it to a row that doesn't exist.
    int newRow = cellList.getKeyboardSelectedRow();
    
    // Scroll the row into view?
  }
  
  // Re-implement DefaultKeyboardSelectionHandler.handledEvent because that's package-private.
  void cancelEvent(CellPreviewEvent<T> event) {
    event.setCanceled(true);
    event.getNativeEvent().preventDefault();
  }

}
