/**
 * SearchSuggestionProvider.java (SearchDropDown)
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

import java.io.Serializable;

/**
 * Interface for suggestions providers
 */
public interface SearchSuggestionProvider<T> extends Serializable {

    /**
     * Method called when suggestions for query is required. Presenter should be always called, or otherwise UI will
     * stay waiting for response. In case of no suggestions just call it with empty list.
     * @param query Query written by user to search field
     * @param presenter Presenter that should be called with suggestions when those are available
     */
    void provideSuggestions(String query, SearchSuggestionPresenter<T> presenter);

    /**
     * Function to override if you use show more results feature of SearchDropDown
     * @param query Query when button was clicked
     */
    default void showMoreResults(String query) {
        throw new RuntimeException("Please override default showMoreResults implementation");
    }

}
