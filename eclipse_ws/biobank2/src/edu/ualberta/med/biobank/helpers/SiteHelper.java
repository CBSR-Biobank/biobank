package edu.ualberta.med.biobank.helpers;

import java.util.List;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;
import org.springframework.remoting.RemoteConnectFailureException;

import edu.ualberta.med.biobank.model.Site;
import edu.ualberta.med.biobank.treeview.SiteAdapter;
import gov.nih.nci.system.applicationservice.WritableApplicationService;

public class SiteHelper {

	public Runnable getSites(final WritableApplicationService appService,
			final ISitesResult result) {
		return new Runnable() {
			public void run() {
				Site site = new Site();				
				try {
					final List<Site> sites = appService.search(Site.class, site);
					result.callback(sites);
					
					Display.getDefault().asyncExec(new Runnable() {
						public void run() {
							for (Object o : sites) {
								SiteAdapter siteNode 
									= new SiteAdapter(sessionNode, (Site) o);
								sessionNode.addChild(siteNode);
							}
							treeViewer.refresh(sessionNode);
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
				catch (Exception exp) {
					exp.printStackTrace();
				}
					
			}
		};
	}

}
