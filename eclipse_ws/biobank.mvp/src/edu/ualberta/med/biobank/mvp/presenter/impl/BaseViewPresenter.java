package edu.ualberta.med.biobank.mvp.presenter.impl;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;

import edu.ualberta.med.biobank.mvp.presenter.DisplayPresenter;
import edu.ualberta.med.biobank.mvp.view.DisplayView;

public abstract class BaseViewPresenter<D extends DisplayView> extends
    BaseCloseablePresenter<D> implements DisplayPresenter<D> {

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
