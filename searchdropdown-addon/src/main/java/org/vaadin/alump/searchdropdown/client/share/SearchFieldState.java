package org.vaadin.alump.searchdropdown.client.share;

import com.vaadin.shared.AbstractFieldState;
import com.vaadin.shared.annotations.DelegateToWidget;
import com.vaadin.shared.annotations.NoLayout;
import com.vaadin.shared.ui.ValueChangeMode;

public class SearchFieldState extends AbstractFieldState {

    /**
     * Maximum character count in text field.
     */
    @DelegateToWidget
    @NoLayout
    public int maxLength = -1;

    /**
     * The prompt to display in an empty field. Null when disabled.
     */
    @DelegateToWidget
    @NoLayout
    public String placeholder = null;

    /**
     * The text in the field.
     */
    @NoLayout
    public String text = "";

    @NoLayout
    public ValueChangeMode valueChangeMode = ValueChangeMode.LAZY;

    @NoLayout
    public int valueChangeTimeout = 400;

    @DelegateToWidget
    @NoLayout
    public boolean showClear = true;

}