package edu.ualberta.med.biobank.common.action.csvimport.shipment;

import java.util.Date;

import edu.ualberta.med.biobank.common.action.csvimport.CsvInfo;

public class ShipmentCsvInfo extends CsvInfo {
    private static final long serialVersionUID = 1L;

    private Date dateReceived;
    private String sendingCenter;
    private String receivingCenter;
    private String patientNumber;
    private String inventoryId;
    private String shippingMethod;
    private String waybill;
    private String comment;

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
