package edu.ualberta.med.biobank.permission.security;

import edu.ualberta.med.biobank.action.ActionContext;
import edu.ualberta.med.biobank.permission.Permission;
import edu.ualberta.med.biobank.model.security.Membership;
import edu.ualberta.med.biobank.model.security.User;

public class UserManagerPermission implements Permission {
    private static final long serialVersionUID = 1L;

    @Override
    public boolean isAllowed(ActionContext context) {
        User user = context.getUser();
        for (Membership membership : user.getAllMemberships()) {
            if (membership.isUserManager()) return true;
        }
        return false;
    }
}
