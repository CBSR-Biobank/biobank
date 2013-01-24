package edu.ualberta.med.biobank.dialogs.scanmanually;

import java.util.Map;
import java.util.Set;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Shell;

/**
 * This class extends WizardDialog since we always set the default button to be the wizard's "Next"
 * button.
 * 
 * When the WizardDialog receives an "Enter" key from the keyboard, the default button is activated.
 * WizardDialog makes the "Finish" button the default button. In this implementation we always
 * want the "Next" button to be the default.
 * 
 * @author Nelson Loyola
 * 
 */
public class ScanTubesManuallyWizardDialog extends WizardDialog {

    ScanTubesManuallyWizardDialog(Shell parentShell, IWizard newWizard) {
        super(parentShell, newWizard);
    }

    @Override
    public void updateButtons() {
        super.updateButtons();
        getShell().setDefaultButton(getButton(IDialogConstants.NEXT_ID));
    }

    /**
     * Displays the wizard and returns the results.
     * 
     * @param parentShell the parent SWT shell
     * @param labels the labels that the user should be prompted for. The order is important.
     * @param existingInventoryIdsByLabel a map of inventory IDs that the user should not enter. The
     *            key for the map is the position label where this inventory ID is present.
     * @return A map of the inventory IDs entered by the user. The key is the label the inventory ID
     *         corresponds to.
     */
    public static Map<String, String> getInventoryIds(Shell parentShell, Set<String> labels,
        Map<String, String> existingInventoryIdsByLabel) {
        ScanTubesManuallyWizard wizard = new ScanTubesManuallyWizard(labels,
            existingInventoryIdsByLabel);
        ScanTubesManuallyWizardDialog dialog =
            new ScanTubesManuallyWizardDialog(parentShell, wizard);
        dialog.create();
        dialog.open();
        return wizard.getInventoryIdsByLabel();
    }

}
