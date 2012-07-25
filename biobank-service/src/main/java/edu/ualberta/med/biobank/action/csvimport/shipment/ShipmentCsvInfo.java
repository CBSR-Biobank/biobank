package edu.ualberta.med.biobank.action.csvimport.shipment;

import java.util.Date;

import edu.ualberta.med.biobank.action.csvimport.ICsvInfo;

public class ShipmentCsvInfo implements ICsvInfo {
    private static final long serialVersionUID = 1L;

    private int lineNumber;
    private Date dateReceived;
    private String sendingCenter;
    private String receivingCenter;
    private String patientNumber;
    private String inventoryId;
    private String shippingMethod;
    private String waybill;
    private String comment;

    @Override
    public int getLineNumber() {
        return lineNumber;
    }

    @Override
    public void setLineNumber(int lineNumber) {
        this.lineNumber = lineNumber;
    }

    public Date getDateReceived() {
        return dateReceived;
    }

    public void setDateReceived(Date dateReceived) {
        this.dateReceived = dateReceived;
    }

    public String getSendingCenter() {
        return sendingCenter;
    }

    public void setSendingCenter(String sendingCenter) {
        this.sendingCenter = sendingCenter;
    }

    public String getReceivingCenter() {
        return receivingCenter;
    }

    public void setReceivingCenter(String receivingCenter) {
        this.receivingCenter = receivingCenter;
    }

    public String getPatientNumber() {
        return patientNumber;
    }

    public void setPatientNumber(String patientNumber) {
        this.patientNumber = patientNumber;
    }

    public String getInventoryId() {
        return inventoryId;
    }

    public void setInventoryId(String inventoryId) {
        this.inventoryId = inventoryId;
    }

    public String getShippingMethod() {
        return shippingMethod;
    }

    public void setShippingMethod(String shippingMethod) {
        this.shippingMethod = shippingMethod;
    }

    public String getWaybill() {
        return waybill;
    }

    public void setWaybill(String waybill) {
        this.waybill = waybill;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

}
