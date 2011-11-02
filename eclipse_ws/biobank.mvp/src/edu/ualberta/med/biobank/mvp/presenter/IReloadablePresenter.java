package edu.ualberta.med.biobank.mvp.presenter;

import edu.ualberta.med.biobank.mvp.view.IReloadableView;

public interface IReloadablePresenter<V extends IReloadableView> extends IPresenter<V> {
    void reload();
}
