package edu.ualberta.med.biobank.action.csvimport.patient;

import edu.ualberta.med.biobank.action.csvimport.IImportInfo;
import edu.ualberta.med.biobank.model.Patient;
import edu.ualberta.med.biobank.model.Study;

/**
 * 
 * @author loyola
 * 
 */
public class PatientImportInfo implements IImportInfo {

    private final PatientCsvInfo csvInfo;
    private Study study;

    PatientImportInfo(PatientCsvInfo csvInfo) {
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
        patient.setTimeCreated(csvInfo.getCreatedAt());
        patient.setStudy(study);
        return patient;
    }

}
