package edu.ualberta.med.biobank.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.helpers.DebugInitializationHelper;

public class InitExamplesHandler extends AbstractHandler {
    public static final String ID = "edu.ualberta.med.biobank.commands.addClinic";

    public Object execute(ExecutionEvent event) throws ExecutionException {
        new DebugInitializationHelper();
        return null;
    }

    @Override
    public boolean isEnabled() {
        return (SessionManager.getInstance().getSession() != null);
    }
}