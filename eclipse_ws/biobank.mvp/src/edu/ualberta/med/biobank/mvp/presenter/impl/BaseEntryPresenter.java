package edu.ualberta.med.biobank.mvp.presenter.impl;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;

import edu.ualberta.med.biobank.mvp.presenter.SaveablePresenter;
import edu.ualberta.med.biobank.mvp.view.EntryView;

public abstract class BaseEntryPresenter<D extends EntryView> extends
    BaseViewPresenter<D> implements SaveablePresenter<D> {

    @Override
    public void save() {
        doSave();
    }

    @Override
    protected void onBind() {
        super.onBind();

        registerHandler(display.getSave().addClickHandler(
            new SaveClickHandler()));
    }

    protected abstract void doSave();

    private class SaveClickHandler implements ClickHandler {
        @Override
        public void onClick(ClickEvent event) {
            save();
        }
    }
}
