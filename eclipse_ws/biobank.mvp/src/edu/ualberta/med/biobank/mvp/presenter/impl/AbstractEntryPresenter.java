package edu.ualberta.med.biobank.mvp.presenter.impl;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.web.bindery.event.shared.EventBus;

import edu.ualberta.med.biobank.mvp.presenter.ISaveablePresenter;
import edu.ualberta.med.biobank.mvp.view.IEntryView;

public abstract class AbstractEntryPresenter<V extends IEntryView> extends
    AbstractFormPresenter<V> implements ISaveablePresenter<V> {

    public AbstractEntryPresenter(V view, EventBus eventBus) {
        super(view, eventBus);
    }

    @Override
    public void save() {
        doSave();
    }

    @Override
    protected void onBind() {
        super.onBind();

        registerHandler(view.getSave().addClickHandler(new SaveClickHandler()));
    }

    protected abstract void doSave();

    private class SaveClickHandler implements ClickHandler {
        @Override
        public void onClick(ClickEvent event) {
            save();
        }
    }
}
