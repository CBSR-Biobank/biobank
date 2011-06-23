package edu.ualberta.med.biobank.rcp;

import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.WorkbenchException;

import edu.ualberta.med.biobank.BiobankPlugin;
import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.forms.BiobankEntryForm;
import edu.ualberta.med.biobank.forms.BiobankFormBase;
import edu.ualberta.med.biobank.forms.linkassign.AbstractSpecimenAdminForm;
import edu.ualberta.med.biobank.gui.common.BgcLogger;
import edu.ualberta.med.biobank.rcp.perspective.ProcessingPerspective;
import edu.ualberta.med.biobank.views.AbstractViewWithAdapterTree;

public class BiobankPartListener implements IPartListener {

    private static BgcLogger logger = BgcLogger
        .getLogger(BiobankPartListener.class.getName());

    @Override
    public void partActivated(IWorkbenchPart part) {
        if (part instanceof AbstractViewWithAdapterTree)
            SessionManager
                .setCurrentAdministrationViewId(((AbstractViewWithAdapterTree) part)
                    .getId());
    }

    @Override
    public void partBroughtToTop(IWorkbenchPart part) {
        if (part instanceof BiobankFormBase) {
            ((BiobankFormBase) part).setBroughtToTop();
        }
    }

    @Override
    public void partClosed(IWorkbenchPart part) {
        IWorkbench workbench = BiobankPlugin.getDefault().getWorkbench();
        if (!workbench.isClosing() && part instanceof AbstractSpecimenAdminForm) {
            // when the form is closed, call the method onClose
            boolean reallyClose = ((AbstractSpecimenAdminForm) part).onClose();
            if (reallyClose) {
                try {
                    workbench.showPerspective(ProcessingPerspective.ID,
                        workbench.getActiveWorkbenchWindow());
                } catch (WorkbenchException e) {
                    logger.error("Error while opening patients perpective", e);
                }
            }
        }
        if (part instanceof BiobankFormBase) {
            ((BiobankFormBase) part).setDeactivated();
        }
        if (part instanceof BiobankEntryForm)
            try {
                ((BiobankEntryForm) part).formClosed();
            } catch (Exception e) {
                logger.error("Error in formClosed method", e);
            }
    }

    @Override
    public void partDeactivated(IWorkbenchPart part) {
        //
    }

    @Override
    public void partOpened(IWorkbenchPart part) {
        if (part instanceof AbstractViewWithAdapterTree
            && SessionManager.getInstance().isConnected()) {
            ((AbstractViewWithAdapterTree) part).opened();
        }
    }
}
