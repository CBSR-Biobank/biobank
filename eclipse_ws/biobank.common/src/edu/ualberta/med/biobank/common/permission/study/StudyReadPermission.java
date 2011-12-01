package edu.ualberta.med.biobank.common.permission.study;

import org.hibernate.Session;

import edu.ualberta.med.biobank.common.action.ActionUtil;
import edu.ualberta.med.biobank.common.permission.Permission;
import edu.ualberta.med.biobank.common.permission.PermissionEnum;
import edu.ualberta.med.biobank.model.Study;
import edu.ualberta.med.biobank.model.User;

public class StudyReadPermission implements Permission {
    private static final long serialVersionUID = 1L;

    private final Integer studyId;

    public StudyReadPermission(Integer studyId) {
        this.studyId = studyId;
    }

    public StudyReadPermission(Study study) {
        this(study.getId());
    }

    @Override
    public boolean isAllowed(User user, Session session) {
        Study study = ActionUtil.sessionGet(session, Study.class, studyId);
        return PermissionEnum.STUDY_READ.isAllowed(user, study);
    }
}
