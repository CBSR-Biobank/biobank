package edu.ualberta.med.biobank.common.action.specimen;

import java.util.Collection;
import java.util.EnumSet;

import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.ActionContext;
import edu.ualberta.med.biobank.common.action.BooleanResult;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.model.Dispatch;
import edu.ualberta.med.biobank.model.DispatchSpecimen;
import edu.ualberta.med.biobank.model.Specimen;
import edu.ualberta.med.biobank.model.type.DispatchSpecimenState;
import edu.ualberta.med.biobank.model.type.DispatchState;

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
        Collection<DispatchSpecimen> dsas = specimen
            .getDispatchSpecimens();
        if (dsas != null)
            for (DispatchSpecimen dsa : dsas) {
                Dispatch dispatch = dsa.getDispatch();
                if (!dispatch.getId().equals(excludedDispatchId)
                    && (EnumSet.of(DispatchState.CREATION,
                        DispatchState.IN_TRANSIT, DispatchState.RECEIVED)
                        .contains(dispatch.getState()))) {
                    if (DispatchSpecimenState.MISSING == dsa.getState()) {
                        return new BooleanResult(false);
                    }
                    return new BooleanResult(true);
                }
            }
        return new BooleanResult(false);
    }
}
