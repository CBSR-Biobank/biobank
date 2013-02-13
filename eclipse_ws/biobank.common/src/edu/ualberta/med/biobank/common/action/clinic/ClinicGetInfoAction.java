package edu.ualberta.med.biobank.common.action.clinic;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Query;

import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.ActionContext;
import edu.ualberta.med.biobank.common.action.ActionResult;
import edu.ualberta.med.biobank.common.action.clinic.ClinicGetInfoAction.ClinicInfo;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.common.action.info.StudyCountInfo;
import edu.ualberta.med.biobank.common.permission.clinic.ClinicReadPermission;
import edu.ualberta.med.biobank.model.Clinic;
import edu.ualberta.med.biobank.model.Contact;

public class ClinicGetInfoAction implements Action<ClinicInfo> {
    private static final long serialVersionUID = 1L;

    @SuppressWarnings("nls")
    private static final String CLINIC_INFO_HQL =
        " FROM " + Clinic.class.getName() + " clinic"
            + " INNER JOIN FETCH clinic.address"
            + " LEFT JOIN FETCH clinic.contacts contacts"
            + " LEFT JOIN FETCH clinic.comments comments"
            + " LEFT JOIN FETCH contacts.studies"
            + " LEFT JOIN FETCH comments.user"
            + " WHERE clinic.id = ?";

    @SuppressWarnings("nls")
    private static final String CLINIC_COUNT_INFO_HQL =
        "SELECT clinic.id,COUNT(DISTINCT patients),COUNT(DISTINCT cevents)"
            + " FROM " + Clinic.class.getName() + " clinic"
            + " LEFT JOIN clinic.originInfos oi"
            + " LEFT JOIN oi.specimens spcs"
            + " LEFT JOIN spcs.collectionEvent cevents"
            + " LEFT JOIN cevents.patient patients"
            + " WHERE clinic.id=?"
            + " GROUP BY clinic.id";

    private final Integer clinicId;
    private final ClinicGetContactsAction getContacts;
    private final ClinicGetStudyInfoAction getStudyInfo;

    public ClinicGetInfoAction(Integer clinicId) {
        if (clinicId == null) {
            throw new IllegalArgumentException();
        }
        this.clinicId = clinicId;
        this.getContacts = new ClinicGetContactsAction(clinicId);
        this.getStudyInfo = new ClinicGetStudyInfoAction(clinicId);
    }

    @Override
    public boolean isAllowed(ActionContext context) throws ActionException {
        return new ClinicReadPermission(clinicId).isAllowed(context);
    }

    @Override
    public ClinicInfo run(ActionContext context) throws ActionException {
        Query query = context.getSession().createQuery(CLINIC_INFO_HQL);
        query.setParameter(0, clinicId);

        Clinic clinic = (Clinic) query.uniqueResult();
        ClinicInfo clinicInfo = new ClinicInfo();
        clinicInfo.clinic = clinic;

        query = context.getSession().createQuery(CLINIC_COUNT_INFO_HQL);
        query.setParameter(0, clinicId);

        Object[] items = (Object[]) query.uniqueResult();
        clinicInfo.patientCount = (Long) items[1];
        clinicInfo.collectionEventCount = (Long) items[2];
        clinicInfo.contacts = getContacts.run(context).getList();
        clinicInfo.studyInfos = getStudyInfo.run(context).getList();

        return clinicInfo;
    }

    public static class ClinicInfo implements ActionResult {
        private static final long serialVersionUID = 1L;

        public Clinic clinic;
        public Long patientCount;
        public Long collectionEventCount;
        public List<Contact> contacts = new ArrayList<Contact>();
        public List<StudyCountInfo> studyInfos =
            new ArrayList<StudyCountInfo>();
    }

}
