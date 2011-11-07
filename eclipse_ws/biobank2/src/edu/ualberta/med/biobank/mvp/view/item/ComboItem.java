package edu.ualberta.med.biobank.mvp.view.item;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.event.shared.HandlerRegistration;

import edu.ualberta.med.biobank.mvp.event.SimpleValueChangeEvent;
import edu.ualberta.med.biobank.mvp.user.ui.HasSelectedValue;
import edu.ualberta.med.biobank.mvp.util.Converter;

public class ComboItem<T> implements HasSelectedValue<T> {
    private static final Listener KILL_MOUSE_WHEEL_LISTENER = new Listener() {
        @Override
        public void handleEvent(Event event) {
            event.doit = false;
        }
    };
    private final HandlerManager handlerManager = new HandlerManager(this);
    private final ISelectionChangedListener selectionChangedListener =
        new ISelectionChangedListener() {
            @Override
            public void selectionChanged(SelectionChangedEvent event) {
                if (fireEvents) {
                    T value = getValue();
                    handlerManager.fireEvent(new SimpleValueChangeEvent<T>(
                        value));
                }
            }
        };
    private ComboViewer comboViewer;
    private T value = null; // track value while Widget not bound
    private List<T> options = new ArrayList<T>();
    private Converter<T, String> optionLabeler;
    private boolean fireEvents;

    public synchronized void setComboViewer(ComboViewer comboViewer) {
        unbindOldComboViewer();

        this.comboViewer = comboViewer;
        comboViewer.setContentProvider(new ArrayContentProvider());
        comboViewer.setLabelProvider(new CustomLabelProvider());
        setOptions(options);
        setValue(value);

        comboViewer.addSelectionChangedListener(selectionChangedListener);
        disableMouseWheel();
    }

    @Override
    public HandlerRegistration addValueChangeHandler(
        ValueChangeHandler<T> handler) {
        return handlerManager.addHandler(ValueChangeEvent.getType(), handler);
    }

    @Override
    public void fireEvent(GwtEvent<?> event) {
        handlerManager.fireEvent(event);
    }

    @Override
    public T getValue() {
        if (comboViewer != null) {
            IStructuredSelection selection = (IStructuredSelection) comboViewer
                .getSelection();

            if ((selection != null) && !selection.isEmpty()) {
                @SuppressWarnings("unchecked")
                T tmp = (T) selection.getFirstElement();
                return tmp;
            }
        }

        return value;
    }

    @Override
    public void setValue(T value) {
        this.value = value;

        if (comboViewer != null) {
            IStructuredSelection selection =
                value != null ? new StructuredSelection(
                    value) : new StructuredSelection();
            comboViewer.setSelection(selection, true);
        }
    }

    @Override
    public void setValue(T value, boolean fireEvents) {
        this.fireEvents = fireEvents;
        setValue(value);
        this.fireEvents = true;
    }

    @Override
    public void setOptions(List<T> options) {
        this.options = options;

        if (comboViewer != null) {
            comboViewer.setInput(options);
        }
    }

    @Override
    public void setOptionLabeller(Converter<T, String> labeler) {
        this.optionLabeler = labeler;
    }

    private void unbindOldComboViewer() {
        if (comboViewer != null) {
            comboViewer
                .removeSelectionChangedListener(selectionChangedListener);
        }
    }

    private void disableMouseWheel() {
        Control combo = comboViewer.getControl();
        combo.addListener(SWT.MouseWheel, KILL_MOUSE_WHEEL_LISTENER);
    }

    private class CustomLabelProvider extends LabelProvider {
        @SuppressWarnings("unchecked")
        @Override
        public String getText(Object element) {
            return optionLabeler.convert((T) element);
        }
    }
}
