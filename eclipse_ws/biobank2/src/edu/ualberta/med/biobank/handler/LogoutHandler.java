package edu.ualberta.med.biobank.handler;

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

    @Override
    public boolean isEnabled() {
        return (SessionManager.getInstance().getSession() != null);
    }

    @Override
    public boolean isHandled() {
        return isEnabled();
    }

}
