package edu.ualberta.med.biobank.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;

import edu.ualberta.med.biobank.BioBankPlugin;
import edu.ualberta.med.biobank.SessionManager;

public class LogoutHandler extends AbstractHandler {

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        IWorkbench workbench = BioBankPlugin.getDefault().getWorkbench();
        IWorkbenchPage activePage = workbench.getActiveWorkbenchWindow()
            .getActivePage();
        // close all editors
        if (activePage.closeAllEditors(true))
            try {
                SessionManager.getInstance().deleteSession();
            } catch (Exception e) {
                e.printStackTrace();
            }
        return null;
    }
}
