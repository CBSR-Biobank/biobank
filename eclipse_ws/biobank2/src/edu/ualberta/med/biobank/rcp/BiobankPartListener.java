package edu.ualberta.med.biobank.rcp;

import org.apache.log4j.Logger;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.WorkbenchException;

import edu.ualberta.med.biobank.BioBankPlugin;
import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.forms.AbstractPatientAdminForm;

public class BiobankPartListener implements IPartListener {

    private static Logger LOGGER = Logger.getLogger(BiobankPartListener.class
        .getName());

    @Override
    public void partActivated(IWorkbenchPart part) {
    }

    @Override
    public void partBroughtToTop(IWorkbenchPart part) {
    }

    @Override
    public void partClosed(IWorkbenchPart part) {
        IWorkbench workbench = BioBankPlugin.getDefault().getWorkbench();
        if (!workbench.isClosing() && part instanceof AbstractPatientAdminForm) {
            // when the form is closed, call the method onClose
            boolean reallyClose = ((AbstractPatientAdminForm) part).onClose();
            if (reallyClose) {
                try {
                    workbench.showPerspective(
                        PatientsAdministrationPerspective.ID, workbench
                            .getActiveWorkbenchWindow());
                } catch (WorkbenchException e) {
                    LOGGER.error("Error while opening patients perpective", e);
                }
            }
            SessionManager.getInstance().setSiteManagerEnabled(true);
        }
    }

    @Override
    public void partDeactivated(IWorkbenchPart part) {
    }

    @Override
    public void partOpened(IWorkbenchPart part) {
        if (part instanceof AbstractPatientAdminForm) {
            SessionManager.getInstance().setSiteManagerEnabled(false);
        }
    }

}
