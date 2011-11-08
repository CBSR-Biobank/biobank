package edu.ualberta.med.biobank.mvp.presenter;

import edu.ualberta.med.biobank.mvp.view.ICloseableView;

public interface ICloseablePresenter<V extends ICloseableView> extends
    IPresenter<V> {
    void close();
}
