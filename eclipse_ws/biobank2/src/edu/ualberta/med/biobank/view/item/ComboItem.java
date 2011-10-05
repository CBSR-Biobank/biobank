package edu.ualberta.med.biobank.view.item;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

import edu.ualberta.med.biobank.event.HandlerRegistration;
import edu.ualberta.med.biobank.event.HasValue;
import edu.ualberta.med.biobank.event.ValueChangeEvent;
import edu.ualberta.med.biobank.event.ValueChangeHandler;

public class ComboItem<T> implements HasValue<T> {
    private static final Listener KILL_MOUSE_WHEEL_LISTENER = new Listener() {
        @Override
        public void handleEvent(Event event) {
            event.doit = false;
        }
    };
    private final ComboViewer comboViewer;
    private final List<ValueChangeHandler<T>> valueChangeHandlers = new ArrayList<ValueChangeHandler<T>>();
    private final ISelectionChangedListener selectionChangedListener = new ISelectionChangedListener() {
        @Override
        public void selectionChanged(SelectionChangedEvent event) {
            if (fireEvents) {
                // TODO: send actual event object
                notifyValueChangeHandlers(null);
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
        valueChangeHandlers.add(handler);
        return new HandlerRegistrationImpl<T>(this, handler);
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

    private void notifyValueChangeHandlers(ValueChangeEvent<T> event) {
        for (ValueChangeHandler<T> valueChangeHandler : valueChangeHandlers) {
            valueChangeHandler.onValueChange(event);
        }
    }

    private void disableMouseWheel() {
        CCombo combo = comboViewer.getCCombo();
        combo.addListener(SWT.MouseWheel, KILL_MOUSE_WHEEL_LISTENER);
    }

    private static class HandlerRegistrationImpl<T> implements
        HandlerRegistration {
        private final ComboItem<T> comboItem;
        private final ValueChangeHandler<T> handler;

        public HandlerRegistrationImpl(ComboItem<T> comboItem,
            ValueChangeHandler<T> handler) {
            this.comboItem = comboItem;
            this.handler = handler;
        }

        @Override
        public void removeHandler() {
            comboItem.valueChangeHandlers.remove(handler);
        }
    }
}
