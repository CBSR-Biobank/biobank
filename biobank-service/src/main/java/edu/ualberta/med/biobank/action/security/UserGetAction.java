package edu.ualberta.med.biobank.action.security;

import java.util.Set;

import org.hibernate.Hibernate;

import edu.ualberta.med.biobank.action.Action;
import edu.ualberta.med.biobank.action.ActionContext;
import edu.ualberta.med.biobank.action.exception.ActionException;
import edu.ualberta.med.biobank.permission.Permission;
import edu.ualberta.med.biobank.permission.security.UserManagerPermission;
import edu.ualberta.med.biobank.model.security.Domain;
import edu.ualberta.med.biobank.model.security.Group;
import edu.ualberta.med.biobank.model.security.Membership;
import edu.ualberta.med.biobank.model.security.Role;
import edu.ualberta.med.biobank.model.security.User;
import edu.ualberta.med.biobank.model.type.PermissionEnum;

public class UserGetAction implements Action<UserGetOutput> {
    private static final long serialVersionUID = 1L;
    private static final Permission PERMISSION = new UserManagerPermission();

    private final UserGetInput input;

    public UserGetAction(UserGetInput input) {
        this.input = input;
    }

    @Override
    public boolean isAllowed(ActionContext context) throws ActionException {
        return PERMISSION.isAllowed(context);
    }

    @Override
    public UserGetOutput run(ActionContext context) throws ActionException {
        User user = context.load(User.class, input.getUserId());

        User copy = new User();

        MembershipContext managerContext = new MembershipContextGetAction(
            new MembershipContextGetInput()).run(context).getContext();

        copyProperties(user, copy);
        copyMemberships(user, copy, context, managerContext.getRoles());
        copyGroups(user, copy, context);

        boolean isFullyManageable = user.isFullyManageable(context.getUser());

        return new UserGetOutput(copy, managerContext, isFullyManageable);
    }

    private void copyProperties(User src, User dst) {
        dst.setId(src.getId());
        dst.setLogin(src.getLogin());
        dst.setFullName(src.getFullName());
        dst.setEmail(src.getEmail());
        dst.setNeedPwdChange(src.getNeedPwdChange());
        dst.setRecvBulkEmails(src.getRecvBulkEmails());
    }

    private void copyMemberships(User src, User dst, ActionContext context,
        Set<Role> allRoles) {
        User executingUser = context.getUser();

        Set<PermissionEnum> permsScope;
        Set<Role> rolesScope;
        for (Membership m : src.getManageableMemberships(executingUser)) {
            Membership copy = new Membership(m, dst);
            copy.setId(m.getId());

            Hibernate.initialize(copy.getDomain());

            Domain domain = copy.getDomain();
            Hibernate.initialize(domain.getCenters());
            Hibernate.initialize(domain.getStudies());

            // limit permission and role scope to manageable ones
            permsScope = m.getManageablePermissions(executingUser);
            rolesScope = m.getManageableRoles(executingUser, allRoles);

            copy.getPermissions().retainAll(permsScope);
            copy.getRoles().retainAll(rolesScope);
        }
    }

    private void copyGroups(User src, User dst, ActionContext context) {
        User executingUser = context.getUser();

        for (Group g : src.getGroups()) {
            if (g.isFullyManageable(executingUser)) {
                Group copy = new Group();
                copy.setId(g.getId());
                copy.setName(g.getName());
                copy.setName(g.getName());

                dst.getGroups().add(copy);
            }
        }
    }
}
