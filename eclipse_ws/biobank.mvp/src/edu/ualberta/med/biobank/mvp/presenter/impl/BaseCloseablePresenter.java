package edu.ualberta.med.biobank.mvp.presenter.impl;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.web.bindery.event.shared.EventBus;

import edu.ualberta.med.biobank.mvp.presenter.CloseablePresenter;
import edu.ualberta.med.biobank.mvp.view.CloseableView;

public abstract class BaseCloseablePresenter<V extends CloseableView> extends
    BasePresenter<V> implements CloseablePresenter<V> {

    public BaseCloseablePresenter(V view, EventBus eventBus) {
        super(view, eventBus);
    }

    @Override
    protected void onBind() {
        registerHandler(view.getClose()
            .addClickHandler(new CloseClickHandler()));
    }

    @Override
    public void close() {
        unbind();
        view.close();
    }

    private class CloseClickHandler implements ClickHandler {
        @Override
        public void onClick(ClickEvent event) {
            close();
        }
    }
}
