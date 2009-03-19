package edu.ualberta.med.biobank.helpers;

import java.util.HashSet;
import java.util.Set;

import edu.ualberta.med.biobank.model.Clinic;
import edu.ualberta.med.biobank.model.Patient;
import edu.ualberta.med.biobank.model.Sdata;
import edu.ualberta.med.biobank.model.StorageContainer;
import edu.ualberta.med.biobank.model.Study;
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

public class StudySaveHelper implements Runnable {
    
    private WritableApplicationService appService;
    
    private Study study;
    
    public StudySaveHelper(WritableApplicationService appService, Study study) {
        this.appService = appService;
        this.study = study;
    }

    @Override
    public void run() {     
        try {
            SDKQuery query;
            SDKQueryResult result;
            
            checkClinics();
            saveSdata();
            
            study.setWorksheet(null);
            
            if (study.getPatientCollection() == null) {
            	study.setPatientCollection(new HashSet<Patient>());
            }
            
            if (study.getStorageContainerCollection() == null) {
            	study.setStorageContainerCollection(new HashSet<StorageContainer>());
            }

            if ((study.getId() == null) || (study.getId() == 0)) {
                query = new InsertExampleQuery(study);
            }
            else { 
                query = new UpdateExampleQuery(study);
            }
            
            result = appService.executeQuery(query);
            study = (Study) result.getObjectResult();
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
    
    private void checkClinics() {
        for (Clinic clinic 
                : study.getClinicCollection().toArray(
                        new Clinic [study.getClinicCollection().size()])) {

            Assert.isTrue((clinic.getId() != null) && (clinic.getId() != 0),
                   "invalid clinic object");
        }
    }
    
    private void saveSdata() {
        SDKQuery query;
        SDKQueryResult result;
        Set<Sdata> savedSdataList = new HashSet<Sdata>();

        try {
            for (Sdata sdata 
                    : study.getSdataCollection().toArray(
                            new Sdata [study.getSdataCollection().size()])) {

                if ((sdata.getId() == null) || (sdata.getId() == 0)) {
                    query = new InsertExampleQuery(sdata);
                }
                else {
                    query = new UpdateExampleQuery(sdata);
                }                  
                
                result = appService.executeQuery(query);
                savedSdataList.add((Sdata) result.getObjectResult());
            }

            study.setSdataCollection(savedSdataList);
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
    
    public Study getResult() {
        return study;
    }


}
