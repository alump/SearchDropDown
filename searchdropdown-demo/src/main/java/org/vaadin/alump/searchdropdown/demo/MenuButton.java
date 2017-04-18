package org.vaadin.alump.searchdropdown.demo;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.ui.Button;

/**
 * Just a menu button used to navigate back to menu
 */
public class MenuButton extends Button {

    public MenuButton(Button.ClickListener listener) {
        super(null, VaadinIcons.MENU);
        addClickListener(listener);
    }
}
