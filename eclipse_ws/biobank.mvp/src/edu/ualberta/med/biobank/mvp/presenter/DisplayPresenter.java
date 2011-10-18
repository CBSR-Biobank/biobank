package edu.ualberta.med.biobank.mvp.presenter;

import edu.ualberta.med.biobank.mvp.view.DisplayView;

public interface DisplayPresenter<D extends DisplayView> extends
    CloseablePresenter<D>, ReloadablePresenter<D> {
}
