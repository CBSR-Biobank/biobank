package edu.ualberta.med.biobank.widgets;

import java.util.Date;

import org.eclipse.core.databinding.observable.Diffs;
import org.eclipse.core.databinding.observable.value.AbstractObservableValue;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;


public class DateTimeObservableValue extends AbstractObservableValue {

    private DateTimeWidget dateTime;

    protected Date oldValue;

    private SelectionListener listener = new SelectionAdapter() {
        @Override
        public void widgetSelected(SelectionEvent e) {
            Date newValue = dateTimeToDate();

            if (!newValue.equals(DateTimeObservableValue.this.oldValue)) {
                fireValueChange(Diffs.createValueDiff(
                    DateTimeObservableValue.this.oldValue, newValue));
                DateTimeObservableValue.this.oldValue = newValue;
            }
        }
    };

    public DateTimeObservableValue(DateTimeWidget dateTime) {
        this.dateTime = dateTime;
        this.dateTime.addSelectionListener(listener);
    }

    @Override
    protected Object doGetValue() {
        return dateTimeToDate();
    }

    @Override
    protected void doSetValue(final Object value) {
        if (value instanceof Date) {
            Date date = (Date) value;
            dateToDateTime(date);
        }
    }

    @Override
    public Object getValueType() {
        return Date.class;
    }

    private void dateToDateTime(final Date date) {
        if (!dateTime.isDisposed()) {
            dateTime.setDate(date);
        }
    }

    private Date dateTimeToDate() {
        Date result = null;
        if (!dateTime.isDisposed()) {
            result = dateTime.getDate();
        }
        return result;
    }

    @Override
    public synchronized void dispose() {
        dateTime.removeSelectionListener(listener);
        super.dispose();
    }

}
