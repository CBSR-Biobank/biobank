package edu.ualberta.med.biobank.common.action.specimen;

import java.util.Collection;
import java.util.EnumSet;

import org.hibernate.Session;

import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.ActionContext;
import edu.ualberta.med.biobank.common.action.BooleanResult;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.common.util.DispatchSpecimenState;
import edu.ualberta.med.biobank.common.util.DispatchState;
import edu.ualberta.med.biobank.model.Dispatch;
import edu.ualberta.med.biobank.model.DispatchSpecimen;
import edu.ualberta.med.biobank.model.Specimen;
import edu.ualberta.med.biobank.model.User;

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
    public boolean isAllowed(User user, Session session) throws ActionException {
        return true;
    }

    @Override
    public BooleanResult run(User user, Session session) throws ActionException {
        Specimen specimen =
            new ActionContext(user, session).load(Specimen.class,
                specimenId);
        // FIXME reused code from wrapper. Might be more efficient to use a hql
        // query!
        Collection<DispatchSpecimen> dsas = specimen
            .getDispatchSpecimenCollection();
        if (dsas != null)
            for (DispatchSpecimen dsa : dsas) {
                Dispatch dispatch = dsa.getDispatch();
                if (!dispatch.getId().equals(excludedDispatchId)
                    && (EnumSet.of(DispatchState.CREATION,
                        DispatchState.IN_TRANSIT, DispatchState.RECEIVED)
                        .contains(DispatchState.getState(dispatch
                            .getState())))) {
                    if (DispatchSpecimenState.MISSING
                        .equals(DispatchSpecimenState.getState(dsa.getState()))) {
                        return new BooleanResult(false);
                    }
                    return new BooleanResult(true);
                }
            }
        return new BooleanResult(false);
    }

}
