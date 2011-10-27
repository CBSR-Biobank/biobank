package edu.ualberta.med.biobank.mvp.presenter;

import edu.ualberta.med.biobank.mvp.view.IDisplayView;

public interface IDisplayPresenter<D extends IDisplayView> extends
    ICloseablePresenter<D>, IReloadablePresenter<D> {
}
