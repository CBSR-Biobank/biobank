package edu.ualberta.med.biobank.widgets;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DateTime;
import org.eclipse.swt.widgets.Label;

import edu.ualberta.med.biobank.BioBankPlugin;

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

        Label l = new Label(this, SWT.NONE);
        l.setText("Date:");
        dateComp = new DateTime(this, SWT.BORDER | SWT.DATE | SWT.DROP_DOWN);

        if (date != null) {
            SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy");
            SimpleDateFormat monthFormat = new SimpleDateFormat("MM");
            SimpleDateFormat dayFormat = new SimpleDateFormat("dd");

            year = Integer.parseInt(yearFormat.format(date));
            month = Integer.parseInt(monthFormat.format(date));
            day = Integer.parseInt(dayFormat.format(date));
            dateComp.setYear(year);
            dateComp.setMonth(month);
            dateComp.setDay(day);
        }

        dateComp.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                year = dateComp.getYear();
                month = dateComp.getMonth();
                day = dateComp.getDay();
            }
        });

        l = new Label(this, SWT.NONE);
        l.setText("Time:");
        timeComp = new DateTime(this, SWT.BORDER | SWT.TIME | SWT.SHORT);

        if (date != null) {
            SimpleDateFormat hourFormat = new SimpleDateFormat("HH");
            SimpleDateFormat minsFormat = new SimpleDateFormat("mm");

            hour = Integer.parseInt(hourFormat.format(date));
            min = Integer.parseInt(minsFormat.format(date));
            timeComp.setTime(hour, min, 0);
        }

        dateComp.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                hour = dateComp.getHours();
                min = dateComp.getMinutes();
            }
        });
    }

    public void addSelectionListener(SelectionListener listener) {
        dateComp.addSelectionListener(listener);
        timeComp.addSelectionListener(listener);
    }

    public String getText() {
        return String.format("%04d-%02d-%02d %02d:%02d", year, month + 1, day,
            hour, min);
    }

    public Date getDate() {
        try {
            return BioBankPlugin.getDateFormatter().parse(getText());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

}
