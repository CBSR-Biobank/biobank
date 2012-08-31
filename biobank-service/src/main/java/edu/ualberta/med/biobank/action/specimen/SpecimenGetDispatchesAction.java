package edu.ualberta.med.biobank.action.specimen;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Query;

import edu.ualberta.med.biobank.action.Action;
import edu.ualberta.med.biobank.action.ActionContext;
import edu.ualberta.med.biobank.action.ActionResult;
import edu.ualberta.med.biobank.action.exception.ActionException;
import edu.ualberta.med.biobank.action.specimen.SpecimenGetDispatchesAction.SpecimenDispatchesInfo;
import edu.ualberta.med.biobank.permission.specimen.SpecimenReadPermission;
import edu.ualberta.med.biobank.model.Shipment;

public class SpecimenGetDispatchesAction implements
    Action<SpecimenDispatchesInfo> {
    private static final long serialVersionUID = 1L;

    @SuppressWarnings("nls")
    private static final String SPECIMEN_DISPATCH_HQL =
        "SELECT dispatch FROM " + Shipment.class.getName() + " dispatch"
            + " INNER JOIN FETCH dispatch.senderCenter"
            + " INNER JOIN FETCH dispatch.receiverCenter"
            + " LEFT JOIN FETCH dispatch.shipmentInfo"
            + " LEFT JOIN FETCH dispatch.dispatchSpecimens dspecs"
            + " LEFT JOIN FETCH dspecs.specimen specimens"
            + " WHERE specimens.id=?";

    public static class SpecimenDispatchesInfo implements ActionResult {
        private static final long serialVersionUID = 1L;

        public List<Shipment> dispatches;

        public SpecimenDispatchesInfo(List<Shipment> dispatches) {
            this.dispatches = dispatches;
        }

        public List<Shipment> getDispatches() {
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
        List<Shipment> dispatches = new ArrayList<Shipment>(0);

        Query query = context.getSession().createQuery(SPECIMEN_DISPATCH_HQL);
        query.setParameter(0, specimenId);

        @SuppressWarnings("unchecked")
        List<Shipment> resultset = query.list();
        if (resultset != null) {
            dispatches.addAll(resultset);
        }

        return new SpecimenDispatchesInfo(dispatches);
    }
}
