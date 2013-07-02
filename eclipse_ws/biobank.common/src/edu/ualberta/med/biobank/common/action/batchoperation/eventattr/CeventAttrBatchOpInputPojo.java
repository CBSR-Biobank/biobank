package edu.ualberta.med.biobank.common.action.batchoperation.eventattr;

import edu.ualberta.med.biobank.common.action.batchoperation.IBatchOpInputPojo;

/**
 * 
 * @author Nelson Loyola
 * 
 */
public class CeventAttrBatchOpInputPojo implements IBatchOpInputPojo {
    private static final long serialVersionUID = 1L;

    private int lineNumber;
    private String patientNumber;
    private String visitNumber;
    private String attrName;
    private String attrValue;

    @Override
    public int getLineNumber() {
        return lineNumber;
    }

    @Override
    public void setLineNumber(int lineNumber) {
        this.lineNumber = lineNumber;
    }

    public String getPatientNumber() {
        return patientNumber;
    }

    public void setPatientNumber(String patientNumber) {
        this.patientNumber = patientNumber;
    }

    public String getVisitNumber() {
        return visitNumber;
    }

    public void setVisitNumber(String visitNumber) {
        this.visitNumber = visitNumber;
    }

    public String getAttrName() {
        return attrName;
    }

    public void setAttrName(String attrName) {
        this.attrName = attrName;
    }

    public String getAttrValue() {
        return attrValue;
    }

    public void setAttrValue(String attrValue) {
        this.attrValue = attrValue;
    }

}
