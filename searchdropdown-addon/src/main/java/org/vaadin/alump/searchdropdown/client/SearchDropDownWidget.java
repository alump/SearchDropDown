package org.vaadin.alump.searchdropdown.client;

import com.google.gwt.aria.client.Roles;
import com.google.gwt.dom.client.*;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.user.client.*;
import com.google.gwt.user.client.ui.*;
import com.vaadin.client.ui.SubPartAware;
import com.vaadin.client.ui.VOverlay;
import com.vaadin.client.ui.menubar.MenuBar;
import com.vaadin.client.ui.menubar.MenuItem;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * GWT widget implementing search field
 */
public class SearchDropDownWidget extends Composite {

    private final FlowPanel panel = new FlowPanel();

    private DivElement iconElement;
    private DivElement clearElement;
    private SearchField textField;
    private SuggestionPopup popup;
    private boolean enabled = true;
    private boolean readonly = false;
    private boolean showClear = true;
    private SuggestionProvider suggestionProvider;
    private List<SelectionSelectionListener> selectionListeners = new ArrayList<>();

    public static final String LOADING_SUGGESTIONS_STYLENAME = "loading-suggestions";
    public static final String WITH_CLEAR_STYLENAME = "with-clear";

    private final static int[] IGNORE_KEY_UPS = { KeyCodes.KEY_ENTER, KeyCodes.KEY_TAB, KeyCodes.KEY_SHIFT,
            KeyCodes.KEY_CTRL, KeyCodes.KEY_ALT, KeyCodes.KEY_DOWN, KeyCodes.KEY_UP, KeyCodes.KEY_PAGEDOWN,
            KeyCodes.KEY_PAGEUP, KeyCodes.KEY_ESCAPE };

    public static class Suggestion {
        private int id;
        private String html;
        private String styleName;

        public Suggestion(int id, String htmlContent, String styleName) {
            this.id = id;
            this.html = htmlContent;
            this.styleName = styleName;
        }
    }

    public interface SuggestionProvider {
        void valueChanged(String searchValue);
    }

    public interface SelectionSelectionListener {
        void searchSelection(Integer suggestionId, String text);
    }

    public SearchDropDownWidget() {
        textField = new SearchField();
        textField.addKeyDownHandler(this::onKeyDown);
        textField.addKeyUpHandler(this::onKeyUp);
        textField.addFocusHandler(this::onFocus);
        textField.addBlurHandler(this::onBlur);
        panel.add(textField);

        initWidget(panel);

        iconElement = Document.get().createDivElement();
        iconElement.addClassName("search-dropdown-icon");
        getElement().appendChild(iconElement);

        clearElement = Document.get().createDivElement();
        clearElement.addClassName("search-dropdown-clear");
        getElement().appendChild(clearElement);
        DOM.sinkEvents(clearElement, Event.ONCLICK);
        DOM.setEventListener(clearElement, this::clearClicked);

        addStyleName("search-dropdown");
    }

    private void clearClicked(Event event) {
        textField.setValue("");
        suggestionProvider.valueChanged("");
    }

    protected void onDetach() {
        hideSuggestions();
        super.onDetach();
    }

    public void addSuggestionSelectionListener(SelectionSelectionListener listener) {
        selectionListeners.add(listener);
    }

    public void removeSuggestionSelectionListener(SelectionSelectionListener listener) {
        selectionListeners.remove(listener);
    }

    public void setSuggestionProvider(SuggestionProvider provider) {
        suggestionProvider = provider;
    }

    private void checkClearVisibility(String value) {
        if(value.isEmpty()) {
            removeStyleName(WITH_CLEAR_STYLENAME);
        } else if(showClear) {
            addStyleName(WITH_CLEAR_STYLENAME);
        }
    }

    private void onBlur(BlurEvent event) {
        removeStyleName("on-focus");
        checkClearVisibility(textField.getValue());
    }

    private void onFocus(FocusEvent event) {
        addStyleName("on-focus");
    }

    private void onKeyUp(KeyUpEvent event) {
        if(enabled && !readonly && suggestionProvider != null) {
            int keyCode = event.getNativeKeyCode();
            if(!Arrays.stream(IGNORE_KEY_UPS).filter(k -> k == keyCode).findFirst().isPresent()) {
                suggestionProvider.valueChanged(textField.getValue());
            }
        }
    }

    private void onKeyDown(KeyDownEvent event) {
        if(popup != null && popup.isVisible()) {
            popupKeyDown(event);
        }
    }

    private void popupKeyDown(KeyDownEvent event) {
        switch (event.getNativeKeyCode()) {
            case KeyCodes.KEY_DOWN:
                popup.selectNextItem();
                event.preventDefault();
                event.stopPropagation();
                break;
            case KeyCodes.KEY_UP:
                popup.selectPrevItem();
                event.preventDefault();
                event.stopPropagation();
                break;
            case KeyCodes.KEY_ESCAPE:
                popup.hide();
                event.preventDefault();
                event.stopPropagation();
                break;
            case KeyCodes.KEY_ENTER:
                executeSelected();
            case KeyCodes.KEY_TAB:
                popup.hide();
                event.stopPropagation();
                break;
        }

    }

    private void executeSelected() {
        if(popup.hasSelection()) {
            popup.executeSelected();
        } else {
            selectionListeners.forEach(l -> l.searchSelection(null, textField.getValue()));
        }
    }

