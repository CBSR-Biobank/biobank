package edu.ualberta.med.biobank.rcp;

import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.forms.CloseForm;
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
        if (part instanceof CloseForm) {
            // when the form is closed, call the method onClose
            boolean openNext = ((CloseForm) part).onClose();
            if (openNext) {
                try {
                    IWorkbenchPage activePage = PlatformUI.getWorkbench()
                        .getActiveWorkbenchWindow().getActivePage();
                    if (activePage != null) {
                        activePage.showView(PatientAdministrationView.ID);
                        activePage.setEditorAreaVisible(false);
                    }
                } catch (PartInitException e) {
                    SessionManager.getLogger().error(
                        "Error while opening PatientAdministrationView", e);
                }
            }
        }
    }

    @Override
    public void partDeactivated(IWorkbenchPart part) {

    }

    @Override
    public void partOpened(IWorkbenchPart part) {

    }

}
