package edu.ualberta.med.biobank.dialogs;

import java.text.DateFormat;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.gface.date.DatePicker;
import com.gface.date.DateSelectedEvent;
import com.gface.date.DateSelectionListener;

public class DatePickerDlg extends Dialog {

    private String title;
    private String prompt;
    private Text date;
    private DatePicker datePicker;

    public DatePickerDlg(Shell parentShell, String title, String prompt) {
        super(parentShell);
        this.title = title;
        this.prompt = prompt;
        
    }
    
    protected void configureShell(Shell shell) {
        super.configureShell(shell);
        shell.setText(title);
    }
    
    protected Control createContents(Composite parent) {
        Control contents = super.createContents(parent);
        return contents;
    }
    
    protected Control createDialogArea(Composite parent) { 
        Composite parentComposite = (Composite) super.createDialogArea(parent);  
        Composite contents = new Composite(parentComposite, SWT.NONE);
        
        Label p = new Label(contents, SWT.LEFT);
        p.setText(prompt);
        
        date = new Text(contents, SWT.BORDER); 
        date.setEditable(false);
        
        datePicker = new DatePicker(contents, SWT.BORDER | SWT.LINE_SOLID);
        datePicker.addDateSelectionListener(new DateSelectionListener() {
            @Override
            public void dateSelected(DateSelectedEvent e) {
                DateFormat df = DateFormat.getDateInstance(DateFormat.LONG,
                        datePicker.getLocale());
                date.setText(df.format(e.date));
            }
        });
        GridData gd = new GridData(GridData.GRAB_HORIZONTAL
            | GridData.FILL_HORIZONTAL);
        date.setLayoutData(gd);
        return contents;
    }
}
