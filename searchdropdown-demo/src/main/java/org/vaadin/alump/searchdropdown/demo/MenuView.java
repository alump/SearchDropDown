package org.vaadin.alump.searchdropdown.demo;

import com.vaadin.navigator.Navigator;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.ExternalResource;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.Link;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

/**
 * Menu of SearchDropDown demo/test application
 */
public class MenuView extends VerticalLayout implements View {

    public final static String VIEW_NAME = "menu";
    private Navigator navigator;

    public MenuView() {
        setWidth(100, Unit.PERCENTAGE);
        setMargin(true);
        setSpacing(true);

        Label header = new Label("SearchDropDown Vaadin add-on");
        header.addStyleName(ValoTheme.LABEL_H1);
        addComponent(header);

        addComponent(new Button("Example View", e -> navigator.navigateTo(ExampleView.VIEW_NAME)));
        addComponent(new Button("Simple Example View", e -> navigator.navigateTo(SimpleView.VIEW_NAME)));
        addComponent(new Button("Testing View", e -> navigator.navigateTo(TestView.VIEW_NAME)));

        addComponent(new GitHubLink());

    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        navigator = event.getNavigator();
    }
}
