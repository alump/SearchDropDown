package org.vaadin.alump.searchdropdown.demo;

import com.vaadin.event.MouseEvents;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.navigator.Navigator;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.Resource;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import org.vaadin.alump.searchdropdown.*;
import org.vaadin.alump.searchdropdown.demo.data.DemoSource;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Example view that shows the basic asynchronous behavior of SearchDropDown
 */
public class ExampleView extends VerticalLayout implements View {

    public final static String VIEW_NAME = "";
    private Navigator navigator;

    /**
     * Example suggestion wrapper for data objects
     */
    public static class ExampleSuggestion extends HighlighedSearchSuggestion<String> {

        private final String text;
        private final Resource icon;
        private final String value;

        public ExampleSuggestion(DemoSource.Data data, Pattern queryPattern) {
            setQuery(queryPattern);
            text = data.toString();
            if(data.getGender() == DemoSource.Gender.FEMALE) {
                icon = VaadinIcons.FEMALE;
            } else {
                icon = VaadinIcons.MALE;
            }
            value = data.toString();
        }

        @Override
        public String getPlainText() {
            return text;
        }

        @Override
        public Optional<Resource> getIcon() {
            return Optional.ofNullable(icon);
        }

        @Override
        public String getValue() {
            return value;
        }
    }

    public ExampleView() {
        setMargin(true);
        setSpacing(true);
        setWidth(100, Unit.PERCENTAGE);

        Label addonInfo = new Label("SearchDropDown add-on is designed to be used when you want to present "
                + "suggestions on drop down list, and you want to resolve suggestions in an asynchronous query.");
        addonInfo.setWidth(100, Unit.PERCENTAGE);

        Label demoInfo = new Label("This is simple example with slow asynchronous query "
            + "(2 seconds) of suggestions. Queries start after you have more 1 letter. Will show max 5 best matches "
            + "to the query. Selection done by enter on text field, or by selecting suggestion.");
        demoInfo.setWidth(100, Unit.PERCENTAGE);

        SearchDropDown<String> peopleSearch = new SearchDropDown<>(githubProvider);
        peopleSearch.setPlaceHolder("Search people by name, city or country");
        peopleSearch.setWidth(600, Unit.PIXELS);
        peopleSearch.addSearchListener(this::performSearch);
        peopleSearch.addClickListener(this::searchClicked);
        peopleSearch.setMoreResultsButton("Show More Results", VaadinIcons.PLUS,
                Arrays.asList(ValoTheme.BUTTON_FRIENDLY));

        addComponents(createNavigationRow(), addonInfo, demoInfo, peopleSearch, new GitHubLink());
    }

    private void searchClicked(MouseEvents.ClickEvent event) {
        Notification.show("User clicked search field icon x:"
                + event.getClientX() + " y:" + event.getClientY());
    }

    private Component createNavigationRow() {
        HorizontalLayout topRow = new HorizontalLayout();
        topRow.setSpacing(true);
        MenuButton menu = new MenuButton(e -> navigator.navigateTo(MenuView.VIEW_NAME));
        Label header = new Label("SearchDropDown Vaadin add-on");
        header.addStyleName(ValoTheme.LABEL_H3);
        topRow.addComponents(menu, header);
        return topRow;
    }

    private void performSearch(SearchEvent<String> event) {
        if(event.isClear()) {
            Notification.show("Search cleared");
        } else if(event.hasSuggestion()) {
            Notification.show("Suggestion '" + event.getSuggestion().get().getValue() + "' selected");
        } else {
            Notification.show("Text '" + event.getText() + "' selected");
        }
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        this.navigator = event.getNavigator();
    }

    private SearchSuggestionProvider<String> githubProvider = new SearchSuggestionProvider<String>() {

        @Override
        public void provideSuggestions(String query, SearchSuggestionPresenter<String> presenter) {

            String trimmed = query.trim();

            if (trimmed.length() < 2) {
                presenter.showSuggestions(query, Collections.EMPTY_LIST, false);
                return;
            }

            Runnable runnable = () -> {
                DemoSource source = new DemoSource();
                // Create pattern already here to optimize (no need to recompile it for each
                // suggestion.
                Pattern pattern = HighlighedSearchSuggestion.createPattern(trimmed);
                List<HighlighedSearchSuggestion<String>> suggestions = source.findData(trimmed).stream()
                        .map(s -> new ExampleSuggestion(s, pattern))
                        .collect(Collectors.toList());
                boolean hasMoreResults = suggestions.size() == DemoSource.MAX_RESULTS;
                if (hasMoreResults) {
                    suggestions.remove(suggestions.size() - 1);
                }
                presenter.showSuggestions(trimmed, suggestions, hasMoreResults);
            };
            Thread thread = new Thread(runnable);
            thread.start();
        };

        @Override
        public void showMoreResults(String query) {
            Notification.show("Not implemented for this demo");

        }
    };
}
