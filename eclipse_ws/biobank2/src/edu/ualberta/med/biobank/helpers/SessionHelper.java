package edu.ualberta.med.biobank.helpers;

import edu.ualberta.med.biobank.BioBankPlugin;
import edu.ualberta.med.biobank.model.Site;
import gov.nih.nci.system.applicationservice.WritableApplicationService;
import gov.nih.nci.system.client.ApplicationServiceProvider;

import java.util.List;

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
			BioBankPlugin.openRemoteConnectErrorMessage();
		}
		catch (final Exception exp) {	
			exp.printStackTrace();
			BioBankPlugin.openAsyncError("Login Failed", exp.getMessage());
		}
	}
	
	public WritableApplicationService getAppService() {
		return appService;
	}
	
	public List<Site> getSites() {
		return sites;
	}
}
