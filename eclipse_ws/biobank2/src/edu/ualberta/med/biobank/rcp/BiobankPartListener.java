package edu.ualberta.med.biobank.rcp;

import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.WorkbenchException;

import edu.ualberta.med.biobank.BioBankPlugin;
import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.forms.AbstractAliquotAdminForm;
import edu.ualberta.med.biobank.forms.BiobankFormBase;
import edu.ualberta.med.biobank.logs.BiobankLogger;

public class BiobankPartListener implements IPartListener {

    private static BiobankLogger logger = BiobankLogger
        .getLogger(BiobankPartListener.class.getName());

    @Override
    public void partActivated(IWorkbenchPart part) {
    }

    @Override
    public void partBroughtToTop(IWorkbenchPart part) {
        if (part instanceof BiobankFormBase) {
            ((BiobankFormBase) part).setBroughtToTop();
        }
    }

    @Override
    public void partClosed(IWorkbenchPart part) {
        IWorkbench workbench = BioBankPlugin.getDefault().getWorkbench();
        if (!workbench.isClosing() && part instanceof AbstractAliquotAdminForm) {
            // when the form is closed, call the method onClose
            boolean reallyClose = ((AbstractAliquotAdminForm) part).onClose();
            if (reallyClose) {
                try {
                    workbench.showPerspective(
                        PatientsAdministrationPerspective.ID, workbench
                            .getActiveWorkbenchWindow());
                } catch (WorkbenchException e) {
                    logger.error("Error while opening patients perpective", e);
                }
            }
            SessionManager.getInstance().unlockSite();
        }
        if (part instanceof BiobankFormBase) {
            ((BiobankFormBase) part).setDeactivated();
        }
    }

    @Override
    public void partDeactivated(IWorkbenchPart part) {

    }

    @Override
    public void partOpened(IWorkbenchPart part) {
        if (part instanceof AbstractAliquotAdminForm) {
            SessionManager.getInstance().lockSite();
        }
    }

}
