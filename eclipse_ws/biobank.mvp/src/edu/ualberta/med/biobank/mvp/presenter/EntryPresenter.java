package edu.ualberta.med.biobank.mvp.presenter;

import edu.ualberta.med.biobank.mvp.view.FormView;

public interface EntryPresenter<D extends FormView> extends
    DisplayPresenter<D>, SaveablePresenter<D> {
}
