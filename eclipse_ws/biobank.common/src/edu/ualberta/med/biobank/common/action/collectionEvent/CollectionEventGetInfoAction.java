package edu.ualberta.med.biobank.common.action.collectionEvent;

import java.util.List;
import java.util.Map;

import org.hibernate.Query;
import org.hibernate.Session;

import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.ActionResult;
import edu.ualberta.med.biobank.common.action.collectionEvent.CollectionEventGetInfoAction.CEventInfo;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.common.action.specimen.SpecimenInfo;
import edu.ualberta.med.biobank.common.peer.CollectionEventPeer;
import edu.ualberta.med.biobank.common.peer.CommentPeer;
import edu.ualberta.med.biobank.common.peer.PatientPeer;
import edu.ualberta.med.biobank.common.permission.collectionEvent.CollectionEventReadPermission;
import edu.ualberta.med.biobank.model.CollectionEvent;
import edu.ualberta.med.biobank.model.Comment;
import edu.ualberta.med.biobank.model.User;

public class CollectionEventGetInfoAction implements Action<CEventInfo> {
    private static final long serialVersionUID = 1L;
    // @formatter:off
    @SuppressWarnings("nls")
    private static final String CEVENT_INFO_QRY =
        "select cevent"
            + " from " + CollectionEvent.class.getName() + " as cevent"
            + " inner join fetch cevent."
            + CollectionEventPeer.PATIENT.getName() + " patient"
            + " inner join fetch cevent."
            + CollectionEventPeer.ACTIVITY_STATUS.getName() + " status"
            + " left join fetch cevent."
            + CollectionEventPeer.COMMENT_COLLECTION.getName() + " comments"
            + " left join fetch comments."
            + CommentPeer.USER.getName() + " commentsUser"
            + " inner join fetch patient." + PatientPeer.STUDY.getName()
            + " study"
            + " where cevent." + CollectionEventPeer.ID.getName() + "=?"
            + " GROUP BY cevent";
    // @formatter:on

    private final Integer ceventId;

    public static class CEventInfo implements ActionResult {

        private static final long serialVersionUID = 1L;
        public CollectionEvent cevent;
        public List<SpecimenInfo> sourceSpecimenInfos;
        public List<SpecimenInfo> aliquotedSpecimenInfos;
        /**
         * Key is the studyeventAttr key this eventAttr refers to
         */
        public Map<Integer, EventAttrInfo> eventAttrs;

    }

    public CollectionEventGetInfoAction(Integer ceventId) {
        this.ceventId = ceventId;
    }

    @Override
    public boolean isAllowed(User user, Session session) {
        return new CollectionEventReadPermission(ceventId).isAllowed(user,
            session);
    }

    @Override
    public CEventInfo run(User user, Session session) throws ActionException {
        CEventInfo ceventInfo = new CEventInfo();

        Query query = session.createQuery(CEVENT_INFO_QRY);
        query.setParameter(0, ceventId);

        @SuppressWarnings("unchecked")
        List<CollectionEvent> rows = query.list();
        if (rows.size() == 1) {
            ceventInfo.cevent = rows.get(0);
            ceventInfo.sourceSpecimenInfos =
                new CollectionEventGetSpecimenInfosAction(
                    ceventId, false).run(user, session).getList();
            ceventInfo.aliquotedSpecimenInfos =
                new CollectionEventGetSpecimenInfosAction(
                    ceventId, true).run(user, session).getList();
            ceventInfo.eventAttrs = new CollectionEventGetEventAttrInfoAction(
                ceventId).run(
                user, session).getMap();
        } else {
            throw new ActionException("Cannot find a collection event with id=" //$NON-NLS-1$
                + ceventId);
        }

        return ceventInfo;
    }

}
