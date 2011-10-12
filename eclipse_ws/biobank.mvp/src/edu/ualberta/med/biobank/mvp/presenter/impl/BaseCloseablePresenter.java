package edu.ualberta.med.biobank.mvp.presenter.impl;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;

import edu.ualberta.med.biobank.mvp.presenter.CloseablePresenter;
import edu.ualberta.med.biobank.mvp.view.CloseableView;

public abstract class BaseCloseablePresenter<D extends CloseableView> extends
    BasePresenter<D> implements CloseablePresenter<D> {

    @Override
    protected void onBind() {
        registerHandler(display.getClose().addClickHandler(
            new CloseClickHandler()));
    }

    @Override
    public void close() {
        display.close();
    }

    private class CloseClickHandler implements ClickHandler {
        @Override
        public void onClick(ClickEvent event) {
            close();
        }
    }
}
