package edu.ualberta.med.biobank.common.action.batchoperation.patient;

import edu.ualberta.med.biobank.common.action.batchoperation.IBatchOpHelper;
import edu.ualberta.med.biobank.model.Patient;
import edu.ualberta.med.biobank.model.Study;

/**
 * 
 * @author Nelson Loyola
 * 
 */
public class PatientBatchOpHelper implements IBatchOpHelper {

    private final PatientBatchOpInputRow csvInfo;
    private Study study;

    PatientBatchOpHelper(PatientBatchOpInputRow csvInfo) {
        this.csvInfo = csvInfo;
    }

    @Override
    public int getCsvLineNumber() {
        return csvInfo.getLineNumber();
    }

    public Study getStudy() {
        return study;
    }

    public void setStudy(Study study) {
        this.study = study;
    }

    public Patient getNewPatient() {
        Patient patient = new Patient();
        patient.setPnumber(csvInfo.getPatientNumber());
        patient.setCreatedAt(csvInfo.getCreatedAt());
        patient.setStudy(study);
        return patient;
    }

}
