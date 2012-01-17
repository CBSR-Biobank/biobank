package edu.ualberta.med.biobank.common.permission.patient;

import edu.ualberta.med.biobank.common.action.ActionContext;
import edu.ualberta.med.biobank.common.permission.Permission;
import edu.ualberta.med.biobank.common.permission.PermissionEnum;
import edu.ualberta.med.biobank.model.Patient;

public class PatientDeletePermission implements Permission {

    private static final long serialVersionUID = 1L;
    private Integer patientId;

    public PatientDeletePermission(Integer patientId) {
        this.patientId = patientId;
    }

    @Override
    public boolean isAllowed(ActionContext context) {
        Patient patient = context.load(Patient.class, patientId);
        return PermissionEnum.PATIENT_DELETE
            .isAllowed(context.getUser(), patient.getStudy());
    }

}
