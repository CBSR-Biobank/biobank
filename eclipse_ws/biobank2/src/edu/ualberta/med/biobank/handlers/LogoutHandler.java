package edu.ualberta.med.biobank.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.PlatformUI;

import edu.ualberta.med.biobank.SessionManager;

public class LogoutHandler extends AbstractHandler {

    public Object execute(ExecutionEvent event) throws ExecutionException {
        SessionManager.getInstance().deleteSession();

        // close all editors
        PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
            .closeAllEditors(true);
        return null;
    }
}
