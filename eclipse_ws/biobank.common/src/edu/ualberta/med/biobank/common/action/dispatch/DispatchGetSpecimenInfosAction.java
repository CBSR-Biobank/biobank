package edu.ualberta.med.biobank.common.action.dispatch;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;

import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.ListResult;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.common.permission.dispatch.DispatchReadPermission;
import edu.ualberta.med.biobank.model.DispatchSpecimen;
import edu.ualberta.med.biobank.model.User;

public class DispatchGetSpecimenInfosAction implements
    Action<ListResult<DispatchSpecimen>> {

    @SuppressWarnings("nls")
    public static final String DISPATCH_SPECIMEN_INFO_HQL =
        "SELECT dspec FROM " + DispatchSpecimen.class.getName() + " dspec"
            + " INNER JOIN FETCH dspec.specimen spec"
            + " INNER JOIN FETCH spec.specimenType"
            + " INNER JOIN FETCH spec.activityStatus"
            + " INNER JOIN FETCH spec.collectionEvent cevent"
            + " INNER JOIN FETCH spec.currentCenter center"
            + " INNER JOIN FETCH cevent.patient patient"
            + " INNER JOIN FETCH patient.study study"
            + " LEFT JOIN FETCH spec.commentCollection"
            + " WHERE dspec.dispatch.id=?";

    private static final long serialVersionUID = 1L;
    private Integer dispatchId;

    public DispatchGetSpecimenInfosAction(Integer dispatchId) {
        this.dispatchId = dispatchId;
    }

    @Override
    public boolean isAllowed(User user, Session session) throws ActionException {
        return new DispatchReadPermission(dispatchId).isAllowed(user, session);
    }

    @Override
    public ListResult<DispatchSpecimen> run(User user, Session session)
        throws ActionException {
        ArrayList<DispatchSpecimen> specInfos =
            new ArrayList<DispatchSpecimen>();

        Query query = session.createQuery(DISPATCH_SPECIMEN_INFO_HQL);
        query.setParameter(0, dispatchId);

        @SuppressWarnings("unchecked")
        List<Object> rows = query.list();
        for (Object row : rows) {
            specInfos.add((DispatchSpecimen) row);
        }
        return new ListResult<DispatchSpecimen>(specInfos);
    }
}
