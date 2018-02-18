/**
 * SearchDropDown.java (SearchDropDown)
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
package org.vaadin.alump.searchdropdown;

import com.vaadin.event.MouseEvents;
import com.vaadin.server.Resource;
import com.vaadin.shared.EventId;
import com.vaadin.shared.MouseEventDetails;
import com.vaadin.shared.Registration;
import com.vaadin.shared.ui.ValueChangeMode;
import com.vaadin.ui.AbstractField;
import com.vaadin.ui.HasValueChangeMode;
import org.vaadin.alump.searchdropdown.client.share.*;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

/**
 * Search drop down field
 * @param <T> Type of suggestion values
 */
public class SearchDropDown<T> extends AbstractField<String> implements SearchSuggestionPresenter<T>, HasValueChangeMode {

    private SearchSuggestionProvider<T> suggestionProvider;
    private List<SearchListener<T>> searchListeners = new ArrayList<>();

    private Set<String> currentResourcesKeys = new HashSet<>();
    private AtomicInteger suggestionCounter = new AtomicInteger(0);
    private Map<Integer,SearchSuggestion<T>> currentSuggestions = new HashMap<>();
    private String textFromClient;

    private SearchDropDownServerRpc serverRpc = new SearchDropDownServerRpc() {

        @Override
        public void suggestionSelected(int suggestionId) {
            SearchSuggestion<T> suggestion = currentSuggestions.get(suggestionId);
            if(suggestion != null) {
                setValue(suggestion.getPlainText(), true);
            } else {
                getLogger().warning("Failed to find selected suggestion " + suggestionId + " "
                        + currentSuggestions.size());
            }
            hideSuggestions();

            SearchEvent<T> event = new SearchEvent<T>(SearchDropDown.this, suggestion);
            searchListeners.forEach(l -> l.search(event));
        }

        @Override
        public void textSelected(String text) {
            setValue(text, true);
            hideSuggestions();

            SearchEvent<T> event = new SearchEvent<T>(SearchDropDown.this, text);
            searchListeners.forEach(l -> l.search(event));
        }

        @Override
        public void showMoreResultsClicked(String query) {
            if(suggestionProvider != null) {
                suggestionProvider.showMoreResults(query);
            }
        }

        @Override
        public void click(MouseEventDetails mouseDetails) {
            fireEvent(new MouseEvents.ClickEvent(SearchDropDown.this, mouseDetails));
        }

        @Override
        public void setText(String text, int cursorPosition) {
            boolean clearEvent = text.isEmpty() && !text.equals(textFromClient);

            textFromClient = text;
            getState(false).text = text;
            updateSuggestions(text);

            if(clearEvent) {
                SearchEvent<T> event = new SearchEvent<T>(SearchDropDown.this, text, true);
                searchListeners.forEach(l -> l.search(event));
            }
        }
    };

    /**
     * Create new SearchDropDown without suggestion provider (must be provided later)
     */
    public SearchDropDown() {
        super();
        setValueChangeMode(ValueChangeMode.LAZY);
        registerRpc(serverRpc, SearchDropDownServerRpc.class);
    }

    /**
     * Create new SearchDropDown with suggestion provider
     * @param provider Suggestion provider used to find suggestions for search queries
     */
    public SearchDropDown(SearchSuggestionProvider<T> provider) {
        this();
        this.suggestionProvider = Objects.requireNonNull(provider);
    }

    @Override
    public void detach() {
        clearSuggestions();
        super.detach();
    }

    /**
     * Ask to hide suggestions. Usually you should not need to call this from outside.
     */
    public void hideSuggestions() {
        getRpcProxy(SearchDropDownClientRpc.class).hideSuggestions();
    }

    private static Logger getLogger() {
        return Logger.getLogger(SearchDropDown.class.getName());
    }

    /**
     * Define suggestion provider for SearchDropDown. New provider will be used when next query is received from client
     * side.
     * @param provider Suggestion provider
     */
    public void setSuggestionProvider(SearchSuggestionProvider<T> provider) {
        this.suggestionProvider = provider;
    }

    /**
     * Get current suggestion provider
     * @return
     */
    protected Optional<SearchSuggestionProvider<T>> getSuggestionProvider() {
        return Optional.ofNullable(suggestionProvider);
    }

    @Override
    protected void doSetValue(String value) {
        textFromClient = value;
        getState().text = value;
    }

    @Override
    protected SearchDropDownState getState() {
        return (SearchDropDownState) super.getState();
    }

    @Override
    protected SearchDropDownState getState(boolean markDirty) {
        return (SearchDropDownState) super.getState(markDirty);
    }


    /**
     * Returns value in text field
     * @return
     */
    @Override
    public String getValue() {
        return textFromClient;
    }

