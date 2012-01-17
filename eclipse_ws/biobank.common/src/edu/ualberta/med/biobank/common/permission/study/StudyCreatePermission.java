package edu.ualberta.med.biobank.common.permission.study;

import edu.ualberta.med.biobank.common.action.ActionContext;
import edu.ualberta.med.biobank.common.permission.Permission;
import edu.ualberta.med.biobank.common.permission.PermissionEnum;

public class StudyCreatePermission implements Permission {
    private static final long serialVersionUID = 1L;

    @Override
    public boolean isAllowed(ActionContext context) {
        return PermissionEnum.STUDY_CREATE.isAllowed(context.getUser());
    }
}
