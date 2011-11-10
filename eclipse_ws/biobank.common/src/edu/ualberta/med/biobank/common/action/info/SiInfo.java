package edu.ualberta.med.biobank.common.action.info;

import java.io.Serializable;
import java.util.Date;

public class SiInfo implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    public Integer siId;
    public String boxNumber;
    public Date packedAt;
    public Date receivedAt;
    public String waybill;
    public CommentInfo commentInfo;
    public ShippingMethodInfo method;

    public SiInfo(Integer siId, String boxNumber, Date packedAt,
        Date receivedAt, String waybill, CommentInfo commentInfo, ShippingMethodInfo method) {
        this.siId = siId;
        this.boxNumber = boxNumber;
        this.packedAt = packedAt;
        this.receivedAt = receivedAt;
        this.waybill = waybill;
        this.commentInfo = commentInfo;
        this.method = method;
    }

}