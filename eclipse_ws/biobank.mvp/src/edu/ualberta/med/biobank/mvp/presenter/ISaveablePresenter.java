package edu.ualberta.med.biobank.mvp.presenter;

import edu.ualberta.med.biobank.mvp.view.ISaveableView;

public interface ISaveablePresenter<V extends ISaveableView> extends IPresenter<V> {
    void save();
}
