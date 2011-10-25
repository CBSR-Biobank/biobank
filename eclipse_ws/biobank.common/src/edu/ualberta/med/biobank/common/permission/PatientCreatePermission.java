package edu.ualberta.med.biobank.common.permission;

import org.hibernate.Session;

import edu.ualberta.med.biobank.model.Patient;
import edu.ualberta.med.biobank.model.Study;
import edu.ualberta.med.biobank.model.User;

public class PatientCreatePermission implements Permission {
    private static final long serialVersionUID = 1L;

    private final Patient patient;

    public PatientCreatePermission(Patient patient) {
        this.patient = patient;
    }

    @Override
    public boolean isAllowed(User user, Session session) {
        Study study = patient.getStudy();

        // check if this user has a permission with className =
        // "PatientCreatePermission"
        // with a membership with study = (the above) or null

        // (1) using an object to define paths to study and center:
        // PermissionUtil.isAllowed(user, new PatientCreatePermission(patient));
        // (2) programmer specifies every time:
        // PermissionUtil.isAllowed(user, Permission.PATIENT_CREATE,
        // patient.getStudy());

        return false;
    }
}
