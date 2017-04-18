package org.vaadin.alump.searchdropdown;

import com.vaadin.shared.ui.ValueChangeMode;
import com.vaadin.ui.AbstractField;
import com.vaadin.ui.HasValueChangeMode;
import org.vaadin.alump.searchdropdown.client.share.SearchDropDownClientRpc;
import org.vaadin.alump.searchdropdown.client.share.SearchDropDownServerRpc;
import org.vaadin.alump.searchdropdown.client.share.SearchFieldState;
import org.vaadin.alump.searchdropdown.client.share.SharedSuggestion;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

/**
 * Search drop down field
 * @param <T>
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
                setValue(suggestion.getText(), true);
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
            getLogger().warning("Text selection: " + text);
            setValue(text, true);
            hideSuggestions();

            SearchEvent<T> event = new SearchEvent<T>(SearchDropDown.this, text);
            searchListeners.forEach(l -> l.search(event));
        }

        @Override
        public void setText(String text, int cursorPosition) {
            textFromClient = text;
            getState(false).text = text;
            updateSuggestions(text);
        }
    };

    public SearchDropDown() {
        super();
        setValueChangeMode(ValueChangeMode.LAZY);
        registerRpc(serverRpc, SearchDropDownServerRpc.class);
    }

    public SearchDropDown(SearchSuggestionProvider<T> provider) {
        this();
        this.suggestionProvider = Objects.requireNonNull(provider);
    }

    public void detach() {
        clearSuggestions();
        super.detach();
    }

    protected void hideSuggestions() {
        getRpcProxy(SearchDropDownClientRpc.class).hideSuggestions();
    }

    private static Logger getLogger() {
        return Logger.getLogger(SearchDropDown.class.getName());
    }

    public void setSearchOptionProvider(SearchSuggestionProvider<T> provider) {
        this.suggestionProvider = provider;
    }

    protected Optional<SearchSuggestionProvider<T>> getSuggestionProvider() {
        return Optional.ofNullable(suggestionProvider);
    }

    @Override
    protected void doSetValue(String value) {
        textFromClient = value;
        getState().text = value;
    }

    @Override
    protected SearchFieldState getState() {
        return (SearchFieldState) super.getState();
    }

    @Override
    protected SearchFieldState getState(boolean markDirty) {
        return (SearchFieldState) super.getState(markDirty);
    }


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
    public void showSuggestions(String query, List<SearchSuggestion<T>> suggestions) {
        if(isAttached()) {
            getUI().access(() -> unsafeShowSuggestions(query, suggestions));
        } else {
            unsafeShowSuggestions(query, suggestions);
        }
    }

    private void unsafeShowSuggestions(String query, List<SearchSuggestion<T>> suggestions) {
        clearSuggestions();

        List<SharedSuggestion> sharedSuggestions = new ArrayList<>();

        Objects.requireNonNull(suggestions).stream().map(option -> {

            int suggestionId = getNextSuggestionId();

            currentSuggestions.put(suggestionId, option);

            SharedSuggestion sharedSuggestion = new SharedSuggestion(suggestionId, option.getText(),
                    option.getStyleName().orElse(null));

            option.getIcon().ifPresent(icon -> {
                String key = getResourceKey(suggestionId);
                currentResourcesKeys.add(key);
                setResource(key, icon);
                sharedSuggestion.iconResource = key;
            });

            return sharedSuggestion;

        }).forEach(sharedSuggestions::add);

        getRpcProxy(SearchDropDownClientRpc.class).showSuggestions(query, sharedSuggestions);
    }

    protected int getNextSuggestionId() {
        return suggestionCounter.incrementAndGet();
    }

    protected static String getResourceKey(int suggestionId) {
        return "suggestion-" + suggestionId;
    }

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

    public void addSearchListener(SearchListener<T> listener) {
        searchListeners.add(listener);
    }

    public void removeSearchListener(SearchListener<T> listener) {
        searchListeners.remove(listener);
    }
}
