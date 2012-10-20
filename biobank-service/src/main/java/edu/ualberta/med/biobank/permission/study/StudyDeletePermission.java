package edu.ualberta.med.biobank.permission.study;

import edu.ualberta.med.biobank.action.ActionContext;
import edu.ualberta.med.biobank.permission.Permission;
import edu.ualberta.med.biobank.model.study.Study;
import edu.ualberta.med.biobank.model.type.PermissionEnum;

public class StudyDeletePermission implements Permission {
    private static final long serialVersionUID = 1L;

    private Integer studyId;

    public StudyDeletePermission(Integer studyId) {
        this.studyId = studyId;
    }

    @Override
    public boolean isAllowed(ActionContext context) {
        Study study = context.load(Study.class, studyId);
        return PermissionEnum.STUDY_DELETE.isAllowed(context.getUser(), study);
    }

}
