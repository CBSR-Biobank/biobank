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
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

import edu.ualberta.med.biobank.mvp.user.ui.SelectedValueField;
import edu.ualberta.med.biobank.mvp.util.Converter;

public class ComboBox<T> extends AbstractValueField<T>
    implements SelectedValueField<T> {
    private static final Listener KILL_MOUSE_WHEEL_LISTENER = new Listener() {
        @Override
        public void handleEvent(Event event) {
            event.doit = false;
        }
    };
    private final ISelectionChangedListener selectionListener =
        new ISelectionChangedListener() {
            @Override
            public void selectionChanged(SelectionChangedEvent event) {
                setValueInternal(getSelectedValue());
            }
        };
    private ComboViewer comboViewer;
    private List<T> options = new ArrayList<T>();
    private Converter<T, String> optionLabeler;

    public synchronized void setComboViewer(ComboViewer comboViewer) {
        unbindOldComboViewer();

        this.comboViewer = comboViewer;
        comboViewer.setContentProvider(new ArrayContentProvider());
        comboViewer.setLabelProvider(new CustomLabelProvider());
        setOptions(options);

        updateGui();

        comboViewer.addSelectionChangedListener(selectionListener);
        disableMouseWheel();
    }

    @Override
    protected void updateGui() {
        if (comboViewer != null) {
            Display display = comboViewer.getCombo().getDisplay();
            display.asyncExec(new Update());
        }
    }

    @Override
    public void setOptions(List<T> options) {
        this.options = new ArrayList<T>(options);

        if (comboViewer != null) {
            Display display = comboViewer.getCombo().getDisplay();
            display.asyncExec(new UpdateOptions(options));
        }
    }

    @Override
    public void setOptionLabeller(Converter<T, String> labeler) {
        this.optionLabeler = labeler;
    }

    private void unbindOldComboViewer() {
        if (comboViewer != null) {
            comboViewer
                .removeSelectionChangedListener(selectionListener);
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

    private IStructuredSelection getStructuredSelection() {
        if (getValue() != null) {
            return new StructuredSelection(getValue());
        }
        return new StructuredSelection();
    }

    private T getSelectedValue() {
        T value = null;

        IStructuredSelection selection = (IStructuredSelection) comboViewer
            .getSelection();

        if (selection != null && !selection.isEmpty()) {
            @SuppressWarnings("unchecked")
            T tmp = (T) selection.getFirstElement();
            value = tmp;
        }

        return value;
    }

    private boolean hasIllegalValue() {
        return getValue() != null && !options.contains(getValue());
    }

    private class UpdateOptions implements Runnable {
        private final List<T> options;

        public UpdateOptions(List<T> options) {
            this.options = options;
        }

        @Override
        public void run() {
            if (comboViewer != null && !comboViewer.getCombo().isDisposed()) {
                comboViewer.setInput(options);
                comboViewer.refresh();

                if (hasIllegalValue()) {
                    setValue(null, true);
                }
            }
        }
    }

    private class Update implements Runnable {
        @Override
        public void run() {
            if (hasIllegalValue()) {
                setValue(null, false);
            } else if (comboViewer != null
                && !comboViewer.getCombo().isDisposed()) {
                comboViewer.removeSelectionChangedListener(selectionListener);
                comboViewer.setSelection(getStructuredSelection(), true);
                comboViewer.addSelectionChangedListener(selectionListener);
            }
        }
    }
}
