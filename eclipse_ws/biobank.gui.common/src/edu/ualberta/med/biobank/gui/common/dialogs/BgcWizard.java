package edu.ualberta.med.biobank.gui.common.dialogs;

import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;

public abstract class BgcWizard extends Wizard {
    public abstract boolean performNext(IWizardPage page);
}
