package edu.ualberta.med.biobank.common.action.patient;

import java.util.ArrayList;
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

public class SearchPatientAction implements Action<PatientInfo> {
    private static final long serialVersionUID = 1L;

    // @formatter:off
    @SuppressWarnings("nls")
    private static final String PATIENT_INFO_QRY = 
        "from " + Patient.class.getName() + " as p" 
        + " INNER JOIN FETCH p.study"
        + " where " + PatientPeer.PNUMBER.getName() + "=?";
    @SuppressWarnings("nls")
    private static final String CEVENT_INFO_QRY = 
        "select cevent, COUNT(DISTINCT sourcesSpecs), min(sourcesSpecs." + SpecimenPeer.CREATED_AT.getName() + ")"
        + " from " + CollectionEvent.class.getName() + " as cevent"
        + " left join cevent." + CollectionEventPeer.ORIGINAL_SPECIMEN_COLLECTION.getName() + " as sourcesSpecs"
        + " where cevent." + Property.concatNames(CollectionEventPeer.PATIENT, PatientPeer.ID) + "=?"
        + " GROUP BY cevent"; 
    // @formatter:on

    private String pnumber;

    public SearchPatientAction(String pnumber) {
        this.pnumber = pnumber;
    }

    @Override
    public boolean isAllowed(User user, Session session) {
        // TODO restrict access
        return true;
    }

    @Override
    public PatientInfo doAction(Session session) throws ActionException {
        PatientInfo patientInfo = new PatientInfo();

        Query query = session.createQuery(PATIENT_INFO_QRY);
        query.setParameter(0, pnumber);

        @SuppressWarnings("unchecked")
        List<Patient> rows = query.list();
        if (rows.size() == 0) {
            return null;
        }
        if (rows.size() == 1) {
            patientInfo.patient = rows.get(0);
            patientInfo.cevents = getCollectionEvents(session,
                patientInfo.patient.getId());
            return patientInfo;
        }
        throw new ActionException(
            "More than one patient found with pnumber " + pnumber); //$NON-NLS-1$
        // FIXME need this kind of test ?
        // if (patient != null) {
        // StudyWrapper study = patient.getStudy();
        // List<CenterWrapper<?>> centers = new ArrayList<CenterWrapper<?>>(
        // study.getSiteCollection(false));
        // centers.addAll(study.getClinicCollection());
        // if (Collections.disjoint(centers, user.getWorkingCenters())) {
        // throw new ApplicationException(MessageFormat.format(
        //                    Messages.getString("PatientWrapper.patient.access.msg"), //$NON-NLS-1$
        // patientNumber));
        // }
        // }

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
            ceventInfo.minSourceSpecimenDate = (Date) row[2];
            ceventInfos.add(ceventInfo);
        }
        return ceventInfos;
    }

}
