package org.vaadin.alump.searchdropdown;

import com.vaadin.shared.ui.ContentMode;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class HighlighedSearchSuggestion<T> implements SearchSuggestion<T> {

    private Pattern pattern;

    public static Pattern createPattern(String query) {
        if(query == null || query.isEmpty()) {
            return null;
        } else {
            return Pattern.compile("(" + Pattern.quote(query) + ")", Pattern.CASE_INSENSITIVE);
        }
    }

    public static String htmlEscape(String text) {
        return text
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;");
    }

    public static String highlightAndHtmlEscape(String text, Pattern pattern) {
        if(text == null || text.isEmpty()) {
            return "";
        } else if (pattern == null) {
            return htmlEscape(text);
        }

        Matcher matcher = pattern.matcher(text);
        if(!matcher.find()) {
            return htmlEscape(text);
        }

        StringBuilder highlighted = new StringBuilder();
        int lastIndex = 0;

        while(matcher.find(lastIndex)) {
            int start = matcher.start(0);
            if(start > lastIndex) {
                highlighted.append(htmlEscape(text.substring(lastIndex, start)));
            }
            highlighted.append("<span class=\"highlight\">" + matcher.group(0) + "</span>");
            lastIndex = matcher.end(0);
        }

        if(lastIndex < text.length()) {
            highlighted.append(htmlEscape(text.substring(lastIndex)));
        }

        return highlighted.toString();
    }

    public void setQuery(String query) {
        if(query == null || query.isEmpty()) {
            this.pattern = null;
        } else {
            this.pattern = createPattern(query);
        }
    }

    public void setQuery(Pattern pattern) {
        this.pattern = pattern;
    }

    final public String getText() {
        return highlightAndHtmlEscape(getPlainText(), pattern);
    }

    final public ContentMode getContentMode() {
        return ContentMode.HTML;
    }

    @Override
    public abstract T getValue();
}
