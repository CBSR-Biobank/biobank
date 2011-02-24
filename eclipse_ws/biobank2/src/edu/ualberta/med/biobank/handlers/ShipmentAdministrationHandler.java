package edu.ualberta.med.biobank.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.WorkbenchException;

import edu.ualberta.med.biobank.BioBankPlugin;
import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.wrappers.CollectionEventWrapper;
import edu.ualberta.med.biobank.rcp.perspective.ProcessingPerspective;

public class ShipmentAdministrationHandler extends AbstractHandler implements
    IHandler {

    public final static String ID = "edu.ualberta.med.biobank.commands.shipmentAdmin";

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        IWorkbench workbench = BioBankPlugin.getDefault().getWorkbench();
        try {
            if (workbench.getActiveWorkbenchWindow().getActivePage()
                .closeAllEditors(true))
                workbench.showPerspective(ProcessingPerspective.ID,
                    workbench.getActiveWorkbenchWindow());
        } catch (WorkbenchException e) {
            throw new ExecutionException(
                "Error while opening shipment perspective", e);
        }
        return null;
    }

    @Override
    public boolean isEnabled() {
        return SessionManager.canView(CollectionEventWrapper.class)
            && SessionManager.getInstance().getSession() != null;
    }
}