    protected void clearSuggestions() {
        currentResourcesKeys.forEach(key -> {
            setResource(key, null);
        });
        currentResourcesKeys.clear();
        currentSuggestions.clear();
    }

    protected void updateSuggestions(String query) {
        getSuggestionProvider().ifPresent(p -> p.provideSuggestions(query, this));
    }

    @Override
    public void showSuggestions(String query, List<? extends SearchSuggestion<T>> suggestions, boolean showMoreResults) {
        if(isAttached()) {
            getUI().access(() -> unsafeShowSuggestions(query, suggestions, showMoreResults));
        } else {
            unsafeShowSuggestions(query, suggestions, showMoreResults);
        }
    }

    private void unsafeShowSuggestions(String query,
                                       List<? extends SearchSuggestion<T>> suggestions,
                                       boolean hasMoreResults) {
        clearSuggestions();

        List<SharedSuggestion> sharedSuggestions = new ArrayList<>();

        Objects.requireNonNull(suggestions).stream().map(option -> {

            int suggestionId = getNextSuggestionId();

            currentSuggestions.put(suggestionId, option);

            SharedSuggestion sharedSuggestion = new SharedSuggestion(suggestionId, option.getText(),
                    option.getStyleName().orElse(null), option.getContentMode());

            option.getIcon().ifPresent(icon -> {
                String key = getResourceKey(suggestionId);
                currentResourcesKeys.add(key);
                setResource(key, icon);
                sharedSuggestion.iconResource = key;
            });

            return sharedSuggestion;

        }).forEach(sharedSuggestions::add);

        getRpcProxy(SearchDropDownClientRpc.class).showSuggestions(query, sharedSuggestions, hasMoreResults);
    }

    protected int getNextSuggestionId() {
        return suggestionCounter.incrementAndGet();
    }

    protected static String getResourceKey(int suggestionId) {
        return "suggestion-" + suggestionId;
    }

    /**
     * Define placeholder on text field, shown when no value is written there
     * @param placeHolder Place holder text shown in text field
     */
    public void setPlaceHolder(String placeHolder) {
        getState().placeholder = placeHolder;
    }

    /**
     * If clear button should be shown in the UI when search field has value
     * @param showClear true to show clear button, false to not show it
     */
    public void setShowClear(boolean showClear) {
        getState().showClear = showClear;
    }

    @Override
    public void setValueChangeMode(ValueChangeMode mode) {
        if(mode == ValueChangeMode.BLUR) {
            getLogger().warning("Using ValueChangeMode.BLUR is a bad idea with SearchDropDown.");
        }
        getState().valueChangeMode = mode;
    }

    @Override
    public ValueChangeMode getValueChangeMode() {
        return getState(false).valueChangeMode;
    }

    @Override
    public void setValueChangeTimeout(int valueChangeTimeout) {
        getState().valueChangeTimeout = valueChangeTimeout;
    }

    @Override
    public int getValueChangeTimeout() {
        return getState(false).valueChangeTimeout;
    }

    /**
     * Add listener for search events
     * @param listener
     */
    public void addSearchListener(SearchListener<T> listener) {
        searchListeners.add(listener);
    }

    /**
     * Remove listener of search events
     * @param listener
     */
    public void removeSearchListener(SearchListener<T> listener) {
        searchListeners.remove(listener);
    }

    /**
     * Add click listener (called when search icon of field is clicked, can be used as search initializing button)
     * @param listener Listener added
     */
    public Registration addClickListener(MouseEvents.ClickListener listener) {
        return addListener(EventId.CLICK_EVENT_IDENTIFIER, MouseEvents.ClickEvent.class, listener,
                MouseEvents.ClickListener.clickMethod);
    }

    /**
     * Remove click listener
     * @param listener Listener removed
     */
    public void removeClickListener(MouseEvents.ClickListener listener) {
        removeListener(EventId.CLICK_EVENT_IDENTIFIER, MouseEvents.ClickEvent.class,
                listener);
    }

    public void setMoreResultsButton(String caption) {
        setMoreResultsButton(caption, null, Collections.EMPTY_LIST);
    }

    public void setMoreResultsButton(String caption, Resource icon) {
        setMoreResultsButton(caption, icon, Collections.EMPTY_LIST);
    }

    public void setMoreResultsButton(String caption, Resource icon, Collection<String> styleNames) {
        ShowMoreResultsButtonState buttonState = new ShowMoreResultsButtonState();
        buttonState.caption = caption;
        if(icon != null) {
            setResource("more-results-icon", icon);
        } else {
            setResource("more-results-icon", null);
        }
        buttonState.styleNames = new ArrayList<>(styleNames);
        getState().showMoreButton = buttonState;
    }
}
