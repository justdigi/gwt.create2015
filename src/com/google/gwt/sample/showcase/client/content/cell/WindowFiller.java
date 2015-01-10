package com.google.gwt.sample.showcase.client.content.cell;

import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.user.cellview.client.CellList;
import com.google.gwt.user.client.Window;

class WindowFiller {

  static void install(CellList<?> cellList) {
    Window.addResizeHandler(new Handler(cellList));
  }
  
  private static class Handler implements ResizeHandler {
    
    private CellList<?> cellList;
    
    public Handler(CellList<?> cellList) {
      this.cellList = cellList;
    }
    
    @Override
    public void onResize(ResizeEvent event) {
      if (cellList.getOffsetHeight() < cellList.getParent().getOffsetHeight()) {
        cellList.setVisibleRange(
            cellList.getPageStart(), cellList.getVisibleItemCount() + 20);
      }
    }
  }
}
