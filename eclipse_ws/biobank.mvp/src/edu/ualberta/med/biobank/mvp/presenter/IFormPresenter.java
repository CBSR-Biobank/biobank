package edu.ualberta.med.biobank.mvp.presenter;

import edu.ualberta.med.biobank.mvp.view.IFormView;

public interface IFormPresenter<D extends IFormView> extends
    ICloseablePresenter<D>, IReloadablePresenter<D> {
}
