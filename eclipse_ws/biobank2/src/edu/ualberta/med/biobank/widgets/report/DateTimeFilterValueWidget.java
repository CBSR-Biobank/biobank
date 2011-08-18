package edu.ualberta.med.biobank.widgets.report;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import edu.ualberta.med.biobank.gui.common.widgets.DateTimeWidget;
import edu.ualberta.med.biobank.model.ReportFilterValue;

public class DateTimeFilterValueWidget implements FilterValueWidget {
    private static final SimpleDateFormat SQL_DATETIME_FORMAT = new SimpleDateFormat(
        "yyyy-MM-dd HH:mm:ss"); //$NON-NLS-1$
    private static final SimpleDateFormat DISPLAY_DATE_FORMAT = new SimpleDateFormat(
        "yyyy-MM-dd"); //$NON-NLS-1$
    private static final SimpleDateFormat DISPLAY_TIME_FORMAT = new SimpleDateFormat(
        "HH:mm:ss"); //$NON-NLS-1$
    private final DateTimeWidget dateTime;
    private final boolean isDate;
    private final boolean isTime;

    public DateTimeFilterValueWidget(Composite parent) {
        this(parent, SWT.DATE | SWT.TIME);
    }

    public DateTimeFilterValueWidget(Composite parent, int style) {
        dateTime = new DateTimeWidget(parent, style, null);

        isDate = (style & SWT.DATE) != 0;
        isTime = (style & SWT.TIME) != 0;
    }

    @Override
    public Collection<ReportFilterValue> getValues() {
        if (!dateTime.isDisposed() && dateTime.getDate() != null) {
            Date date = getDate();
            String dateString = SQL_DATETIME_FORMAT.format(date);

            if (!dateString.isEmpty()) {
                ReportFilterValue value = new ReportFilterValue();
                value.setPosition(0);
                value.setValue(dateString);

                return Arrays.asList(value);
            }
        }
        return new ArrayList<ReportFilterValue>();
    }

    @Override
    public void setValues(Collection<ReportFilterValue> values) {
        if (!dateTime.isDisposed()) {
            setDate(null);
            for (ReportFilterValue value : values) {
                if (value != null && value.getValue() != null) {
                    try {
                        Date date = SQL_DATETIME_FORMAT.parse(value.getValue());
                        setDate(date);
                    } catch (ParseException e) {
                        //
                    }
                }
                break;
            }
        }
    }

    @Override
    public void addChangeListener(
        final ChangeListener<ChangeEvent> changeListener) {
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

    @Override
    public boolean isValid(ReportFilterValue value) {
        return value.getValue() != null && !value.getValue().isEmpty()
            && value.getSecondValue() == null;
    }

    @Override
    public String toString(ReportFilterValue value) {
        String string = value.getValue();
        Date date = null;

        try {
            date = SQL_DATETIME_FORMAT.parse(value.getValue());
        } catch (ParseException e) {
            //
        }

        if (date != null) {
            string = ""; //$NON-NLS-1$

            if (isDate) {
                string = DISPLAY_DATE_FORMAT.format(date);
            }

            if (isTime) {
                if (!string.isEmpty()) {
                    string += " "; //$NON-NLS-1$
                }

                string += DISPLAY_TIME_FORMAT.format(date);
            }
        }

        return string;
    }

    private Date getDate() {
        Date date = dateTime.getDate();
        Date cleanedDate = cleanDate(date);
        return cleanedDate;
    }

    private void setDate(Date date) {
        Date cleanedDate = cleanDate(date);
        dateTime.setDate(cleanedDate);
    }

    private Date cleanDate(Date date) {
        if (date == null) {
            return null;
        }

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.SECOND, 0);

        if (!isTime) {
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
        }

        return calendar.getTime();
    }
}
