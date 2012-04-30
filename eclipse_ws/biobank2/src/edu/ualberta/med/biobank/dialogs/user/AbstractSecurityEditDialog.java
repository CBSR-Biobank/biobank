package edu.ualberta.med.biobank.dialogs.user;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

import edu.ualberta.med.biobank.gui.common.dialogs.BgcBaseDialog;

public abstract class AbstractSecurityEditDialog extends BgcBaseDialog {
    public AbstractSecurityEditDialog(Shell parentShell) {
        super(parentShell);
    }

    @Override
    protected void createButtonsForButtonBar(Composite parent) {
        super.createButtonsForButtonBar(parent);

        Button okButton = getButton(IDialogConstants.OK_ID);
        okButton.setText("Save");
    }
}
