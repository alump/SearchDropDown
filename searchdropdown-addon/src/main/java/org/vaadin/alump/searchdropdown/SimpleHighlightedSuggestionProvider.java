package org.vaadin.alump.searchdropdown;

import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public interface SimpleHighlightedSuggestionProvider extends SimpleSuggestionProvider {

    default List<SearchSuggestion<String>> getSuggestions(String query) {
        Pattern pattern = HighlighedSearchSuggestion.createPattern(query);

        return getSuggestionTexts(query).stream()
                .map(s -> new SimpleHighlightedSuggestion(s, pattern))
                .collect(Collectors.toList());
    }

}

