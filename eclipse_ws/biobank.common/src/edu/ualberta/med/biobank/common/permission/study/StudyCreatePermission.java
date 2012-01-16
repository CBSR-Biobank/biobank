package edu.ualberta.med.biobank.common.permission.study;

import org.hibernate.Session;

import edu.ualberta.med.biobank.common.permission.Permission;
import edu.ualberta.med.biobank.common.permission.PermissionEnum;
import edu.ualberta.med.biobank.model.User;

public class StudyCreatePermission implements Permission {
    private static final long serialVersionUID = 1L;

    @Override
    public boolean isAllowed(User user, Session session) {
        return PermissionEnum.STUDY_CREATE.isAllowed(user);
    }
}
