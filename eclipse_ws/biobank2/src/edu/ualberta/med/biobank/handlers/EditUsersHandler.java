package edu.ualberta.med.biobank.handlers;

import java.util.List;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;

import edu.ualberta.med.biobank.BioBankPlugin;
import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.security.User;
import edu.ualberta.med.biobank.dialogs.EditUsersDialog;
import edu.ualberta.med.biobank.server.applicationservice.BiobankApplicationService;
import gov.nih.nci.system.applicationservice.ApplicationException;

public class EditUsersHandler extends AbstractHandler implements IHandler {

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        // TODO: is the busy indicator necessary?
        BusyIndicator.showWhile(Display.getDefault(), new Runnable() {
            @Override
            public void run() {
                BiobankApplicationService appService = SessionManager
                    .getAppService();
                List<User> users = null;

                try {
                    users = appService.getSecurityUsers();
                } catch (ApplicationException e) {
                    BioBankPlugin.openAsyncError("Unable to load users.", e);
                }

                new EditUsersDialog(PlatformUI.getWorkbench()
                    .getActiveWorkbenchWindow().getShell(), users).open();
            }
        });
        return null;
    }
}
