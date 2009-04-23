package edu.ualberta.med.biobank.helpers;

import java.util.List;
import edu.ualberta.med.biobank.model.Site;
import gov.nih.nci.system.applicationservice.WritableApplicationService;
import gov.nih.nci.system.client.ApplicationServiceProvider;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;
import org.springframework.remoting.RemoteAccessException;

public class SessionHelper implements Runnable {
	
	private String serverUrl;
	
	private String userName;
	
	private String password;

	private WritableApplicationService appService;
	
	private List<Site> sites;
	
	public SessionHelper(String server, String userName, String password) {
		this.serverUrl = "http://" + server + "/biobank2";
		this.userName = userName;
		this.password = password;
		
		appService = null;
		sites = null;
	}
	
	public void run() {					
		try {
			if (userName.length() == 0) {
				appService =  (WritableApplicationService) 
				ApplicationServiceProvider.getApplicationServiceFromUrl(
						serverUrl);
			}
			else {
				appService = (WritableApplicationService) 
				ApplicationServiceProvider.getApplicationServiceFromUrl(
						serverUrl, userName, password);
			}	

			Site site = new Site();		
			sites = appService.search(Site.class, site);
		}
		catch (final RemoteAccessException exp) {
			Display.getDefault().asyncExec(new Runnable() {
				public void run() {
					MessageDialog.openError(
							PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), 
							"Connection Attempt Failed", 
					"Could not connect to server. Make sure server is running.");
				}
			});
		}
		catch (final Exception exp) {	
			exp.printStackTrace();

			Display.getDefault().asyncExec(new Runnable() {
				public void run() {
					MessageDialog.openError(
							PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), 
							"Login Failed", exp.getMessage());
				}
			});
		}
	}
	
	public WritableApplicationService getAppService() {
		return appService;
	}
	
	public List<Site> getSites() {
		return sites;
	}
}
