package edu.ualberta.med.biobank.mvp.presenter.impl;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;

import edu.ualberta.med.biobank.mvp.presenter.Presenter;
import edu.ualberta.med.biobank.mvp.view.CloseableView;

public class BaseCloseablePresenter<D extends CloseableView> extends
    BasePresenter<D> implements Presenter<D> {

    @Override
    protected void onBind() {
        registerHandler(display.getClose().addClickHandler(
            new CloseClickHandler()));
    }

    @Override
    protected void onUnbind() {
    }

    private class CloseClickHandler implements ClickHandler {
        @Override
        public void onClick(ClickEvent event) {
            display.close();
        }
    }
}
