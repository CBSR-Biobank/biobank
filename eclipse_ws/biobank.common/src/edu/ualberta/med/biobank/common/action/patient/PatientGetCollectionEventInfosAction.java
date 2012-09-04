package edu.ualberta.med.biobank.common.action.patient;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.hibernate.Query;

import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.ActionContext;
import edu.ualberta.med.biobank.common.action.ListResult;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.common.action.patient.PatientGetCollectionEventInfosAction.PatientCEventInfo;
import edu.ualberta.med.biobank.common.peer.SpecimenPeer;
import edu.ualberta.med.biobank.common.util.NotAProxy;
import edu.ualberta.med.biobank.model.CollectionEvent;

public class PatientGetCollectionEventInfosAction implements
    Action<ListResult<PatientCEventInfo>> {
    private static final long serialVersionUID = 1L;

    @SuppressWarnings("nls")
    private static final String CEVENT_INFO_QRY =
        "SELECT distinct cevent"
            + " FROM " + CollectionEvent.class.getName() + " as cevent"
            + " LEFT JOIN FETCH cevent.comments comments"
            + " LEFT JOIN FETCH comments.user"
            + " WHERE cevent.patient.id=?";

    @SuppressWarnings("nls")
    private static final String CEVENT_COUNT_INFO_QRY =
        "SELECT cevent.id, COUNT(DISTINCT sourcesSpecs), "
            + "COUNT(DISTINCT allSpecs) - COUNT(DISTINCT sourcesSpecs),"
            + " MIN(sourcesSpecs." + SpecimenPeer.CREATED_AT.getName() + ")"
            + " FROM " + CollectionEvent.class.getName() + " as cevent"
            + " LEFT JOIN cevent.originalSpecimens as sourcesSpecs"
            + " LEFT JOIN cevent.allSpecimens as allSpecs"
            + " WHERE cevent.patient.id=?"
            + " GROUP BY cevent.id";

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
    public boolean isAllowed(ActionContext context) {
        return true;
    }

    @SuppressWarnings("unchecked")
    @Override
    public ListResult<PatientCEventInfo> run(ActionContext context)
        throws ActionException {
        HashMap<Integer, PatientCEventInfo> ceventInfoMap =
            new HashMap<Integer, PatientCEventInfo>();

        Query query = context.getSession().createQuery(CEVENT_INFO_QRY);
        query.setParameter(0, patientId);

        List<CollectionEvent> rows = query.list();
        for (CollectionEvent cevent : rows) {
            PatientCEventInfo ceventInfo = new PatientCEventInfo();
            ceventInfo.cevent = cevent;
            ceventInfoMap.put(ceventInfo.cevent.getId(), ceventInfo);
        }

        query = context.getSession().createQuery(CEVENT_COUNT_INFO_QRY);
        query.setParameter(0, patientId);

        List<Object[]> rows2 = query.list();
        for (Object[] row : rows2) {
            Integer ceventId = (Integer) row[0];
            PatientCEventInfo ceventInfo = ceventInfoMap.get(ceventId);

            if (ceventInfo == null) {
                throw new NullPointerException(
                    "collection event not found in query result"); //$NON-NLS-1$
            }

            ceventInfo.sourceSpecimenCount = (Long) row[1];
            ceventInfo.aliquotedSpecimenCount = (Long) row[2];
            ceventInfo.minSourceSpecimenDate = (Date) row[3];

            ceventInfoMap.put(ceventInfo.cevent.getId(), ceventInfo);
        }

        return new ListResult<PatientCEventInfo>(ceventInfoMap.values());
    }
}
