package edu.ualberta.med.biobank.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.ui.PlatformUI;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.dialogs.ChangePasswordDialog;
import edu.ualberta.med.biobank.dialogs.startup.LoginDialog;

public class LoginHandler extends AbstractHandler implements IHandler {

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        LoginDialog loginDialog = new LoginDialog(PlatformUI.getWorkbench()
            .getActiveWorkbenchWindow().getShell());
        if (loginDialog.open() == Dialog.OK)
            if (SessionManager.getInstance().getSession().getUser()
                .needChangePassword()) {
                ChangePasswordDialog dlg = new ChangePasswordDialog(PlatformUI
                    .getWorkbench().getActiveWorkbenchWindow().getShell(), true);
                dlg.open();
            }
        return null;
    }
}
