package edu.ualberta.med.biobank.dialogs;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

import edu.ualberta.med.biobank.gui.common.dialogs.BiobankDialog;
import edu.ualberta.med.biobank.gui.common.widgets.BiobankText;

/**
 * Allows the user to move a container and its contents to a new location
 */
public class SaveReportDialog extends BiobankDialog {

    private String fileName;

    public SaveReportDialog(Shell parent) {
        super(parent);
    }

    @Override
    protected String getDialogShellTitle() {
        return "Save";
    }

    @Override
    protected String getTitleAreaMessage() {
        return "Please enter a name for this report.";
    }

    @Override
    protected String getTitleAreaTitle() {
        return "Enter Name";
    }

    @Override
    protected void createDialogAreaInternal(Composite parent) {
        Composite contents = new Composite(parent, SWT.NONE);
        contents.setLayout(new GridLayout(2, false));
        contents.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        createBoundWidgetWithLabel(contents, BiobankText.class, SWT.FILL,
            "Name", null, this, "fileName", null);
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String name) {
        fileName = name;
    }

}
