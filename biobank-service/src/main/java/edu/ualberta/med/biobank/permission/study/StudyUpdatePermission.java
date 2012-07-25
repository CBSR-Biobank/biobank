package edu.ualberta.med.biobank.permission.study;

import edu.ualberta.med.biobank.action.ActionContext;
import edu.ualberta.med.biobank.permission.Permission;
import edu.ualberta.med.biobank.model.Study;
import edu.ualberta.med.biobank.model.type.PermissionEnum;

public class StudyUpdatePermission implements Permission {

    private static final long serialVersionUID = 1L;
    private Integer studyId;

    public StudyUpdatePermission(Integer studyId) {
        this.studyId = studyId;
    }

    public StudyUpdatePermission() {

    }

    @Override
    public boolean isAllowed(ActionContext context) {
        // get is intended, null value indicates any study
        Study study = context.get(Study.class, studyId);
        return PermissionEnum.STUDY_UPDATE.isAllowed(context.getUser(), study);
    }
}
