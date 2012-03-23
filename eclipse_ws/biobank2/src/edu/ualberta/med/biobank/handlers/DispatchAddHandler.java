package edu.ualberta.med.biobank.handlers;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.Assert;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.permission.dispatch.DispatchCreatePermission;
import edu.ualberta.med.biobank.common.wrappers.DispatchWrapper;
import edu.ualberta.med.biobank.gui.common.BgcPlugin;
import edu.ualberta.med.biobank.treeview.admin.SessionAdapter;
import edu.ualberta.med.biobank.treeview.dispatch.DispatchAdapter;
import gov.nih.nci.system.applicationservice.ApplicationException;

public class DispatchAddHandler extends LogoutSensitiveHandler {

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        SessionAdapter sessionAdapter = SessionManager.getInstance()
            .getSession();
        Assert.isNotNull(sessionAdapter);
        DispatchWrapper dispatch = new DispatchWrapper(
            SessionManager.getAppService());
        DispatchAdapter node = new DispatchAdapter(sessionAdapter, dispatch);
        node.openEntryForm();
        return null;
    }

    @Override
    public boolean isEnabled() {
        try {
            if (createAllowed == null)
                createAllowed = SessionManager.getAppService().isAllowed(
                    new
                    DispatchCreatePermission(SessionManager.getUser()
                        .getCurrentWorkingCenter().getId()));
        } catch (ApplicationException e) {
            BgcPlugin.openAsyncError("Error", "Unable to retrieve permissions");
            return false;
        }
        return createAllowed;
    }

}