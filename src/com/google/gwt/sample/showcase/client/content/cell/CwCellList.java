/*
 * Copyright 2010 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.google.gwt.sample.showcase.client.content.cell;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.cell.client.Cell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.dom.client.HasKeyDownHandlers;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.i18n.client.Constants;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.CssResource.Import;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.sample.showcase.client.ContentWidget;
import com.google.gwt.sample.showcase.client.Settings;
import com.google.gwt.sample.showcase.client.ShowcaseAnnotations.ShowcaseData;
import com.google.gwt.sample.showcase.client.ShowcaseAnnotations.ShowcaseRaw;
import com.google.gwt.sample.showcase.client.ShowcaseAnnotations.ShowcaseSource;
import com.google.gwt.sample.showcase.client.content.cell.ContactDatabase.ContactInfo;
import com.google.gwt.sample.showcase.client.content.cell.CustomKeyboardHandler.SelectableWidget;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.CellList;
import com.google.gwt.user.cellview.client.HasKeyboardPagingPolicy.KeyboardPagingPolicy;
import com.google.gwt.user.cellview.client.HasKeyboardSelectionPolicy.KeyboardSelectionPolicy;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SingleSelectionModel;

/**
 * Example file.
 */
@ShowcaseRaw({
    "ContactDatabase.java", "CwCellList.ui.xml", "ContactInfoForm.java",
    "ShowMorePagerPanel.java", "RangeLabelPager.java", "CwCellList.css"})
public class CwCellList extends ContentWidget {

  /**
   * The UiBinder interface used by this example.
   */
  @ShowcaseSource
  interface Binder extends UiBinder<Widget, CwCellList> {
  }

  /**
   * The constants used in this Content Widget.
   */
  @ShowcaseSource
  public static interface CwConstants extends Constants {
    String cwCellListDescription();

    String cwCellListName();
  }

  /**
   * The images used for this example.
   */
  @ShowcaseSource
  static interface Images extends ClientBundle {
    ImageResource contact();
    ImageResource star();
    ImageResource starOutline();
  }

  /**
   * The resources used by this example.
   */
  @ShowcaseSource
  interface Resources extends ClientBundle {

    /**
     * Get the styles used but this example.
     */
    @Source("CwCellList.css")
    @Import(CellList.Style.class)
    Styles styles();
  }
  
  /**
   * The CSS Resources used by this example.
   */
  @ShowcaseSource
  interface Styles extends CssResource {
    String contactFormCell();
    String range();
    String selfAndOthersContainer();
    String selfContact();
    String scrollContainer();
    String scrollable();
  }

  /**
   * The Cell used to render a {@link ContactInfo}.
   */
  @ShowcaseSource
  static class ContactCell extends AbstractCell<ContactInfo> {

    /**
     * The html of the image used for contacts.
     */
    private final SafeHtml imageHtml;

    public ContactCell(ImageResource image) {
      this.imageHtml = AbstractImagePrototype.create(image).getSafeHtml();
    }

    interface Templates extends SafeHtmlTemplates {
      @Template(
          "<table><tr>"
          + "<td rowspan='3'>{0}</td>"
          + "<td style='font-size:95%;'>{1}</td></tr><tr>"
          + "<td>{2}</td>"
          + "</tr></table>")
      SafeHtml cell(SafeHtml imageHtml, String fullName, String address);
    }
    
    @Override
    public void render(Context context, ContactInfo value, SafeHtmlBuilder sb) {
      // Value can be null, so do a null check..
      if (value == null) {
        return;
      }
      sb.append(
          GWT.<Templates>create(Templates.class).cell(
              imageHtml, value.getFullName(), value.getAddress()));
    }
  }

  /**
   * The contact form used to update contacts.
   */
  @ShowcaseData
  @UiField
  ContactInfoForm contactForm;

  /**
   * The button used to generate more contacts.
   */
  @ShowcaseData
  @UiField
  Button generateButton;

  /**
   * The pager used to change the range of data.
   */
  @ShowcaseData
  @UiField
  ShowMorePagerPanel pagerPanel;

  /**
   * The pager used to display the current range.
   */
  @ShowcaseData
  @UiField
  RangeLabelPager rangeLabelPager;

  @UiField
  CheckBox predictiveScrollingCheckbox;

  @UiField
  CheckBox prefetchingCheckbox;

  @UiField
  CheckBox conservativeStartCheckbox;

  @UiField
  CheckBox windowFillingCheckbox;
  
  @UiField
  CheckBox keyHandlingCheckbox;

  @UiField
  CheckBox compositeCellCheckbox;

  @UiField
  FocusPanel selfContactContainer;

