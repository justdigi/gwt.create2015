package com.google.gwt.sample.showcase.client.content.cell;

import java.util.logging.Logger;

import com.google.gwt.sample.showcase.client.Settings;
import com.google.gwt.user.cellview.client.CellList;
import com.google.gwt.user.cellview.client.LoadingStateChangeEvent;
import com.google.gwt.user.client.Timer;
import com.google.gwt.view.client.Range;

class FollowUpFetcher {

  private static final Logger logger = 
      Logger.getLogger(FollowUpFetcher.class.getName());
  
  private Timer timer;

  private FollowUpFetcher(CellList<?> cellList) {
    cellList.addLoadingStateChangeHandler(new Handler(cellList));
  }
  
  static FollowUpFetcher install(CellList<?> cellList) {
    return new FollowUpFetcher(cellList);
  }
  
  private class Handler implements LoadingStateChangeEvent.Handler {
    
    private static final int FACTOR_LARGER_THAN_VIEWPORT = 3;
    private CellList<?> cellList;
    
    public Handler(CellList<?> cellList) {
      this.cellList = cellList;
    }
    
    @Override
    public void onLoadingStateChanged(LoadingStateChangeEvent event) {
      if (!followUpFetchingEnabled()) {
        return;
      }
      if (event.getLoadingState() == 
          LoadingStateChangeEvent.LoadingState.LOADING) {
        return;
      }
      if (timer != null && timer.isRunning()) {
        // Another thread requested data while we were waiting, so postpone
        // until the other request is handled.
        timer.schedule(250);
      }
      if (timer != null && !timer.isRunning()) {
        // We already did a follow-up fetch, so we're done.
        return;
      }
      timer = new Timer() {
        @Override
        public void run() {
          maybeExtend();
        }
      };
      timer.schedule(250);
    }

    private void maybeExtend() {
      logger.info("maybeExtend");
      if (cellList.getOffsetHeight() 
          < (FACTOR_LARGER_THAN_VIEWPORT 
              * cellList.getParent().getOffsetHeight())) {
        int visibleItemCount = cellList.getVisibleItemCount();
        double pixelsPerItem = 
            cellList.getOffsetHeight() / (double) visibleItemCount;
        Range newRange = new Range(
            cellList.getVisibleRange().getStart(), 
            (int) Math.ceil(
                FACTOR_LARGER_THAN_VIEWPORT 
                * cellList.getParent().getOffsetHeight() / pixelsPerItem));
        logger.info("maybeExtend: setting visible to " + newRange);
        cellList.setVisibleRange(newRange);
      }
    }   
    
    private boolean followUpFetchingEnabled() {
      return Settings.get().getFollowUpFetching();
    }
  }
}
