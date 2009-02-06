package edu.ualberta.med.biobank.handler;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.runtime.Assert;
import org.eclipse.ui.handlers.HandlerUtil;

import edu.ualberta.med.biobank.BioBankPlugin;
import edu.ualberta.med.biobank.forms.SiteEntryForm;
import edu.ualberta.med.biobank.forms.WsObjectInput;
import edu.ualberta.med.biobank.model.Address;
import edu.ualberta.med.biobank.model.SessionNode;
import edu.ualberta.med.biobank.model.Site;
import edu.ualberta.med.biobank.model.SiteNode;

public class SiteAddHandler extends AbstractHandler implements IHandler {

	public Object execute(ExecutionEvent event) throws ExecutionException {
		int numSessions = BioBankPlugin.getDefault().getSessionCount();
		
		Assert.isTrue(numSessions >= 1);
		
		SessionNode sessionNode;
		SiteNode siteNode = null;
		
		if (numSessions == 1) {
			sessionNode = BioBankPlugin.getDefault().getSessionNode(0);
			Site site = new Site();
			site.setAddress(new Address());
			siteNode = new SiteNode(sessionNode, site);
		}
		else {
			Assert.isTrue(false, "not implemented yet");
		}
		
		WsObjectInput input = new WsObjectInput(siteNode);
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
		return (BioBankPlugin.getDefault().getSessionCount() > 0);
	}
}
