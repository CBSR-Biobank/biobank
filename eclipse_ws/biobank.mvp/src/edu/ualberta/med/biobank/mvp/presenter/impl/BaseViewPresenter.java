package edu.ualberta.med.biobank.mvp.presenter.impl;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.web.bindery.event.shared.EventBus;

import edu.ualberta.med.biobank.mvp.presenter.DisplayPresenter;
import edu.ualberta.med.biobank.mvp.view.DisplayView;

public abstract class BaseViewPresenter<V extends DisplayView> extends
    BaseCloseablePresenter<V> implements DisplayPresenter<V> {

    public BaseViewPresenter(V view, EventBus eventBus) {
        super(view, eventBus);
    }

    public abstract void doReload();

    @Override
    public void reload() {
        doReload();
    }

    @Override
    protected void onBind() {
        registerHandler(view.getReload().addClickHandler(
            new ReloadClickHandler()));
    }

    private class ReloadClickHandler implements ClickHandler {
        @Override
        public void onClick(ClickEvent event) {
            doReload();
        }
    }
}
