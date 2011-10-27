package edu.ualberta.med.biobank.mvp.presenter;

import edu.ualberta.med.biobank.mvp.view.ISaveableView;

public interface ISaveablePresenter<D extends ISaveableView> extends IPresenter<D> {
    void save();
}
