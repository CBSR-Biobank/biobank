package edu.ualberta.med.biobank.dialogs.startup;

import java.io.File;

import org.eclipse.jface.dialogs.IDialogConstants;
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

import edu.ualberta.med.biobank.BiobankPlugin;
import edu.ualberta.med.biobank.gui.common.dialogs.BiobankDialog;
import edu.ualberta.med.biobank.preferences.PreferenceConstants;
import edu.ualberta.med.biobank.utils.FilePromptUtil;

public class ActivityLogLocationDialog extends BiobankDialog {

    private Text activityLogDirText;

    private Button browseBtn;

    private Button activityLogDirBtn;

    public ActivityLogLocationDialog(Shell parentShell) {
        super(parentShell);
    }

    @Override
    protected String getDialogShellTitle() {
        return "Activity Logs Location";
    }

    @Override
    protected String getTitleAreaMessage() {
        return "Do you wish to save activity logs to files?";
    }

    @Override
    protected String getTitleAreaTitle() {
        return "Activity Logs Location";
    }

    @Override
    protected void createDialogAreaInternal(Composite parent) throws Exception {
        Composite contents = new Composite(parent, SWT.NONE);
        GridLayout layout = new GridLayout(1, false);
        layout.marginTop = 5;
        layout.marginLeft = 2;
        layout.verticalSpacing = 3;
        contents.setLayout(layout);
        contents.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        activityLogDirBtn = new Button(contents, SWT.CHECK);
        activityLogDirBtn.setText("Save activity logs to files");
        activityLogDirBtn.setSelection(true);
        activityLogDirBtn.addSelectionListener(new SelectionListener() {

            @Override
            public void widgetSelected(SelectionEvent e) {

                boolean saveActivityLogs = activityLogDirBtn.getSelection();
                activityLogDirText.setEditable(saveActivityLogs);
                browseBtn.setEnabled(saveActivityLogs);
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
            }
        });
        createFileLocationSelector(contents, "Folder");
    }

    private void createFileLocationSelector(final Composite parent,
        String labelText) {
        final Composite fileSelectionComposite = new Composite(parent, SWT.NONE);
        GridLayout layout = new GridLayout(3, false);
        fileSelectionComposite.setLayout(layout);
        fileSelectionComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL,
            true, true));

        createLabel(fileSelectionComposite, labelText);

        final String biobankDir = System.getProperty("user.home")
            + System.getProperty("file.separator") + "biobank";
        activityLogDirText = new Text(fileSelectionComposite, SWT.BORDER
            | SWT.FILL);
        activityLogDirText.setLayoutData(new GridData(GridData.FILL,
            GridData.FILL, true, false));
        activityLogDirText.setText(biobankDir);

        browseBtn = new Button(fileSelectionComposite, SWT.BUTTON1);
        browseBtn.setText("  Browse...  ");
        browseBtn.addSelectionListener(new SelectionListener() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                DirectoryDialog fd = new DirectoryDialog(fileSelectionComposite
                    .getShell(), SWT.SAVE);
                fd.setText("Select Directory");
                fd.setFilterPath(biobankDir);
                String selected = fd.open();
                if (selected != null) {
                    activityLogDirText.setText(selected);
                    File f = new File(selected);
                    f.canWrite();
                } else {
                    activityLogDirText.setText("");
                }
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
            }
        });
    }

    @Override
    protected void okPressed() {

        String activityLogDir = activityLogDirText.getText();

        if (activityLogDirBtn.getSelection()) {

            File activityLogDirFile = new File(activityLogDir);

            if (!FilePromptUtil.isWritableDir(activityLogDirFile)) {
                return;
            }

            BiobankPlugin
                .getDefault()
                .getPreferenceStore()
                .setValue(PreferenceConstants.LINK_ASSIGN_ACTIVITY_LOG_PATH,
                    activityLogDir.toString());
            BiobankPlugin
                .getDefault()
                .getPreferenceStore()
                .setValue(
                    PreferenceConstants.LINK_ASSIGN_ACTIVITY_LOG_INTO_FILE,
                    true);
            super.okPressed();

        } else { /* don't save to a log file */
            BiobankPlugin
                .getDefault()
                .getPreferenceStore()
                .setValue(PreferenceConstants.LINK_ASSIGN_ACTIVITY_LOG_PATH, "");
            BiobankPlugin
                .getDefault()
                .getPreferenceStore()
                .setValue(
                    PreferenceConstants.LINK_ASSIGN_ACTIVITY_LOG_INTO_FILE,
                    false);
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