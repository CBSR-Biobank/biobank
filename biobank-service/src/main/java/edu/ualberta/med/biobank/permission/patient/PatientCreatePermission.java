package edu.ualberta.med.biobank.permission.patient;

import edu.ualberta.med.biobank.action.ActionContext;
import edu.ualberta.med.biobank.permission.Permission;
import edu.ualberta.med.biobank.model.PermissionEnum;
import edu.ualberta.med.biobank.model.Study;

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
    public boolean isAllowed(ActionContext context) {
        return PermissionEnum.PATIENT_CREATE
            .isAllowed(context.getUser(),
                context.get(Study.class, studyId));
    }
}
