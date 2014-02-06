package edu.ualberta.med.biobank.common.action.info;

import java.util.Set;

import edu.ualberta.med.biobank.common.action.ActionResult;

public class OriginInfoSaveInfo implements ActionResult {
    private static final long serialVersionUID = 1L;

    public Integer oiId;
    public Integer siteId;
    public Integer centerId;
    public Set<Integer> addedSpecIds;
    public Set<Integer> removedSpecIds;
    public String comment;

    public OriginInfoSaveInfo(
        Integer oiId,
        Integer siteId,
        Integer centerId,
        String comment,
        Set<Integer> addedSpecimenIds,
        Set<Integer> removedSpecimenIds) {

        this.oiId = oiId;
        this.siteId = siteId;
        this.centerId = centerId;
        this.comment = comment;
        this.addedSpecIds = addedSpecimenIds;
        this.removedSpecIds = removedSpecimenIds;
    }

}
