package edu.ualberta.med.biobank.common.action.patient;

import java.util.Collections;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;

import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.ActionException;
import edu.ualberta.med.biobank.common.action.cevent.GetPatientCollectionEventInfosAction;
import edu.ualberta.med.biobank.common.peer.CollectionEventPeer;
import edu.ualberta.med.biobank.common.peer.PatientPeer;
import edu.ualberta.med.biobank.common.peer.SpecimenPeer;
import edu.ualberta.med.biobank.model.Patient;
import edu.ualberta.med.biobank.model.User;

public class GetPatientInfoAction implements Action<PatientInfo> {
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
    // @formatter:on

    private final Integer patientId;

    public GetPatientInfoAction(Integer patientId) {
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
            pInfo.cevents = new GetPatientCollectionEventInfosAction(patientId)
                .doAction(session);
            Collections.sort(pInfo.cevents);

        } else {
            // TODO: throw exception?
        }

        return pInfo;
    }

}
