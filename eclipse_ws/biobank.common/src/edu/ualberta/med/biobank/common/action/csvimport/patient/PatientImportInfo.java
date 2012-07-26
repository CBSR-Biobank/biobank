package edu.ualberta.med.biobank.common.action.csvimport.patient;

import edu.ualberta.med.biobank.common.action.csvimport.IImportInfo;
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
        patient.setCreatedAt(csvInfo.getCreatedAt());
        patient.setStudy(study);
        return patient;
    }

}