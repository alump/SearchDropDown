package org.vaadin.alump.searchdropdown;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class SimpleHighlightedSuggestion extends HighlighedSearchSuggestion<String> {

    public static List<SearchSuggestion<String>> generate(String query, String ... texts) {
        Pattern pattern = HighlighedSearchSuggestion.createPattern(query);
        return Arrays.stream(texts)
                .map(s -> new SimpleHighlightedSuggestion(s, pattern))
                .collect(Collectors.toList());
    }

    private final String rawText;

    public SimpleHighlightedSuggestion(String rawText, Pattern pattern) {
        this.rawText = rawText;
        setQuery(pattern);
    }

    public SimpleHighlightedSuggestion(String rawText, String query) {
        this(rawText, HighlighedSearchSuggestion.createPattern(query));
    }

    @Override
    public String getPlainText() {
        return rawText;
    }

    public String getValue() {
        return getPlainText();
    }
}
