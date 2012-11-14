package edu.ualberta.med.biobank.common.action.info;

import java.util.Date;

import edu.ualberta.med.biobank.common.action.ActionResult;

public class ShipmentInfoSaveInfo implements ActionResult {
    private static final long serialVersionUID = 1L;

    public final Integer siId;
    public final String boxNumber;
    public final Date packedAt;
    public final Date receivedAt;
    public final String waybill;
    public final Integer shippingMethodId;

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