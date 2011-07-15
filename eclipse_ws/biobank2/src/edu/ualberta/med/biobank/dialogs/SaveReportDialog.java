package edu.ualberta.med.biobank.dialogs;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

import edu.ualberta.med.biobank.gui.common.dialogs.BgcBaseDialog;
import edu.ualberta.med.biobank.gui.common.widgets.BgcBaseText;

/**
 * Allows the user to move a container and its contents to a new location
 */
public class SaveReportDialog extends BgcBaseDialog {

    private String fileName;

    public SaveReportDialog(Shell parent) {
        super(parent);
    }

    @Override
    protected String getDialogShellTitle() {
        return Messages.SaveReportDialog_title;
    }

    @Override
    protected String getTitleAreaMessage() {
        return Messages.SaveReportDialog_description;
    }

    @Override
    protected String getTitleAreaTitle() {
        return Messages.SaveReportDialog_titleArea;
    }

    @Override
    protected void createDialogAreaInternal(Composite parent) {
        Composite contents = new Composite(parent, SWT.NONE);
        contents.setLayout(new GridLayout(2, false));
        contents.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        createBoundWidgetWithLabel(contents, BgcBaseText.class, SWT.FILL,
            Messages.SaveReportDialog_name_label, null, this, "fileName", null); //$NON-NLS-1$
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String name) {
        fileName = name;
    }

}
