package edu.ualberta.med.biobank.common.action.patient;

import java.io.Serializable;
import java.text.MessageFormat;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;

import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.ActionException;
import edu.ualberta.med.biobank.common.action.patient.SearchPatientAction.SearchedPatientInfo;
import edu.ualberta.med.biobank.common.peer.PatientPeer;
import edu.ualberta.med.biobank.common.util.NotAProxy;
import edu.ualberta.med.biobank.model.Patient;
import edu.ualberta.med.biobank.model.Study;
import edu.ualberta.med.biobank.model.User;

public class SearchPatientAction implements Action<SearchedPatientInfo> {
    private static final long serialVersionUID = 1L;

    // @formatter:off
    @SuppressWarnings("nls")
    private static final String PATIENT_INFO_QRY = 
        " select p, study, count(cevents)"
        + " from " + Patient.class.getName() + " as p" 
        + " LEFT JOIN p." + PatientPeer.STUDY.getName() + " as study"
        + " LEFT JOIN p." + PatientPeer.COLLECTION_EVENT_COLLECTION.getName() + " as cevents"
        + " where {0} GROUP BY p";
    private static final String WHERE_FOR_PNUMBER = "p." + PatientPeer.PNUMBER.getName() + "=?"; //$NON-NLS-1$
    private static final String WHERE_FOR_ID = "p." +  PatientPeer.ID.getName() + "=?"; //$NON-NLS-1$
    // @formatter:on

    private String pnumber;
    private Integer patientId;

    public static class SearchedPatientInfo implements Serializable, NotAProxy {
        private static final long serialVersionUID = 1L;
        public Patient patient;
        public Study study;
        public Long ceventsCount;
    }

    public SearchPatientAction(String pnumber) {
        this.pnumber = pnumber;
    }

    public SearchPatientAction(Integer id) {
        this.patientId = id;
    }

    @Override
    public boolean isAllowed(User user, Session session) {
        // TODO restrict access
        return true;
    }

    @Override
    public SearchedPatientInfo run(User user, Session session)
        throws ActionException {
        String hql = MessageFormat.format(PATIENT_INFO_QRY,
            pnumber == null ? WHERE_FOR_ID : WHERE_FOR_PNUMBER);

        Query query = session.createQuery(hql);
        query.setParameter(0, pnumber == null ? patientId : pnumber);

        @SuppressWarnings("unchecked")
        List<Object[]> rows = query.list();
        if (rows.size() == 0) {
            return null;
        }
        if (rows.size() == 1) {
            SearchedPatientInfo pinfo = new SearchedPatientInfo();
            Object[] row = rows.get(0);
            pinfo.patient = (Patient) row[0];
            pinfo.study = (Study) row[1];
            pinfo.ceventsCount = (Long) row[2];
            return pinfo;
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
    // private List<CollectionEventInfo> getCollectionEvents(Session session,
    // Integer patientId) {
    // List<CollectionEventInfo> ceventInfos = new
    // ArrayList<CollectionEventInfo>();
    //
    // Query query = session.createQuery(CEVENT_INFO_QRY);
    // query.setParameter(0, patientId);
    //
    // @SuppressWarnings("unchecked")
    // List<Object[]> rows = query.list();
    // for (Object[] row : rows) {
    // CollectionEventInfo ceventInfo = new CollectionEventInfo();
    // ceventInfo.cevent = (CollectionEvent) row[0];
    // ceventInfo.sourceSpecimenCount = (Long) row[1];
    // ceventInfo.minSourceSpecimenDate = (Date) row[2];
    // ceventInfos.add(ceventInfo);
    // }
    // return ceventInfos;
    // }

}
