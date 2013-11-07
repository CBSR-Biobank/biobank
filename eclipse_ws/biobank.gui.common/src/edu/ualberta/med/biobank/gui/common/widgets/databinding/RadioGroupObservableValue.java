package edu.ualberta.med.biobank.gui.common.widgets.databinding;

import org.eclipse.core.databinding.observable.value.AbstractObservableValue;
import org.eclipse.core.databinding.observable.value.ValueDiff;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;

import edu.ualberta.med.biobank.gui.common.widgets.RadioGroup;

/**
 * An ObservableValue class for RadioGroup objects.
 * 
 */
public class RadioGroupObservableValue extends AbstractObservableValue {
    private final RadioGroup group;
    private Object selection = null;

    public RadioGroupObservableValue(RadioGroup group) {
        this.group = group;
        group.addSelectionListener(selectionListener);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.core.databinding.observable.value.AbstractObserv ableValue#dispose()
     */
    @Override
    public synchronized void dispose() {
        group.removeSelectionListener(selectionListener);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.core.databinding.observable.value.AbstractObserv
     * ableValue#doSetValue(java.lang.Object)
     */
    @Override
    protected void doSetValue(Object value) {
        group.setSelection(value);
        selection = value;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.core.databinding.observable.value.AbstractObserv ableValue#doGetValue()
     */
    @Override
    protected Object doGetValue() {
        return group.getSelection();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.core.databinding.observable.value.IObservableVal ue#getValueType()
     */
    @Override
    public Object getValueType() {
        return Object.class;
    }

    SelectionListener selectionListener = new SelectionListener() {
        @Override
        public void widgetDefaultSelected(SelectionEvent e) {
            widgetSelected(e);
        }

        @Override
        public void widgetSelected(SelectionEvent e) {
            final Object newSelection = group.getSelection();
            fireValueChange(new ValueDiff() {
                @Override
                public Object getNewValue() {
                    return newSelection;
                }

                @Override
                public Object getOldValue() {
                    return selection;
                }
            });
            selection = newSelection;
        }

    };
}
