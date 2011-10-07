package edu.ualberta.med.biobank.mvp.view;

import com.google.gwt.event.dom.client.HasClickHandlers;

/**
 * Currently it is assumed that when a {@link View} is created, it is open.
 * However, it might be useful to add an open method, but perhaps a view is
 * considered open as soon as it is created.
 * 
 * @author jferland
 * 
 */
public interface View {
    /**
     * Close and/or dispose the {@link View} (e.g. tears down and closes the
     * display).
     */
    void close();

    HasClickHandlers getClose();
}
