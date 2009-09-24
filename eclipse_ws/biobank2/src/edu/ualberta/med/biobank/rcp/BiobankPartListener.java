package edu.ualberta.med.biobank.rcp;

import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IWorkbenchPart;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.forms.AbstractPatientAdminForm;

public class BiobankPartListener implements IPartListener {

    @Override
    public void partActivated(IWorkbenchPart part) {
    }

    @Override
    public void partBroughtToTop(IWorkbenchPart part) {

    }

    @Override
    public void partClosed(IWorkbenchPart part) {
        if (part instanceof AbstractPatientAdminForm) {
            try {
                // when the form is closed, call the method onClose
                boolean reallyClose = ((AbstractPatientAdminForm) part)
                    .onClose();
                if (reallyClose) {
                    PatientsAdministrationPerspective.showOnlyPatientView();
                }
            } catch (Exception e) {
                SessionManager.getLogger().error(
                    "Error while opening PatientAdministrationView", e);
            }
        }
        if (part instanceof AbstractPatientAdminForm) {
            SessionManager.getInstance().getSiteCombo().setEnabled(true);
        }
    }

    @Override
    public void partDeactivated(IWorkbenchPart part) {
    }

    @Override
    public void partOpened(IWorkbenchPart part) {
        if (part instanceof AbstractPatientAdminForm) {
            SessionManager.getInstance().getSiteCombo().setEnabled(false);
        }
    }

}
