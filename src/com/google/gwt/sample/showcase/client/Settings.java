package com.google.gwt.sample.showcase.client;

import java.util.LinkedList;

import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.user.client.History;

public class Settings {

  private static Settings instance;
  
  public static Settings get() {
    if (instance == null) {
      instance = new Settings();
      instance.ensureUpdatesOnHistoryChange();
    }
    return instance;
  }
  
  private ObservableBoolean predictiveScrolling = new ObservableBoolean("ps", false);
  private ObservableBoolean followUpFetching = new ObservableBoolean("ff", false);
  private ObservableBoolean conservativeStart = new ObservableBoolean("cs", false);
  private ObservableBoolean windowFilling = new ObservableBoolean("wf", false);
  private ObservableBoolean keyHandling = new ObservableBoolean("kh", false);
  
  private ObservableBoolean[] observables = new ObservableBoolean[] {
      followUpFetching, 
      predictiveScrolling, 
      conservativeStart,
      windowFilling,
      keyHandling
  };
  
  private HandlerRegistration historyHandlerReg;
  
  void ensureUpdatesOnHistoryChange() {
    if (historyHandlerReg != null) {
      return;
    }
    historyHandlerReg = 
        History.addValueChangeHandler(new HistoryChangeHandler());
  }
  
  public boolean getPredictiveScrolling() {
    return predictiveScrolling.getValue();
  }

  public void setPredictiveScrolling(Boolean value) {
    if (predictiveScrolling.setValue(value)) {
      updateHistory();
    }
  }

  public HandlerRegistration addPredictiveScrollingValueChangeHandler(
      ValueChangeHandler<Boolean> handler) {
    return predictiveScrolling.addValueChangeHandler(handler);
  }
  
  public boolean getFollowUpFetching() {
    return followUpFetching.getValue();
  }

  public void setFollowUpFetching(Boolean value) {
    if (followUpFetching.setValue(value)) {
      updateHistory();
    }
  }

  public HandlerRegistration addFollowUpFetchingValueChangeHandler(
      ValueChangeHandler<Boolean> handler) {
    return followUpFetching.addValueChangeHandler(handler);
  }
  
  public boolean getConservativeStart() {
    return conservativeStart.getValue();
  }

  public void setConservativeStart(Boolean value) {
    if (conservativeStart.setValue(value)) {
      updateHistory();
    }
  }

  public HandlerRegistration addConservativeStartChangeHandler(
      ValueChangeHandler<Boolean> handler) {
    return conservativeStart.addValueChangeHandler(handler);
  }
  
  public boolean getWindowFilling() {
    return windowFilling.getValue();
  }

  public void setWindowFilling(Boolean value) {
    if (windowFilling.setValue(value)) {
      updateHistory();
    }
  }

  public HandlerRegistration addWindowFillingChangeHandler(
      ValueChangeHandler<Boolean> handler) {
    return windowFilling.addValueChangeHandler(handler);
  }
  
  public boolean getKeyHandling() {
    return keyHandling.getValue();
  }
  
  public void setKeyHandling(Boolean value) {
    if (keyHandling.setValue(value)) {
      updateHistory();
    }
  }
  
  public HandlerRegistration addKeyHandlingChangeHandler(
      ValueChangeHandler<Boolean> handler) {
    return keyHandling.addValueChangeHandler(handler);
  }
  
  private void updateHistory() {
    String historyToken = History.getToken();
    int startOfParams = historyToken.indexOf("?");
    String widgetToken = (startOfParams == -1)
        ? historyToken : historyToken.substring(0, startOfParams);
    History.replaceItem(widgetToken + getHistorySuffix(), false);
  }

  String getHistorySuffix() {
    LinkedList<ObservableBoolean> nonDefaults = 
        removeThoseWithDefaultValues(observables);
    if (nonDefaults.isEmpty()) {
      return "";
    }
    StringBuilder sb = new StringBuilder();
    sb.append(nonDefaults.pop().toString());
    while (!nonDefaults.isEmpty()) {      
      sb.append("&");
      sb.append(nonDefaults.pop().toString());
    }
    return "?" + sb.toString();
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
  
  private final class HistoryChangeHandler 
      implements ValueChangeHandler<String> {
    @Override
    public void onValueChange(ValueChangeEvent<String> event) {
      // Parse the history token
      for (ObservableBoolean setting : observables) {
        setting.updateValueFromQueryString(event.getValue());
      }
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
