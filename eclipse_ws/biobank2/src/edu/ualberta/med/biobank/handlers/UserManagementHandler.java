package edu.ualberta.med.biobank.handlers;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.ui.PlatformUI;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.action.security.ManagerContext;
import edu.ualberta.med.biobank.common.action.security.ManagerContextGetAction;
import edu.ualberta.med.biobank.common.action.security.ManagerContextGetInput;
import edu.ualberta.med.biobank.common.permission.security.UserManagerPermission;
import edu.ualberta.med.biobank.dialogs.user.UserManagementDialog;
import edu.ualberta.med.biobank.gui.common.BgcPlugin;
import edu.ualberta.med.biobank.gui.common.handlers.LogoutSensitiveHandler;
import edu.ualberta.med.biobank.server.applicationservice.BiobankApplicationService;
import gov.nih.nci.system.applicationservice.ApplicationException;

public class UserManagementHandler extends LogoutSensitiveHandler
    implements IHandler {
    private static final I18n i18n = I18nFactory
        .getI18n(UserManagementHandler.class);

    @SuppressWarnings("nls")
    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        try {
            BiobankApplicationService service = SessionManager.getAppService();
            ManagerContext context = service.doAction(
                new ManagerContextGetAction(
                    new ManagerContextGetInput())).getContext();

            new UserManagementDialog(PlatformUI.getWorkbench()
                .getActiveWorkbenchWindow().getShell(), context).open();
        } catch (ApplicationException e) {
            BgcPlugin.openAsyncError(
                // dialog title
                i18n.tr("Unable to Load User Management Data")
                , e.getMessage());
        }

        return null;
    }

    @Override
    public boolean isEnabled() {
        if (allowed == null)
            try {
                allowed = SessionManager.getAppService().isAllowed(
                    new UserManagerPermission());
            } catch (ApplicationException e) {
                BgcPlugin.openAsyncError(
                    "Unable to Load User Management Data", e.getMessage());
            }
        return allowed;
    }

}
