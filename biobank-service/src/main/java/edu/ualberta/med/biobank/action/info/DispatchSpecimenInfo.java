package edu.ualberta.med.biobank.action.info;

import edu.ualberta.med.biobank.action.ActionResult;
import edu.ualberta.med.biobank.model.type.ShipmentItemState;

public class DispatchSpecimenInfo implements ActionResult {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    public Integer id;
    public Integer specimenId;
    public ShipmentItemState state;

    public DispatchSpecimenInfo(Integer id, Integer specimenId, ShipmentItemState state) {
        this.id = id;
        this.specimenId = specimenId;
        this.state = state;
    }

}
