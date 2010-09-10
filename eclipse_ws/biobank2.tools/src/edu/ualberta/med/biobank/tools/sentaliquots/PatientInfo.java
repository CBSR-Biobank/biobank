package edu.ualberta.med.biobank.tools.sentaliquots;

public class PatientInfo {
    private String patientNo;

    private String inventoryId;

    private String closeComment;

    public String getPatientNo() {
        return patientNo;
    }

    public void setPatientNo(String patientNo) {
        this.patientNo = patientNo;
    }

    public String getInventoryId() {
        return inventoryId;
    }

    public void setInventoryId(String inventoryId) {
        this.inventoryId = inventoryId;
    }

    public String getCloseComment() {
        return closeComment;
    }

    public void setCloseComment(String closeComment) {
        this.closeComment = closeComment;
    }
}
