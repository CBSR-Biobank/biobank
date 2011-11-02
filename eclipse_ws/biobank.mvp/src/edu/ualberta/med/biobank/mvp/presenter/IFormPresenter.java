package edu.ualberta.med.biobank.mvp.presenter;

import edu.ualberta.med.biobank.mvp.view.IFormView;

public interface IFormPresenter<V extends IFormView> extends
    ICloseablePresenter<V>, IReloadablePresenter<V> {
}
