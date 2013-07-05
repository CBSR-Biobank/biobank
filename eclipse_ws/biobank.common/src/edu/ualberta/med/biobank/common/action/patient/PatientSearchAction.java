package edu.ualberta.med.biobank.common.action.patient;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.Restrictions;

import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.ActionContext;
import edu.ualberta.med.biobank.common.action.ActionResult;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.common.action.patient.PatientSearchAction.SearchedPatientInfo;
import edu.ualberta.med.biobank.common.permission.patient.PatientReadPermission;
import edu.ualberta.med.biobank.model.Patient;
import edu.ualberta.med.biobank.model.Study;

public class PatientSearchAction implements Action<SearchedPatientInfo> {
    private static final long serialVersionUID = 1L;

    @SuppressWarnings("nls")
    private static final String PATIENT_INFO_QRY =
        " SELECT p.id,COUNT(cevents)"
            + " FROM " + Patient.class.getName() + " p"
            + " LEFT JOIN p.study study"
            + " LEFT JOIN p.collectionEvents cevents"
            + " WHERE p.id=?"
            + " GROUP BY p.id";

    private final String pnumber;
    private final Integer patientId;

    public static class SearchedPatientInfo implements ActionResult {
        private static final long serialVersionUID = 1L;
        public Patient patient;
        public Study study;
        public Long ceventsCount;
    }

    public PatientSearchAction(String pnumber) {
        this.pnumber = pnumber;
        this.patientId = null;
    }

    public PatientSearchAction(Integer id) {
        this.patientId = id;
        this.pnumber = null;
    }

    @Override
    public boolean isAllowed(ActionContext context) {
        if (patientId != null) {
            return new PatientReadPermission(patientId).isAllowed(context);
        }
        return new PatientReadPermission(pnumber).isAllowed(context);
    }

    @Override
    public SearchedPatientInfo run(ActionContext context)
        throws ActionException {
        @SuppressWarnings("nls")
        Criteria criteria = context.getSession()
            .createCriteria(Patient.class, "p");

        if (pnumber != null) {
            criteria.add(Restrictions.eq("pnumber", pnumber)); //$NON-NLS-1$
        } else {
            criteria.add(Restrictions.eq("id", patientId)); //$NON-NLS-1$
        }

        Patient patient = (Patient) criteria.uniqueResult();

        if (patient == null) {
            return null;
        }

        Query query = context.getSession().createQuery(PATIENT_INFO_QRY);
        query.setParameter(0, patient.getId());

        Object[] row = (Object[]) query.uniqueResult();

        if (row == null) {
            throw new NullPointerException("query returned null"); //$NON-NLS-1$
        }

        // load study details
        patient.getStudy().getNameShort();

        SearchedPatientInfo pinfo = new SearchedPatientInfo();
        pinfo.patient = patient;
        pinfo.study = patient.getStudy();
        pinfo.ceventsCount = (Long) row[1];
        return pinfo;
    }
}
