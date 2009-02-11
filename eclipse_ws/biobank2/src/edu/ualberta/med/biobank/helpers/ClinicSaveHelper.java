package edu.ualberta.med.biobank.helpers;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;
import org.springframework.remoting.RemoteAccessException;

import edu.ualberta.med.biobank.model.Address;
import edu.ualberta.med.biobank.model.Clinic;
import gov.nih.nci.system.applicationservice.WritableApplicationService;
import gov.nih.nci.system.query.SDKQuery;
import gov.nih.nci.system.query.SDKQueryResult;
import gov.nih.nci.system.query.example.InsertExampleQuery;
import gov.nih.nci.system.query.example.UpdateExampleQuery;

public class ClinicSaveHelper implements Runnable {
	
	private WritableApplicationService appService;
	
	private Clinic clinic;
	
	public ClinicSaveHelper(WritableApplicationService appService, Clinic clinic) {
		this.appService = appService;
		this.clinic = clinic;
	}

	@Override
	public void run() {		
		try {
			SDKQuery query;
			SDKQueryResult result;

			if ((clinic.getId() == null) || (clinic.getId() == 0)) {
				Assert.isTrue(clinic.getAddress().getId() == null, "insert invoked on address already in database");
				
				query = new InsertExampleQuery(clinic.getAddress());					
				result = appService.executeQuery(query);
				clinic.setAddress((Address) result.getObjectResult());
				query = new InsertExampleQuery(clinic);	
			}
			else { 
				Assert.isNotNull(clinic.getAddress().getId(), "update invoked on address not in database");

				query = new UpdateExampleQuery(clinic.getAddress());					
				result = appService.executeQuery(query);
				clinic.setAddress((Address) result.getObjectResult());
				query = new UpdateExampleQuery(clinic);	
			}
			
			appService.executeQuery(query);
			clinic = (Clinic) result.getObjectResult();
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
	
	public Clinic getResult() {
		return clinic;
	}

}
