package edu.ualberta.med.biobank.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.WorkbenchException;

import edu.ualberta.med.biobank.BiobankPlugin;
import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.permission.reports.ReportsPermission;
import edu.ualberta.med.biobank.rcp.perspective.ReportsPerspective;

public class ReportsHandler extends AbstractHandler {

    private Boolean reportsAllowed;

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        IWorkbench workbench = BiobankPlugin.getDefault().getWorkbench();
        try {
            if (workbench.getActiveWorkbenchWindow().getActivePage()
                .closeAllEditors(true))
                workbench.showPerspective(ReportsPerspective.ID,
                    workbench.getActiveWorkbenchWindow());
        } catch (WorkbenchException e) {
            throw new ExecutionException(Messages.ReportsHandler_init_error, e);
        }
        return null;

    }

    @Override
    public boolean isEnabled() {
        if (reportsAllowed == null)
            try {
                if (!SessionManager.getInstance().isConnected())
                    return false;
                reportsAllowed =
                    SessionManager.getAppService().isAllowed(
                        new ReportsPermission());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        return reportsAllowed;
    }
}
