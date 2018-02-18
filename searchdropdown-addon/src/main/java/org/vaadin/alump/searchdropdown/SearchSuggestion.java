package org.vaadin.alump.searchdropdown;

import com.vaadin.server.Resource;
import com.vaadin.shared.ui.ContentMode;

import java.io.Serializable;
import java.util.Optional;

/**
 * Suggestion shown for search
 */
public interface SearchSuggestion<T> extends Serializable {

    /**
     * Get text part of suggestion. If content mode is HTML then this text can contain HTML encoding.
     * @return Text part of suggestion
     */
    String getText();

    /**
     * Override this method if getText returns HTML formatted content that does not work
     * in search field. By default this is mapped to getText method.
     * @return Plain text version of getText value
     */
    default String getPlainText() {
        return getText();
    }

    default Optional<Resource> getIcon() {
        return Optional.empty();
    }

    /**
     * Content mode of search text
     * @return Content mode of search text content, defaults to TEXT
     */
    default ContentMode getContentMode() { return ContentMode.TEXT; }

    /**
     * Actual value of this suggestion
     * @return Actual value of suggestion, usually model instance
     */
    T getValue();

    /**
     * Style value added to suggestion
     * @return Get additional style names for given suggestion
     */
    default Optional<String> getStyleName() {
        return Optional.empty();
    }
}
