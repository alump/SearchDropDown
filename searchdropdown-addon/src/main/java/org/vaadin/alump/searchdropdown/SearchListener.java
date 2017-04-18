package org.vaadin.alump.searchdropdown;

import java.io.Serializable;

/**
 * Interface for SearchDropDown listeners. Called when user can selected search suggestion or text.
 */
public interface SearchListener<T> extends Serializable {

    void search(SearchEvent<T> event);

}
