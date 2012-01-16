package edu.ualberta.med.biobank.common.permission.study;

import org.hibernate.Session;

import edu.ualberta.med.biobank.common.action.ActionContext;
import edu.ualberta.med.biobank.common.permission.Permission;
import edu.ualberta.med.biobank.common.permission.PermissionEnum;
import edu.ualberta.med.biobank.model.Study;
import edu.ualberta.med.biobank.model.User;

public class StudyDeletePermission implements Permission {
    private static final long serialVersionUID = 1L;

    private Integer studyId;

    public StudyDeletePermission(Integer studyId) {
        this.studyId = studyId;
    }

    @Override
    public boolean isAllowed(User user, Session session) {
        Study study =
            new ActionContext(user, session).load(Study.class, studyId);
        return PermissionEnum.STUDY_DELETE.isAllowed(user, study);
    }

}
