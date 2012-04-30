package edu.ualberta.med.biobank.mvp.presenter;

import edu.ualberta.med.biobank.mvp.view.IEntryFormView;

public interface IEntryFormPresenter<V extends IEntryFormView> extends
    IFormPresenter<V>, ISaveablePresenter<V>, IValidatablePresenter,
    IStatefulPresenter {
}
