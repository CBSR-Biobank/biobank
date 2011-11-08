package edu.ualberta.med.biobank.common.action.info;

import java.util.Date;

public class SiInfo {

    public Integer siId;
    public String boxNumber;
    public Date packedAt;
    public Date receivedAt;
    public String waybill;
    public CommentInfo commentInfo;

    public SiInfo(Integer siId, String boxNumber, Date packedAt,
        Date receivedAt, String waybill, CommentInfo commentInfo) {
        this.siId = siId;
        this.boxNumber = boxNumber;
        this.packedAt = packedAt;
        this.receivedAt = receivedAt;
        this.waybill = waybill;
        this.commentInfo = commentInfo;
    }

}