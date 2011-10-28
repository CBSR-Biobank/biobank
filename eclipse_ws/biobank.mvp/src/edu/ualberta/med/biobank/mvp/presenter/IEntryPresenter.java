package edu.ualberta.med.biobank.mvp.presenter;

import edu.ualberta.med.biobank.mvp.view.IEntryView;

public interface IEntryPresenter<D extends IEntryView> extends
    IFormPresenter<D>, ISaveablePresenter<D> {
}
