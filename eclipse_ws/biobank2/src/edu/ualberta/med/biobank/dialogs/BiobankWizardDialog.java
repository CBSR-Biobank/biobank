package edu.ualberta.med.biobank.dialogs;

import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Shell;

import edu.ualberta.med.biobank.gui.common.dialogs.BgcWizard;

public class BiobankWizardDialog extends WizardDialog {

    public BiobankWizardDialog(Shell parentShell, IWizard newWizard) {
        super(parentShell, newWizard);
    }

    @Override
    protected void nextPressed() {
        IWizardPage page = getCurrentPage();
        IWizard wizard = this.getWizard();
        if (wizard instanceof BgcWizard) {
            BgcWizard biobankWizard = (BgcWizard) wizard;
            if (!biobankWizard.performNext(page)) {
                return;
            }
        }

        super.nextPressed();
    }
}
