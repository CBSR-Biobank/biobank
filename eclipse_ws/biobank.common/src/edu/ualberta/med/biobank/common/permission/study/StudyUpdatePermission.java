package edu.ualberta.med.biobank.common.permission.study;

import org.hibernate.Session;

import edu.ualberta.med.biobank.common.action.ActionUtil;
import edu.ualberta.med.biobank.common.permission.Permission;
import edu.ualberta.med.biobank.common.permission.PermissionEnum;
import edu.ualberta.med.biobank.model.Study;
import edu.ualberta.med.biobank.model.User;

public class StudyUpdatePermission implements Permission {

    private static final long serialVersionUID = 1L;
    private Integer studyId;

    public StudyUpdatePermission(Integer siteId) {
        this.studyId = siteId;
    }

    @Override
    public boolean isAllowed(User user, Session session) {
        Study study = ActionUtil.sessionGet(session, Study.class, studyId);
        return PermissionEnum.STUDY_UPDATE.isAllowed(user, study);
    }

}
