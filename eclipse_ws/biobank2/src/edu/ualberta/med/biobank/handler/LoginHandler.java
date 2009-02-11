package edu.ualberta.med.biobank.handler;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.ui.PlatformUI;

import edu.ualberta.med.biobank.BioBankPlugin;
import edu.ualberta.med.biobank.dialogs.LoginDialog;
import edu.ualberta.med.biobank.helpers.SessionHelper;
import edu.ualberta.med.biobank.helpers.StudyEntryHelper;

public class LoginHandler extends AbstractHandler implements IHandler {

	public Object execute(ExecutionEvent event) throws ExecutionException {
		LoginDialog loginDialog = new LoginDialog(PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow().getShell());
		if (loginDialog.open() == Window.OK) {	
		}
		return null;
	}
}
