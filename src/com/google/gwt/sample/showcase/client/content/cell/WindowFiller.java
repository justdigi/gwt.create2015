package com.google.gwt.sample.showcase.client.content.cell;

import java.util.logging.Logger;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.sample.showcase.client.Settings;
import com.google.gwt.user.cellview.client.CellList;
import com.google.gwt.user.cellview.client.LoadingStateChangeEvent;
import com.google.gwt.user.client.Window;
import com.google.gwt.view.client.Range;

class WindowFiller {

  private static final Logger logger = 
      Logger.getLogger(WindowFiller.class.getName());

  private WindowFiller(CellList<?> cellList) {
    Handler handler = new Handler(cellList);
    Window.addResizeHandler(handler);
    cellList.addLoadingStateChangeHandler(handler);
  }
  
  static WindowFiller install(CellList<?> cellList) {
    return new WindowFiller(cellList);
  }
  
  private class Handler 
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
      maybeExtendAfterDomHasFinishedUpdating(); 
    }

    @Override
    public void onLoadingStateChanged(LoadingStateChangeEvent event) {
      if (!fillingEnabled()) {
        return;
      }
      if (event.getLoadingState() == 
          LoadingStateChangeEvent.LoadingState.LOADING) {
        logger.info("onLoadingStateChanged: LOADING");
        return;
      }
      if (!theresMoreDataThanVisible()) {
        return;
      }
      logger.info("onLoadingStateChanged: " 
          + (event.getLoadingState() == 
              LoadingStateChangeEvent.LoadingState.LOADED 
              ? "LOADED" : "PARTIALLY_LOADED"));
      maybeExtendAfterDomHasFinishedUpdating(); 
    }

    private void maybeExtendAfterDomHasFinishedUpdating() {
      Scheduler.get().scheduleDeferred(new ScheduledCommand() {
        @Override
        public void execute() {
          maybeExtend();
        }
      });
    }

    private void maybeExtend() {
      logger.info("maybeExtend");
      if (cellList.getOffsetHeight() 
          >= cellList.getParent().getOffsetHeight()) {
        return;
      }
      int visibleItemCount = cellList.getVisibleItemCount();
      double pixelsPerItem = 
          cellList.getOffsetHeight() / (double) visibleItemCount;
      Range newRange = new Range(
          cellList.getVisibleRange().getStart(), 
          (int) Math.ceil(
              cellList.getParent().getOffsetHeight() / pixelsPerItem));
      logger.info("maybeExtend: setting visible to " + newRange);
      cellList.setVisibleRange(newRange);
    }   
    
    private boolean theresMoreDataThanVisible() {
      return !cellList.isRowCountExact() 
          || (cellList.getVisibleItemCount() < cellList.getRowCount());
    }
  }
  
  private boolean fillingEnabled() {
    return Settings.get().getWindowFilling();
  }  
}
