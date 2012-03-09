package edu.ualberta.med.biobank.common.action.security;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.ActionContext;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.common.permission.Permission;
import edu.ualberta.med.biobank.common.permission.security.UserManagerPermission;
import edu.ualberta.med.biobank.model.Role;
import edu.ualberta.med.biobank.model.User;

public class UserGetAction implements Action<ManagedUser> {
    private static final long serialVersionUID = 1L;
    private static final Permission PERMISSION = new UserManagerPermission();

    private Integer userId;

    public UserGetAction(User user) {
        this.userId = user.getId();
    }

    @Override
    public boolean isAllowed(ActionContext context) throws ActionException {
        return PERMISSION.isAllowed(context);
    }

    @Override
    public ManagedUser run(ActionContext context) throws ActionException {
        User user = context.load(User.class, userId);

        @SuppressWarnings("unchecked")
        Set<Role> allRoles = new HashSet<Role>((List<Role>) context
            .getSession().createCriteria(Role.class).list());

        // ManagedUser managedUser =
        // new ManagedUser(user, context.getUser(), allRoles);

        return managedUser;
    }
}
