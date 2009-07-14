package edu.ualberta.med.biobank.handler;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.Assert;
import org.eclipse.ui.PlatformUI;

import edu.ualberta.med.biobank.SessionManager;

public class LogoutHandler extends AbstractHandler {

	public Object execute(ExecutionEvent event) throws ExecutionException {
		int count = SessionManager.getInstance().getSessionCount();
		String[] names = SessionManager.getInstance().getSessionNames();
		
		if (count == 1) {
			SessionManager.getInstance().deleteSession(names[0]);
		}
		else {
			Assert.isTrue(false, "not implemented yet");
		}
		
		// close all editors
		PlatformUI.getWorkbench().getActiveWorkbenchWindow()
			.getActivePage().closeAllEditors(true);
		return null;
	}

	@Override
	public boolean isEnabled() {
		return (SessionManager.getInstance().getSessionCount() > 0);
	}

}
