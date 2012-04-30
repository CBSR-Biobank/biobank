package edu.ualberta.med.biobank.mvp.presenter.state;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.event.shared.HandlerRegistration;

import edu.ualberta.med.biobank.mvp.event.ui.ListChangeEvent;
import edu.ualberta.med.biobank.mvp.event.ui.ListChangeHandler;
import edu.ualberta.med.biobank.mvp.user.ui.HasList;

public class ListState<E> extends AbstractState {
    private final SourceMonitor sourceMonitor = new SourceMonitor();
    private final HandlerRegistration handlerRegistration;
    private final HasList<E> source;
    private List<E> checkpointValue;

    ListState(HasList<E> source) {
        this.source = source;

        handlerRegistration = source.addListChangeHandler(sourceMonitor);

        checkpoint();
    }

    @Override
    public void checkpoint() {
        checkpointValue = new ArrayList<E>(source.asUnmodifiableList());
        dirty.setValue(false);
    }

    @Override
    public void revert() {
        source.setElements(checkpointValue, true);
    }

    @Override
    public void dispose() {
        handlerRegistration.removeHandler();
    }

    private boolean computeDirty() {
        List<E> value = source.asUnmodifiableList();

        if (value.size() != checkpointValue.size()) {
            return true;
        }

        for (int i = 0; i < checkpointValue.size(); i++) {
            if (!areEqual(value.get(i), checkpointValue.get(i))) {
                return true;
            }
        }

        return false;
    }

    private class SourceMonitor implements ListChangeHandler<E> {
        @Override
        public void onListChange(ListChangeEvent<E> event) {
            dirty.setValue(computeDirty());
        }
    }
}
