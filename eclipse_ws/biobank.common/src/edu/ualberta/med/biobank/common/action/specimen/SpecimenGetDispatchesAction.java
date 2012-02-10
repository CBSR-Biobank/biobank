package edu.ualberta.med.biobank.common.action.specimen;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Query;

import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.ActionContext;
import edu.ualberta.med.biobank.common.action.ActionResult;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.common.action.specimen.SpecimenGetDispatchesAction.SpecimenDispatchesInfo;
import edu.ualberta.med.biobank.common.permission.specimen.SpecimenReadPermission;
import edu.ualberta.med.biobank.model.Dispatch;

public class SpecimenGetDispatchesAction implements
    Action<SpecimenDispatchesInfo> {
    private static final long serialVersionUID = 1L;

    @SuppressWarnings("nls")
    private static final String SPECIMEN_DISPATCH_HQL =
        "SELECT dispatch FROM " + Dispatch.class.getName() + " dispatch"
            + " INNER JOIN FETCH dispatch.senderCenter"
            + " INNER JOIN FETCH dispatch.receiverCenter"
            + " LEFT JOIN FETCH dispatch.shipmentInfo"
            + " LEFT JOIN FETCH dispatch.dispatchSpecimenCollection dspecs"
            + " LEFT JOIN FETCH dspecs.specimen specimens"
            + " WHERE specimens.id=?";

    public static class SpecimenDispatchesInfo implements ActionResult {
        private static final long serialVersionUID = 1L;

        public List<Dispatch> dispatches;

        public SpecimenDispatchesInfo(List<Dispatch> dispatches) {
            this.dispatches = dispatches;
        }

        public List<Dispatch> getDispatches() {
            return dispatches;
        }
    }

    private final Integer specimenId;

    public SpecimenGetDispatchesAction(Integer specimenId) {
        this.specimenId = specimenId;
    }

    @Override
    public boolean isAllowed(ActionContext context) throws ActionException {
        return new SpecimenReadPermission(specimenId).isAllowed(context);
    }

    @Override
    public SpecimenDispatchesInfo run(ActionContext context)
        throws ActionException {
        List<Dispatch> dispatches = new ArrayList<Dispatch>(0);

        Query query = context.getSession().createQuery(SPECIMEN_DISPATCH_HQL);
        query.setParameter(0, specimenId);

        @SuppressWarnings("unchecked")
        List<Dispatch> resultset = query.list();
        if (resultset != null) {
            dispatches.addAll(resultset);
        }

        return new SpecimenDispatchesInfo(dispatches);
    }
}
