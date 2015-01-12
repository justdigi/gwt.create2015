package com.google.gwt.sample.showcase.client.content.cell;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.user.cellview.client.CellList;
import com.google.gwt.user.cellview.client.LoadingStateChangeEvent;
import com.google.gwt.user.client.Window;

class WindowFiller {

  private WindowFiller(CellList<?> cellList) {
    Handler handler = new Handler(cellList);
    Window.addResizeHandler(handler);
    cellList.addLoadingStateChangeHandler(handler);
  }
  
  void reset() {
  }
  
  static WindowFiller install(CellList<?> cellList) {
    return new WindowFiller(cellList);
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
          LoadingStateChangeEvent.LoadingState.LOADING) {
        return;
      }
      if (cellList.getVisibleItemCount() < cellList.getRowCount()) {
        // Wait for the cell list to finish drawing before adding more.
        Scheduler.get().scheduleDeferred(new ScheduledCommand() {
          public void execute() {
            maybeExtend();
          }
        });
      }
    }

    private void maybeExtend() {
      if (cellList.getOffsetHeight() < cellList.getParent().getOffsetHeight()) {
        int visibleItemCount = cellList.getVisibleItemCount();
        double pixelsPerItem = 
            cellList.getOffsetHeight() / (double) visibleItemCount;
        cellList.setVisibleRange(
            cellList.getVisibleRange().getStart(), 
            (int) Math.ceil(
                cellList.getParent().getOffsetHeight() / pixelsPerItem));
      }
    }   
    
    private boolean fillingEnabled() {
      return Settings.get().getWindowFilling();
    }
  }
}
