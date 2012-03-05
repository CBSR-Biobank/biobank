package edu.ualberta.med.biobank.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.Assert;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.permission.clinic.ClinicCreatePermission;
import edu.ualberta.med.biobank.gui.common.BgcPlugin;
import edu.ualberta.med.biobank.treeview.admin.SessionAdapter;
import gov.nih.nci.system.applicationservice.ApplicationException;

public class ClinicAddHandler extends AbstractHandler {
    public static final String ID =
        "edu.ualberta.med.biobank.commands.addClinic"; //$NON-NLS-1$

    private Boolean createAllowed;

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        SessionAdapter session = SessionManager.getInstance().getSession();
        Assert.isNotNull(session);
        session.addClinic();
        return null;
    }

    @Override
    public boolean isEnabled() {
        try {
            if (createAllowed == null)
                createAllowed =
                    SessionManager.getAppService().isAllowed(
                        new ClinicCreatePermission());
            return SessionManager.isSuperAdminMode()
                && SessionManager.getInstance().getSession() != null &&
                createAllowed;
        } catch (ApplicationException e) {
            BgcPlugin.openAsyncError("Error", "Unable to retrieve permissions");
            return false;
        }
    }
}
