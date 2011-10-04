package edu.ualberta.med.biobank.view.component;

import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

import edu.ualberta.med.biobank.event.HandlerRegistration;
import edu.ualberta.med.biobank.event.HasValue;
import edu.ualberta.med.biobank.event.ValueChangeHandler;

public class ComboViewWrapper<T> implements HasValue<T> {
    private static final Listener KILL_MOUSE_WHEEL_LISTENER = new Listener() {
        @Override
        public void handleEvent(Event event) {
            event.doit = false;
        }
    };
    private final ComboViewer comboViewer;

    // private final Class<T> klazz;

    public ComboViewWrapper(ComboViewer comboViewer) {
        this.comboViewer = comboViewer;
        // this.klazz = klazz;

        // comboViewer.addSelectionChangedListener(listener)

        disableMouseWheel();
    }

    @Override
    public HandlerRegistration addValueChangeHandler(
        ValueChangeHandler<T> handler) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public T getValue() {
        IStructuredSelection selection = (IStructuredSelection) comboViewer
            .getSelection();

        // if ((selection != null) && (selection.size() > 0)) {
        // csu.doSelection(selection.getFirstElement());
        // } else {
        // csu.doSelection(null);
        // }

        return null;
    }

    @Override
    public void setValue(T value) {
        comboViewer.setSelection(new StructuredSelection(value), true);
    }

    @Override
    public void setValue(T value, boolean fireEvents) {
        // TODO Auto-generated method stub

    }

    public interface Transformer<T> {
        public T getValue(Object object);

        public void setValue(Object object);
    }

    private void disableMouseWheel() {
        CCombo combo = comboViewer.getCCombo();
        combo.addListener(SWT.MouseWheel, KILL_MOUSE_WHEEL_LISTENER);
    }
}
