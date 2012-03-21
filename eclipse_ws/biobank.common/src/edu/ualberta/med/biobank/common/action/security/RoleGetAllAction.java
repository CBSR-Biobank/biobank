package edu.ualberta.med.biobank.common.action.security;

import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import org.hibernate.Criteria;

import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.ActionContext;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.common.permission.Permission;
import edu.ualberta.med.biobank.common.permission.security.RoleManagementPermission;
import edu.ualberta.med.biobank.model.Role;

public class RoleGetAllAction implements Action<RoleGetAllOutput> {
    private static final long serialVersionUID = 1L;
    private static final Permission PERMISSION = new RoleManagementPermission();

    @SuppressWarnings("unused")
    private final RoleGetAllInput input;

    public RoleGetAllAction(RoleGetAllInput input) {
        this.input = input;
    }

    @Override
    public boolean isAllowed(ActionContext context) throws ActionException {
        return PERMISSION.isAllowed(context);
    }

    @Override
    public RoleGetAllOutput run(ActionContext context) throws ActionException {
        @SuppressWarnings("unchecked")
        List<Role> results = (List<Role>) context.getSession()
            .createCriteria(Role.class)
            .createAlias("permissions", "p", Criteria.LEFT_JOIN)
            .list();

        SortedSet<Role> roles = new TreeSet<Role>(Role.NAME_COMPARATOR);
        roles.addAll(results);

        return new RoleGetAllOutput(roles);
    }
}
