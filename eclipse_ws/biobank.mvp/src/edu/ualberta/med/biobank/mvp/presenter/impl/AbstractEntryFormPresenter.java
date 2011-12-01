package edu.ualberta.med.biobank.mvp.presenter.impl;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.web.bindery.event.shared.EventBus;
import com.pietschy.gwt.pectin.client.condition.Condition;
import com.pietschy.gwt.pectin.client.condition.Conditions;
import com.pietschy.gwt.pectin.client.form.validation.HasValidation;

import edu.ualberta.med.biobank.mvp.exception.InitPresenterException;
import edu.ualberta.med.biobank.mvp.presenter.HasState;
import edu.ualberta.med.biobank.mvp.presenter.IEntryFormPresenter;
import edu.ualberta.med.biobank.mvp.presenter.state.ModelState;
import edu.ualberta.med.biobank.mvp.presenter.validation.ValidationTree;
import edu.ualberta.med.biobank.mvp.view.IEntryFormView;

public abstract class AbstractEntryFormPresenter<V extends IEntryFormView>
    extends AbstractFormPresenter<V>
    implements IEntryFormPresenter<V> {
    protected final ValidationTree validation = new ValidationTree();
    protected final ModelState state = new ModelState();
    private final SaveClickHandler saveClickHandler = new SaveClickHandler();

    @SuppressWarnings("unchecked")
    private final Condition validAndDirty = Conditions.and(state.dirty(),
        validation.valid());

    public AbstractEntryFormPresenter(V view, EventBus eventBus) {
        super(view, eventBus);
    }

    @Override
    public HasState getState() {
        return state;
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

        state.addView(view);
        validation.bindValidationTo(view);
    }

    @Override
    protected void onUnbind() {
        validation.dispose();
        state.dispose();
    }

    @Override
    protected V load(Loadable loadable) throws InitPresenterException {
        super.load(loadable);

        state.checkpoint();

        return view;
    }

    protected abstract void doSave();

    private class SaveClickHandler implements ClickHandler {
        @Override
        public void onClick(ClickEvent event) {
            save();
        }
    }
}
