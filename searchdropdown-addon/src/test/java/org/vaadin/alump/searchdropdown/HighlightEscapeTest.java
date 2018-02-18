package org.vaadin.alump.searchdropdown;

import junit.framework.Assert;
import org.junit.Test;

import java.util.regex.Pattern;

public class HighlightEscapeTest {

    @Test
    public void simpleHighlight() {
        String source = "Hello World";
        String query = "wor";
        Pattern pattern = SimpleHighlightedSuggestion.createPattern(query);

        String escaped = SimpleHighlightedSuggestion.highlightAndHtmlEscape(source, pattern);
        Assert.assertEquals("Hello <span class=\"highlight\">Wor</span>ld", escaped);
    }

    @Test
    public void highlightAtStart() {
        String source = "Hello World";
        String query = "hel";
        Pattern pattern = SimpleHighlightedSuggestion.createPattern(query);

        String escaped = SimpleHighlightedSuggestion.highlightAndHtmlEscape(source, pattern);
        Assert.assertEquals("<span class=\"highlight\">Hel</span>lo World", escaped);
    }

    @Test
    public void highlightAtEnd() {
        String source = "Hello World";
        String query = "ld";
        Pattern pattern = SimpleHighlightedSuggestion.createPattern(query);

        String escaped = SimpleHighlightedSuggestion.highlightAndHtmlEscape(source, pattern);
        Assert.assertEquals("Hello Wor<span class=\"highlight\">ld</span>", escaped);
    }

    @Test
    public void noHit() {
        String source = "Hello World";
        String query = "Goodbye";
        Pattern pattern = SimpleHighlightedSuggestion.createPattern(query);

        String escaped = SimpleHighlightedSuggestion.highlightAndHtmlEscape(source, pattern);
        Assert.assertEquals("Hello World", escaped);
    }

    @Test
    public void htmlCharactersNotHit() {
        String source = "<b>Hello World</b>";
        String query = "Goodbye";
        Pattern pattern = SimpleHighlightedSuggestion.createPattern(query);

        String escaped = SimpleHighlightedSuggestion.highlightAndHtmlEscape(source, pattern);
        Assert.assertEquals("&lt;b&gt;Hello World&lt;/b&gt;", escaped);
    }

    @Test
    public void htmlCharactersWithHit() {
        String source = "<b>Hello World</b>";
        String query = "o wo";
        Pattern pattern = SimpleHighlightedSuggestion.createPattern(query);

        String escaped = SimpleHighlightedSuggestion.highlightAndHtmlEscape(source, pattern);
        Assert.assertEquals("&lt;b&gt;Hell<span class=\"highlight\">o Wo</span>rld&lt;/b&gt;", escaped);
    }

    @Test
    public void htmlCharactersWithHit2() {
        String source = "<b>Hello & World</b>";
        String query = "o";
        Pattern pattern = SimpleHighlightedSuggestion.createPattern(query);

        String escaped = SimpleHighlightedSuggestion.highlightAndHtmlEscape(source, pattern);
        Assert.assertEquals("&lt;b&gt;Hell<span class=\"highlight\">o</span> &amp; W<span class=\"highlight\">o</span>rld&lt;/b&gt;", escaped);
    }

    @Test
    public void emptyQuery() {
        String source = "Hello World";
        String query = "";
        Pattern pattern = SimpleHighlightedSuggestion.createPattern(query);

        String escaped = SimpleHighlightedSuggestion.highlightAndHtmlEscape(source, pattern);
        Assert.assertEquals("Hello World", escaped);
    }

    @Test
    public void nullQuery() {
        String source = "Hello World";
        String query = null;
        Pattern pattern = SimpleHighlightedSuggestion.createPattern(query);

        String escaped = SimpleHighlightedSuggestion.highlightAndHtmlEscape(source, pattern);
        Assert.assertEquals("Hello World", escaped);
    }
}
