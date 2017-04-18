package org.vaadin.alump.searchdropdown;

import java.io.Serializable;
import java.util.Optional;

/**
 * Event given when user has entered search
 */
public class SearchEvent<T> implements Serializable {

    private final SearchDropDown<T> source;
    private final String text;
    private final SearchSuggestion<T> suggestion;

    public SearchEvent(SearchDropDown<T> source, String text) {
        this.source = source;
        this.text = text;
        this.suggestion = null;
    }

    public SearchEvent(SearchDropDown<T> source, SearchSuggestion<T> suggestion) {
        this.source = source;
        this.text = null;
        this.suggestion = suggestion;
    }

    public SearchDropDown<T> getSource() {
        return source;
    }

    public String getText() {
        return getSuggestion().map(i -> i.getText()).orElse(text);
    }

    public Optional<SearchSuggestion<T>> getSuggestion() {
        return Optional.ofNullable(suggestion);
    }

    public boolean hasSuggestion() {
        return getSuggestion().isPresent();
    }

}
