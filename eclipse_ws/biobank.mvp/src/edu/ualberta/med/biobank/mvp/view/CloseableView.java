package edu.ualberta.med.biobank.mvp.view;

import com.google.gwt.event.dom.client.HasClickHandlers;

public interface CloseableView extends BaseView {
    /**
     * Close and/or dispose the {@link CloseableView} (e.g. tears down and
     * closes the display).
     */
    void close();

    HasClickHandlers getClose();
}
