package edu.ualberta.med.biobank.action.info;

import edu.ualberta.med.biobank.action.ActionResult;
import edu.ualberta.med.biobank.model.type.DispatchSpecimenState;

public class DispatchSpecimenInfo implements ActionResult {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    public Integer id;
    public Integer specimenId;
    public DispatchSpecimenState state;

    public DispatchSpecimenInfo(Integer id, Integer specimenId, DispatchSpecimenState state) {
        this.id = id;
        this.specimenId = specimenId;
        this.state = state;
    }

}
