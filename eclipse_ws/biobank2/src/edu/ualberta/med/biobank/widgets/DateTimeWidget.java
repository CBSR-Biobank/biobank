package edu.ualberta.med.biobank.widgets;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import com.gface.date.DatePickerCombo;
import com.gface.date.DatePickerStyle;

import edu.ualberta.med.biobank.BioBankPlugin;

public class DateTimeWidget extends BiobankWidget {

    private DatePickerCombo datePicker;

    private Combo hour;

    private Combo minutes;

    public DateTimeWidget(Composite parent, int style, Date date) {
        super(parent, style | SWT.BORDER);

        SimpleDateFormat hourFormat = new SimpleDateFormat("HH");
        SimpleDateFormat minsFormat = new SimpleDateFormat("mm");

        String hourStr = date == null ? "" : hourFormat.format(date);
        String minStr = date == null ? "" : minsFormat.format(date);

        GridLayout layout = new GridLayout(6, false);
        layout.horizontalSpacing = 5;
        setLayout(layout);
        setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        datePicker = createDatePickerSection(this, "Date:", date);

        Label l = new Label(this, SWT.NONE);
        l.setText("Hour:");
        hour = new Combo(this, SWT.NONE);
        for (int h = 0; h < 24; ++h) {
            hour.add(String.format("%02d", h));
        }
        hour.setText(hourStr);

        l = new Label(this, SWT.NONE);
        l.setText("min:");
        minutes = new Combo(this, SWT.NONE);
        minutes.add("00");
        for (int m = 10; m < 60; m += 10) {
            minutes.add(String.format("%02d", m));
        }
        minutes.setText(minStr);
    }

    public void addSelectionListener(SelectionListener listener) {
        datePicker.addSelectionListener(listener);
        hour.addSelectionListener(listener);
        minutes.addSelectionListener(listener);
    }

    public void addModifyListener(ModifyListener listener) {
        datePicker.addModifyListener(listener);
        hour.addModifyListener(listener);
        minutes.addModifyListener(listener);
    }

    private DatePickerCombo createDatePickerSection(Composite client,
        String labelStr, Date date) {
        Label l = new Label(client, SWT.NONE);
        l.setText(labelStr);
        DatePickerCombo datePicker = new DatePickerCombo(client, SWT.BORDER,
            DatePickerStyle.BUTTONS_ON_BOTTOM | DatePickerStyle.YEAR_BUTTONS
                | DatePickerStyle.HIDE_WHEN_NOT_IN_FOCUS);
        // datePicker.setLayout(new GridLayout());
        datePicker.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        datePicker
            .setDateFormat(new SimpleDateFormat(BioBankPlugin.DATE_FORMAT));
        datePicker.setDate(date);
        return datePicker;
    }

    public String getText() {
        Date date = datePicker.getDate();
        if ((date == null) || (hour.getText().length() != 2)
            && (minutes.getText().length() != 2)) {
            return null;
        }
        SimpleDateFormat sdf = new SimpleDateFormat(BioBankPlugin.DATE_FORMAT);
        return sdf.format(date) + " " + hour.getText() + ":"
            + minutes.getText();
    }

}
