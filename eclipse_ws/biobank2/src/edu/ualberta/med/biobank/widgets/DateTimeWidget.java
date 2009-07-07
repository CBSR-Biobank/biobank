
package edu.ualberta.med.biobank.widgets;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import com.gface.date.DatePickerCombo;
import com.gface.date.DatePickerStyle;

public class DateTimeWidget extends Composite {

    public static final String DATE_FORMAT = "yyyy-MM-dd";

    public DateTimeWidget(Composite parent, int style, String value) {
        super(parent, style);
        GridLayout layout = new GridLayout(2, false);
        layout.horizontalSpacing = 10;
        setLayout(layout);
        setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        createDatePickerSection(this, value);
    }

    private Control createDatePickerSection(Composite client, String value) {
        DatePickerCombo datePicker = new DatePickerCombo(client, SWT.BORDER,
            DatePickerStyle.BUTTONS_ON_BOTTOM | DatePickerStyle.YEAR_BUTTONS
                | DatePickerStyle.HIDE_WHEN_NOT_IN_FOCUS);
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
        return datePicker;
    }

}
