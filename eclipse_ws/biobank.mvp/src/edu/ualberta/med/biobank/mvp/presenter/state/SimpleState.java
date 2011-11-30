package edu.ualberta.med.biobank.mvp.presenter.state;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.google.gwt.user.client.ui.HasValue;
import com.pietschy.gwt.pectin.client.binding.Disposable;
import com.pietschy.gwt.pectin.client.condition.OrFunction;
import com.pietschy.gwt.pectin.client.condition.ReducingCondition;
import com.pietschy.gwt.pectin.client.value.ValueModel;

import edu.ualberta.med.biobank.mvp.presenter.HasState;
import edu.ualberta.med.biobank.mvp.presenter.IStatefulPresenter;
import edu.ualberta.med.biobank.mvp.user.ui.HasList;

public class SimpleState implements HasState, Disposable {
    private final ReducingCondition dirty = new ReducingCondition(
        new OrFunction(), new ArrayList<ValueModel<Boolean>>());
    private final Set<HasState> states = new HashSet<HasState>();
    private final List<Disposable> disposables = new ArrayList<Disposable>();

    public void add(IStatefulPresenter presenter) {
        HasState state = presenter.getState();
        addState(state);
    }

    public <T> void add(HasValue<T> source) {
        ValueState<T> valueState = new ValueState<T>(source);
        addAbstractState(valueState);
    }

    public <E> void add(HasList<E> source) {
        ListState<E> listState = new ListState<E>(source);
        addAbstractState(listState);
    }

    @Override
    public ValueModel<Boolean> dirty() {
        return dirty;
    }

    @Override
    public void checkpoint() {
        dirty.recomputeAfterRunning(new CheckpointRunnable());
    }

    @Override
    public void revert() {
        dirty.recomputeAfterRunning(new RevertRunnable());
    }

    @Override
    public void dispose() {
        for (Disposable disposable : disposables) {
            disposable.dispose();
        }
    }

    private void addState(HasState state) {
        states.add(state);
        dirty.addSourceModel(state.dirty());
    }

    private void addAbstractState(AbstractState state) {
        addState(state);
        disposables.add(state);
    }

    private class CheckpointRunnable implements Runnable {
        @Override
        public void run() {
            for (HasState state : states) {
                state.checkpoint();
            }
        }
    }

    private class RevertRunnable implements Runnable {
        @Override
        public void run() {
            for (HasState state : states) {
                state.revert();
            }
        }
    }
}
