package edu.ualberta.med.biobank.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.WorkbenchException;

import edu.ualberta.med.biobank.BiobankPlugin;
import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.security.SecurityFeature;
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
                "Error while opening Processing perpective", e);
        }
        return null;
    }

    @Override
    public boolean isEnabled() {
        return SessionManager.getInstance().isConnected()
            && SessionManager.getUser().canPerformActions(
                SecurityFeature.ASSIGN, SecurityFeature.CLINIC_SHIPMENT,
                SecurityFeature.COLLECTION_EVENT,
                SecurityFeature.DISPATCH_REQUEST, SecurityFeature.LINK,
                SecurityFeature.PROCESSING_EVENT);
    }
}
