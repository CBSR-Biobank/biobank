
package edu.ualberta.med.biobank.widgets;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import com.gface.date.DatePickerCombo;
import com.gface.date.DatePickerStyle;

public class DateTimeWidget extends Composite {

    public static final String DATE_FORMAT = "yyyy-MM-dd";

    private DatePickerCombo datePicker;

    private Combo hour;

    private Combo minutes;

    public DateTimeWidget(Composite parent, int style, String value) {
        super(parent, style | SWT.BORDER);
        GridLayout layout = new GridLayout(6, false);
        layout.horizontalSpacing = 10;
        setLayout(layout);
        setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        createDatePickerSection(this, value);

        Label l = new Label(this, SWT.NONE);
        l.setText("Hour:");
        hour = new Combo(this, SWT.BORDER);
        for (int h = 23; h >= 0; --h) {
            hour.add("" + h);
        }
        l = new Label(this, SWT.NONE);
        l.setText("min:");
        minutes = new Combo(this, SWT.BORDER);
        for (int m = 0; m < 60; m += 10) {
            minutes.add("" + m);
        }
    }

    private void createDatePickerSection(Composite client, String value) {
        Label l = new Label(client, SWT.NONE);
        l.setText("Date:");
        datePicker = new DatePickerCombo(client, SWT.BORDER,
            DatePickerStyle.BUTTONS_ON_BOTTOM | DatePickerStyle.YEAR_BUTTONS
                | DatePickerStyle.HIDE_WHEN_NOT_IN_FOCUS);
        // datePicker.setLayout(new GridLayout());
        datePicker.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        datePicker.setDateFormat(new SimpleDateFormat(DATE_FORMAT));

        if ((value != null) && (value.length() > 0)) {
            SimpleDateFormat df = new SimpleDateFormat(DATE_FORMAT);
            try {
                datePicker.setDate(df.parse(value));
            }
            catch (ParseException e1) {
                e1.printStackTrace();
            }
        }
    }

    public String getText() {
        return datePicker.getText() + " " + hour.getText() + ":"
            + minutes.getText();
    }

}
