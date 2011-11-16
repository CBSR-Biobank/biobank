package edu.ualberta.med.biobank.common.action.patient;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;

import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.ActionResult;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.common.action.patient.PatientGetCollectionEventInfosAction.PatientCEventInfo;
import edu.ualberta.med.biobank.common.action.patient.PatientGetInfoAction.PatientInfo;
import edu.ualberta.med.biobank.common.peer.CollectionEventPeer;
import edu.ualberta.med.biobank.common.peer.PatientPeer;
import edu.ualberta.med.biobank.common.permission.patient.PatientReadPermission;
import edu.ualberta.med.biobank.model.Patient;
import edu.ualberta.med.biobank.model.User;

/**
 * Retrieve a patient information using a patient id
 * 
 * @author delphine
 * 
 */
public class PatientGetInfoAction implements Action<PatientInfo> {
    private static final long serialVersionUID = 1L;
    // @formatter:off
    @SuppressWarnings("nls")
    private static final String PATIENT_INFO_HQL = "SELECT patient, COUNT(DISTINCT sourceSpecs), COUNT(DISTINCT allSpecs) - COUNT(DISTINCT sourceSpecs)"
        + " FROM "
        + Patient.class.getName()
        + " patient"
        + " INNER JOIN FETCH patient."
        + PatientPeer.STUDY.getName()
        + " study"
        + " LEFT JOIN patient."
        + PatientPeer.COLLECTION_EVENT_COLLECTION.getName()
        + " AS cevents"
        + " LEFT JOIN cevents."
        + CollectionEventPeer.ORIGINAL_SPECIMEN_COLLECTION.getName()
        + " AS sourceSpecs"
        + " LEFT JOIN cevents."
        + CollectionEventPeer.ALL_SPECIMEN_COLLECTION.getName()
        + " AS allSpecs"
        + " WHERE patient.id = ?"
        + " GROUP BY patient";
    // @formatter:on

    private final Integer patientId;

    public static class PatientInfo implements ActionResult {
        private static final long serialVersionUID = 1L;

        public Patient patient;
        public List<PatientCEventInfo> cevents;
        public Long sourceSpecimenCount;
        public Long aliquotedSpecimenCount;

    }

    public PatientGetInfoAction(Integer patientId) {
        this.patientId = patientId;
    }

    @Override
    public boolean isAllowed(User user, Session session) {
        return new PatientReadPermission(patientId).isAllowed(user, session);
    }

    @Override
    public PatientInfo run(User user, Session session) throws ActionException {
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
            pInfo.cevents = new PatientGetCollectionEventInfosAction(patientId)
                .run(user, session).getList();

        } else {
            throw new ActionException("No patient found with id:" + patientId); //$NON-NLS-1$
        }

        return pInfo;
    }

}
