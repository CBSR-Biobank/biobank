package edu.ualberta.med.biobank.mvp.presenter;

import edu.ualberta.med.biobank.mvp.view.ICloseableView;

public interface ICloseablePresenter<D extends ICloseableView> extends
    IPresenter<D> {
    void close();
}
