package org.vaadin.alump.searchdropdown;

import com.vaadin.server.Resource;

import java.io.Serializable;
import java.util.Optional;

/**
 * Created by alump on 13/04/2017.
 */
public interface SearchSuggestion<T> extends Serializable {

    String getText();

    default Optional<Resource> getIcon() {
        return Optional.empty();
    }

    T getValue();

    default Optional<String> getStyleName() {
        return Optional.empty();
    }
}
