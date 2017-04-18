package org.vaadin.alump.searchdropdown.demo;

import com.vaadin.server.ExternalResource;
import com.vaadin.ui.Link;


public class GitHubLink extends Link {

    public static final String LINK_TEXT = "Project's GitHub page (code, issues, documentation)";

    public GitHubLink() {
        super(LINK_TEXT, new ExternalResource("https://github.com/alump/SearchDropDown"));
    }
}
