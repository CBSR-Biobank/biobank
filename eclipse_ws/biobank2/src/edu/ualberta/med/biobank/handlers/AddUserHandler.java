package edu.ualberta.med.biobank.handlers;

import java.text.MessageFormat;
import java.util.List;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;

import edu.ualberta.med.biobank.BioBankPlugin;
import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.security.Group;
import edu.ualberta.med.biobank.common.security.User;
import edu.ualberta.med.biobank.dialogs.UserEditDialog;
import gov.nih.nci.system.applicationservice.ApplicationException;

public class AddUserHandler extends AbstractHandler implements IHandler {
    private static final String GROUP_LOAD_ERROR_TITLE = "Unable to Load Groups";
    private static final String GROUP_LOAD_ERROR_MESSAGE = "There was an error loading groups.";
    private static final String USER_ADDED_TITLE = "User Added";
    private static final String USER_ADDED_MESSAGE = "Successfully added new user \"{0}\".";

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        // TODO: is the busy indicator necessary?
        BusyIndicator.showWhile(Display.getDefault(), new Runnable() {
            @Override
            public void run() {
                List<Group> groups = null;

                try {
                    groups = SessionManager.getAppService().getSecurityGroups();
                } catch (ApplicationException e) {
                    BioBankPlugin.openAsyncError(GROUP_LOAD_ERROR_TITLE,
                        GROUP_LOAD_ERROR_MESSAGE);
                }

                User user = new User();
                UserEditDialog dlg = new UserEditDialog(PlatformUI
                    .getWorkbench().getActiveWorkbenchWindow().getShell(),
                    user, groups, true);
                int res = dlg.open();
                if (res == Status.OK) {
                    BioBankPlugin.openAsyncInformation(
                        USER_ADDED_TITLE,
                        MessageFormat.format(USER_ADDED_MESSAGE,
                            user.getLogin()));
                }
            }
        });
        return null;
    }
}
