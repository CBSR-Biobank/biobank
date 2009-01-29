package edu.ualberta.med.biobank.handler;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;

import edu.ualberta.med.biobank.Activator;
import org.eclipse.ui.PlatformUI;
import org.eclipse.jface.window.Window;
import edu.ualberta.med.biobank.dialogs.LoginDialog;

public class LoginHandler extends AbstractHandler implements IHandler {

	public Object execute(ExecutionEvent event) throws ExecutionException {
		LoginDialog loginDialog = new LoginDialog(PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow().getShell());
		if (loginDialog.open() == Window.OK) {
			Activator.getDefault().createSession();
		}
		return null;
	}
}
