package edu.ualberta.med.biobank.dialogs.user;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import edu.ualberta.med.biobank.gui.common.dialogs.BgcBaseDialog;

public abstract class AbstractSecurityEditDialog extends BgcBaseDialog {
    private static final I18n i18n = I18nFactory
        .getI18n(AbstractSecurityEditDialog.class);

    public AbstractSecurityEditDialog(Shell parentShell) {
        super(parentShell);
    }

    @SuppressWarnings("nls")
    @Override
    protected void createButtonsForButtonBar(Composite parent) {
        super.createButtonsForButtonBar(parent);

        Button okButton = getButton(IDialogConstants.OK_ID);
        okButton.setText(i18n.trc("Button", "Save"));
    }
}
