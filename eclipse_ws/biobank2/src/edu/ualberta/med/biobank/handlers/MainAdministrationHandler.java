package edu.ualberta.med.biobank.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.WorkbenchException;

import edu.ualberta.med.biobank.BiobankPlugin;
import edu.ualberta.med.biobank.rcp.perspective.MainPerspective;

public class MainAdministrationHandler extends AbstractHandler implements
    IHandler {

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        IWorkbench workbench = BiobankPlugin.getDefault().getWorkbench();
        try {
            if (workbench.getActiveWorkbenchWindow().getActivePage()
                .closeAllEditors(true))
                workbench.showPerspective(MainPerspective.ID,
                    workbench.getActiveWorkbenchWindow());
        } catch (WorkbenchException e) {
            throw new ExecutionException("Error while opening Main perpective",
                e);
        }
        return null;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
