/**
 * SearchDropDownConnector.java (SearchDropDown)
 *
 * Copyright 2017 Vaadin Ltd, Sami Viitanen <sami.viitanen@vaadin.org>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.vaadin.alump.searchdropdown.client;

import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.FocusEvent;
import com.vaadin.client.ApplicationConnection;
import com.vaadin.client.WidgetUtil;
import com.vaadin.client.annotations.OnStateChange;
import com.vaadin.client.ui.ClickEventHandler;
import com.vaadin.client.ui.Icon;
import com.vaadin.client.ui.textfield.ValueChangeHandler;
import com.vaadin.shared.MouseEventDetails;
import com.vaadin.shared.ui.ContentMode;
import org.vaadin.alump.searchdropdown.SearchDropDown;

import com.vaadin.client.communication.StateChangeEvent;
import com.vaadin.client.ui.AbstractComponentConnector;
import com.vaadin.shared.ui.Connect;
import org.vaadin.alump.searchdropdown.client.share.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Connector for SearchDropDown
 */
@Connect(SearchDropDown.class)
public class SearchDropDownConnector extends AbstractComponentConnector implements ValueChangeHandler.Owner {

    private String waitingSuggestionTo = null;
    private ValueChangeHandler valueChangeHandler;

    private SearchDropDownClientRpc clientRpc = new SearchDropDownClientRpc() {
        @Override
        public void showSuggestions(String value, List<SharedSuggestion> suggestions, boolean showMoreButton) {

            if(value.equals(waitingSuggestionTo)) {
                List<SearchDropDownWidget.Suggestion> converted = new ArrayList<>();
                for (SharedSuggestion suggestion : suggestions) {
                    SearchDropDownWidget.Suggestion created = new SearchDropDownWidget.Suggestion(suggestion.id,
                            generateSuggestionHtml(suggestion), suggestion.styleName);

                    converted.add(created);
                }
                getWidget().showSuggestions(converted);

                if(showMoreButton && getState().showMoreButton != null) {
                    ShowMoreResultsButtonState button = getState().showMoreButton;
                    Icon icon = null;
                    if(SearchDropDownConnector.this.getResourceUrl("more-results-icon") != null) {
                        icon = SearchDropDownConnector.this.getConnection().getIcon(
                                SearchDropDownConnector.this.getResourceUrl("more-results-icon"));
                    }
                    getWidget().showMoreResultsButton(button.caption, icon, button.styleNames);
                } else {
                    getWidget().hideMoreResultsButton();
                }

                getWidget().setLoadingSuggestions(false);
                waitingSuggestionTo = null;
            } else {
                if (waitingSuggestionTo != null) {
                    getRpcProxy(SearchDropDownServerRpc.class).setText(waitingSuggestionTo, 0);
                } else {
                    getWidget().setLoadingSuggestions(false);
                }
            }
        }

        @Override
        public void hideSuggestions() {
            getWidget().hideSuggestions();
        }
    };

    public SearchDropDownConnector() {
        // To receive RPC events from server, we register ClientRpc implementation 
        registerRpc(SearchDropDownClientRpc.class, clientRpc);
    }

    @Override
    public SearchDropDownWidget getWidget() {
        return (SearchDropDownWidget) super.getWidget();
    }

    @Override
    public SearchDropDownState getState() {
        return (SearchDropDownState) super.getState();
    }

    @Override
    protected void init() {
        super.init();

        valueChangeHandler = new ValueChangeHandler(this);

        getWidget().setSuggestionProvider(this::provideSuggestions);
        getWidget().addFieldBlurListener(this::onFieldBlur);
        getWidget().addFieldFocusListener(this::onFieldFocus);
        getWidget().addSuggestionSelectionListener(this::onSelection);
        getWidget().setShowMoreClickHandler(this::showMoreButtonClicked);
    }

    private void showMoreButtonClicked(ClickEvent event) {
        getWidget().hideSuggestions();
        getRpcProxy(SearchDropDownServerRpc.class).showMoreResultsClicked(getWidget().getText());
    }

    private void onFieldBlur(BlurEvent event) {
        waitingSuggestionTo = null;
    }

    private void onFieldFocus(FocusEvent event) {
        sendQuery(getWidget().getText());
    }

    private void onSelection(Integer suggestionId, String text) {
        if(suggestionId != null) {
            getRpcProxy(SearchDropDownServerRpc.class).suggestionSelected(suggestionId);
        } else if(text != null) {
            getRpcProxy(SearchDropDownServerRpc.class).textSelected(text);
        }
    }

    private void provideSuggestions(String value) {
        valueChangeHandler.scheduleValueChange();
    }

    // Whenever the state changes in the server-side, this method is called
    @Override
    public void onStateChanged(StateChangeEvent stateChangeEvent) {
        super.onStateChanged(stateChangeEvent);

        clickEventHandler.handleEventHandlerRegistration();

        if(waitingSuggestionTo == null) {
            getWidget().setText(getState().text);
        }
    }

    public String generateSuggestionHtml(SharedSuggestion suggestion) {
        final StringBuffer sb = new StringBuffer();
        ApplicationConnection client = getConnection();
        if(suggestion.iconResource != null) {
            final Icon icon = client.getIcon(
                    getState().resources.get(suggestion.iconResource).getURL());
            if (icon != null) {
                sb.append(icon.getElement().getString());
            }
        }
        if ("".equals(suggestion.text)) {
            sb.append("<span>&nbsp;</span>");
        } else if(suggestion.contentMode == ContentMode.PREFORMATTED) {
            sb.append("<pre>" + WidgetUtil.escapeHTML(suggestion.text) + "</pre>");
        } else if(suggestion.contentMode == ContentMode.HTML) {
            sb.append(suggestion.text);
        } else {
            sb.append("<span>" + WidgetUtil.escapeHTML(suggestion.text) + "</span>");
        }
        return sb.toString();
    }

    protected void sendQuery(String query) {
        boolean wasWaiting = waitingSuggestionTo != null;
        waitingSuggestionTo = query;
        if(!wasWaiting) {
            getWidget().setLoadingSuggestions(true);
            getRpcProxy(SearchDropDownServerRpc.class).setText(query, 0);
        }
    }

    @Override
    public void sendValueChange() {
        sendQuery(getWidget().getText());
    }

    @OnStateChange("valueChangeMode")
    private void updateValueChangeMode() {
        valueChangeHandler.setValueChangeMode(getState().valueChangeMode);
    }

    @OnStateChange("valueChangeTimeout")
    private void updateValueChangeTimeout() {
        valueChangeHandler.setValueChangeTimeout(getState().valueChangeTimeout);
    }

    protected final ClickEventHandler clickEventHandler = new ClickEventHandler(
            this) {

        @Override
        protected void fireClick(NativeEvent event, MouseEventDetails mouseDetails) {
            if(getWidget().isEventAtIcon(event)) {
                getRpcProxy(SearchDropDownServerRpc.class).click(mouseDetails);
                event.stopPropagation();
            }
        }

    };

}
