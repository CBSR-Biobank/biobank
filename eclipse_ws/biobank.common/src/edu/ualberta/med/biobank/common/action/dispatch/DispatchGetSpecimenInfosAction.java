package edu.ualberta.med.biobank.common.action.dispatch;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;

import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.ListResult;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.common.peer.CollectionEventPeer;
import edu.ualberta.med.biobank.common.peer.DispatchPeer;
import edu.ualberta.med.biobank.common.peer.DispatchSpecimenPeer;
import edu.ualberta.med.biobank.common.peer.PatientPeer;
import edu.ualberta.med.biobank.common.peer.SpecimenPeer;
import edu.ualberta.med.biobank.common.permission.dispatch.DispatchReadPermission;
import edu.ualberta.med.biobank.common.wrappers.Property;
import edu.ualberta.med.biobank.model.DispatchSpecimen;
import edu.ualberta.med.biobank.model.User;

public class DispatchGetSpecimenInfosAction implements
    Action<ListResult<DispatchSpecimen>> {

    @SuppressWarnings("nls")
    public static final String DISPATCH_SPECIMEN_INFO_HQL = "select dspec from "
        + DispatchSpecimen.class.getName() + " as dspec inner join fetch dspec."
        + DispatchSpecimenPeer.SPECIMEN.getName() + " as spec inner join fetch spec."
        + SpecimenPeer.SPECIMEN_TYPE.getName()  + " inner join fetch spec."
        + SpecimenPeer.ACTIVITY_STATUS.getName() + " inner join fetch spec."
        + SpecimenPeer.COLLECTION_EVENT.getName() + " cevent inner join fetch spec."
        + SpecimenPeer.CURRENT_CENTER.getName() + " as center"
        + " inner join fetch cevent." + CollectionEventPeer.PATIENT.getName()
        + " as patient inner join fetch patient." + PatientPeer.STUDY.getName()
        + " study left join fetch spec."
        + SpecimenPeer.COMMENT_COLLECTION.getName()
        + " where dspec." + Property.concatNames(DispatchSpecimenPeer.DISPATCH, DispatchPeer.ID) +"=?";

    private static final long serialVersionUID = 1L;
    private Integer oiId;

    public DispatchGetSpecimenInfosAction(Integer oiId) {
        this.oiId = oiId;
    }

    @Override
    public boolean isAllowed(User user, Session session) throws ActionException {
        return new DispatchReadPermission(oiId).isAllowed(user, session);
    }

    @Override
    public ListResult<DispatchSpecimen> run(User user, Session session)
        throws ActionException {
        ArrayList<DispatchSpecimen> specInfos =
            new ArrayList<DispatchSpecimen>();

        Query query = session.createQuery(DISPATCH_SPECIMEN_INFO_HQL);
        query.setParameter(0, oiId);

        @SuppressWarnings("unchecked")
        List<Object> rows = query.list();
        for (Object row : rows) {
            specInfos.add((DispatchSpecimen) row);
        }
        return new ListResult<DispatchSpecimen>(specInfos);
    }
}
