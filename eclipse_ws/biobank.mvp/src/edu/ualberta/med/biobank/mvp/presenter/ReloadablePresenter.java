package edu.ualberta.med.biobank.mvp.presenter;

import edu.ualberta.med.biobank.mvp.view.ReloadableView;

public interface ReloadablePresenter<D extends ReloadableView> extends Presenter<D> {
    void reload();
}
