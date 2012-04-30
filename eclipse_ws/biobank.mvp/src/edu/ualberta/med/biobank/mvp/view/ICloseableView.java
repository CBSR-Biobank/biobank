package edu.ualberta.med.biobank.mvp.view;

import edu.ualberta.med.biobank.mvp.user.ui.HasButton;

public interface ICloseableView extends IView {
    /**
     * Close and/or dispose the {@link ICloseableView} (e.g. tears down and
     * closes the display).
     */
    void close();

    HasButton getClose();
}
