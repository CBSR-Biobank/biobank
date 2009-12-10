package edu.ualberta.med.biobank.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.helpers.CbsrConfigurationJob;

public class CbsrConfigurationHandler extends AbstractHandler {

    public Object execute(ExecutionEvent event) throws ExecutionException {
        new CbsrConfigurationJob();
        return null;
    }

    @Override
    public boolean isEnabled() {
        return (SessionManager.getInstance().getSession() != null);
    }
}