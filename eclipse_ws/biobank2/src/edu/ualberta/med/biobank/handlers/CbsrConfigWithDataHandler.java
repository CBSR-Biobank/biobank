package edu.ualberta.med.biobank.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.helpers.CbsrConfigWithDataJob;

public class CbsrConfigWithDataHandler extends AbstractHandler {

    public Object execute(ExecutionEvent event) throws ExecutionException {
        new CbsrConfigWithDataJob();
        return null;
    }

    @Override
    public boolean isEnabled() {
        return (SessionManager.getInstance().getSession() != null);
    }
}