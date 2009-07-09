
package edu.ualberta.med.biobank.widgets;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.eclipse.swt.SWT;
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

        String hourStr = hourFormat.format(date);
        String minStr = minsFormat.format(date);

        GridLayout layout = new GridLayout(6, false);
        layout.horizontalSpacing = 5;
        setLayout(layout);
        setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        datePicker = createDatePickerSection(this, "Date:", date);

        Label l = new Label(this, SWT.NONE);
        l.setText("Hour:");
        hour = new Combo(this, SWT.BORDER);
        for (int h = 0; h < 24; ++h) {
            hour.add(String.format("%02d", h));
        }
        hour.setText(hourStr);

        l = new Label(this, SWT.NONE);
        l.setText("min:");
        minutes = new Combo(this, SWT.BORDER);
        minutes.add("00");
        for (int m = 10; m < 60; m += 10) {
            minutes.add(String.format("%02d", m));
        }
        minutes.setText(minStr);
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
        datePicker.setDateFormat(new SimpleDateFormat(BioBankPlugin.DATE_FORMAT));
        datePicker.setDate(date);
        return datePicker;
    }

    public String getText() {
        String dateStr = "";
        SimpleDateFormat sdf = new SimpleDateFormat(BioBankPlugin.DATE_FORMAT);
        Date date = datePicker.getDate();
        if (date != null) {
            dateStr = sdf.format(date);
        }

        if ((dateStr.length() == 0) && (hour.getText().length() == 0)
            && (minutes.getText().length() == 0)) {
            return null;
        }

        return dateStr + " " + hour.getText() + ":" + minutes.getText();
    }

}
