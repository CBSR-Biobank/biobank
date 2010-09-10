package edu.ualberta.med.biobank.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.views.AbstractViewWithAdapterTree;

public class ReloadViewWithTreeHandler extends AbstractHandler implements
    IHandler {

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        AbstractViewWithAdapterTree view = SessionManager
            .getCurrentAdapterViewWithTree();
        if (view != null) {
            view.reload();
        }
        return null;
    }

}
