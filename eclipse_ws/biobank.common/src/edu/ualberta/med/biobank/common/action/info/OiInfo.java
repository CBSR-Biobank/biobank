package edu.ualberta.med.biobank.common.action.info;

import java.io.Serializable;
import java.util.List;

public class OiInfo implements Serializable {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    
    public Integer oiId;
    public Integer siteId;
    public Integer centerId;
    public List<Integer> addedSpecIds;
    public List<Integer> removedSpecIds;

    public OiInfo(Integer oiId, Integer siteId, Integer centerId, 
        List<Integer> addedSpecIds, List<Integer> removedSpecIds) {
        this.oiId = oiId;
        this.siteId = siteId;
        this.centerId = centerId;
        this.addedSpecIds = addedSpecIds;
        this.removedSpecIds = removedSpecIds;
    }

}
