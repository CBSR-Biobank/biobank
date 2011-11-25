package edu.ualberta.med.biobank.common.action.clinic;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;

import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.ActionResult;
import edu.ualberta.med.biobank.common.action.clinic.ClinicGetInfoAction.ClinicInfo;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.common.action.info.StudyCountInfo;
import edu.ualberta.med.biobank.model.Clinic;
import edu.ualberta.med.biobank.model.Contact;
import edu.ualberta.med.biobank.model.User;

public class ClinicGetInfoAction implements Action<ClinicInfo> {
    private static final long serialVersionUID = 1L;

    // @formatter:off
    @SuppressWarnings("nls")
    private static final String CLINIC_INFO_HQL = 
        "SELECT clinic,COUNT(DISTINCT patients),COUNT(DISTINCT cevents)"
        + " FROM "+ Clinic.class.getName() + " clinic"
        + " LEFT JOIN clinic.originInfoCollection oi"
        + " LEFT JOIN oi.specimenCollection spcs"
        + " LEFT JOIN spcs.collectionEvent cevents"
        + " LEFT JOIN cevents.patient patients"
        + " WHERE clinic.id=?";
    // @formatter:on

    private final Integer clinicId;
    private final ClinicGetContactsAction getContacts;
    private final ClinicGetStudyInfoAction getStudyInfo;

    public ClinicGetInfoAction(Integer clinicId) {
        this.clinicId = clinicId;
        this.getContacts = new ClinicGetContactsAction(clinicId);
        this.getStudyInfo = new ClinicGetStudyInfoAction(clinicId);
    }

    @Override
    public boolean isAllowed(User user, Session session) throws ActionException {
        return true;
    }

    @Override
    public ClinicInfo run(
        User user, Session session) throws ActionException {
        ClinicInfo info = new ClinicInfo();

        Query query = session.createQuery(CLINIC_INFO_HQL);
        query.setParameter(0, clinicId);

        @SuppressWarnings("unchecked")
        List<Object[]> rows = query.list();
        if (rows.size() == 1) {
            Object[] row = rows.get(0);

            info.clinic = (Clinic) row[0];
            info.patientCount = (Long) row[1];
            info.ceventCount = (Long) row[2];
            info.contacts = getContacts.run(user, session).getList();
            info.studyInfos = getStudyInfo.run(user, session).getList();
        }

        return info;
    }

    public static class ClinicInfo implements ActionResult {
        private static final long serialVersionUID = 1L;

        public Clinic clinic;
        public Long patientCount;
        public Long ceventCount;
        public List<Contact> contacts;
        public List<StudyCountInfo> studyInfos;

        public Clinic getClinic() {
            return clinic;
        }

        public Long getPatientCount() {
            return patientCount;
        }

        public Long getCeventCount() {
            return ceventCount;
        }

        public List<StudyCountInfo> getStudyInfos() {
            return studyInfos;
        }

    }

}