    public void setPlaceholder(String placeholder) {
        textField.setPlaceHolder(placeholder);
    }

    public void setText(String text) {
        textField.setValue(text);
        checkClearVisibility(textField.getValue());
    }

    public String getText() {
        return textField.getValue();
    }

    public void setShowClear(boolean showClear) {
        this.showClear = showClear;
        checkClearVisibility(textField.getValue());
    }

    public void setMaxLength(int maxLength) {
        if(maxLength > 0) {
            textField.setMaxLength(maxLength);
        }
    }

    public void showSuggestions(List<Suggestion> suggestions) {
        SuggestionPopup popup = getPopup();
        popup.setSuggestions(suggestions);

        if(suggestions.isEmpty()) {
            popup.hide();
        } else {
            popup.showRelativeTo(this);
        }
    }

    public void hideSuggestions() {
        if(popup != null) {
            popup.hide();
        }
    }

    protected SuggestionPopup getPopup() {
        if(popup == null) {
            popup = new SuggestionPopup();
        }
        return popup;
    }

    public class SuggestionMenu extends MenuBar implements SubPartAware, LoadHandler {

        SuggestionMenu() {
            super(true);

            addStyleName("search-dropdown-menu");
            addDomHandler(this, LoadEvent.getType());

            setScrollEnabled(true);
        }

        @Override
        public void onLoad(LoadEvent event) {

        }

        @Override
        public int getPreferredHeight() {
            return super.getPreferredHeight();
        }

        @Override
        public com.google.gwt.user.client.Element getSubPartElement(String subPart) {
            return null;
        }

        @Override
        public String getSubPartName(com.google.gwt.user.client.Element subElement) {
            return null;
        }

        private void suggestionClicked(int suggestionId) {
            selectionListeners.forEach(l -> l.searchSelection(suggestionId, null));
        }

        public void addSuggestion(Suggestion suggestion) {
            final com.vaadin.client.ui.menubar.MenuItem item = new com.vaadin.client.ui.menubar.MenuItem(
                    suggestion.html, true, () -> suggestionClicked(suggestion.id));

            if(suggestion.styleName != null) {
                item.setStyleName(suggestion.styleName);
            }

            addItem(item);
        }

        public void executeSelected() {
            MenuItem item = getSelectedItem();
            if(item != null) {
                item.getCommand().execute();
            }
        }
    }

    public class SuggestionPopup extends VOverlay
            implements PopupPanel.PositionCallback, CloseHandler<PopupPanel> {

        private static final int Z_INDEX = 30000;
        private SuggestionMenu menu;

        public SuggestionPopup() {
            super(true, false);

            addStyleName("search-dropdown-popup");

            setOwner(SearchDropDownWidget.this);
            menu = new SuggestionMenu();
            setWidget(menu);

            getElement().getStyle().setZIndex(Z_INDEX);

            final Element root = getContainerElement();

            DOM.sinkEvents(root, Event.ONMOUSEDOWN | Event.ONMOUSEWHEEL);
            addCloseHandler(this);

            Roles.getListRole().set(getElement());

            setPreviewingAllNativeEvents(true);
        }

        @Override
        public void onClose(CloseEvent<PopupPanel> event) {
            SearchDropDownWidget.this.popup = null;
        }

        @Override
        public void setPosition(int offsetWidth, int offsetHeight) {

            int heightPx = menu.getPreferredHeight();
            if(heightPx < 10) {
                // this is work around, should not happen, remove later
                heightPx = 150;
            }

            setHeight(heightPx + "px");

            setPopupPosition(0, 0);
        }

        public void clearSuggestions() {
            menu.clearItems();
        }

        public void setSuggestions(List<Suggestion> suggestions) {
            clearSuggestions();
            suggestions.forEach(suggestion -> addSuggestion(suggestion));

            SearchDropDownWidget widget = SearchDropDownWidget.this;
            int widthPx = widget.getElement().getClientWidth();
            setWidth(widthPx + "px");
        }

        public void addSuggestion(Suggestion suggestion) {
            menu.addSuggestion(suggestion);
        }

        public void selectNextItem() {
            final int index = menu.getSelectedIndex() + 1;
            if (menu.getItems().size() > index) {
                menu.selectItem(menu.getItems().get(index));
            }
        }

        public void selectPrevItem() {
            final int index = menu.getSelectedIndex() - 1;
            if (index >= 0) {
                menu.selectItem(menu.getItems().get(index));
            }
        }

        public void executeSelected() {
            menu.executeSelected();
        }

        public boolean hasSelection() {
            return menu.getSelectedItem() != null;
        }
    }

    public class SearchField extends TextBox {

        protected SearchField() {
            addStyleName("search-dropdown-field");
            getElement().setAttribute("autocomplete", "nope");
        }

        public void setPlaceHolder(String placeholder) {
            if(placeholder != null) {
                getElement().setAttribute("placeholder", placeholder);
            } else {
                getElement().removeAttribute("placeholder");
            }
        }
    }

    public void setLoadingSuggestions(boolean loading) {
        if(loading) {
            addStyleName(LOADING_SUGGESTIONS_STYLENAME);
            if(popup != null) {
                popup.getElement().addClassName(LOADING_SUGGESTIONS_STYLENAME);
            }
        } else {
            removeStyleName(LOADING_SUGGESTIONS_STYLENAME);
            if(popup != null) {
                popup.getElement().removeClassName(LOADING_SUGGESTIONS_STYLENAME);
            }
        }
    }

}