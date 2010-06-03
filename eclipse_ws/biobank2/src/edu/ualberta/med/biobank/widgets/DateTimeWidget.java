package edu.ualberta.med.biobank.widgets;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.eclipse.nebula.widgets.cdatetime.CDT;
import org.eclipse.nebula.widgets.cdatetime.CDateTime;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DateTime;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

import edu.ualberta.med.biobank.BioBankPlugin;

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

    private CDateTime dateEntry;

    private Button dateButton;

    private List<ModifyListener> modifyListeners = new ArrayList<ModifyListener>();

    /*
     * Allow date to be null. it typeShown == SWT.DATE, show only date; if
     * typeShown == SWT.TIME, show only time otherwise show both of them
     */
    public DateTimeWidget(Composite parent, int style, Date date) {
        super(parent, style);

        GridLayout layout = new GridLayout(3, false);
        layout.horizontalSpacing = 0;
        layout.marginWidth = 0;
        layout.verticalSpacing = 0;
        setLayout(layout);

        setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));

        dateEntry = new CDateTime(this, CDT.CLOCK_24_HOUR | CDT.BORDER
            | CDT.CLOCK_DISCRETE | style);
        if ((style & SWT.TIME) != 0 && (style & SWT.DATE) != 0)
            dateEntry.setPattern("yyyy-MM-dd  HH:mm");
        else if ((style & SWT.TIME) != 0)
            dateEntry.setPattern("HH:mm");
        else
            dateEntry.setPattern("yyyy-MM-dd");

        Point size = dateEntry.computeSize(SWT.DEFAULT, SWT.DEFAULT);
        GridData gd = new GridData();
        gd.widthHint = size.x + 10;
        gd.heightHint = size.y;
        dateEntry.setLayoutData(gd);

        dateButton = new Button(this, SWT.NONE);
        dateButton.setImage(BioBankPlugin.getDefault().getImageRegistry().get(
            BioBankPlugin.IMG_CALENDAR));
        dateButton.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseUp(MouseEvent e) {
                final Shell dialog = new Shell(PlatformUI.getWorkbench()
                    .getActiveWorkbenchWindow().getShell(), SWT.DIALOG_TRIM);
                dialog.setLayout(new GridLayout(3, false));

                final DateTime calendar = new DateTime(dialog, SWT.CALENDAR
                    | SWT.BORDER);
                if (dateEntry.getSelection() != null) {
                    Calendar c = Calendar.getInstance();
                    c.setTime(dateEntry.getSelection());
                    calendar.setDate(c.get(Calendar.YEAR), c
                        .get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
                }
                new Label(dialog, SWT.BORDER);

                calendar.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseDoubleClick(MouseEvent e) {

                        Calendar c = Calendar.getInstance();
                        if (dateEntry.getSelection() != null)
                            c.setTime(dateEntry.getSelection());
                        c.set(Calendar.DAY_OF_MONTH, calendar.getDay());
                        c.set(Calendar.MONTH, calendar.getMonth());
                        c.set(Calendar.YEAR, calendar.getYear());
                        dateEntry.setSelection(c.getTime());
                        fireModifyListeners();
                        dialog.close();
                    }
                });
                dialog.pack();
                dialog.open();

            }

        });

        if ((style & SWT.DATE) == 0)
            dateButton.setVisible(false);

        dateEntry.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                fireModifyListeners();
            }
        });
        if (date != null) {
            setDate(date);
        }
    }

    public String getText() {
        return getDate().toString();
    }

    public Date getDate() {
        Calendar cal = new GregorianCalendar();
        if (dateEntry.getSelection() != null) {
            cal.setTime(dateEntry.getSelection());
            return cal.getTime();
        }
        return null;
    }

    public void setDate(Date date) {
        if (date == null)
            return;

        Calendar cal = new GregorianCalendar();
        cal.setTime(date);
        dateEntry.setSelection(date);
    }

    public void addModifyListener(ModifyListener modifyListener) {
        modifyListeners.add(modifyListener);
    }

    protected void fireModifyListeners() {
        Event event = new Event();
        event.type = SWT.Modify;
        event.widget = this;
        ModifyEvent modifyEvent = new ModifyEvent(event);
        for (ModifyListener listener : modifyListeners) {
            listener.modifyText(modifyEvent);
        }
    }

    public void removeModifyListener(ModifyListener listener) {
        modifyListeners.remove(listener);
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        if (dateEntry != null) {
            dateEntry.setEnabled(enabled);
        }
    }
}
