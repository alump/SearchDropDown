/**
 * SearchSuggestionPresenter.java (SearchDropDown)
 *
 * Copyright 2017 Vaadin Ltd, Sami Viitanen <sami.viitanen@vaadin.org>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.vaadin.alump.searchdropdown;

import java.util.List;

/**
 * Interface for search suggestion presenters
 * @param <T> Type of suggestions used in SearchDropDown
 */
public interface SearchSuggestionPresenter<T> {

    /**
     * Ask to show given suggestions. This method is safe to be called outside main UI context / thread.
     * @param query Query earlier given to SearchSuggestionProvider
     * @param suggestions Suggestions to be shown
     * @param hasMoreResults If true and show more results button is define there will be button at the end of list
     */
    void showSuggestions(String query, List<? extends SearchSuggestion<T>> suggestions, boolean hasMoreResults);


    @Deprecated
    default void showSuggestions(String query, List<? extends SearchSuggestion<T>> suggestions) {
        showSuggestions(query, suggestions, false);
    }
}
