package com.google.gwt.sample.showcase.client.content.cell;

import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.user.client.History;

class Settings {

  private static Settings instance;
  
  static Settings get() {
    if (instance == null) {
      instance = new Settings();
    }
    return instance;
  }
  
  private ObservableBoolean predictiveScrolling = new ObservableBoolean(false);
  private ObservableBoolean followUpFetching = new ObservableBoolean(false);
  
  Settings() {
    History.addValueChangeHandler(new HistoryChangeHandler());
  }
  
  boolean getPredictiveScrolling() {
    return predictiveScrolling.getValue();
  }

  void setPredictiveScrolling(Boolean value) {
    if (predictiveScrolling.setValue(value)) {
      updateHistoryForParam("ps", value);
    }
  }

  HandlerRegistration addPredictiveScrollingValueChangeHandler(
      ValueChangeHandler<Boolean> handler) {
    return predictiveScrolling.addValueChangeHandler(handler);
  }
  
  boolean getFollowUpFetching() {
    return followUpFetching.getValue();
  }

  void setFollowUpFetching(Boolean value) {
    if (followUpFetching.setValue(value)) {
      updateHistoryForParam("ff", value);
    }
  }

  HandlerRegistration addFollowUpFetchingValueChangeHandler(
      ValueChangeHandler<Boolean> handler) {
    return followUpFetching.addValueChangeHandler(handler);
  }
  
  private void updateHistoryForParam(String key, boolean value) {
    String historyToken = History.getToken();
    int startOfParams = historyToken.indexOf("?");
    String widgetToken, params;
    if (startOfParams == -1) {
      widgetToken = historyToken;
      params = "";
    } else {
      widgetToken = historyToken.substring(0, startOfParams);
      params = historyToken.substring(
          startOfParams + 1, historyToken.length());
    }
    params = params.replaceAll(key + "=.", "");
    params += value ? key + "=1" : key + "=0";
    History.replaceItem(widgetToken + "?" + params, false);
  }

  private final class HistoryChangeHandler 
      implements ValueChangeHandler<String> {
    @Override
    public void onValueChange(ValueChangeEvent<String> event) {
      String historyToken = event.getValue();
      
      // Parse the history token
      if (historyToken.contains("ps=1")) {
        setPredictiveScrolling(true);
      } else if (historyToken.contains("ps=0")) {
        setPredictiveScrolling(false);
      }
    }
  }
  
  private static class ObservableBoolean 
      implements HasValueChangeHandlers<Boolean> {
    
    private EventBus eventBus = new SimpleEventBus();
    private boolean value;

    ObservableBoolean(boolean value) {
      this.value = value;
    }
    
    public boolean getValue() {
      return value;
    }

    boolean setValue(boolean newValue) {
      boolean oldValue = value;
      value = newValue;
      ValueChangeEvent.fireIfNotEqual(this, oldValue, newValue);
      return value != oldValue;
    }

    @Override
    public void fireEvent(GwtEvent<?> event) {
      eventBus.fireEvent(event);
    }

    @Override
    public HandlerRegistration addValueChangeHandler(
        ValueChangeHandler<Boolean> handler) {
      return eventBus.addHandler(ValueChangeEvent.getType(), handler);
    }
  }
}
