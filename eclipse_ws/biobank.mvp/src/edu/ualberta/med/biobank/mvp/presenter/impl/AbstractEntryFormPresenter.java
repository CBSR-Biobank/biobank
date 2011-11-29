package edu.ualberta.med.biobank.mvp.presenter.impl;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.web.bindery.event.shared.EventBus;
import com.pietschy.gwt.pectin.client.condition.Condition;
import com.pietschy.gwt.pectin.client.condition.Conditions;
import com.pietschy.gwt.pectin.client.form.validation.HasValidation;

import edu.ualberta.med.biobank.mvp.presenter.HasState;
import edu.ualberta.med.biobank.mvp.presenter.IEntryFormPresenter;
import edu.ualberta.med.biobank.mvp.presenter.model.SimpleViewState;
import edu.ualberta.med.biobank.mvp.presenter.validation.ValidationTree;
import edu.ualberta.med.biobank.mvp.view.IEntryFormView;

public abstract class AbstractEntryFormPresenter<V extends IEntryFormView>
    extends AbstractFormPresenter<V>
    implements IEntryFormPresenter<V> {
    protected final ValidationTree validation = new ValidationTree();
    protected final SimpleViewState viewState = new SimpleViewState();
    private final SaveClickHandler saveClickHandler = new SaveClickHandler();

    @SuppressWarnings("unchecked")
    private final Condition validAndDirty = Conditions.and(viewState.dirty(),
        validation.valid());

    public AbstractEntryFormPresenter(V view, EventBus eventBus) {
        super(view, eventBus);
    }

    @Override
    public HasState getViewState() {
        return viewState;
    }

    @Override
    public HasValidation getValidation() {
        return validation;
    }

    @Override
    public void save() {
        if (Boolean.TRUE.equals(validAndDirty.getValue())) {
            doSave();
        }
    }

    @Override
    protected void onBind() {
        super.onBind();

        registerHandler(view.getSave().addClickHandler(saveClickHandler));
        binder.enable(view.getSave()).when(validAndDirty);
    }

    @Override
    protected void onUnbind() {
        validation.dispose();
    }

    protected abstract void doSave();

    private class SaveClickHandler implements ClickHandler {
        @Override
        public void onClick(ClickEvent event) {
            save();
        }
    }
}
