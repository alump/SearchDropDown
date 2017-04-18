package org.vaadin.alump.searchdropdown.demo;

import com.vaadin.navigator.Navigator;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.ui.Notification;
import com.vaadin.ui.VerticalLayout;
import org.vaadin.alump.searchdropdown.SimpleSearchDropDown;
import org.vaadin.alump.searchdropdown.SimpleSuggestionProvider;

import java.util.Arrays;
import java.util.Collections;
import java.util.stream.Collectors;

/**
 * Simple example view. This is just API example, usually in cases like this you would want to use ComboBox that allows
 * new values.
 */
public class SimpleView extends VerticalLayout implements View {

    public final static String VIEW_NAME = "simple";
    private Navigator navigator;

    public static final String VALUES[] = { "alfa", "bravo", "charlie", "delta", "echo", "foxtrot", "golf", "hotel",
            "india", "juliett", "kilo", "lima", "mike", "november", "oscar", "papa", "quebec", "romea", "sierra",
            "tango", "uniform", "victor", "whiskey", "xray", "yankee", "zulu" };

    public SimpleView() {
        setWidth(100, Unit.PERCENTAGE);
        setMargin(true);
        setSpacing(true);

        addComponent(new MenuButton(e -> navigator.navigateTo(MenuView.VIEW_NAME)));

        SimpleSearchDropDown simpleSearch = new SimpleSearchDropDown(provider);
        simpleSearch.setWidth(500, Unit.PIXELS);
        simpleSearch.setPlaceHolder("Search Phonetic Alphabets");
        simpleSearch.addSearchListener(e -> Notification.show("User selected: " + e.getText()));
        addComponent(simpleSearch);
    }

    SimpleSuggestionProvider provider = (query) -> {
        // For empty query, do not provide any suggestions
        final String cleaned = query.toLowerCase().trim();
        if(cleaned.isEmpty()) {
            return Collections.EMPTY_LIST;
        }

        // Normally here you would perform database or REST query or queries, to find suitable suggestions.
        // Simple API is always synchronous, so when you want to go to asynchronous use base class.
        return Arrays.stream(VALUES).filter(v -> v.contains(cleaned)).collect(Collectors.toList());
    };

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        navigator = event.getNavigator();
    }
}
