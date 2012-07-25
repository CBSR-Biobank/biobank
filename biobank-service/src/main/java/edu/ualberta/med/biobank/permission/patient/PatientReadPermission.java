package edu.ualberta.med.biobank.permission.patient;

import edu.ualberta.med.biobank.action.ActionContext;
import edu.ualberta.med.biobank.permission.Permission;
import edu.ualberta.med.biobank.model.Patient;
import edu.ualberta.med.biobank.model.type.PermissionEnum;

public class PatientReadPermission implements Permission {

    private static final long serialVersionUID = 1L;

    private Integer patientId;

    public PatientReadPermission(Integer patientId) {
        this.patientId = patientId;
    }

    @Override
    public boolean isAllowed(ActionContext context) {
        Patient patient = context.get(Patient.class,
            patientId);
        if (patient == null)
            return PermissionEnum.PATIENT_READ.isAllowed(context.getUser(),
                null, null);
        return PermissionEnum.PATIENT_READ.isAllowed(context.getUser(),
            patient.getStudy());
    }
}
