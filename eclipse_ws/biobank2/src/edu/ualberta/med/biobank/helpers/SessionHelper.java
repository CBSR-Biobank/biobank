package edu.ualberta.med.biobank.helpers;

import edu.ualberta.med.biobank.SessionCredentials;
import edu.ualberta.med.biobank.model.Site;
import gov.nih.nci.system.applicationservice.WritableApplicationService;
import gov.nih.nci.system.client.ApplicationServiceProvider;

import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;
import org.springframework.remoting.RemoteConnectFailureException;

public class SessionHelper {
	
	public static Runnable createSession(final String server, final String userName, final String password) {
		return new Runnable() {
			public void run() {					
				try {
					final WritableApplicationService appService;					
					final String url = "http://" + server + "/biobank2";
					
					if (userName.length() == 0) {
						appService =  (WritableApplicationService) 
						ApplicationServiceProvider.getApplicationServiceFromUrl(
								url);
					}
					else {
						appService = (WritableApplicationService) 
						ApplicationServiceProvider.getApplicationServiceFromUrl(
								url, userName, password);
					}	

					Site site = new Site();		
					final List<Object> sites = appService.search(Site.class, site);
					
					Display.getDefault().asyncExec(new Runnable() {
				          public void run() {
				        	  addSession(appService, sc.getServer(), sites);
				          }
					});
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
		};
	}

}
