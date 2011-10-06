package edu.ualberta.med.biobank.common.action.cevent;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;

import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.ActionException;
import edu.ualberta.med.biobank.common.action.specimen.GetCEventSpecimenInfosAction;
import edu.ualberta.med.biobank.common.peer.CollectionEventPeer;
import edu.ualberta.med.biobank.common.peer.PatientPeer;
import edu.ualberta.med.biobank.model.CollectionEvent;
import edu.ualberta.med.biobank.model.User;

public class GetCollectionEventInfoAction implements
    Action<CollectionEventWithFullInfo> {
    private static final long serialVersionUID = 1L;
    // @formatter:off
    @SuppressWarnings("nls")
    private static final String CEVENT_INFO_QRY = 
        "select cevent"
        + " from " + CollectionEvent.class.getName() + " as cevent"
        + " inner join fetch cevent." + CollectionEventPeer.PATIENT.getName() + " patient"
        + " inner join fetch cevent." + CollectionEventPeer.ACTIVITY_STATUS.getName() + " status"
        + " inner join fetch patient." + PatientPeer.STUDY.getName() + " study"
        + " where cevent." + CollectionEventPeer.ID.getName() + "=?"
        + " GROUP BY cevent";
    // @formatter:on

    private final Integer ceventId;

    public GetCollectionEventInfoAction(Integer ceventId) {
        this.ceventId = ceventId;
    }

    @Override
    public boolean isAllowed(User user, Session session) {
        return true; // TODO: restrict access
    }

    @Override
    public CollectionEventWithFullInfo doAction(Session session)
        throws ActionException {
        CollectionEventWithFullInfo ceventInfo = new CollectionEventWithFullInfo();

        Query query = session.createQuery(CEVENT_INFO_QRY);
        query.setParameter(0, ceventId);

        @SuppressWarnings("unchecked")
        List<CollectionEvent> rows = query.list();
        if (rows.size() == 1) {
            ceventInfo.cevent = rows.get(0);
            ceventInfo.sourceSpecimenInfos = new GetCEventSpecimenInfosAction(
                ceventId, false).doAction(session);
            ceventInfo.sourceSpecimenCount = (long) ceventInfo.sourceSpecimenInfos
                .size();
            ceventInfo.aliquotedSpecimenInfos = new GetCEventSpecimenInfosAction(
                ceventId, true).doAction(session);
            ceventInfo.aliquotedSpecimenCount = (long) ceventInfo.aliquotedSpecimenInfos
                .size();
            ceventInfo.eventAttrs = new GetEventAttrInfoAction(ceventId)
                .doAction(session);
        } else {
            // TODO: throw exception?
        }

        return ceventInfo;
    }

}
