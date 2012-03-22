package edu.ualberta.med.biobank.common.permission.security;

import edu.ualberta.med.biobank.common.action.ActionContext;
import edu.ualberta.med.biobank.common.permission.Permission;
import edu.ualberta.med.biobank.model.Membership;
import edu.ualberta.med.biobank.model.Rank;
import edu.ualberta.med.biobank.model.User;

public class UserManagerPermission implements Permission {
    private static final long serialVersionUID = 1L;

    @Override
    public boolean isAllowed(ActionContext context) {
        User user = context.getUser();
        for (Membership membership : user.getAllMemberships()) {
            if (membership.isRankGe(Rank.MANAGER)) return true;
        }
        return false;
    }
}
