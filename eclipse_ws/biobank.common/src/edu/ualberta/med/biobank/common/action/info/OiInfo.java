package edu.ualberta.med.biobank.common.action.info;

import java.util.List;

public class OiInfo {
    public Integer oiId;
    public Integer siteId;
    public Integer centerId;
    public Integer siId;
    public List<Integer> specIds;

    public OiInfo(Integer oiId, Integer siteId, Integer centerId, Integer siId,
        List<Integer> specIds) {
        this.oiId = oiId;
        this.siteId = siteId;
        this.centerId = centerId;
        this.siId = siId;
        this.specIds = specIds;
    }

}
