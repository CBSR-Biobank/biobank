package edu.ualberta.med.biobank.mvp.presenter.state;

import com.pietschy.gwt.pectin.client.binding.Disposable;
import com.pietschy.gwt.pectin.client.value.ValueHolder;
import com.pietschy.gwt.pectin.client.value.ValueModel;

import edu.ualberta.med.biobank.mvp.presenter.HasState;

public abstract class AbstractState implements HasState, Disposable {
    protected final ValueHolder<Boolean> dirty =
        new ValueHolder<Boolean>(false);

    @Override
    public ValueModel<Boolean> dirty() {
        return dirty;
    }

    protected static <T> boolean areEqual(T one, T two) {
        return one == null ? two == null : one.equals(two);
    }
}
