package org.vaadin.alump.searchdropdown.demo;

import javax.servlet.annotation.WebServlet;

import com.vaadin.annotations.Push;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Title;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.navigator.Navigator;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.*;

@Theme("demo")
@Title("SearchDropDown Add-on Demo")
@Push
@SuppressWarnings("serial")
public class DemoUI extends UI {

    @WebServlet(value = "/*", asyncSupported = true)
    @VaadinServletConfiguration(productionMode = false, ui = DemoUI.class, widgetset = "org.vaadin.alump.searchdropdown.demo.WidgetSet")
    public static class Servlet extends VaadinServlet {
    }

    @Override
    protected void init(VaadinRequest request) {
        Navigator navigator = new Navigator(this, this);
        navigator.addView(MenuView.VIEW_NAME, MenuView.class);
        navigator.addView(TestView.VIEW_NAME, TestView.class);
        navigator.addView(ExampleView.VIEW_NAME, ExampleView.class);
        navigator.addView(SimpleView.VIEW_NAME, SimpleView.class);
        navigator.setErrorView(MenuView.class);
        setNavigator(navigator);
    }

}
