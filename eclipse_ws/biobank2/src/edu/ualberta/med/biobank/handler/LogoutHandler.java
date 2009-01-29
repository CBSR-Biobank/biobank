package edu.ualberta.med.biobank.handler;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;

import edu.ualberta.med.biobank.BioBankPlugin;

public class LogoutHandler extends AbstractHandler implements IHandler {

	@Override
	public void dispose() {
		// TODO Auto-generated method stub

	}

	public Object execute(ExecutionEvent event) throws ExecutionException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isEnabled() {
		return (BioBankPlugin.getDefault().getSessionCount() > 0);
	}

}
