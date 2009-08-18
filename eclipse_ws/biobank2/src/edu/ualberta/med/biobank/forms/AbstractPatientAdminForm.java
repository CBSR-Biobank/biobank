package edu.ualberta.med.biobank.forms;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.PlatformUI;

import edu.ualberta.med.biobank.BioBankPlugin;

public abstract class AbstractPatientAdminForm extends BiobankEntryForm
    implements CloseForm {

    /**
     * Indicate if this form has been saved
     */
    private boolean isSaved = false;

    /*
     * @see edu.ualberta.med.biobank.forms.CloseForm#onClose()
     */
    public boolean onClose() {
        if (!isSaved && BioBankPlugin.isAskPrint()) {
            // ask print only if this for is not saved. If it is saved, a new
            // form is opened. The printing only occurs when the form is close
            // with the close button
            boolean doPrint = MessageDialog.openQuestion(PlatformUI
                .getWorkbench().getActiveWorkbenchWindow().getShell(), "Print",
                "Do you want to print information ?");
            if (doPrint) {
                print();
            }
            return true;
        }
        return false;
    }

    protected abstract void print();

    protected void setSaved(boolean saved) {
        this.isSaved = saved;
    }
}
