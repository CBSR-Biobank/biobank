package edu.ualberta.med.biobank.common.action.batchoperation.patient;

import java.util.Date;

import edu.ualberta.med.biobank.common.action.batchoperation.IBatchOpInputPojo;

/**
 * 
 * @author Nelson Loyola
 * 
 */
public class PatientBatchOpInputPojo implements IBatchOpInputPojo {
    private static final long serialVersionUID = 1L;

    private int lineNumber;
    private String studyName;
    private String patientNumber;
    private Date enrollmentDate;
    private String comment;

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

    public Date getEnrollmentDate() {
        return enrollmentDate;
    }

    public void setEnrollmentDate(Date enrollmentDate) {
        this.enrollmentDate = enrollmentDate;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

}
