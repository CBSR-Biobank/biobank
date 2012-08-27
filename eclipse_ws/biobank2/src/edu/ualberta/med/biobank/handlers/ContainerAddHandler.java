package edu.ualberta.med.biobank.handlers;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.permission.container.ContainerCreatePermission;
import edu.ualberta.med.biobank.common.util.StringUtil;
import edu.ualberta.med.biobank.common.wrappers.ContainerWrapper;
import edu.ualberta.med.biobank.gui.common.BgcPlugin;
import edu.ualberta.med.biobank.gui.common.handlers.LogoutSensitiveHandler;
import edu.ualberta.med.biobank.treeview.admin.ContainerAdapter;
import gov.nih.nci.system.applicationservice.ApplicationException;

public class ContainerAddHandler extends LogoutSensitiveHandler {
    @SuppressWarnings("nls")
    public static final String ID =
        "edu.ualberta.med.biobank.commands.containerAdd";

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
            if (allowed == null) {
                Integer centerId =
                    SessionManager.getUser()
                        .getCurrentWorkingCenter() != null ? SessionManager
                        .getUser()
                        .getCurrentWorkingCenter().getId() : null;
                allowed =
                    SessionManager.getAppService().isAllowed(
                        new ContainerCreatePermission(centerId));
            }
            return SessionManager.getInstance().getSession() != null &&
                allowed;
        } catch (ApplicationException e) {
            BgcPlugin.openAsyncError(StringUtil.EMPTY_STRING,
                StringUtil.EMPTY_STRING);
            return false;
        }
    }
}
