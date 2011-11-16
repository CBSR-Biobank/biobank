package edu.ualberta.med.biobank.common.action.patient;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;

import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.ListResult;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.common.action.patient.PatientGetCollectionEventInfosAction.PatientCEventInfo;
import edu.ualberta.med.biobank.common.peer.CollectionEventPeer;
import edu.ualberta.med.biobank.common.peer.PatientPeer;
import edu.ualberta.med.biobank.common.peer.SpecimenPeer;
import edu.ualberta.med.biobank.common.util.NotAProxy;
import edu.ualberta.med.biobank.common.wrappers.Property;
import edu.ualberta.med.biobank.model.CollectionEvent;
import edu.ualberta.med.biobank.model.User;

public class PatientGetCollectionEventInfosAction implements
    Action<ListResult<PatientCEventInfo>> {
    private static final long serialVersionUID = 1L;
    // @formatter:off
    @SuppressWarnings("nls")
    private static final String CEVENT_INFO_QRY =
        "select cevent, COUNT(DISTINCT sourcesSpecs), COUNT(DISTINCT allSpecs) - COUNT(DISTINCT sourcesSpecs)," 
            + " min(sourcesSpecs." + SpecimenPeer.CREATED_AT.getName() + ")" 
            + " from " + CollectionEvent.class.getName() + " as cevent" 
            + " left join cevent." + CollectionEventPeer.ORIGINAL_SPECIMEN_COLLECTION.getName() + " as sourcesSpecs"
            + " left join cevent." + CollectionEventPeer.ALL_SPECIMEN_COLLECTION.getName() + " as allSpecs"
            + " left join fetch cevent." + CollectionEventPeer.COMMENT_COLLECTION.getName()
            + " where cevent." + Property.concatNames(CollectionEventPeer.PATIENT, PatientPeer.ID) + "=?"
            + " GROUP BY cevent";
    // @formatter:on

    private final Integer patientId;

    public static class PatientCEventInfo implements Serializable, NotAProxy {
        private static final long serialVersionUID = 1L;
        public CollectionEvent cevent;
        public Long sourceSpecimenCount;
        public Long aliquotedSpecimenCount;
        public Date minSourceSpecimenDate;
    }

    public PatientGetCollectionEventInfosAction(Integer patientId) {
        this.patientId = patientId;
    }

    @Override
    public boolean isAllowed(User user, Session session) {
        return true;
    }

    @Override
    public ListResult<PatientCEventInfo> run(User user, Session session)
        throws ActionException {
        ArrayList<PatientCEventInfo> ceventInfos =
            new ArrayList<PatientCEventInfo>();

        Query query = session.createQuery(CEVENT_INFO_QRY);
        query.setParameter(0, patientId);

        @SuppressWarnings("unchecked")
        List<Object[]> rows = query.list();
        for (Object[] row : rows) {
            PatientCEventInfo ceventInfo = new PatientCEventInfo();
            ceventInfo.cevent = (CollectionEvent) row[0];
            ceventInfo.sourceSpecimenCount = (Long) row[1];
            ceventInfo.aliquotedSpecimenCount = (Long) row[2];
            ceventInfo.minSourceSpecimenDate = (Date) row[3];
            ceventInfos.add(ceventInfo);
        }
        return new ListResult<PatientCEventInfo>(ceventInfos);
    }
}
