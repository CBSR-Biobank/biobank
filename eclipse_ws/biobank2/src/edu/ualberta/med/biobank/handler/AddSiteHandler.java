package edu.ualberta.med.biobank.handler;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.jface.window.Window;
import org.eclipse.ui.PlatformUI;

import edu.ualberta.med.biobank.BioBankPlugin;
import edu.ualberta.med.biobank.forms.SiteDialog;

public class AddSiteHandler extends AbstractHandler implements IHandler {

	public Object execute(ExecutionEvent event) throws ExecutionException {
		try {
			SiteDialog siteDialog = new SiteDialog(
					PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
					BioBankPlugin.getDefault().getSessionNames());
			if (siteDialog.open() == Window.OK) {

			}
		}
		catch (Exception exp) {
			exp.printStackTrace();
		}
		
		return null;
	}
	
	public boolean isEnabled() {
		return (BioBankPlugin.getDefault().getSessionCount() > 0);
	}
}
