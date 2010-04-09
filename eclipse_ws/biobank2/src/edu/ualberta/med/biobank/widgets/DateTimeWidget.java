package edu.ualberta.med.biobank.widgets;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DateTime;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

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

        dateEntry = new DateTime(this, SWT.DATE);
        Button open = new Button(this, SWT.PUSH);
        timeEntry = new DateTime(this, SWT.TIME | SWT.SHORT);

        open.setText("Select Date");
        open.addSelectionListener(new SelectionAdapter() {
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
                    public void widgetSelected(SelectionEvent e) {
                        dialog.close();
                    }
                });
                dialog.setDefaultButton(ok);
                dialog.pack();
                dialog.open();
            }
        });

        Calendar cal = new GregorianCalendar();
        cal.setTime(date);

        dateEntry.setDate(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal
            .get(Calendar.DAY_OF_MONTH));
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
        return null;
    }

    public void setDate(Date date) {
    }
}
