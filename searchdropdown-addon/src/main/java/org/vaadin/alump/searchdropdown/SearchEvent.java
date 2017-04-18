/**
 * SearchEvent.java (SearchDropDown)
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
import java.util.Optional;

/**
 * Event given when user has entered search
 */
public class SearchEvent<T> implements Serializable {

    private final SearchDropDown<T> source;
    private final String text;
    private final SearchSuggestion<T> suggestion;

    public SearchEvent(SearchDropDown<T> source, String text) {
        this.source = source;
        this.text = text;
        this.suggestion = null;
    }

    public SearchEvent(SearchDropDown<T> source, SearchSuggestion<T> suggestion) {
        this.source = source;
        this.text = null;
        this.suggestion = suggestion;
    }

    public SearchDropDown<T> getSource() {
        return source;
    }

    public String getText() {
        return getSuggestion().map(i -> i.getText()).orElse(text);
    }

    public Optional<SearchSuggestion<T>> getSuggestion() {
        return Optional.ofNullable(suggestion);
    }

    public boolean hasSuggestion() {
        return getSuggestion().isPresent();
    }

}
