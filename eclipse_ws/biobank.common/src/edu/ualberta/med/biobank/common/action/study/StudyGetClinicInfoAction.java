package edu.ualberta.med.biobank.common.action.study;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;

import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.common.action.study.StudyGetClinicInfoAction.ClinicInfo;
import edu.ualberta.med.biobank.common.util.NotAProxy;
import edu.ualberta.med.biobank.model.Clinic;
import edu.ualberta.med.biobank.model.Study;
import edu.ualberta.med.biobank.model.User;

public class StudyGetClinicInfoAction implements Action<ArrayList<ClinicInfo>> {
    private static final long serialVersionUID = 1L;

    // @formatter:off
    @SuppressWarnings("nls")
    private static final String STUDY_INFO_HQL = 
        "SELECT clinics,COUNT(DISTINCT patients)," 
        + " COUNT(DISTINCT cevents)"
        + " FROM " + Study.class.getName() + " study"
        + " LEFT JOIN study.contactCollection contacts"
        + " LEFT JOIN contacts.clinic clinics"
        + " LEFT JOIN clinics.originInfoCollection originInfo"
        + " LEFT JOIN originInfo.specimenCollection specimens"
        + " LEFT JOIN specimens.collectionEvent cevents"
        + " LEFT JOIN cevents.patient patients"
        + " WHERE study.id = ?"
        + " GROUP BY clinics";
    // @formatter:on

    private final Integer studyId;

    public StudyGetClinicInfoAction(Integer studyId) {
        this.studyId = studyId;
    }

    public StudyGetClinicInfoAction(Study study) {
        this(study.getId());
    }

    @Override
    public boolean isAllowed(User user, Session session) throws ActionException {
        return true;
    }

    @Override
    public ArrayList<ClinicInfo> run(User user, Session session)
        throws ActionException {
        ArrayList<ClinicInfo> infos = new ArrayList<ClinicInfo>();

        Query query = session.createQuery(STUDY_INFO_HQL);
        query.setParameter(0, studyId);

        @SuppressWarnings("unchecked")
        List<Object[]> results = query.list();
        for (Object[] row : results) {
            ClinicInfo info = new ClinicInfo((Clinic) row[0], (Long) row[1],
                (Long) row[2]);
            infos.add(info);
        }

        return infos;
    }

    public static class ClinicInfo implements Serializable, NotAProxy {
        private static final long serialVersionUID = 1L;

        private final Clinic clinic;
        private final Long patientCount;
        private final Long ceventCount;

        public ClinicInfo(Clinic clinic, Long patientCount, Long ceventCount) {
            this.clinic = clinic;
            this.patientCount = patientCount;
            this.ceventCount = ceventCount;
        }

        public Clinic getClinic() {
            return clinic;
        }

        public Long getPatientCount() {
            return patientCount;
        }

        public Long getCeventCount() {
            return ceventCount;
        }
    }
}
