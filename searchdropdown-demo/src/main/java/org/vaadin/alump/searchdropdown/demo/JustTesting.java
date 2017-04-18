package org.vaadin.alump.searchdropdown.demo;

import com.vaadin.data.provider.GridSortOrder;
import com.vaadin.data.provider.GridSortOrderBuilder;
import com.vaadin.data.provider.ListDataProvider;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.SerializableComparator;
import com.vaadin.ui.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Example of injecting values to grid with list provider
 */
public class JustTesting extends VerticalLayout implements View {

    /**
     * Please ignore, just something I used to test this as a view
     */
    public final static String VIEW_NAME =  "JustTesting";

    /**
     * Initial data set
     */
    private final static String STRINGS[] = new String[] {
            "Lorem", "Ipsum", "Foo", "Bar", "OK", "np"
    };

    private List<ModelItem> myModel = new ArrayList<>();
    private Grid<ModelItem> grid;
    private ListDataProvider<ModelItem> provider;

    /**
     * Just simple bean to be used in this example
     */
    public static class ModelItem {
        private String data;

        public ModelItem(String data) {
            this.data = data;
        }

        /**
         * One bean value
         * @return
         */
        public String getText() {
            return data;
        }

        /**
         * For editor
         * @param text
         * @return
         */
        public void setText(String text) {
            data = text;
        }

        /**
         * Second bean value
         * @return
         */
        public Integer getLength() {
            return data.length();
        }
    }

    /**
     * Example view
     */
    public JustTesting() {

        myModel = new ArrayList<>(Arrays.stream(STRINGS).map(s -> new ModelItem(s)).collect(Collectors.toList()));
        setMargin(true);

        // Just simple insert UI to test this out
        HorizontalLayout inserts = new HorizontalLayout();
        inserts.setSpacing(true);
        addComponent(inserts);
        final TextField field = new TextField();
        Button addButton = new Button("Add", e -> add(field.getValue()));
        inserts.addComponents(field, addButton);

        grid = new Grid<>();
        grid.setWidth(600, Unit.PIXELS);
        grid.setHeight(300, Unit.PIXELS);
        addComponent(grid);

        // List provider used, with initial data set as initial content of myModel list
        provider = new ListDataProvider<>(myModel);
        grid.setDataProvider(provider);

        // Set comparator that will make sure data is shown in correct order
        provider.setSortComparator(comparator);

        // Enabling also editing
        grid.getEditor().setEnabled(true);
        grid.getEditor().addSaveListener(event -> {
            // refresh item does not update order, as in this example also the order can change use refresh all
            provider.refreshAll();
            // In your application, I think provider.refreshItem(event.getBean()) should be enough, as you do not
            // cause order changes in editing
        });

        // Easy way to define columns, also easy way to do it in runtime based on content
        // Using text field to allow editing of text value
        TextField textEditor = new TextField();
        grid.addColumn(item -> item.getText()).setCaption("Text").setEditorComponent(textEditor, ModelItem::setText);
        grid.addColumn(item -> item.getLength()).setCaption("Length");

    }

    /**
     * Example comparator, will first compare length, and only if it's same will compare actual string
     * If your case, you can first compare Acc ID, if those are same, then compare secondary ID (forgot the name of
     * it), this should keep the order you want.
     */
    private SerializableComparator<ModelItem> comparator = (a, b) -> {
        int val = a.getLength().compareTo(b.getLength());
        if(val == 0) {
            val = a.getText().compareTo(b.getText());
        }
        return val;
    };

    private void add(String string) {
        ModelItem item = new ModelItem(string);
        // because list provider is used, just directly inject to list
        myModel.add(item);
        // because list provider is used, always when adding or removing items, refresh all, so change in size is noticed
        provider.refreshAll();
        // As comparator is defined order should be correct automagically
    }

    /**
     * Please ignore this, just part of View implementation
     * @param event
     */
    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {

    }
}
