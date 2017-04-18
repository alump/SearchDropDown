package org.vaadin.alump.searchdropdown;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by alump on 14/04/2017.
 */
public interface SimpleSearchSuggestionProvider extends SyncSearchSuggestionProvider<String> {

    default List<SearchSuggestion<String>> getSuggestions(String query) {
        return getSuggestionTexts(query).stream()
                .map(s -> (SimpleSearchSuggestion) () -> s)
                .collect(Collectors.toList());
     }

    List<String> getSuggestionTexts(String query);

}
