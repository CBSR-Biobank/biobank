package edu.ualberta.med.biobank.widgets;

import java.text.ParseException;
import java.util.Date;

import org.eclipse.nebula.widgets.cdatetime.CDT;
import org.eclipse.nebula.widgets.cdatetime.CDateTime;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

import edu.ualberta.med.biobank.BioBankPlugin;

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
        cdt.setPattern(BioBankPlugin.DATE_TIME_FORMAT);
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

        try {
            return BioBankPlugin.getDateFormatter().parse(cdt.getText());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

}
