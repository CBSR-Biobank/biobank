package edu.ualberta.med.biobank.mvp.presenter.state;

import java.lang.reflect.Method;
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
import edu.ualberta.med.biobank.mvp.view.IView;

public class ModelState implements HasState, Disposable {
    private final ReducingCondition dirty = new ReducingCondition(
        new OrFunction(), new ArrayList<ValueModel<Boolean>>());
    private final Set<HasState> states = new HashSet<HasState>();
    private final List<Disposable> disposables = new ArrayList<Disposable>();

    public void addView(IView view) {
        Class<?>[] interfaces = view.getClass().getInterfaces();
        for (Class<?> klazz : interfaces) {
            if (IView.class.isAssignableFrom(klazz)) {
                @SuppressWarnings("unchecked")
                Class<? extends IView> iView = (Class<? extends IView>) klazz;
                add(view, iView);
            }
        }
    }

    public void addPresenter(IStatefulPresenter presenter) {
        HasState state = presenter.getState();
        addState(state);
    }

    public <T> void addValue(HasValue<T> source) {
        ValueState<T> valueState = new ValueState<T>(source);
        addAbstractState(valueState);
    }

    public <E> void addList(HasList<E> source) {
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

    private void add(IView view, Class<? extends IView> klazz) {
        for (Method method : klazz.getMethods()) {
            Class<?> returnType = method.getReturnType();

            try {
                if (HasValue.class.isAssignableFrom(returnType)) {
                    HasValue<?> hasValue = (HasValue<?>) method.invoke(view);
                    addValue(hasValue);
                } else if (HasList.class.isAssignableFrom(returnType)) {
                    HasList<?> hasList = (HasList<?>) method.invoke(view);
                    addList(hasList);
                }
            } catch (Exception caught) {
                // TODO: need a logger (for non-user errors).
            }
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
