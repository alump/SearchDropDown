package org.vaadin.alump.searchdropdown.client.share;

import java.io.Serializable;

/**
 * Created by alump on 13/04/2017.
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
