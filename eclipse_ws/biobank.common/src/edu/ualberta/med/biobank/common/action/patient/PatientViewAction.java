package edu.ualberta.med.biobank.common.action.patient;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;

import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.ActionException;
import edu.ualberta.med.biobank.common.action.cevent.CollectionEventInfo;
import edu.ualberta.med.biobank.common.peer.CollectionEventPeer;
import edu.ualberta.med.biobank.common.peer.PatientPeer;
import edu.ualberta.med.biobank.common.peer.SpecimenPeer;
import edu.ualberta.med.biobank.common.wrappers.Property;
import edu.ualberta.med.biobank.model.CollectionEvent;
import edu.ualberta.med.biobank.model.Patient;
import edu.ualberta.med.biobank.model.User;

public class PatientViewAction implements Action<PatientInfo> {
    private static final long serialVersionUID = 1L;
    // @formatter:off
    @SuppressWarnings("nls")
    private static final String PATIENT_INFO_HQL = "SELECT patient, COUNT(DISTINCT sourceSpecs), COUNT(DISTINCT aliquotedSpecs)"
        + " FROM " + Patient.class.getName() + " patient"
        + " INNER JOIN FETCH patient." + PatientPeer.STUDY.getName() + " study"
        + " LEFT JOIN patient." + PatientPeer.COLLECTION_EVENT_COLLECTION.getName() + " AS cevents"
        + " LEFT JOIN cevents." + CollectionEventPeer.ORIGINAL_SPECIMEN_COLLECTION.getName() + " AS sourceSpecs"
        + " LEFT JOIN cevents." +  CollectionEventPeer.ALL_SPECIMEN_COLLECTION.getName() + " AS aliquotedSpecs"
        + " WHERE patient.id = ?"
        + " AND aliquotedSpecs." + SpecimenPeer.PARENT_SPECIMEN.getName()+ " IS NULL" // count only aliquoted Specimen-s
        + " GROUP BY patient";
    @SuppressWarnings("nls")
    private static final String CEVENT_INFO_QRY = 
        "select cevent, COUNT(DISTINCT sourcesSpecs), COUNT(DISTINCT aliquotedSpecs), min(sourcesSpecs." + SpecimenPeer.CREATED_AT.getName() + ")"
        + " from " + CollectionEvent.class.getName() + " as cevent"
        + " left join cevent." + CollectionEventPeer.ORIGINAL_SPECIMEN_COLLECTION.getName() + " as sourcesSpecs"
        + " left join cevent." + CollectionEventPeer.ALL_SPECIMEN_COLLECTION.getName() + " as aliquotedSpecs"
        + " where cevent." + Property.concatNames(CollectionEventPeer.PATIENT, PatientPeer.ID) + "=?"
        + " and aliquotedSpecs." + SpecimenPeer.PARENT_SPECIMEN.getName()+ " is null" // count only aliquoted Specimen-s
        + " GROUP BY cevent"; 
    // @formatter:on

    private final Integer patientId;

    public PatientViewAction(Integer patientId) {
        this.patientId = patientId;
    }

    @Override
    public boolean isAllowed(User user, Session session) {
        return true; // TODO: restrict access
    }

    @Override
    public PatientInfo doAction(Session session) throws ActionException {
        PatientInfo pInfo = new PatientInfo();

        Query query = session.createQuery(PATIENT_INFO_HQL);
        query.setParameter(0, patientId);

        @SuppressWarnings("unchecked")
        List<Object[]> rows = query.list();
        if (rows.size() == 1) {
            Object[] row = rows.get(0);

            pInfo.patient = (Patient) row[0];
            pInfo.sourceSpecimenCount = (Long) row[1];
            pInfo.aliquotedSpecimenCount = (Long) row[2];
            pInfo.cevents = getCollectionEvents(session, pInfo.patient.getId());
            Collections.sort(pInfo.cevents);

        } else {
            // TODO: throw exception?
        }

        return pInfo;
    }

    private List<CollectionEventInfo> getCollectionEvents(Session session,
        Integer patientId) {
        List<CollectionEventInfo> ceventInfos = new ArrayList<CollectionEventInfo>();

        Query query = session.createQuery(CEVENT_INFO_QRY);
        query.setParameter(0, patientId);

        @SuppressWarnings("unchecked")
        List<Object[]> rows = query.list();
        for (Object[] row : rows) {
            CollectionEventInfo ceventInfo = new CollectionEventInfo();
            ceventInfo.cevent = (CollectionEvent) row[0];
            ceventInfo.sourceSpecimenCount = (Long) row[1];
            ceventInfo.aliquotedSpecimenCount = (Long) row[2];
            ceventInfo.minSourceSpecimenDate = (Date) row[3];
            ceventInfos.add(ceventInfo);
        }
        return ceventInfos;
    }
}
