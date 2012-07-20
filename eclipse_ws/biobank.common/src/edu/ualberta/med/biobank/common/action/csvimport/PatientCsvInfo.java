package edu.ualberta.med.biobank.common.action.csvimport;

import java.io.Serializable;
import java.util.Date;

public class PatientCsvInfo implements Serializable {
    private static final long serialVersionUID = 1L;

    private String studyName;
    private String patientNumber;
    private Date createdAt;

    public String getStudyName() {
        return studyName;
    }

    public void setStudyName(String studyName) {
        this.studyName = studyName;
    }

    public String getPatientNumber() {
        return patientNumber;
    }

    public void setPatientNumber(String patientNumber) {
        this.patientNumber = patientNumber;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

}
