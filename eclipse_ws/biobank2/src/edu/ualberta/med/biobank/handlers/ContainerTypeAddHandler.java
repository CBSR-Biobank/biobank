package edu.ualberta.med.biobank.handlers;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.permission.containerType.ContainerTypeCreatePermission;
import edu.ualberta.med.biobank.common.wrappers.ContainerTypeWrapper;
import edu.ualberta.med.biobank.gui.common.BgcPlugin;
import edu.ualberta.med.biobank.treeview.admin.ContainerTypeAdapter;
import gov.nih.nci.system.applicationservice.ApplicationException;

public class ContainerTypeAddHandler extends LogoutSensitiveHandler {
    public static final String ID =
        "edu.ualberta.med.biobank.commands.containerTypeAdd"; //$NON-NLS-1$

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        ContainerTypeAdapter containerTypeAdapter = new ContainerTypeAdapter(
            null, new ContainerTypeWrapper(SessionManager.getAppService()));
        ((ContainerTypeWrapper) containerTypeAdapter.getModelObject())
            .setSite(SessionManager.getUser().getCurrentWorkingSite());
        containerTypeAdapter.openEntryForm(false);
        return null;
    }

    @Override
    public boolean isEnabled() {
        try {
            if (allowed == null) {
                Integer centerId =
                    SessionManager.getUser()
                        .getCurrentWorkingCenter() != null ? SessionManager
                        .getUser()
                        .getCurrentWorkingCenter().getId() : null;
                allowed =
                    SessionManager.getAppService().isAllowed(
                        new ContainerTypeCreatePermission(centerId));
            }
            return SessionManager.getInstance().getSession() != null &&
                allowed;
        } catch (ApplicationException e) {
            BgcPlugin.openAsyncError(Messages.HandlerPermission_error,
                Messages.HandlerPermission_message);
            return false;
        }
    }
}
