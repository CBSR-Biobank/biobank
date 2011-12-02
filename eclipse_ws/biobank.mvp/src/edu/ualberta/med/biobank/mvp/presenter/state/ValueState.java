package edu.ualberta.med.biobank.mvp.presenter.state;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.HasValue;

public class ValueState<T> extends AbstractState {
    private final SourceMonitor sourceMonitor = new SourceMonitor();
    private final HandlerRegistration handlerRegistration;
    private final HasValue<T> source;
    private T checkpointValue;

    ValueState(HasValue<T> source) {
        this.source = source;

        handlerRegistration = source.addValueChangeHandler(sourceMonitor);

        checkpoint();
    }

    @Override
    public void checkpoint() {
        checkpointValue = source.getValue();
        dirty.setValue(false);
    }

    @Override
    public void revert() {
        source.setValue(checkpointValue, true);
    }

    @Override
    public void dispose() {
        handlerRegistration.removeHandler();
    }

    private class SourceMonitor implements ValueChangeHandler<T> {
        @Override
        public void onValueChange(ValueChangeEvent<T> event) {
            boolean equal = areEqual(source.getValue(), checkpointValue);
            dirty.setValue(!equal);
        }
    }
}
