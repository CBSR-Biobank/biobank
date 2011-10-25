package edu.ualberta.med.biobank.mvp.presenter;

import edu.ualberta.med.biobank.mvp.view.IFormView;

public interface IEntryPresenter<D extends IFormView> extends
    IDisplayPresenter<D>, ISaveablePresenter<D> {
}
