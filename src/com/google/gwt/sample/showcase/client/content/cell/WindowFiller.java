package com.google.gwt.sample.showcase.client.content.cell;

import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.user.cellview.client.CellList;
import com.google.gwt.user.cellview.client.LoadingStateChangeEvent;
import com.google.gwt.user.client.Window;

class WindowFiller {

  static void install(CellList<?> cellList) {
    Handler handler = new Handler(cellList);
    Window.addResizeHandler(handler);
    cellList.addLoadingStateChangeHandler(handler);
  }
  
  private static class Handler 
      implements ResizeHandler, LoadingStateChangeEvent.Handler {
    
    private CellList<?> cellList;
    
    public Handler(CellList<?> cellList) {
      this.cellList = cellList;
    }
    
    @Override
    public void onResize(ResizeEvent event) {
      if (!fillingEnabled()) {
        return;
      }
      maybeExtend();
    }

    @Override
    public void onLoadingStateChanged(LoadingStateChangeEvent event) {
      if (!fillingEnabled()) {
        return;
      }
      if (event.getLoadingState() == 
          LoadingStateChangeEvent.LoadingState.LOADED
          && (cellList.getVisibleItemCount() < cellList.getRowCount())) {
        maybeExtend();
      }
    }

    private void maybeExtend() {
      if (cellList.getOffsetHeight() < cellList.getParent().getOffsetHeight()) {
        cellList.setVisibleRange(
            cellList.getPageStart(), cellList.getVisibleItemCount() + 20);
      }
    }   
    
    private boolean fillingEnabled() {
      return Settings.get().getWindowFilling();
    }
  }
}
