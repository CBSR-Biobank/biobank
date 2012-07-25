package edu.ualberta.med.biobank.permission.patient;

import edu.ualberta.med.biobank.action.ActionContext;
import edu.ualberta.med.biobank.permission.Permission;
import edu.ualberta.med.biobank.model.Patient;
import edu.ualberta.med.biobank.model.PermissionEnum;

public class PatientUpdatePermission implements Permission {

    private static final long serialVersionUID = 1L;

    private Integer patientId;

    public PatientUpdatePermission(Integer patientId) {
        this.patientId = patientId;
    }

    @Override
    public boolean isAllowed(ActionContext context) {
        Patient patient = context.load(Patient.class, patientId);
        return PermissionEnum.PATIENT_UPDATE
            .isAllowed(context.getUser(), patient.getStudy());
    }

}
