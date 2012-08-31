package edu.ualberta.med.biobank.action.dispatch;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hibernate.Query;

import edu.ualberta.med.biobank.action.Action;
import edu.ualberta.med.biobank.action.ActionContext;
import edu.ualberta.med.biobank.action.SetResult;
import edu.ualberta.med.biobank.action.exception.ActionException;
import edu.ualberta.med.biobank.permission.dispatch.DispatchReadPermission;
import edu.ualberta.med.biobank.model.ShipmentSpecimen;

public class DispatchGetSpecimenInfosAction implements
    Action<SetResult<ShipmentSpecimen>> {

    @SuppressWarnings("nls")
    public static final String DISPATCH_SPECIMEN_INFO_HQL =
        "SELECT dspec FROM " + ShipmentSpecimen.class.getName() + " dspec"
            + " INNER JOIN FETCH dspec.specimen spec"
            + " INNER JOIN FETCH spec.specimenType"
            + " INNER JOIN FETCH spec.collectionEvent cevent"
            + " INNER JOIN FETCH spec.currentCenter center"
            + " INNER JOIN FETCH cevent.patient patient"
            + " INNER JOIN FETCH patient.study study"
            + " LEFT JOIN FETCH dspec.comments"
            + " WHERE dspec.dispatch.id=?";

    private static final long serialVersionUID = 1L;
    private Integer dispatchId;

    public DispatchGetSpecimenInfosAction(Integer dispatchId) {
        this.dispatchId = dispatchId;
    }

    @Override
    public boolean isAllowed(ActionContext context) throws ActionException {
        return new DispatchReadPermission(dispatchId).isAllowed(context);
    }

    @Override
    public SetResult<ShipmentSpecimen> run(ActionContext context)
        throws ActionException {
        Set<ShipmentSpecimen> specInfos =
            new HashSet<ShipmentSpecimen>();

        Query query =
            context.getSession().createQuery(DISPATCH_SPECIMEN_INFO_HQL);
        query.setParameter(0, dispatchId);

        @SuppressWarnings("unchecked")
        List<Object> rows = query.list();
        for (Object row : rows) {
            specInfos.add((ShipmentSpecimen) row);
        }
        return new SetResult<ShipmentSpecimen>(specInfos);
    }
}
