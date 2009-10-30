package edu.ualberta.med.biobank.widgets;

import java.util.Date;

import org.eclipse.nebula.widgets.cdatetime.CDT;
import org.eclipse.nebula.widgets.cdatetime.CDateTime;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

import edu.ualberta.med.biobank.common.formatters.DateFormatter;

/**
 * Wrapper for around Nebula's CDateTime widget.
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

    CDateTime cdt;

    /**
     * Allow date to be null.
     */
    public DateTimeWidget(Composite parent, int style, Date date) {
        super(parent, style);

        setLayout(new GridLayout(1, false));
        setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));

        cdt = new CDateTime(this, CDT.BORDER | CDT.COMPACT | CDT.DROP_DOWN
            | CDT.DATE_LONG | CDT.TIME_MEDIUM);
        cdt.setPattern(DateFormatter.DATE_TIME_FORMAT);
        cdt.setSelection(date);
        cdt.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
    }

    public void addSelectionListener(SelectionListener listener) {
        cdt.addSelectionListener(listener);
    }

    public String getText() {
        String text = cdt.getText();
        if (text.equals("<choose date>"))
            return null;
        return text;
    }

    public Date getDate() {
        String text = cdt.getText();
        if (text.equals("<choose date>"))
            return null;

        return DateFormatter.parseToDateTime(cdt.getText());
    }

    public void setDate(Date date) {
        cdt.setSelection(date);
    }
}
