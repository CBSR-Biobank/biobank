package edu.ualberta.med.biobank.wizards;

import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;

public abstract class BiobankWizard extends Wizard {
    public abstract boolean performNext(IWizardPage page);
}
