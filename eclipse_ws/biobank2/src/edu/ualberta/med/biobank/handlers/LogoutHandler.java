package edu.ualberta.med.biobank.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;

import edu.ualberta.med.biobank.BioBankPlugin;
import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.rcp.MainPerspective;

public class LogoutHandler extends AbstractHandler {

    public Object execute(ExecutionEvent event) throws ExecutionException {
        IWorkbench workbench = BioBankPlugin.getDefault().getWorkbench();
        IWorkbenchPage activePage = workbench.getActiveWorkbenchWindow()
            .getActivePage();
        if (!activePage.getPerspective().getId().equals(MainPerspective.ID)) {
            // close all active views
            // for (IViewReference ref : activePage.getViewReferences()) {
            // activePage.hideView(ref);
            // }
            // try {
            // workbench.showPerspective(MainPerspective.ID, workbench
            // .getActiveWorkbenchWindow());
            // } catch (WorkbenchException e) {
            // LOGGER.error(
            // "Error while opening main perspective", e);
            // }
        }
        // close all editors
        activePage.closeAllEditors(true);
        SessionManager.getInstance().deleteSession();
        return null;
    }
}
