package edu.ualberta.med.biobank.handler;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.handlers.HandlerUtil;

import edu.ualberta.med.biobank.forms.LoginForm;
import edu.ualberta.med.biobank.model.LoginInput;

public class LoginHandler extends AbstractHandler implements IHandler {

	public Object execute(ExecutionEvent event) throws ExecutionException {
		try {
			HandlerUtil.getActiveWorkbenchWindowChecked(event).getActivePage()
			.openEditor(new LoginInput(), LoginForm.ID, true);
		} 
		catch (PartInitException e) {
			throw new ExecutionException("Failed to activate editor", e);
		}
		return null;
	}
}
