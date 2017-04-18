package org.vaadin.alump.searchdropdown.demo;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.navigator.Navigator;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.ui.*;
import org.vaadin.alump.searchdropdown.SearchDropDown;
import org.vaadin.alump.searchdropdown.SimpleSearchSuggestionProvider;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by alump on 14/04/2017.
 */
public class TestView extends HorizontalLayout implements View {

    public final static String VIEW_NAME = "test";

    private SearchDropDown<String> searchField;
    private Navigator navigator;

    public TestView() {
        setWidth(100, Unit.PERCENTAGE);
        setMargin(true);
        setSpacing(true);

        VerticalLayout leftColumn = new VerticalLayout();
        leftColumn.setSpacing(true);
        leftColumn.setWidth(100, Unit.PERCENTAGE);

        VerticalLayout rightColumn = new VerticalLayout();
        rightColumn.setSpacing(true);
        rightColumn.setWidth(100, Unit.PERCENTAGE);

        addComponents(leftColumn, rightColumn);
        setExpandRatio(leftColumn, 1f);
        setExpandRatio(rightColumn, 1f);

        searchField = new SearchDropDown<String>(provider);
        searchField.setCaption("SearchDropDown");
        searchField.setPlaceHolder("Write your search here");
        searchField.setWidth(100, Unit.PERCENTAGE);
        leftColumn.addComponent(searchField);

        leftColumn.addComponent(new Button("Clear value", e -> searchField.setValue(null)));
        leftColumn.addComponent(new Button("Set value", e -> searchField.setValue("Lorem Ipsum")));

        TextField textField = new TextField();
        textField.setCaption("TextField (to compare look'n feel)");
        textField.setPlaceholder("Placeholder");
        textField.setWidth(100, Unit.PERCENTAGE);
        rightColumn.addComponent(textField);

        ComboBox comboBox = new ComboBox();
        comboBox.setCaption("ComboBox (to compare look'n feel)");
        comboBox.setWidth(100, Unit.PERCENTAGE);
        comboBox.setItems("Lorem", "Ipsum");
        rightColumn.addComponent(comboBox);

        CheckBox showClear = new CheckBox("Show clear", true);
        showClear.addValueChangeListener(e -> searchField.setShowClear(e.getValue()));
        rightColumn.addComponent(showClear);

        Button menuButton = new Button(null, VaadinIcons.MENU);
        menuButton.addClickListener(e -> navigator.navigateTo(MenuView.VIEW_NAME));
        rightColumn.addComponent(menuButton);
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        navigator = event.getNavigator();
    }

    private SimpleSearchSuggestionProvider provider = query -> {
        List<String> results = new ArrayList<>();
        if(query.isEmpty()) {
            return results;
        }
        results.add("Foo");
        results.add("Bar");
        results.add("Lorem");
        results.add("Ipsum");
        return results;
    };
}
