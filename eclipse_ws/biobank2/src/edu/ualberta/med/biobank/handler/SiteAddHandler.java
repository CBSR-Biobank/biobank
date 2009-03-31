package edu.ualberta.med.biobank.handler;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.Assert;
import org.eclipse.ui.handlers.HandlerUtil;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.forms.SiteEntryForm;
import edu.ualberta.med.biobank.forms.input.FormInput;
import edu.ualberta.med.biobank.model.Address;
import edu.ualberta.med.biobank.model.Site;
import edu.ualberta.med.biobank.treeview.SiteAdapter;

public class SiteAddHandler extends AbstractHandler {

	public Object execute(ExecutionEvent event) throws ExecutionException {
		int numSessions = SessionManager.getInstance().getSessionCount();
		
		Assert.isTrue(numSessions >= 1);
		
		Site site = new Site();
		site.setAddress(new Address());
		SiteAdapter siteNode = new SiteAdapter(null, site);
		
		FormInput input = new FormInput(siteNode);
		try {
			HandlerUtil.getActiveWorkbenchWindowChecked(event).getActivePage()
			.openEditor(input, SiteEntryForm.ID, true);
		}
		catch (Exception exp) {
			exp.printStackTrace();
		}
		
		return null;
	}
	
	public boolean isEnabled() {
		return (SessionManager.getInstance().getSessionCount() > 0);
	}
}
