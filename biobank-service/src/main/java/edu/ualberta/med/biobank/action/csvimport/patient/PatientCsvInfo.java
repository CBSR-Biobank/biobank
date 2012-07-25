package edu.ualberta.med.biobank.action.csvimport.patient;

import java.util.Date;

import edu.ualberta.med.biobank.action.csvimport.ICsvInfo;

/**
 * 
 * @author loyola
 * 
 */
public class PatientCsvInfo implements ICsvInfo {
    private static final long serialVersionUID = 1L;

    private int lineNumber;
    private String studyName;
    private String patientNumber;
    private Date createdAt;

    @Override
    public int getLineNumber() {
        return lineNumber;
    }

    @Override
    public void setLineNumber(int lineNumber) {
        this.lineNumber = lineNumber;
    }

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
