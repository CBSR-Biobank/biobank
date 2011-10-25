package edu.ualberta.med.biobank.common.permission.patient;

import org.hibernate.Session;

import edu.ualberta.med.biobank.common.action.ActionUtil;
import edu.ualberta.med.biobank.common.permission.Permission;
import edu.ualberta.med.biobank.common.permission.PermissionEnum;
import edu.ualberta.med.biobank.model.Patient;
import edu.ualberta.med.biobank.model.User;

public class PatientReadPermission implements Permission {

    private static final long serialVersionUID = 1L;

    private Integer patientId;

    public PatientReadPermission(Integer patientId) {
        this.patientId = patientId;
    }

    @Override
    public boolean isAllowed(User user, Session session) {
        Patient patient = ActionUtil.sessionGet(session, Patient.class,
            patientId);
        return PermissionEnum.PATIENT_READ.isAllowed(user, patient.getStudy());
    }

}
