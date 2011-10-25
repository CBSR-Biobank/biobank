package edu.ualberta.med.biobank.common.permission.patient;

import org.hibernate.Session;

import edu.ualberta.med.biobank.common.action.ActionUtil;
import edu.ualberta.med.biobank.common.permission.Permission;
import edu.ualberta.med.biobank.common.permission.PermissionEnum;
import edu.ualberta.med.biobank.model.Study;
import edu.ualberta.med.biobank.model.User;

public class PatientCreatePermission implements Permission {
    private static final long serialVersionUID = 1L;
    private Integer studyId;

    /**
     * called from the client
     */
    public PatientCreatePermission(Integer studyId) {
        this.studyId = studyId;
    }

    @Override
    public boolean isAllowed(User user, Session session) {
        return PermissionEnum.PATIENT_CREATE
            .isAllowed(user,
                ActionUtil.sessionGet(session, Study.class, studyId));
    }
}
