package org.vaadin.alump.searchdropdown;

import java.util.List;

/**
 * Created by alump on 14/04/2017.
 */
public interface SyncSearchSuggestionProvider<T> extends SearchSuggestionProvider<T> {

    default void provideSuggestions(String query, SearchSuggestionPresenter<T> presenter) {
        presenter.showSuggestions(query, getSuggestions(query));
    }

    List<SearchSuggestion<T>> getSuggestions(String query);

}
