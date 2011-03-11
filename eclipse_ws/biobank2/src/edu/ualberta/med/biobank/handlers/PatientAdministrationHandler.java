package edu.ualberta.med.biobank.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.WorkbenchException;

import edu.ualberta.med.biobank.BiobankPlugin;
import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.wrappers.ProcessingEventWrapper;
import edu.ualberta.med.biobank.rcp.perspective.ProcessingPerspective;

/**
 * This handler open the PatientAdministration perspective
 */
public class PatientAdministrationHandler extends AbstractHandler implements
    IHandler {

    public final static String ID = "edu.ualberta.med.biobank.commands.patientAdmin";

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
                "Error while opening patients perspective", e);
        }
        return null;
    }

    @Override
    public boolean isEnabled() {
        return SessionManager.canView(ProcessingEventWrapper.class)
            && SessionManager.getInstance().getSession() != null;
    }

}
