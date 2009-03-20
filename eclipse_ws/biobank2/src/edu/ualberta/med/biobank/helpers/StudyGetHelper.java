package edu.ualberta.med.biobank.helpers;

import java.util.List;
import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;
import org.springframework.remoting.RemoteConnectFailureException;
import edu.ualberta.med.biobank.model.Study;
import gov.nih.nci.system.applicationservice.WritableApplicationService;

/**
 * Returns the objects required by the form.
 *
 */
public class StudyGetHelper implements Runnable {
	
	private WritableApplicationService appService;
	
	private int id;
	
	private Study study;
	
	public StudyGetHelper(WritableApplicationService appService, int id, int flags) {
		this.appService = appService;
		this.id = id;
	}

	@Override
	public void run() {
	    try {
	        Study study = new Study();
	        study.setId(id);

	        List<Study> result = appService.search(Study.class, study);
	        Assert.isTrue(result.size() == 1);
	        
	        study = result.get(0);
	        
	        study.getClinicCollection();
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

	public Study getResult() {
	    return study;
	}
}
