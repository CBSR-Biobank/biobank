package edu.ualberta.med.biobank.dialogs.user;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

public class SecurityWizardDialog extends WizardDialog {
    private static final I18n i18n = I18nFactory
        .getI18n(SecurityWizardDialog.class);

    public SecurityWizardDialog(Shell parentShell, IWizard newWizard) {
        super(parentShell, newWizard);
    }

    @SuppressWarnings("nls")
    @Override
    protected void createButtonsForButtonBar(Composite parent) {
        super.createButtonsForButtonBar(parent);

        Button okButton = getButton(IDialogConstants.FINISH_ID);
        okButton.setText(i18n.trc(
            "User, Role, and Group Wizard save/ finish button name", "Save"));
    }
}
