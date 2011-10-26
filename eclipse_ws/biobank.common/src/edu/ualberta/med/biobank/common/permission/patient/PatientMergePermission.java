package edu.ualberta.med.biobank.common.permission.patient;

import org.hibernate.Session;

import edu.ualberta.med.biobank.common.action.ActionUtil;
import edu.ualberta.med.biobank.common.permission.Permission;
import edu.ualberta.med.biobank.common.permission.PermissionEnum;
import edu.ualberta.med.biobank.model.Patient;
import edu.ualberta.med.biobank.model.User;

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
    public boolean isAllowed(User user, Session session) {
        Patient patient1 = ActionUtil.sessionGet(session, Patient.class,
            patientId1);
        // both patients are supposed to be in the same study for the merge
        return PermissionEnum.PATIENT_MERGE
            .isAllowed(user, patient1.getStudy())
            && new PatientUpdatePermission(patientId1).isAllowed(user,
                session)
            && new PatientDeletePermission(patientId2).isAllowed(user,
                session);
    }
}
