package edu.ualberta.med.biobank.action.info;

import java.util.Date;

import edu.ualberta.med.biobank.action.ActionResult;

public class ShipmentInfoSaveInfo implements ActionResult {
    private static final long serialVersionUID = 1L;

    public Integer siId;
    public String boxNumber;
    public Date packedAt;
    public Date receivedAt;
    public String waybill;
    public Integer shippingMethodId;

    public ShipmentInfoSaveInfo(Integer siId, String boxNumber, Date packedAt,
        Date receivedAt, String waybill, Integer shippingMethodId) {
        this.siId = siId;
        this.boxNumber = boxNumber;
        this.packedAt = packedAt;
        this.receivedAt = receivedAt;
        this.waybill = waybill;
        this.shippingMethodId = shippingMethodId;
    }

}