package edu.ualberta.med.biobank.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

public class MaintenanceModeChangeHandler extends AbstractHandler {

    // private static final I18n i18n = I18nFactory.getI18n(DecodeImageHandler.class);

    // @SuppressWarnings("nls")
    // private static final String TITLE = i18n.tr("Server Maintenance Mode");

    // @SuppressWarnings("nls")
    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        // comment this out for now, maintenance mode on the server can be changed with an ANT
        // command

        // final MaintenanceMode serverMaintenanceMode =
        // SessionManager.getAppService().maintenanceMode();
        //
        // String message;
        // MaintenanceMode newMode;
        //
        // if (serverMaintenanceMode == MaintenanceMode.PREVENT_USER_LOGIN) {
        // message = i18n.tr("Maintenance mode is enabled. Do you want to disable it?");
        // newMode = MaintenanceMode.NONE;
        // } else {
        // message = i18n.tr("Maintenance mode is disabled. Do you want to enable it?");
        // newMode = MaintenanceMode.PREVENT_USER_LOGIN;
        // }
        //
        // boolean confirm = MessageDialog.openConfirm(
        // PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
        // TITLE,
        // message);
        //
        // if (confirm) {
        // try {
        // SessionManager.getAppService().maintenanceMode(newMode);
        // } catch (ApplicationException e) {
        // BgcPlugin.openAccessDeniedErrorMessage(e);
        // }
        // }
        return null;
    }

}
