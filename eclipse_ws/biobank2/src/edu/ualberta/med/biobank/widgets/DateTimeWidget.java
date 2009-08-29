package edu.ualberta.med.biobank.widgets;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DateTime;
import org.eclipse.swt.widgets.Label;

public class DateTimeWidget extends BiobankWidget {

    private DateTime dateComp;

    private DateTime timeComp;

    private int year;

    private int month;

    private int day;

    private int hour;

    private int min;

    /**
     * Allow date to be null.
     */
    public DateTimeWidget(Composite parent, int style, Date date) {
        super(parent, style | SWT.BORDER);

        GridLayout layout = new GridLayout(4, false);
        layout.horizontalSpacing = 5;
        setLayout(layout);
        setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        SimpleDateFormat hourFormat = new SimpleDateFormat("HH");
        SimpleDateFormat minsFormat = new SimpleDateFormat("mm");

        hour = (date == null) ? 0 : Integer.parseInt(hourFormat.format(date));
        min = (date == null) ? 0 : Integer.parseInt(minsFormat.format(date));

        createDatePickerSection(this, date, hour, min);
    }

    private void createDatePickerSection(Composite client, Date date, int hour,
        int min) {
        Label l = new Label(client, SWT.NONE);
        l.setText("Date:");
        dateComp = new DateTime(client, SWT.BORDER | SWT.DATE | SWT.DROP_DOWN);
        dateComp.setData(date);
        dateComp.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                date = dateComp.getYear();
            }
        });

        l = new Label(client, SWT.NONE);
        l.setText("Time:");
        timeComp = new DateTime(client, SWT.BORDER | SWT.TIME | SWT.SHORT);
        timeComp.setTime(hour, min, 0);
    }

    public void addModifyListener(ModifyListener listener) {

    }

    public String getText() {
        return null;
    }

    public Date getDate() {
        return null;
    }

}
