package edu.ualberta.med.biobank.handler;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.Assert;

import edu.ualberta.med.biobank.BioBankPlugin;
import edu.ualberta.med.biobank.views.SessionsView;

public class LogoutHandler extends AbstractHandler {

	public Object execute(ExecutionEvent event) throws ExecutionException {
		SessionsView view = BioBankPlugin.getDefault().getSessionsView(); 
		int count = view.getSessionCount();
		String[] names = view.getSessionNames();
		
		if (count == 1) {
			view.deleteSession(names[0]);
		}
		else {
			Assert.isTrue(false, "not implemented yet");
		}
		return null;
	}

	@Override
	public boolean isEnabled() {
		return (BioBankPlugin.getDefault().getSessionCount() > 0);
	}

}
