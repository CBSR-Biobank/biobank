package edu.ualberta.med.biobank.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.views.AbstractViewWithTree;

public class SessionReloadHandler extends AbstractHandler implements IHandler {

    public Object execute(ExecutionEvent event) throws ExecutionException {
        AbstractViewWithTree view = SessionManager.getCurrentViewWithTree();
        view.reload();
        return null;
    }

}
