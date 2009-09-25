package edu.ualberta.med.biobank.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.WorkbenchException;
import org.eclipse.ui.handlers.HandlerUtil;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.rcp.ReportsPerspective;

public class ReportsHandler extends AbstractHandler {

    public Object execute(ExecutionEvent event) throws ExecutionException {
        IWorkbenchWindow window = HandlerUtil
            .getActiveWorkbenchWindowChecked(event);
        try {
            window.getWorkbench()
                .showPerspective(ReportsPerspective.ID, window);
        } catch (WorkbenchException e) {
            throw new ExecutionException(
                "Perspective could not be initialized", e);
        }
        return null;

    }

    @Override
    public boolean isEnabled() {
        return (SessionManager.getInstance().getSession() != null);
    }
}
