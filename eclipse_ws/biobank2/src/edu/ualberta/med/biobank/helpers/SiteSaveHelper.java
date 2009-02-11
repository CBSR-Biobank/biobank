package edu.ualberta.med.biobank.helpers;

import edu.ualberta.med.biobank.model.Address;
import edu.ualberta.med.biobank.model.Site;
import gov.nih.nci.system.applicationservice.WritableApplicationService;
import gov.nih.nci.system.query.SDKQuery;
import gov.nih.nci.system.query.SDKQueryResult;
import gov.nih.nci.system.query.example.InsertExampleQuery;
import gov.nih.nci.system.query.example.UpdateExampleQuery;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;
import org.springframework.remoting.RemoteAccessException;

public class SiteSaveHelper implements Runnable {
	
	private WritableApplicationService appService;
	
	private Site site;
	
	public SiteSaveHelper(WritableApplicationService appService, Site site) {
		this.appService = appService;
		this.site = site;
	}

	@Override
	public void run() {		
		try {
			SDKQuery query;
			SDKQueryResult result;

			if ((site.getId() == null) || (site.getId() == 0)) {
				Assert.isTrue(site.getAddress().getId() == null, "insert invoked on address already in database");
				
				query = new InsertExampleQuery(site.getAddress());					
				result = appService.executeQuery(query);
				site.setAddress((Address) result.getObjectResult());
				query = new InsertExampleQuery(site);	
			}
			else { 
				Assert.isNotNull(site.getAddress().getId(), "update invoked on address not in database");

				query = new UpdateExampleQuery(site.getAddress());					
				result = appService.executeQuery(query);
				site.setAddress((Address) result.getObjectResult());
				query = new UpdateExampleQuery(site);	
			}
			
			result = appService.executeQuery(query);
			site = (Site) result.getObjectResult();
		}
		catch (final RemoteAccessException exp) {
			Display.getDefault().asyncExec(new Runnable() {
				public void run() {
					MessageDialog.openError(
							PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), 
							"Connection Attempt Failed", 
							"Could not perform database operation. Make sure server is running correct version.");
				}
			});
		}
		catch (Exception exp) {
			exp.printStackTrace();
		}
	}
	
	public Site getResult() {
		return site;
	}


}
