package edu.ualberta.med.biobank.mvp.presenter.impl;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.web.bindery.event.shared.EventBus;

import edu.ualberta.med.biobank.mvp.presenter.ReloadablePresenter;
import edu.ualberta.med.biobank.mvp.view.ReloadableView;

public abstract class BaseViewPresenter<D extends ReloadableView> extends
    BasePresenter<D> implements ReloadablePresenter<D> {

    @Override
    public void bind(D display, EventBus eventBus) {
        super.bind(display, eventBus);
        doInit();
    }

    /**
     * 
     */
    public abstract void doInit();

    @Override
    public void reload() {
        doInit();
    }

    @Override
    protected void onBind() {
        registerHandler(display.getReload().addClickHandler(
            new ReloadClickHandler()));
    }

    private class ReloadClickHandler implements ClickHandler {
        @Override
        public void onClick(ClickEvent event) {
            doInit();
        }
    }
}
