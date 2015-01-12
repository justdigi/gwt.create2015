package com.google.gwt.sample.showcase.client.content.cell;

import com.google.gwt.user.cellview.client.CellList;
import com.google.gwt.user.cellview.client.LoadingStateChangeEvent;
import com.google.gwt.user.client.Timer;

class FollowUpFetcher {

  private boolean alreadyScheduled = false;

  private FollowUpFetcher(CellList<?> cellList) {
    Handler handler = new Handler(cellList);
    cellList.addLoadingStateChangeHandler(handler);
  }
  
  void reset() {
    alreadyScheduled = false;
  }
  
  static FollowUpFetcher install(CellList<?> cellList) {
    return new FollowUpFetcher(cellList);
  }
  
  private class Handler implements LoadingStateChangeEvent.Handler {
    
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
      if (alreadyScheduled) {
        return;
      }
      // Wait for the cell list to finish drawing before adding more.
      Timer timer = new Timer() {
        @Override
        public void run() {
          maybeExtend();
        }
      };
      timer.schedule(250);
      alreadyScheduled = true;
    }

    private void maybeExtend() {
      if (cellList.getOffsetHeight() < 2 * cellList.getParent().getOffsetHeight()) {
        int visibleItemCount = cellList.getVisibleItemCount();
        double pixelsPerItem = 
            cellList.getOffsetHeight() / (double) visibleItemCount;
        cellList.setVisibleRange(
            cellList.getVisibleRange().getStart(), 
            (int) Math.ceil(
                2 * cellList.getParent().getOffsetHeight() / pixelsPerItem));
      }
    }   
    
    private boolean followUpFetchingEnabled() {
      return Settings.get().getFollowUpFetching();
    }
  }
  
  
}
