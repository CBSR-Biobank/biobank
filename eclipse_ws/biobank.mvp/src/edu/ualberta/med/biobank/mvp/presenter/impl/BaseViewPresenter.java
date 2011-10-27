package edu.ualberta.med.biobank.mvp.presenter.impl;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.web.bindery.event.shared.EventBus;

import edu.ualberta.med.biobank.mvp.presenter.IDisplayPresenter;
import edu.ualberta.med.biobank.mvp.view.IDisplayView;

public abstract class BaseViewPresenter<V extends IDisplayView> extends
    BaseCloseablePresenter<V> implements IDisplayPresenter<V> {

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
