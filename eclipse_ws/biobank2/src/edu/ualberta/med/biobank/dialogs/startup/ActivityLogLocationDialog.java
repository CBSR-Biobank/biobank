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
import edu.ualberta.med.biobank.gui.common.dialogs.BgcBaseDialog;
import edu.ualberta.med.biobank.preferences.PreferenceConstants;
import edu.ualberta.med.biobank.utils.FilePromptUtil;

public class ActivityLogLocationDialog extends BgcBaseDialog {

    private Text activityLogDirText;

    private Button browseBtn;

    private Button activityLogDirBtn;

    public ActivityLogLocationDialog(Shell parentShell) {
        super(parentShell);
    }

    @Override
    protected String getDialogShellTitle() {
        return Messages.ActivityLogLocationDialog_title;
    }

    @Override
    protected String getTitleAreaMessage() {
        return Messages.ActivityLogLocationDialog_description;
    }

    @Override
    protected String getTitleAreaTitle() {
        return Messages.ActivityLogLocationDialog_title;
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
        activityLogDirBtn.setText(Messages.ActivityLogLocationDialog_button_save_label);
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
        createFileLocationSelector(contents, Messages.ActivityLogLocationDialog_folder_selection_label);
    }

    private void createFileLocationSelector(final Composite parent,
        String labelText) {
        final Composite fileSelectionComposite = new Composite(parent, SWT.NONE);
        GridLayout layout = new GridLayout(3, false);
        fileSelectionComposite.setLayout(layout);
        fileSelectionComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL,
            true, true));

        createLabel(fileSelectionComposite, labelText);

        final String biobankDir = System.getProperty("user.home") //$NON-NLS-1$
            + System.getProperty("file.separator") + "biobank"; //$NON-NLS-1$ //$NON-NLS-2$
        activityLogDirText = new Text(fileSelectionComposite, SWT.BORDER
            | SWT.FILL);
        activityLogDirText.setLayoutData(new GridData(GridData.FILL,
            GridData.FILL, true, false));
        activityLogDirText.setText(biobankDir);

        browseBtn = new Button(fileSelectionComposite, SWT.BUTTON1);
        browseBtn.setText(Messages.ActivityLogLocationDialog_browse_button_label);
        browseBtn.addSelectionListener(new SelectionListener() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                DirectoryDialog fd = new DirectoryDialog(fileSelectionComposite
                    .getShell(), SWT.SAVE);
                fd.setText(Messages.ActivityLogLocationDialog_directory_select_label);
                fd.setFilterPath(biobankDir);
                String selected = fd.open();
                if (selected != null) {
                    activityLogDirText.setText(selected);
                    File f = new File(selected);
                    f.canWrite();
                } else {
                    activityLogDirText.setText(""); //$NON-NLS-1$
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
                .setValue(PreferenceConstants.LINK_ASSIGN_ACTIVITY_LOG_PATH, ""); //$NON-NLS-1$
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
        label.setText(labelText + ": "); //$NON-NLS-1$
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