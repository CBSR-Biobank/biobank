package edu.ualberta.med.biobank.common.permission.site;

import org.hibernate.Session;

import edu.ualberta.med.biobank.common.permission.Permission;
import edu.ualberta.med.biobank.common.permission.PermissionEnum;
import edu.ualberta.med.biobank.model.User;

public class SiteCreatePermission implements Permission {
    private static final long serialVersionUID = 1L;

    public SiteCreatePermission() {
    }

    @Override
    public boolean isAllowed(User user, Session session) {
        return PermissionEnum.SITE_CREATE.isAllowed(user);
    }
}
