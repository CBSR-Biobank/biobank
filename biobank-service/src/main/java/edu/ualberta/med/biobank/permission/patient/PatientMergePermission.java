package edu.ualberta.med.biobank.permission.patient;

import edu.ualberta.med.biobank.action.ActionContext;
import edu.ualberta.med.biobank.permission.Permission;
import edu.ualberta.med.biobank.model.Patient;
import edu.ualberta.med.biobank.model.PermissionEnum;

public class PatientMergePermission implements Permission {
    private static final long serialVersionUID = 1L;
    private Integer patientId1;
    private Integer patientId2;

    /**
     * can be called from an action
     */
    public PatientMergePermission(Integer patientId1, Integer patientId2) {
        this.patientId1 = patientId1;
        this.patientId2 = patientId2;
    }

    @Override
    public boolean isAllowed(ActionContext context) {
        Patient patient1 = context.load(Patient.class,
            patientId1);
        // both patients are supposed to be in the same study for the merge
        Boolean firstPatientAllowed = PermissionEnum.PATIENT_MERGE
            .isAllowed(context.getUser(), patient1.getStudy())
            && new PatientUpdatePermission(patientId1).isAllowed(context);
        Boolean secondPatientAllowed = true;
        if (patientId2 != null)
            secondPatientAllowed =
                new PatientDeletePermission(patientId2).isAllowed(context);
        return firstPatientAllowed & secondPatientAllowed;
    }
}
