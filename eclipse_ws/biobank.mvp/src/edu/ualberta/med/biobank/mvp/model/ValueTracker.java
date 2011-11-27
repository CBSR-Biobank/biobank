package edu.ualberta.med.biobank.mvp.model;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.pietschy.gwt.pectin.client.bean.HasDirtyModel;
import com.pietschy.gwt.pectin.client.binding.Disposable;
import com.pietschy.gwt.pectin.client.value.MutableValueModel;
import com.pietschy.gwt.pectin.client.value.ValueHolder;
import com.pietschy.gwt.pectin.client.value.ValueModel;

import edu.ualberta.med.biobank.mvp.util.HandlerRegManager;

public class ValueTracker<T> implements HasDirtyModel, Disposable {
    private final HandlerRegManager hrManager = new HandlerRegManager();
    private final ValueHolder<Boolean> dirty = new ValueHolder<Boolean>(false);
    private final ChangeMonitor changeMonitor = new ChangeMonitor();
    private final MutableValueModel<T> mutableValueModel;
    private T checkpointValue;

    public ValueTracker(MutableValueModel<T> mutableValueModel) {
        this.mutableValueModel = mutableValueModel;

        hrManager.add(mutableValueModel.addValueChangeHandler(changeMonitor));
    }

    @Override
    public ValueModel<Boolean> dirty() {
        return dirty;
    }

    public void checkpoint() {
        checkpointValue = mutableValueModel.getValue();
        dirty.setValue(false);
    }

    public void revert() {
        mutableValueModel.setValue(checkpointValue);
    }

    @Override
    public void dispose() {
        hrManager.dispose();
    }

    private class ChangeMonitor implements ValueChangeHandler<T> {
        @Override
        public void onValueChange(ValueChangeEvent<T> event) {
            T value = event.getValue();
        }
    }
}
