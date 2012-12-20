package edu.ualberta.med.biobank.action.patient;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.hibernate.Query;

import edu.ualberta.med.biobank.action.Action;
import edu.ualberta.med.biobank.action.ActionContext;
import edu.ualberta.med.biobank.action.ListResult;
import edu.ualberta.med.biobank.action.exception.ActionException;
import edu.ualberta.med.biobank.action.patient.PatientGetCollectionEventInfosAction.PatientCEventInfo;
import edu.ualberta.med.biobank.model.study.CollectionEvent;
import edu.ualberta.med.biobank.model.util.NotAProxy;

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
        "SELECT cevent, COUNT(DISTINCT sourcesSpecs), "
            + "COUNT(DISTINCT allSpecs) - COUNT(DISTINCT sourcesSpecs),"
            + " MIN(sourcesSpecs.createdAt)"
            + " FROM " + CollectionEvent.class.getName() + " as cevent"
            + " LEFT JOIN cevent.originalSpecimens as sourcesSpecs"
            + " LEFT JOIN cevent.allSpecimens as allSpecs"
            + " WHERE cevent.patient.id=?"
            + " GROUP BY cevent";

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
            PatientCEventInfo ceventInfo =
                ceventInfoMap.get(((CollectionEvent) row[0]).getId());
            ceventInfo.sourceSpecimenCount = (Long) row[1];
            ceventInfo.aliquotedSpecimenCount = (Long) row[2];
            ceventInfo.minSourceSpecimenDate = (Date) row[3];

            ceventInfoMap.put(ceventInfo.cevent.getId(), ceventInfo);
        }

        return new ListResult<PatientCEventInfo>(ceventInfoMap.values());
    }
}
