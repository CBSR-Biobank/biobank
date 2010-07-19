package edu.ualberta.med.biobank.dialogs;

import java.io.File;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import edu.ualberta.med.biobank.BioBankPlugin;
import edu.ualberta.med.biobank.preferences.PreferenceConstants;

public class ActivityLogDialog extends TitleAreaDialog {

    Text activityLogFileText;

    public ActivityLogDialog(Shell parentShell) {
        super(parentShell);

        GridLayout gl = new GridLayout(3, true);

        parentShell.setLayout(gl);

        Label lbl = new Label(parentShell, SWT.NONE);
        lbl.setText("Activity Logs File");
    }

    @Override
    protected Control createContents(Composite parent) {
        Control contents = super.createContents(parent);
        setTitle("Where to save activity logs?");
        setMessage("Please select a file to save your activity log data into.");
        return contents;
    }

    @Override
    protected void configureShell(Shell shell) {
        super.configureShell(shell);
        shell.setText("Activity Log Location");
    }

    @Override
    protected Control createDialogArea(Composite parent) {
        Composite contents = new Composite(parent, SWT.NONE);
        GridLayout layout = new GridLayout(3, false);
        layout.marginTop = 10;
        contents.setLayout(layout);
        contents.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        activityLogFileText = createFileLocationSelector(contents, "&Log file");

        return contents;
    }

    private Text createFileLocationSelector(final Composite parent,
        String labelText) {
        createLabel(parent, labelText);

        final String defaultPath;
        String biobank2Dir = System.getProperty("user.home")
            + System.getProperty("file.separator") + "biobank2";

        if (new File(biobank2Dir).exists())
            defaultPath = biobank2Dir;
        else
            defaultPath = System.getProperty("user.home");

        final Text text = new Text(parent, SWT.BORDER | SWT.FILL);
        text.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true,
            false));
        text.setText(defaultPath);
        Button btn = new Button(parent, SWT.BUTTON1);
        btn.setText("  Browse...  ");
        btn.addSelectionListener(new SelectionListener() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                DirectoryDialog fd = new DirectoryDialog(parent.getShell(),
                    SWT.SAVE);
                fd.setText("Select Directory");
                fd.setFilterPath(defaultPath);
                String selected = fd.open();
                if (selected != null)
                    text.setText(selected);

            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
                // TODO Auto-generated method stub
            }
        });
        return text;
    }

    @Override
    protected void okPressed() {

        String activityLogDir = activityLogFileText.getText();
        if (new File(activityLogDir).exists()) {
            BioBankPlugin
                .getDefault()
                .getPreferenceStore()
                .setValue(PreferenceConstants.LINK_ASSIGN_ACTIVITY_LOG_PATH,
                    activityLogDir.toString());
            super.okPressed();
        }

    }

    private Label createLabel(Composite parent, String labelText) {
        Label label = new Label(parent, SWT.NONE);
        label.setText(labelText + ": ");
        label.setLayoutData(new GridData(GridData.END, GridData.CENTER, false,
            false));
        return label;
    }

    @Override
    protected Control createButtonBar(Composite parent) {
        Control contents = super.createButtonBar(parent);
        this.getButton(IDialogConstants.CANCEL_ID).setEnabled(false);
        return contents;
    }
}