/**
 * SharedSuggestion.java (SearchDropDown)
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
package org.vaadin.alump.searchdropdown.client.share;

import java.io.Serializable;

/**
 * Suggestions shared from server to client
 */
public class SharedSuggestion implements Serializable {

    public Integer id;
    public String text;
    public String iconResource;
    public String styleName;

    public SharedSuggestion() {

    }

    public SharedSuggestion(Integer id, String text, String styleName) {
        this.id = id;
        this.text = text;
        this.styleName = styleName;
    }

}
