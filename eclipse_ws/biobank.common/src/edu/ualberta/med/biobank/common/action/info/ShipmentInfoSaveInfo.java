package edu.ualberta.med.biobank.common.action.info;

import java.util.Date;

import edu.ualberta.med.biobank.common.action.ActionResult;

public class ShipmentInfoSaveInfo implements ActionResult {
    private static final long serialVersionUID = 1L;

    public Integer siId;
    public String boxNumber;
    public Date packedAt;
    public Date receivedAt;
    public String waybill;
    public ShippingMethodInfo method;

    public ShipmentInfoSaveInfo(Integer siId, String boxNumber, Date packedAt,
        Date receivedAt, String waybill, ShippingMethodInfo method) {
        this.siId = siId;
        this.boxNumber = boxNumber;
        this.packedAt = packedAt;
        this.receivedAt = receivedAt;
        this.waybill = waybill;
        this.method = method;
    }

}