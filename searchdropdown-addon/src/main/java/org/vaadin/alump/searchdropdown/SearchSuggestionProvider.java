package org.vaadin.alump.searchdropdown;

import java.io.Serializable;
import java.util.List;

/**
 * Created by alump on 13/04/2017.
 */
public interface SearchSuggestionProvider<T> extends Serializable {

    void provideSuggestions(String query, SearchSuggestionPresenter<T> presenter);

}
