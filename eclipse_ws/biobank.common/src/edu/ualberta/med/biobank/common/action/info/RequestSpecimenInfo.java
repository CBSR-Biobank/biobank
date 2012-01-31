package edu.ualberta.med.biobank.common.action.info;

import edu.ualberta.med.biobank.common.action.ActionResult;

public class RequestSpecimenInfo implements ActionResult {

    /**
     * 
     */
    private static final long serialVersionUID = 5260133684503788232L;
    public Integer requestSpecimenId;
    public Integer requestSpecimenStateId;
    public String claimedBy;

    public RequestSpecimenInfo(Integer requestSpecimenId,
        Integer requestSpecimenStateId, String claimedBy) {
        this.requestSpecimenId = requestSpecimenId;
        this.requestSpecimenStateId = requestSpecimenStateId;
        this.claimedBy = claimedBy;
    }
}
