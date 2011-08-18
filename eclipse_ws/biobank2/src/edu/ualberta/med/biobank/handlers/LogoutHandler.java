package edu.ualberta.med.biobank.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;

import edu.ualberta.med.biobank.BiobankPlugin;
import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.gui.common.BgcLogger;

public class LogoutHandler extends AbstractHandler {

    private static BgcLogger logger = BgcLogger
        .getLogger(LogoutHandler.class.getName());

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        IWorkbench workbench = BiobankPlugin.getDefault().getWorkbench();
        IWorkbenchPage activePage = workbench.getActiveWorkbenchWindow()
            .getActivePage();
        // close all editors
        if (activePage.closeAllEditors(true))
            try {
                SessionManager.getInstance().deleteSession();
            } catch (Exception e) {
                logger.error("Error while deleting the session", e); //$NON-NLS-1$
            }
        return null;
    }
}
