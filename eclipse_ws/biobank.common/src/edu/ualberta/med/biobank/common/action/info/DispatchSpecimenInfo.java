package edu.ualberta.med.biobank.common.action.info;

import edu.ualberta.med.biobank.common.action.ActionResult;
import edu.ualberta.med.biobank.model.type.DispatchSpecimenState;

public class DispatchSpecimenInfo implements ActionResult {
    private static final long serialVersionUID = 1L;

    public final Integer dispatchSpecimenId;
    public final Integer specimenId;
    public final DispatchSpecimenState state;

    public DispatchSpecimenInfo(Integer dispatchSpecimenId, Integer specimenId,
        DispatchSpecimenState state) {
        this.dispatchSpecimenId = dispatchSpecimenId;
        this.specimenId = specimenId;
        this.state = state;
    }

}
