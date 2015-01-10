package com.google.gwt.sample.showcase.client.content.cell;

import java.util.LinkedList;

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
  
  private ObservableBoolean predictiveScrolling = 
      new ObservableBoolean("ps", false);
  private ObservableBoolean followUpFetching = 
      new ObservableBoolean("ff", false);
  private ObservableBoolean conservativeStart = 
      new ObservableBoolean("cs", false);
  
  Settings() {
    History.addValueChangeHandler(new HistoryChangeHandler());
  }
  
  boolean getPredictiveScrolling() {
    return predictiveScrolling.getValue();
  }

  void setPredictiveScrolling(Boolean value) {
    if (predictiveScrolling.setValue(value)) {
      updateHistory();
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
      updateHistory();
    }
  }

  HandlerRegistration addFollowUpFetchingValueChangeHandler(
      ValueChangeHandler<Boolean> handler) {
    return followUpFetching.addValueChangeHandler(handler);
  }
  
  boolean getConservativeStart() {
    return conservativeStart.getValue();
  }

  void setConservativeStart(Boolean value) {
    if (conservativeStart.setValue(value)) {
      updateHistory();
    }
  }

  HandlerRegistration addConservativeStartChangeHandler(
      ValueChangeHandler<Boolean> handler) {
    return conservativeStart.addValueChangeHandler(handler);
  }
  
  private void updateHistory() {
    updateHistory(followUpFetching, predictiveScrolling, conservativeStart);
  }
  
  private void updateHistory(ObservableBoolean... settings) {
    LinkedList<ObservableBoolean> nonDefaults = 
        removeThoseWithDefaultValues(settings);
    if (nonDefaults.isEmpty()) {
      updateHistoryForParams("");
      return;
    }
    StringBuilder sb = new StringBuilder();
    sb.append(nonDefaults.pop().toString());
    while (!nonDefaults.isEmpty()) {      
      sb.append("&");
      sb.append(nonDefaults.pop().toString());
    }
    updateHistoryForParams(sb.toString());
  }
  
  private LinkedList<ObservableBoolean> removeThoseWithDefaultValues(
      ObservableBoolean... settings) {
    LinkedList<ObservableBoolean> result = new LinkedList<ObservableBoolean>();
    for (ObservableBoolean setting : settings) {
      if (!setting.isDefault()) {
        result.add(setting);
      }
    }
    return result;
  }
  
  private void updateHistoryForParams(String params) {
    String historyToken = History.getToken();
    int startOfParams = historyToken.indexOf("?");
    String widgetToken = (startOfParams == -1)
        ? historyToken : historyToken.substring(0, startOfParams);
    History.replaceItem(
        widgetToken + (params.isEmpty() ? "" : "?" + params), false);
  }

  private final class HistoryChangeHandler 
      implements ValueChangeHandler<String> {
    @Override
    public void onValueChange(ValueChangeEvent<String> event) {
      String historyToken = event.getValue();
      
      // Parse the history token
      predictiveScrolling.updateValueFromQueryString(historyToken);
      followUpFetching.updateValueFromQueryString(historyToken);
    }
  }
  
  private static class ObservableBoolean 
      implements HasValueChangeHandlers<Boolean> {
    
    private EventBus eventBus = new SimpleEventBus();
    private String key;
    private boolean defaultValue;
    private boolean value;

    ObservableBoolean(String key, boolean defaultValue) {
      this.key = key;
      this.defaultValue = defaultValue;
      this.value = defaultValue;
    }
    
    boolean isDefault() {
      return value == defaultValue;
    }
    
    boolean getValue() {
      return value;
    }

    boolean setValue(boolean newValue) {
      boolean oldValue = value;
      value = newValue;
      ValueChangeEvent.fireIfNotEqual(this, oldValue, newValue);
      return value != oldValue;
    }

    void updateValueFromQueryString(String query) {
      if (query.contains(createOpposite().toString())) {
        setValue(!value);
      }
    }
    
    ObservableBoolean createOpposite() {
      return new ObservableBoolean(key, !value);
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
    
    @Override
    public String toString() {
      return key + "=" + (value ? "1" : "0");
    }
  }
}
