package edu.ualberta.med.biobank.mvp.presenter;

import edu.ualberta.med.biobank.mvp.view.IReloadableView;

public interface IReloadablePresenter<D extends IReloadableView> extends IPresenter<D> {
    void reload();
}
