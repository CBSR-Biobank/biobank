package edu.ualberta.med.biobank.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.WorkbenchException;

import edu.ualberta.med.biobank.BiobankPlugin;
import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.SessionSecurityHelper;
import edu.ualberta.med.biobank.common.wrappers.CollectionEventWrapper;
import edu.ualberta.med.biobank.common.wrappers.ProcessingEventWrapper;
import edu.ualberta.med.biobank.common.wrappers.ResearchGroupWrapper;
import edu.ualberta.med.biobank.rcp.perspective.ProcessingPerspective;

public class ProcessingAdministrationHandler extends AbstractHandler implements
    IHandler {

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        IWorkbench workbench = BiobankPlugin.getDefault().getWorkbench();
        try {
            if (workbench.getActiveWorkbenchWindow().getActivePage()
                .closeAllEditors(true))
                workbench.showPerspective(ProcessingPerspective.ID,
                    workbench.getActiveWorkbenchWindow());
        } catch (WorkbenchException e) {
            throw new ExecutionException(
                Messages.ProcessingAdministrationHandler_perspective_error, e);
        }
        return null;
    }

    @Override
    public boolean isEnabled() {
        return SessionManager.getInstance().isConnected()
            && SessionManager.getUser().getCurrentWorkingCenter() != null
            // FIXME do we want this test on research groups or do we just test
            // the privileges ?
            && !(SessionManager.getUser().getCurrentWorkingCenter() instanceof ResearchGroupWrapper)
            && SessionManager.isAllowed(
                SessionSecurityHelper.SPECIMEN_ASSIGN_KEY_DESC,
                SessionSecurityHelper.CLINIC_SHIPMENT_KEY_DESC,
                SessionSecurityHelper.DISPATCH_RECEIVE_KEY_DESC,
                SessionSecurityHelper.DISPATCH_SEND_KEY_DESC,
                SessionSecurityHelper.REQUEST_RECEIVE_DESC,
                SessionSecurityHelper.SPECIMEN_LINK_KEY_DESC,
                // FIXME this is not very nice when we need to check access to a
                // class...
                new CollectionEventWrapper(null).getWrappedClass()
                    .getSimpleName(), new ProcessingEventWrapper(null)
                    .getWrappedClass().getSimpleName());
    }
}
