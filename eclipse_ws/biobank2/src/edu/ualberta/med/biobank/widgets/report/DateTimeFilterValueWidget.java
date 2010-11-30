package edu.ualberta.med.biobank.widgets.report;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import edu.ualberta.med.biobank.model.ReportFilterValue;
import edu.ualberta.med.biobank.widgets.DateTimeWidget;

public class DateTimeFilterValueWidget implements FilterValueWidget {
    private static final SimpleDateFormat SQL_DATE_FORMAT = new SimpleDateFormat(
        "yyyy-MM-dd HH:mm:ss");
    private final DateTimeWidget dateTime;

    public DateTimeFilterValueWidget(Composite parent) {
        dateTime = new DateTimeWidget(parent, SWT.DATE | SWT.TIME, null);
    }

    @Override
    public Collection<ReportFilterValue> getValues() {
        if (!dateTime.isDisposed() && dateTime.getDate() != null) {
            String dateString = SQL_DATE_FORMAT.format(dateTime.getDate());

            ReportFilterValue value = new ReportFilterValue();
            value.setPosition(0);
            value.setValue(dateString);

            return Arrays.asList(value);
        }
        return new ArrayList<ReportFilterValue>();
    }

    @Override
    public void setValues(Collection<ReportFilterValue> values) {
        for (ReportFilterValue value : values) {
            if (!dateTime.isDisposed() && value != null) {
                try {
                    Date date = SQL_DATE_FORMAT.parse(value.getValue());
                    dateTime.setDate(date);
                } catch (ParseException e) {
                }
            }
            break;
        }
    }

    @Override
    public void addChangeListener(final ChangeListener<Object> changeListener) {
        dateTime.addModifyListener(new ModifyListener() {
            @Override
            public void modifyText(ModifyEvent e) {
                changeListener.handleEvent(null);
            }
        });
    }

    @Override
    public Control getControl() {
        return dateTime;
    }
}
