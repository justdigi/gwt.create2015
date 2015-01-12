package com.google.gwt.sample.showcase.client.content.cell;

import java.util.logging.Logger;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.user.cellview.client.CellList;
import com.google.gwt.user.cellview.client.LoadingStateChangeEvent;
import com.google.gwt.user.client.Window;
import com.google.gwt.view.client.Range;
import com.google.gwt.view.client.RowCountChangeEvent;

class WindowFiller {

  private static final Logger logger = 
      Logger.getLogger(WindowFiller.class.getName());

  private boolean waitingToSeeIfRangeChangeWasSufficient;

  private WindowFiller(CellList<?> cellList) {
    Handler handler = new Handler(cellList);
    Window.addResizeHandler(handler);
    cellList.addLoadingStateChangeHandler(handler);
    cellList.addRowCountChangeHandler(handler);
  }
  
  void reset() {
    waitingToSeeIfRangeChangeWasSufficient = false;
  }
  
  static WindowFiller install(CellList<?> cellList) {
    return new WindowFiller(cellList);
  }
  
  private class Handler 
      implements 
          ResizeHandler,  
          LoadingStateChangeEvent.Handler, 
          RowCountChangeEvent.Handler {
    
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
      if (waitingToSeeIfRangeChangeWasSufficient) {
        return;
      }
      if (!theresMoreDataThanVisible()) {
        waitingToSeeIfRangeChangeWasSufficient = false;
        return;
      }
      // Give the DOM a chance to update then check to see if we could fill.
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
        waitingToSeeIfRangeChangeWasSufficient = false;
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
      waitingToSeeIfRangeChangeWasSufficient = true;
      cellList.setVisibleRange(newRange);
    }   
    
    @Override
    public void onRowCountChange(RowCountChangeEvent event) {
      if (!waitingToSeeIfRangeChangeWasSufficient) {
        return;
      }
      if (!theresMoreDataThanVisible()) {
        waitingToSeeIfRangeChangeWasSufficient = false;
        return;
      }
      maybeExtend();
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
