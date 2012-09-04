package edu.ualberta.med.biobank.common.action.patient;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.Restrictions;

import edu.ualberta.med.biobank.CommonBundle;
import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.ActionContext;
import edu.ualberta.med.biobank.common.action.ActionResult;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.common.action.patient.PatientSearchAction.SearchedPatientInfo;
import edu.ualberta.med.biobank.common.permission.patient.PatientReadPermission;
import edu.ualberta.med.biobank.i18n.Bundle;
import edu.ualberta.med.biobank.i18n.Tr;
import edu.ualberta.med.biobank.model.Patient;
import edu.ualberta.med.biobank.model.Study;

public class PatientSearchAction implements Action<SearchedPatientInfo> {
    private static final long serialVersionUID = 1L;
    private static final Bundle bundle = new CommonBundle();

    @SuppressWarnings("nls")
    public static final Tr MULTIPLE_PATIENTS_FOUND =
        bundle.tr("More than one patient found with pnumber \"{0}\".");

    @SuppressWarnings("nls")
    private static final String PATIENT_INFO_QRY =
        " SELECT p.id,COUNT(cevents)"
            + " FROM " + Patient.class.getName() + " p"
            + " LEFT JOIN p.study study"
            + " LEFT JOIN p.collectionEvents cevents"
            + " WHERE p.id=?"
            + " GROUP BY p.id";

    private String pnumber;
    private Integer patientId;

    public static class SearchedPatientInfo implements ActionResult {
        private static final long serialVersionUID = 1L;
        public Patient patient;
        public Study study;
        public Long ceventsCount;
    }

    public PatientSearchAction(String pnumber) {
        this.pnumber = pnumber;
    }

    public PatientSearchAction(Integer id) {
        this.patientId = id;
    }

    @Override
    public boolean isAllowed(ActionContext context) {
        return new PatientReadPermission(patientId).isAllowed(context);
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
            throw new NullPointerException("patient not found in query result"); //$NON-NLS-1$
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