  private SimpleContactView selfContactView; 

  /**
   * The CellList.
   */
  @ShowcaseData
  private CellList<ContactInfo> cellList;

  /**
   * Constructor.
   *
   * @param constants the constants
   */
  public CwCellList(CwConstants constants) {
    super(constants.cwCellListName(), constants.cwCellListDescription(), false,
        "ContactDatabase.java", "CwCellList.ui.xml", "ContactInfoForm.java",
        "ShowMorePagerPanel.java", "RangeLabelPager.java");
  }

  /**
   * Initialize this example.
   */
  @ShowcaseSource
  @Override
  public Widget onInitialize() {
    GWT.<Resources>create(Resources.class).styles().ensureInjected();
    Images images = GWT.create(Images.class);

    // Create the UiBinder.
    // Bind this before creating the CellList so we can rely on the UiFields
    // existing when the custom keyboard handler is set up.
    Binder uiBinder = GWT.create(Binder.class);
    Widget widget = uiBinder.createAndBindUi(this);

    // Create a CellList.
    Cell<ContactInfo> contactCell = 
        Settings.get().getCompositeCell()
            ? CompositeContactCellFactory.create(
                images, pagerPanel.getScrollable())
            : new ContactCell(images.contact());

    // Set a key provider that provides a unique key for each contact. If key is
    // used to identify contacts when fields (such as the name and address)
    // change.
    cellList = new CellList<>(contactCell,
        ContactDatabase.ContactInfo.KEY_PROVIDER);
    cellList.setPageSize(getInitialPageSize());

    setupKeyboardHandling();

    // Add a selection model so we can select cells.
    final SingleSelectionModel<ContactInfo> selectionModel = 
        new SingleSelectionModel<ContactInfo>(
            ContactDatabase.ContactInfo.KEY_PROVIDER);
    cellList.setSelectionModel(selectionModel);
    selectionModel.addSelectionChangeHandler(new SelectionChangeEvent.Handler() {
      public void onSelectionChange(SelectionChangeEvent event) {
        contactForm.setContact(selectionModel.getSelectedObject());
      }
    });

    // Use the same cell to create a widget to show a contact for the user
    selfContactView = new SimpleContactView(contactCell);
    selfContactView.setContact(ContactDatabase.get().createContactForMe());
    selfContactContainer.setWidget(selfContactView);
    addSelectHandlers(selfContactContainer, new Runnable() {
      @Override
      public void run() {
        showSelfContactInfo();
      }
    });

    // Add the CellList to the data provider in the database.
    ContactDatabase.get().addDataDisplay(cellList);
    
    // Set the cellList as the display of the pagers. This example has two
    // pagers. pagerPanel is a scrollable pager that extends the range when the
    // user scrolls to the bottom. rangeLabelPager is a pager that displays the
    // current range, but does not have any controls to change the range.
    pagerPanel.setDisplay(cellList);
    rangeLabelPager.setDisplay(cellList);

    // Handle events from the generate button.
    // Buttons fire their click handler when Enter is pressed
    generateButton.addClickHandler(new ClickHandler() {
      public void onClick(ClickEvent event) {
        ContactDatabase.get().generateContacts(50);
      }
    });
    
    Prefetcher.install(cellList);
    WindowFiller.install(cellList);
    
    Settings.get().addPredictiveScrollingValueChangeHandler(
        new ValueChangeHandler<Boolean>() {
          @Override
          public void onValueChange(ValueChangeEvent<Boolean> event) {
            predictiveScrollingCheckbox.setValue(event.getValue());
          }
        });
    predictiveScrollingCheckbox.setValue(
        Settings.get().getPredictiveScrolling());
    
    Settings.get().addPrefetchingValueChangeHandler(
        new ValueChangeHandler<Boolean>() {
          @Override
          public void onValueChange(ValueChangeEvent<Boolean> event) {
            prefetchingCheckbox.setValue(event.getValue());
          }
        });
    prefetchingCheckbox.setValue(Settings.get().getPrefetching());

    Settings.get().addConservativeStartChangeHandler(
        new ValueChangeHandler<Boolean>() {
          @Override
          public void onValueChange(ValueChangeEvent<Boolean> event) {
            conservativeStartCheckbox.setValue(event.getValue());
          }
        });
    conservativeStartCheckbox.setValue(Settings.get().getConservativeStart());

    Settings.get().addWindowFillingChangeHandler(
        new ValueChangeHandler<Boolean>() {
          @Override
          public void onValueChange(ValueChangeEvent<Boolean> event) {
            windowFillingCheckbox.setValue(event.getValue());
          }
        });
    windowFillingCheckbox.setValue(Settings.get().getWindowFilling());

    Settings.get().addKeyHandlingChangeHandler(
        new ValueChangeHandler<Boolean>() {
          @Override
          public void onValueChange(ValueChangeEvent<Boolean> event) {
            keyHandlingCheckbox.setValue(event.getValue());
            setKeyboardPagingPolicy();
          }
        });
    keyHandlingCheckbox.setValue(Settings.get().getKeyHandling());

    Settings.get().addCompositeCellChangeHandler(
        new ValueChangeHandler<Boolean>() {
          @Override
          public void onValueChange(ValueChangeEvent<Boolean> event) {
            compositeCellCheckbox.setValue(event.getValue());
            setKeyboardPagingPolicy();
          }
        });
    compositeCellCheckbox.setValue(Settings.get().getCompositeCell());

    return widget;
  }

