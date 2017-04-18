package org.vaadin.alump.searchdropdown;

import java.util.List;

/**
 * Created by alump on 14/04/2017.
 */
public interface SearchSuggestionPresenter<T> {

    void showSuggestions(String query, List<SearchSuggestion<T>> suggestions);
}
