package edu.ualberta.med.biobank.gui.common.widgets.utils;

import java.util.Date;

import org.eclipse.core.databinding.observable.Diffs;
import org.eclipse.core.databinding.observable.value.AbstractObservableValue;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;

import edu.ualberta.med.biobank.gui.common.widgets.DateTimeWidget;


public class DateTimeObservableValue extends AbstractObservableValue {

    private DateTimeWidget dateTime;

    protected Date oldValue;

    private ModifyListener listener = new ModifyListener() {
        @Override
        public void modifyText(ModifyEvent e) {
            Date newValue = dateTimeToDate();

            if (oldValue == null || newValue == null
                || !newValue.equals(DateTimeObservableValue.this.oldValue)) {
                fireValueChange(Diffs.createValueDiff(
                    DateTimeObservableValue.this.oldValue, newValue));
                DateTimeObservableValue.this.oldValue = newValue;
            }
        }
    };

    public DateTimeObservableValue(DateTimeWidget dateTime) {
        this.dateTime = dateTime;
        this.dateTime.addModifyListener(listener);
    }

    @Override
    protected Object doGetValue() {
        return dateTimeToDate();
    }

    @Override
    protected void doSetValue(final Object value) {
        if (value == null || value instanceof Date) {
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
        dateTime.removeModifyListener(listener);
        super.dispose();
    }

}
