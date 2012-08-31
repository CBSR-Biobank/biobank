package edu.ualberta.med.biobank.action.specimen;

import java.util.Collection;
import java.util.EnumSet;

import edu.ualberta.med.biobank.action.Action;
import edu.ualberta.med.biobank.action.ActionContext;
import edu.ualberta.med.biobank.action.BooleanResult;
import edu.ualberta.med.biobank.action.exception.ActionException;
import edu.ualberta.med.biobank.model.Shipment;
import edu.ualberta.med.biobank.model.ShipmentSpecimen;
import edu.ualberta.med.biobank.model.Specimen;
import edu.ualberta.med.biobank.model.type.ShipmentItemState;
import edu.ualberta.med.biobank.model.type.ShipmentState;

public class SpecimenIsUsedInDispatchAction implements Action<BooleanResult> {

    private static final long serialVersionUID = 1L;
    private Integer specimenId;
    private Integer excludedDispatchId;

    public SpecimenIsUsedInDispatchAction(Integer specimenId) {
        this.specimenId = specimenId;
    }

    public SpecimenIsUsedInDispatchAction(Integer specimenId,
        Integer excludedDispatchId) {
        this.specimenId = specimenId;
        this.excludedDispatchId = excludedDispatchId;
    }

    @Override
    public boolean isAllowed(ActionContext context) throws ActionException {
        return true;
    }

    @Override
    public BooleanResult run(ActionContext context) throws ActionException {
        Specimen specimen = context.load(Specimen.class,
            specimenId);
        // FIXME reused code from wrapper. Might be more efficient to use a hql
        // query!
        Collection<ShipmentSpecimen> dsas = specimen
            .getDispatchSpecimens();
        if (dsas != null)
            for (ShipmentSpecimen dsa : dsas) {
                Shipment dispatch = dsa.getDispatch();
                if (!dispatch.getId().equals(excludedDispatchId)
                    && (EnumSet.of(ShipmentState.PACKED,
                        ShipmentState.IN_TRANSIT, ShipmentState.RECEIVED)
                        .contains(dispatch.getState()))) {
                    if (ShipmentItemState.MISSING == dsa.getState()) {
                        return new BooleanResult(false);
                    }
                    return new BooleanResult(true);
                }
            }
        return new BooleanResult(false);
    }
}
