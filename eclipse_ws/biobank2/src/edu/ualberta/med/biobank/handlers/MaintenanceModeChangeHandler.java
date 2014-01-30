package edu.ualberta.med.biobank.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.PlatformUI;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.gui.common.BgcPlugin;
import gov.nih.nci.system.applicationservice.ApplicationException;

public class MaintenanceModeChangeHandler extends AbstractHandler {

    private static final I18n i18n = I18nFactory.getI18n(DecodeImageHandler.class);

    @SuppressWarnings("nls")
    private static final String TITLE = i18n.tr("Server Maintenance Mode");

    @SuppressWarnings("nls")
    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        final int serverMaintenanceMode = SessionManager.getAppService().maintenanceMode();

        String message = (serverMaintenanceMode == 0)
            ? i18n.tr("Maintenance mode is disabled. Do you want to enable it?")
            : i18n.tr("Maintenance mode is enabled. Do you want to disable it?");

        boolean confirm = MessageDialog.openConfirm(
            PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
            TITLE,
            message);

        if (confirm) {
            try {
                SessionManager.getAppService().maintenanceMode(1 - serverMaintenanceMode);
            } catch (ApplicationException e) {
                BgcPlugin.openAccessDeniedErrorMessage(e);
            }
        }
        return null;
    }

}
