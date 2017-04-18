package org.vaadin.alump.searchdropdown;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by alump on 14/04/2017.
 */
public interface SimpleSearchSuggestion extends SearchSuggestion<String> {

    static List<SearchSuggestion<String>> generate(String ... texts) {
        return Arrays.stream(texts).map(s -> new SimpleSearchSuggestion() {
            @Override
            public String getText() {
                return s;
            }
        }).collect(Collectors.toList());
    }

    default String getValue() {
        return getText();
    }
}
