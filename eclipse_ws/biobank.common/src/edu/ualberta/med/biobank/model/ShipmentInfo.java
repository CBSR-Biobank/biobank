package edu.ualberta.med.biobank.model;

import java.util.Date;

public class ShipmentInfo extends AbstractBiobankModel {
    private static final long serialVersionUID = 1L;

    private Date receivedAt;
    private Date packedAt;
    private String waybill;
    private String boxNumber;
    private ShippingMethod shippingMethod;

    public Date getReceivedAt() {
        return receivedAt;
    }

    public void setReceivedAt(Date receivedAt) {
        this.receivedAt = receivedAt;
    }

    public Date getPackedAt() {
        return packedAt;
    }

    public void setPackedAt(Date packedAt) {
        this.packedAt = packedAt;
    }

    public String getWaybill() {
        return waybill;
    }

    public void setWaybill(String waybill) {
        this.waybill = waybill;
    }

    public String getBoxNumber() {
        return boxNumber;
    }

    public void setBoxNumber(String boxNumber) {
        this.boxNumber = boxNumber;
    }

    public ShippingMethod getShippingMethod() {
        return shippingMethod;
    }

    public void setShippingMethod(ShippingMethod shippingMethod) {
        this.shippingMethod = shippingMethod;
    }
}
