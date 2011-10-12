package edu.ualberta.med.biobank.mvp.presenter;

import edu.ualberta.med.biobank.mvp.view.EntryView;

public interface EntryPresenter<D extends EntryView> extends
    DisplayPresenter<D>, SaveablePresenter<D> {
}
