package edu.ualberta.med.biobank.mvp.presenter;

import edu.ualberta.med.biobank.mvp.view.SaveableView;

public interface SaveablePresenter<D extends SaveableView> extends
    ReloadablePresenter<D> {
    void save();
}
