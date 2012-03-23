package edu.ualberta.med.biobank.handlers;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.permission.container.ContainerCreatePermission;
import edu.ualberta.med.biobank.common.wrappers.ContainerWrapper;
import edu.ualberta.med.biobank.gui.common.BgcPlugin;
import edu.ualberta.med.biobank.treeview.admin.ContainerAdapter;
import gov.nih.nci.system.applicationservice.ApplicationException;

public class ContainerAddHandler extends LogoutSensitiveHandler {
    public static final String ID =
        "edu.ualberta.med.biobank.commands.containerAdd"; //$NON-NLS-1$

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        ContainerAdapter containerAdapter =
            new ContainerAdapter(null, new ContainerWrapper(
                SessionManager.getAppService()));
        ((ContainerWrapper) containerAdapter.getModelObject())
            .setSite(SessionManager.getUser().getCurrentWorkingSite());
        containerAdapter.openEntryForm(false);
        return null;
    }

    @Override
    public boolean isEnabled() {
        try {
            if (createAllowed == null)
                createAllowed =
                    SessionManager.getAppService().isAllowed(
                        new ContainerCreatePermission(SessionManager.getUser()
                            .getCurrentWorkingCenter().getId()));
            return SessionManager.getInstance().getSession() != null &&
                createAllowed;
        } catch (ApplicationException e) {
            BgcPlugin.openAsyncError(Messages.HandlerPermission_error,
                Messages.HandlerPermission_message);
            return false;
        }
    }
}
