package edu.ualberta.med.biobank.rcp;

import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.forms.AbstractPatientAdminForm;
import edu.ualberta.med.biobank.views.PatientAdministrationView;

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
            // when the form is closed, call the method onClose
            boolean reallyClose = ((AbstractPatientAdminForm) part).onClose();
            if (reallyClose) {
                try {
                    IWorkbenchPage activePage = PlatformUI.getWorkbench()
                        .getActiveWorkbenchWindow().getActivePage();
                    if (activePage != null) {
                        for (IViewReference ref : activePage
                            .getViewReferences()) {
                            activePage.hideView(ref);
                        }
                        activePage.showView(PatientAdministrationView.ID);
                    }
                } catch (PartInitException e) {
                    SessionManager.getLogger().error(
                        "Error while opening PatientAdministrationView", e);
                }
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
