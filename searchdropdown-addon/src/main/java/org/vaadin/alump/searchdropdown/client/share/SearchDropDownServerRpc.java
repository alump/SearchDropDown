package org.vaadin.alump.searchdropdown.client.share;

import com.vaadin.shared.ui.textfield.AbstractTextFieldServerRpc;

/**
 * Server RPC for SearchDropDown
 */
public interface SearchDropDownServerRpc extends AbstractTextFieldServerRpc {

    void suggestionSelected(int suggestionId);

    void textSelected(String text);

}
