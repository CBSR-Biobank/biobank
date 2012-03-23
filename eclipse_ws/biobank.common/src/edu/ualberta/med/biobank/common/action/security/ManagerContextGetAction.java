package edu.ualberta.med.biobank.common.action.security;

import java.util.Set;

import org.hibernate.Hibernate;

import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.ActionContext;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.model.Group;
import edu.ualberta.med.biobank.model.Membership;
import edu.ualberta.med.biobank.model.Role;
import edu.ualberta.med.biobank.model.User;

public class ManagerContextGetAction implements Action<ManagerContextGetOutput> {
    private static final long serialVersionUID = 1L;

    @SuppressWarnings("unused")
    private final ManagerContextGetInput input;

    public ManagerContextGetAction(ManagerContextGetInput input) {
        this.input = input;
    }

    @Override
    public boolean isAllowed(ActionContext context) throws ActionException {
        return true;
    }

    @Override
    public ManagerContextGetOutput run(ActionContext context)
        throws ActionException {
        User manager = context.getUser();

        initManager(manager);

        Set<Role> allRoles = new RoleGetAllAction(new RoleGetAllInput())
            .run(context).getAllRoles();
        Set<Group> manageableGroups = new GroupGetAllAction(
            new GroupGetAllInput())
            .run(context)
            .getAllManageableGroups();

        ManagerContext managerContext =
            new ManagerContext(manager, allRoles, manageableGroups);

        return new ManagerContextGetOutput(managerContext);
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
            Hibernate.initialize(membership.getCenter());
            Hibernate.initialize(membership.getStudy());
            Hibernate.initialize(membership.getPermissions());
            Hibernate.initialize(membership.getRoles());
            for (Role role : membership.getRoles()) {
                Hibernate.initialize(role.getPermissions());
            }
        }
    }
}
