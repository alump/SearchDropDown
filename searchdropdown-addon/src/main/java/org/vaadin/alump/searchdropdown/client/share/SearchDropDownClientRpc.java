package org.vaadin.alump.searchdropdown.client.share;

import com.vaadin.shared.communication.ClientRpc;

import java.util.List;

public interface SearchDropDownClientRpc extends ClientRpc {

    void showSuggestions(String value, List<SharedSuggestion> suggestions);

    void hideSuggestions();

}