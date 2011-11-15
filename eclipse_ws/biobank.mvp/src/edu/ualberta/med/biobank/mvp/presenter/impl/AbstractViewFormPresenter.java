package edu.ualberta.med.biobank.mvp.presenter.impl;

import com.google.web.bindery.event.shared.EventBus;

import edu.ualberta.med.biobank.mvp.view.IViewFormView;

public abstract class AbstractViewFormPresenter<V extends IViewFormView>
    extends AbstractFormPresenter<V> {
    public AbstractViewFormPresenter(V view, EventBus eventBus) {
        super(view, eventBus);
    }
}
