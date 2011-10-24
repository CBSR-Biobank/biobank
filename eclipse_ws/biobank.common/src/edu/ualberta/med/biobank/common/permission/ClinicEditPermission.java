package edu.ualberta.med.biobank.common.permission;

import org.hibernate.Session;

import edu.ualberta.med.biobank.model.User;

public class ClinicEditPermission implements Permission {
    private static final long serialVersionUID = 1L;

    @Override
    public boolean isAllowed(User user, Session session) {
        return PermissionUtil.isAllowed(user, this);
    }

}
