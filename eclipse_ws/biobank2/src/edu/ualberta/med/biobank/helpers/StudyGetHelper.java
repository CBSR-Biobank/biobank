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
    
    public static final int LOAD_BASIC    = 0;
    public static final int LOAD_CLINICS  = 1 << 0;
    public static final int LOAD_PATIENTS = 1 << 1;
    public static final int LOAD_STORAGE_CONTAINERS = 1 << 2;
    public static final int LOAD_SDATA = 1 << 3;
    public static final int LOAD_ALL = 
        LOAD_CLINICS & LOAD_PATIENTS & LOAD_STORAGE_CONTAINERS & LOAD_SDATA;
	
	private WritableApplicationService appService;
	
	private int id;
    private int flags;
	
	private Study study;
	
	public StudyGetHelper(WritableApplicationService appService, int id, int flags) {
		this.appService = appService;
		this.id = id;
		this.flags = flags;
	}

	@Override
	public void run() {
	    try {
	        Study study = new Study();
	        study.setId(id);

	        List<Study> result = appService.search(Study.class, study);
	        Assert.isTrue(result.size() == 1);
	        
	        study = result.get(0);

            if ((flags & LOAD_CLINICS) != 0) { 
                study.getClinicCollection();
            }

            if ((flags & LOAD_PATIENTS) != 0) { 
                study.getPatientCollection();
            }

            if ((flags & LOAD_STORAGE_CONTAINERS) != 0) { 
                study.getPatientCollection();
            }

            if ((flags & LOAD_SDATA) != 0) { 
                study.getSdataCollection();
            }
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
