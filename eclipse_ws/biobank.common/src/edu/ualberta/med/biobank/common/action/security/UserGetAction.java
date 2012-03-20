package edu.ualberta.med.biobank.common.action.security;

import java.util.Set;

import org.hibernate.Hibernate;

import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.ActionContext;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.common.permission.Permission;
import edu.ualberta.med.biobank.common.permission.security.UserManagerPermission;
import edu.ualberta.med.biobank.model.Group;
import edu.ualberta.med.biobank.model.Membership;
import edu.ualberta.med.biobank.model.PermissionEnum;
import edu.ualberta.med.biobank.model.Role;
import edu.ualberta.med.biobank.model.User;

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

        ManagerContext managerContext = getManagerContext(context);

        copyProperties(user, copy);
        copyMemberships(user, copy, context, managerContext.getRoles());
        copyGroups(user, copy, context);

        return new UserGetOutput(copy, managerContext);
    }

    private ManagerContext getManagerContext(ActionContext context) {
        User manager = context.getUser();

        initManager(manager);

        Set<Role> allRoles = new RoleGetAllAction(new RoleGetAllInput())
            .run(context).getAllRoles();
        Set<Group> manageableGroups = null;

        return new ManagerContext(manager, allRoles, manageableGroups);
    }

    /**
     * Ensure all necessary areas of the {@link User} are initialized.
     * 
     * @param manager
     */
    private void initManager(User manager) {
        Hibernate.initialize(manager);
        initMemberships(manager.getMemberships());
        for (Group group : manager.getGroups()) {
            initMemberships(group.getMemberships());
        }
    }

    private void initMemberships(Set<Membership> memberships) {
        for (Membership membership : memberships) {
            Hibernate.initialize(membership.getPermissions());
            Hibernate.initialize(membership.getRoles());
            for (Role role : membership.getRoles()) {
                Hibernate.initialize(role.getPermissions());
            }
        }
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
        for (Membership m : src.getMemberships()) {
            if (m.isPartiallyManageable(executingUser)) {
                // TODO: what are the implications of evict?
                context.getSession().evict(m);
                context.getSession().evict(m.getPermissions());
                context.getSession().evict(m.getRoles());

                dst.getMemberships().add(m);
                m.setPrincipal(dst);

                // limit permission and role scope to manageable ones
                permsScope = m.getManageablePermissions(executingUser);
                rolesScope = m.getManageableRoles(executingUser, allRoles);

                m.getPermissions().retainAll(permsScope);
                m.getRoles().retainAll(rolesScope);
            }
        }
    }

    private void copyGroups(User src, User dst, ActionContext context) {
        User executingUser = context.getUser();

        for (Group g : src.getGroups()) {
            if (g.isFullyManageable(executingUser)) {
                // TODO: what are the implications of evict?
                context.getSession().evict(g);
                dst.getGroups().add(g);
            }
        }
    }
}
