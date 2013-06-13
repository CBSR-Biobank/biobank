package edu.ualberta.med.biobank.common.permission.patient;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

import edu.ualberta.med.biobank.common.action.ActionContext;
import edu.ualberta.med.biobank.common.permission.Permission;
import edu.ualberta.med.biobank.model.Patient;
import edu.ualberta.med.biobank.model.PermissionEnum;
import edu.ualberta.med.biobank.model.Study;

public class PatientReadPermission implements Permission {
    private static final long serialVersionUID = 1L;

    private final Integer patientId;
    private final String pnumber;

    public PatientReadPermission() {
        this.patientId = null;
        this.pnumber = null;
    }

    public PatientReadPermission(Integer patientId) {
        this.patientId = patientId;
        this.pnumber = null;
    }

    public PatientReadPermission(String pnumber) {
        this.patientId = null;
        this.pnumber = pnumber;
    }

    @Override
    public boolean isAllowed(ActionContext context) {
        Patient patient = null;
        Study study = null;

        if (patientId != null) {
            patient = context.get(Patient.class, patientId);
        } else if (pnumber != null) {
            @SuppressWarnings("nls")
            Criteria criteria = context.getSession()
                .createCriteria(Patient.class, "p")
                .add(Restrictions.eq("pnumber", pnumber));
            patient = (Patient) criteria.uniqueResult();
        }

        if (patient != null) {
            study = patient.getStudy();
        }

        // study can be null here
        return PermissionEnum.PATIENT_READ.isAllowed(context.getUser(), study);
    }
}
