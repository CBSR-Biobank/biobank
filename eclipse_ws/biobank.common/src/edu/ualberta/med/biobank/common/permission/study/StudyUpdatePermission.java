package edu.ualberta.med.biobank.common.permission.study;

import edu.ualberta.med.biobank.common.action.ActionContext;
import edu.ualberta.med.biobank.common.permission.Permission;
import edu.ualberta.med.biobank.model.PermissionEnum;
import edu.ualberta.med.biobank.model.Study;

public class StudyUpdatePermission implements Permission {

    private static final long serialVersionUID = 1L;
    private Integer studyId;

    public StudyUpdatePermission(Integer siteId) {
        this.studyId = siteId;
    }

    @Override
    public boolean isAllowed(ActionContext context) {
        Study study = context.load(Study.class, studyId);
        return PermissionEnum.STUDY_UPDATE.isAllowed(context.getUser(), study);
    }

}
