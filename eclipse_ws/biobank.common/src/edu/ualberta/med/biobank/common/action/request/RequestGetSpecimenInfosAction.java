package edu.ualberta.med.biobank.common.action.request;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;

import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.ListResult;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.common.peer.CollectionEventPeer;
import edu.ualberta.med.biobank.common.peer.PatientPeer;
import edu.ualberta.med.biobank.common.peer.RequestPeer;
import edu.ualberta.med.biobank.common.peer.RequestSpecimenPeer;
import edu.ualberta.med.biobank.common.peer.SpecimenPeer;
import edu.ualberta.med.biobank.common.permission.request.RequestReadPermission;
import edu.ualberta.med.biobank.common.wrappers.Property;
import edu.ualberta.med.biobank.model.RequestSpecimen;
import edu.ualberta.med.biobank.model.User;

public class RequestGetSpecimenInfosAction implements
    Action<ListResult<RequestSpecimen>> {

    @SuppressWarnings("nls")
    public static final String Request_SPECIMEN_INFO_HQL =
        "select rspec from "
            + RequestSpecimen.class.getName()
            + " as rspec inner join fetch rspec."
            + RequestSpecimenPeer.SPECIMEN.getName()
            + " as spec inner join fetch spec."
            + SpecimenPeer.SPECIMEN_TYPE.getName()
            + " inner join fetch spec."
            + SpecimenPeer.ACTIVITY_STATUS.getName()
            + " inner join fetch spec."
            + SpecimenPeer.COLLECTION_EVENT.getName()
            + " cevent inner join fetch spec."
            + SpecimenPeer.CURRENT_CENTER.getName()
            + " as center"
            + " inner join fetch cevent."
            + CollectionEventPeer.PATIENT.getName()
            + " as patient inner join fetch patient."
            + PatientPeer.STUDY.getName()
            + " study left join fetch spec."
            + SpecimenPeer.COMMENT_COLLECTION.getName()
            + " where rspec."
            + Property.concatNames(RequestSpecimenPeer.REQUEST,
                RequestPeer.ID) + "=?";

    private static final long serialVersionUID = 1L;
    private Integer oiId;

    public RequestGetSpecimenInfosAction(Integer oiId) {
        this.oiId = oiId;
    }

    @Override
    public boolean isAllowed(User user, Session session) throws ActionException {
        return new RequestReadPermission().isAllowed(user, session);
    }

    @Override
    public ListResult<RequestSpecimen> run(User user, Session session)
        throws ActionException {
        ArrayList<RequestSpecimen> specInfos =
            new ArrayList<RequestSpecimen>();

        Query query = session.createQuery(Request_SPECIMEN_INFO_HQL);
        query.setParameter(0, oiId);

        @SuppressWarnings("unchecked")
        List<Object> rows = query.list();
        for (Object row : rows) {
            specInfos.add((RequestSpecimen) row);
        }
        return new ListResult<RequestSpecimen>(specInfos);
    }
}
