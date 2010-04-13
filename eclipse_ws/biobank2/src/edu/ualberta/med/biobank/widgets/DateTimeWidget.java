package edu.ualberta.med.biobank.widgets;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.eclipse.nebula.widgets.datechooser.DateChooserCombo;
import org.eclipse.nebula.widgets.formattedtext.DateFormatter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DateTime;

//import edu.ualberta.med.biobank.common.formatters.DateFormatter;

/**
 * Wrapper around Nebula's CDateTime widget.
 * 
 * HISTORY
 * 
 * Previously used gface Date Picker combo (http://gface.sourceforge.net/) but
 * it did not display well on Windows and Linux.
 * 
 * Attempted to use SWT DateTime but at the time it did not support null dates
 * and null times.
 */
public class DateTimeWidget extends BiobankWidget {

    private DateChooserCombo dateEntry;

    private DateTime timeEntry;

    /**
     * Allow date to be null.
     */
    public DateTimeWidget(Composite parent, int style, Date date) {
        super(parent, style);

        GridLayout layout = new GridLayout(3, false);
        layout.horizontalSpacing = 0;
        layout.marginWidth = 0;
        layout.verticalSpacing = 0;
        setLayout(layout);

        setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));

        dateEntry = new DateChooserCombo(this, SWT.BORDER);
        timeEntry = new DateTime(this, SWT.BORDER | SWT.TIME | SWT.SHORT);

        Calendar cal = new GregorianCalendar();
        cal.setTime(date);

        dateEntry.setFormatter(new DateFormatter("yyyy-MM-dd"));
        dateEntry.setValue(date);
        timeEntry.setTime(cal.get(Calendar.HOUR), cal.get(Calendar.MINUTE), cal
            .get(Calendar.SECOND));
    }

    public void addSelectionListener(SelectionListener listener) {
        dateEntry.addSelectionListener(listener);
        timeEntry.addSelectionListener(listener);
    }

    public String getText() {
        return null;
    }

    public Date getDate() {
        Calendar cal = new GregorianCalendar();
        cal.setTime(dateEntry.getValue());
        cal.set(Calendar.HOUR, timeEntry.getHours());
        cal.set(Calendar.MINUTE, timeEntry.getMinutes());
        return cal.getTime();
    }

    public void setDate(Date date) {
        dateEntry.setValue(date);
    }
}
