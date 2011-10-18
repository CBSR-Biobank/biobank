package edu.ualberta.med.biobank.mvp.presenter;

import edu.ualberta.med.biobank.mvp.view.CloseableView;

public interface CloseablePresenter<D extends CloseableView> extends
    Presenter<D> {
    void close();
}
