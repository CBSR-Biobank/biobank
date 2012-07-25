package edu.ualberta.med.biobank.permission.study;

import edu.ualberta.med.biobank.action.ActionContext;
import edu.ualberta.med.biobank.permission.Permission;
import edu.ualberta.med.biobank.model.Study;
import edu.ualberta.med.biobank.model.type.PermissionEnum;

public class StudyReadPermission implements Permission {
    private static final long serialVersionUID = 1L;

    private final Integer studyId;

    public StudyReadPermission() {
        this.studyId = null;
    }

    public StudyReadPermission(Integer studyId) {
        this.studyId = studyId;
    }

    public StudyReadPermission(Study study) {
        this(study.getId());
    }

    @Override
    public boolean isAllowed(ActionContext context) {
        if (studyId != null) {
            Study study = context.load(Study.class, studyId);
            return PermissionEnum.STUDY_READ
                .isAllowed(context.getUser(), study);
        }

        return PermissionEnum.STUDY_READ.isAllowed(context.getUser());
    }
}
