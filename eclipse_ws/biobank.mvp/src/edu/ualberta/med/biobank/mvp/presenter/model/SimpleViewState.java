package edu.ualberta.med.biobank.mvp.presenter.model;

import com.pietschy.gwt.pectin.client.condition.DelegatingCondition;
import com.pietschy.gwt.pectin.client.value.ValueModel;

import edu.ualberta.med.biobank.mvp.presenter.HasState;

public class SimpleViewState implements HasState {
    private final DelegatingCondition dirty = new DelegatingCondition(false);

    @Override
    public ValueModel<Boolean> dirty() {
        return dirty;
    }

    @Override
    public void checkpoint() {
        dirty.setValue(false);
    }

    @Override
    public void revert() {
    }
}
