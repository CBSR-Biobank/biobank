package edu.ualberta.med.biobank.mvp.view.item;

import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.HasValue;

import edu.ualberta.med.biobank.mvp.event.SimpleValueChangeEvent;

public class ComboItem<T> implements HasValue<T> {
    private static final Listener KILL_MOUSE_WHEEL_LISTENER = new Listener() {
        @Override
        public void handleEvent(Event event) {
            event.doit = false;
        }
    };
    private final ComboViewer comboViewer;
    private final HandlerManager handlerManager = new HandlerManager(this);
    private final ISelectionChangedListener selectionChangedListener = new ISelectionChangedListener() {
        @Override
        public void selectionChanged(SelectionChangedEvent event) {
            if (fireEvents) {
                T value = getValue();
                handlerManager.fireEvent(new SimpleValueChangeEvent<T>(value));
            }
        }
    };
    private boolean fireEvents;

    public ComboItem(ComboViewer comboViewer) {
        this.comboViewer = comboViewer;

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
        IStructuredSelection selection = (IStructuredSelection) comboViewer
            .getSelection();

        if ((selection != null) && !selection.isEmpty()) {
            @SuppressWarnings("unchecked")
            T tmp = (T) selection.getFirstElement();
            return tmp;
        }

        return null;
    }

    @Override
    public void setValue(T value) {
        comboViewer.setSelection(new StructuredSelection(value), true);
    }

    @Override
    public void setValue(T value, boolean fireEvents) {
        this.fireEvents = fireEvents;
        setValue(value);
        this.fireEvents = true;
    }

    private void disableMouseWheel() {
        CCombo combo = comboViewer.getCCombo();
        combo.addListener(SWT.MouseWheel, KILL_MOUSE_WHEEL_LISTENER);
    }
}
