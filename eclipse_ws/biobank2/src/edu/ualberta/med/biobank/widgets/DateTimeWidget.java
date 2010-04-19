package edu.ualberta.med.biobank.widgets;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DateTime;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

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

    private DateTime dateEntry;

    private Button dateButton;

    private DateTime timeEntry;

    public DateTimeWidget(Composite parent, int style, Date date) {
        this(parent, style, date, true);
    }

    /**
     * Allow date to be null.
     */
    public DateTimeWidget(Composite parent, int style, Date date,
        boolean showDate) {
        super(parent, style);

        GridLayout layout = new GridLayout(3, false);
        layout.horizontalSpacing = 0;
        layout.marginWidth = 0;
        layout.verticalSpacing = 0;
        setLayout(layout);

        setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));

        if (showDate) {
            dateEntry = new DateTime(this, SWT.BORDER);
            Point size = dateEntry.computeSize(SWT.DEFAULT, SWT.DEFAULT);
            GridData gd = new GridData();
            gd.widthHint = size.x + 10;
            dateEntry.setLayoutData(gd);

            dateButton = new Button(this, SWT.NONE);
            dateButton.setText("calendar");
            dateButton.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent e) {
                    final Shell dialog = new Shell(PlatformUI.getWorkbench()
                        .getActiveWorkbenchWindow().getShell(), SWT.DIALOG_TRIM);
                    dialog.setLayout(new GridLayout(3, false));

                    final DateTime calendar = new DateTime(dialog, SWT.CALENDAR
                        | SWT.BORDER);

                    new Label(dialog, SWT.NONE);
                    Button ok = new Button(dialog, SWT.PUSH);
                    ok.setText("OK");
                    ok.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false,
                        false));
                    ok.addSelectionListener(new SelectionAdapter() {
                        @Override
                        public void widgetSelected(SelectionEvent e) {
                            dialog.close();
                        }
                    });
                    dialog.setDefaultButton(ok);
                    dialog.pack();
                    dialog.open();

                }
            });
        }

        timeEntry = new DateTime(this, SWT.BORDER | SWT.TIME | SWT.SHORT);
        timeEntry.setTime(0, 0, 0);

        if (date != null) {
            Calendar cal = new GregorianCalendar();
            cal.setTime(date);

            if (showDate) {
                dateEntry.setDate(cal.get(Calendar.YEAR), cal
                    .get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
            }
            timeEntry.setTime(cal.get(Calendar.HOUR), cal.get(Calendar.MINUTE),
                cal.get(Calendar.SECOND));
        }
    }

    public String getText() {
        return null;
    }

    public Date getDate() {
        Calendar cal = new GregorianCalendar();
        if (dateEntry != null) {
            cal.set(Calendar.YEAR, dateEntry.getYear());
            cal.set(Calendar.MONTH, dateEntry.getMonth());
            cal.set(Calendar.DAY_OF_MONTH, dateEntry.getDay());
        }
        cal.set(Calendar.HOUR, timeEntry.getHours());
        cal.set(Calendar.MINUTE, timeEntry.getMinutes());
        return cal.getTime();
    }

    public void setDate(Date date) {
        if (date == null)
            return;

        Calendar cal = new GregorianCalendar();
        cal.setTime(date);

        timeEntry.setTime(cal.get(Calendar.HOUR_OF_DAY), cal
            .get(Calendar.MINUTE), cal.get(Calendar.SECOND));

        cal.set(Calendar.HOUR, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.AM_PM, Calendar.AM);
        if (dateEntry != null) {
            dateEntry.setDate(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH));
        }
    }

    public void addSelectionListener(SelectionListener listener) {
        if (dateEntry != null) {
            dateEntry.addSelectionListener(listener);
        }
        timeEntry.addSelectionListener(listener);
    }

    public void removeSelectionListener(SelectionListener listener) {
        if (dateEntry != null) {
            dateEntry.removeSelectionListener(listener);
        }
        timeEntry.removeSelectionListener(listener);
    }

}
