package edu.ualberta.med.biobank.handler;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.ui.handlers.HandlerUtil;

import edu.ualberta.med.biobank.BioBankPlugin;
import edu.ualberta.med.biobank.forms.SiteForm;
import edu.ualberta.med.biobank.model.SiteInput;

public class AddSiteHandler extends AbstractHandler implements IHandler {

	public Object execute(ExecutionEvent event) throws ExecutionException {
		SiteInput input = new SiteInput(0, null);
		try {
			HandlerUtil.getActiveWorkbenchWindowChecked(event).getActivePage()
			.openEditor(input, SiteForm.ID, true);
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
