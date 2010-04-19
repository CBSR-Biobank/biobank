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
import org.eclipse.swt.widgets.Listener;
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

    /**
     * Show date and time widget
     */
    public DateTimeWidget(Composite parent, int style, Date date) {
        this(parent, style, date, -1);
    }

    /**
     * Allow date to be null. it typeShown == SWT.DATE, show only date; if
     * typeShown == SWT.TIME, show only time otherwise show both of them
     */
    public DateTimeWidget(Composite parent, int style, Date date, int typeShown) {
        super(parent, style);

        GridLayout layout = new GridLayout(3, false);
        layout.horizontalSpacing = 0;
        layout.marginWidth = 0;
        layout.verticalSpacing = 0;
        setLayout(layout);

        setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));

        if (typeShown != SWT.TIME) {
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
        if (typeShown != SWT.DATE) {
            timeEntry = new DateTime(this, SWT.BORDER | SWT.TIME | SWT.SHORT);
            timeEntry.setTime(0, 0, 0);
        }
        if (date != null) {
            Calendar cal = new GregorianCalendar();
            cal.setTime(date);

            if (typeShown != SWT.TIME) {
                dateEntry.setDate(cal.get(Calendar.YEAR), cal
                    .get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
            }
            if (typeShown != SWT.DATE) {
                timeEntry.setTime(cal.get(Calendar.HOUR), cal
                    .get(Calendar.MINUTE), cal.get(Calendar.SECOND));
            }
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
        if (timeEntry == null) {
            cal.set(Calendar.HOUR, 0);
            cal.set(Calendar.MINUTE, 0);
        } else {
            cal.set(Calendar.HOUR, timeEntry.getHours());
            cal.set(Calendar.MINUTE, timeEntry.getMinutes());
        }
        return cal.getTime();
    }

    public void setDate(Date date) {
        if (date == null)
            return;

        Calendar cal = new GregorianCalendar();
        cal.setTime(date);

        if (timeEntry != null) {
            timeEntry.setTime(cal.get(Calendar.HOUR_OF_DAY), cal
                .get(Calendar.MINUTE), cal.get(Calendar.SECOND));
        }

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
        if (timeEntry != null) {
            timeEntry.addSelectionListener(listener);
        }
    }

    public void removeSelectionListener(SelectionListener listener) {
        if (dateEntry != null) {
            dateEntry.removeSelectionListener(listener);
        }
        if (timeEntry != null) {
            timeEntry.removeSelectionListener(listener);
        }
    }

    @Override
    public void addListener(int eventType, Listener listener) {
        if (dateEntry != null) {
            dateEntry.addListener(eventType, listener);
        }
        if (timeEntry != null) {
            timeEntry.addListener(eventType, listener);
        }
    }

    @Override
    public void removeListener(int eventType, Listener listener) {
        if (dateEntry != null) {
            dateEntry.removeListener(eventType, listener);
        }
        if (timeEntry != null) {
            timeEntry.removeListener(eventType, listener);
        }
    }
}