  @Override
  protected void asyncOnInitialize(final AsyncCallback<Widget> callback) {
    GWT.runAsync(CwCellList.class, new RunAsyncCallback() {

      public void onFailure(Throwable caught) {
        callback.onFailure(caught);
      }

      public void onSuccess() {
        callback.onSuccess(onInitialize());
      }
    });
  }
  
  @Override
  public boolean hasScrollableContent() {
    return false;
  }

  private <W extends HasClickHandlers & HasKeyDownHandlers>
      void addSelectHandlers(W widget, final Runnable handler) {
    widget.addKeyDownHandler(new KeyDownHandler() {
      @Override
      public void onKeyDown(KeyDownEvent event) {
        if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
          handler.run();
        }
      }
    });
    widget.addClickHandler(new ClickHandler() {
      @Override
      public void onClick(ClickEvent event) {
        handler.run();
      }
    });
  }

  void showSelfContactInfo() {
    // We always set the self contact, so we can count on it not being null
    contactForm.setContact(selfContactView.getContact());
  }

  void setupKeyboardHandling() {
    SelectableWidget topWidgetForKeyHandler = new SelectableWidget() {
      @Override
      public HandlerRegistration addKeyDownHandler(KeyDownHandler handler) {
        return selfContactContainer.addKeyDownHandler(handler);
      }
      @Override
      public void fireEvent(GwtEvent<?> event) {
        selfContactContainer.fireEvent(event);
      }
      @Override
      public void selectWidget() {
        selfContactContainer.setFocus(true);
        showSelfContactInfo();
      }
    };

    cellList.setKeyboardSelectionHandler(
        new CustomKeyboardHandler<>(cellList, topWidgetForKeyHandler));

    setKeyboardPagingPolicy();
    cellList.setKeyboardSelectionPolicy(
        KeyboardSelectionPolicy.BOUND_TO_SELECTION);
  }

  void setKeyboardPagingPolicy() {
    if (Settings.get().getKeyHandling()) {
      cellList.setKeyboardPagingPolicy(KeyboardPagingPolicy.CURRENT_PAGE);
    } else {
      cellList.setKeyboardPagingPolicy(KeyboardPagingPolicy.INCREASE_RANGE);
    }
  }

  @UiHandler("predictiveScrollingCheckbox")
  protected void onPredictiveScrollingCheckboxChange(
      ValueChangeEvent<Boolean> event) {
    Settings.get().setPredictiveScrolling(event.getValue());
  }
  
  @UiHandler("prefetchingCheckbox")
  protected void onPrefetchingCheckboxChange(
      ValueChangeEvent<Boolean> event) {
    Settings.get().setPrefetching(event.getValue());
  }
  
  @UiHandler("conservativeStartCheckbox")
  protected void onConservativeStartCheckboxChange(
      ValueChangeEvent<Boolean> event) {
    Settings.get().setConservativeStart(event.getValue());
  }
  
  @UiHandler("windowFillingCheckbox")
  protected void onWindowFillingCheckboxChange(
      ValueChangeEvent<Boolean> event) {
    Settings.get().setWindowFilling(event.getValue());
  }
  
  @UiHandler("keyHandlingCheckbox")
  protected void onKeyHandlingCheckboxChange(
      ValueChangeEvent<Boolean> event) {
    Settings.get().setKeyHandling(event.getValue());
    setKeyboardPagingPolicy();
  }
  
  @UiHandler("compositeCellCheckbox")
  protected void onCompositeCellCheckboxChange(
      ValueChangeEvent<Boolean> event) {
    Settings.get().setCompositeCell(event.getValue());
  }
  
  private static int getInitialPageSize() {
    return Settings.get().getConservativeStart() ? 5 : 15;
  }
}
