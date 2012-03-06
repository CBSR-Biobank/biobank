package edu.ualberta.med.biobank.common.action.patient;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.hibernate.Query;

import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.ActionContext;
import edu.ualberta.med.biobank.common.action.MapResult;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.common.action.patient.PatientGetSimpleCollectionEventInfosAction.SimpleCEventInfo;
import edu.ualberta.med.biobank.common.peer.CollectionEventPeer;
import edu.ualberta.med.biobank.common.peer.PatientPeer;
import edu.ualberta.med.biobank.common.peer.SpecimenPeer;
import edu.ualberta.med.biobank.common.util.NotAProxy;
import edu.ualberta.med.biobank.common.wrappers.Property;
import edu.ualberta.med.biobank.model.CollectionEvent;

public class PatientGetSimpleCollectionEventInfosAction implements
    Action<MapResult<Integer, SimpleCEventInfo>> {
    private static final long serialVersionUID = 1L;
    // @formatter:off
    @SuppressWarnings("nls")
    private static final String CEVENT_INFO_QRY =
        "select cevent, COUNT(DISTINCT sourcesSpecs), min(sourcesSpecs."
            + SpecimenPeer.CREATED_AT.getName() + ")"
            + " from " + CollectionEvent.class.getName() + " as cevent"
            + " left join cevent."
            + CollectionEventPeer.ORIGINAL_SPECIMENS.getName()
            + " as sourcesSpecs"
            + " inner join fetch cevent.patient"
            + " where cevent."
            + Property.concatNames(CollectionEventPeer.PATIENT, PatientPeer.ID)
            + "=? group by cevent";
    // @formatter:on

    private final Integer patientId;

    public static class SimpleCEventInfo implements Serializable, NotAProxy {
        private static final long serialVersionUID = 1L;
        public CollectionEvent cevent;
        public Long sourceSpecimenCount;
        public Date minSourceSpecimenDate;
    }

    public PatientGetSimpleCollectionEventInfosAction(Integer patientId) {
        this.patientId = patientId;
    }

    @Override
    public boolean isAllowed(ActionContext context) {
        return true;
    }

    @Override
    public MapResult<Integer, SimpleCEventInfo> run(ActionContext context)
        throws ActionException {
        HashMap<Integer, SimpleCEventInfo> ceventInfos =
            new HashMap<Integer, SimpleCEventInfo>();

        Query query = context.getSession().createQuery(CEVENT_INFO_QRY);
        query.setParameter(0, patientId);

        @SuppressWarnings("unchecked")
        List<Object[]> rows = query.list();
        for (Object[] row : rows) {
            SimpleCEventInfo ceventInfo = new SimpleCEventInfo();
            ceventInfo.cevent = (CollectionEvent) row[0];
            ceventInfo.sourceSpecimenCount = (Long) row[1];
            ceventInfo.minSourceSpecimenDate = (Date) row[2];
            ceventInfos.put(ceventInfo.cevent.getId(), ceventInfo);
        }

        return new MapResult<Integer, SimpleCEventInfo>(ceventInfos);
    }
}
