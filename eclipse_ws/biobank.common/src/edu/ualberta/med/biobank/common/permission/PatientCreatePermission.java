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
        return false;
    }
}
