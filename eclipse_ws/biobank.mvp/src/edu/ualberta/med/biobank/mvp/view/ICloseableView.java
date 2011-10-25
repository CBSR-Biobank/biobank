package edu.ualberta.med.biobank.mvp.view;

import com.google.gwt.event.dom.client.HasClickHandlers;

public interface ICloseableView extends IView {
    /**
     * Close and/or dispose the {@link ICloseableView} (e.g. tears down and
     * closes the display).
     */
    void close();

    HasClickHandlers getClose();
}
