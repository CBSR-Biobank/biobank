package edu.ualberta.med.biobank.common.permission;

import org.hibernate.Session;

import edu.ualberta.med.biobank.model.Patient;
import edu.ualberta.med.biobank.model.User;

public class PatientReadPermission implements Permission {
    private static final long serialVersionUID = 1L;

    public PatientReadPermission(Patient patient) {

    }

    @Override
    public boolean isAllowed(User user, Session session) {
        // TODO Auto-generated method stub
        return false;
    }
}
