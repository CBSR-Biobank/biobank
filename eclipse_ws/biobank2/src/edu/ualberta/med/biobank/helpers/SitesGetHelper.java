package edu.ualberta.med.biobank.helpers;

import java.util.List;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;
import org.springframework.remoting.RemoteConnectFailureException;

import edu.ualberta.med.biobank.model.Site;
import gov.nih.nci.system.applicationservice.WritableApplicationService;

public class SitesGetHelper implements Runnable {

	private WritableApplicationService appService;
	
	private List<Site> sites;

	public SitesGetHelper(WritableApplicationService appService) {
		this.appService = appService;
	}

	public void run() {
		Site site = new Site();				
		try {
			sites = appService.search(Site.class, site);
		}
		catch (final RemoteConnectFailureException exp) {
			Display.getDefault().asyncExec(new Runnable() {
				public void run() {
					MessageDialog.openError(
							PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), 
							"Connection Attempt Failed", 
					"Could not connect to server. Make sure server is running.");
				}
			});
		}
		catch (Exception exp) {
			exp.printStackTrace();
		}
	}
	
	public List<Site> getSites() {
		return sites;
	}
}
