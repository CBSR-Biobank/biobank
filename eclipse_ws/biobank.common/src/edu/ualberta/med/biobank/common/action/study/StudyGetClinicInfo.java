package edu.ualberta.med.biobank.common.action.study;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;

import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.common.action.study.StudyGetClinicInfo.ClinicInfo;
import edu.ualberta.med.biobank.common.util.NotAProxy;
import edu.ualberta.med.biobank.model.Clinic;
import edu.ualberta.med.biobank.model.Contact;
import edu.ualberta.med.biobank.model.Study;
import edu.ualberta.med.biobank.model.User;

public class StudyGetClinicInfo implements Action<ArrayList<ClinicInfo>> {
    private static final long serialVersionUID = 1L;

    // @formatter:off
    @SuppressWarnings("nls")
    private static final String STUDY_INFO_HQL = 
        "SELECT clinic,COUNT(DISTINCT patients),COUNT(DISTINCT cevents)" 
        + "contacts" 
        + "FROM "+ Study.class.getName() + " study"
        + " LEFT JOIN study.contactCollection AS contacts"
        + " LEFT JOIN contacts.clinic AS clinics"
        + " LEFT JOIN clinics.originInfoCollection AS originInfo"
        + " LEFT JOIN originInfo.specimenCollection as specimens"
        + " LEFT JOIN specimens.collectionEvent AS cevent"
        + " LEFT JOIN cevent.patient as patients"
        + " WHERE study.id = ?"
        + " GROUP BY clinic";
    // @formatter:on

    private final Integer studyId;

    public StudyGetClinicInfo(Integer studyId) {
        this.studyId = studyId;
    }

    public StudyGetClinicInfo(Study study) {
        this(study.getId());
    }

    @Override
    public boolean isAllowed(User user, Session session) throws ActionException {
        return true;
    }

    @Override
    public ArrayList<ClinicInfo> run(User user, Session session)
        throws ActionException {
        ArrayList<ClinicInfo> studies = new ArrayList<ClinicInfo>();

        Query query = session.createQuery(STUDY_INFO_HQL);
        query.setParameter(0, studyId);

        @SuppressWarnings("unchecked")
        List<Object[]> results = query.list();
        for (Object[] row : results) {

            ClinicInfo info = new ClinicInfo((Clinic) row[0], (Long) row[1],
                (Long) row[2], (Contact) row[3]);

            studies.add(info);
        }

        return studies;
    }

    public static class ClinicInfo implements Serializable, NotAProxy {
        private static final long serialVersionUID = 1L;

        private final Clinic clinic;
        private final Long patientCount;
        private final Long ceventCount;
        private final Contact contact;

        public ClinicInfo(Clinic clinic, Long patientCount, Long ceventCount,
            Contact contact) {
            this.clinic = clinic;
            this.patientCount = patientCount;
            this.ceventCount = ceventCount;
            this.contact = contact;

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

        public String getContactName() {
            return contact.getName();
        }

        public String getContactTitle() {
            return contact.getTitle();
        }
    }
}
