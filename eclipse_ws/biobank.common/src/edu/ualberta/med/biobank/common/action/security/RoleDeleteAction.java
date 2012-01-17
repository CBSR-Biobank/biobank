package edu.ualberta.med.biobank.common.action.security;

import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.ActionContext;
import edu.ualberta.med.biobank.common.action.EmptyResult;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.common.permission.security.UserManagementPermission;
import edu.ualberta.med.biobank.model.Role;

public class RoleDeleteAction implements Action<EmptyResult> {
    private static final long serialVersionUID = 1L;

    private final Integer roleId;

    public RoleDeleteAction(Integer id) {
        this.roleId = id;

    }

    @Override
    public boolean isAllowed(ActionContext context) throws ActionException {
        return new UserManagementPermission().isAllowed(null);
    }

    @Override
    public EmptyResult run(ActionContext context) throws ActionException {
        Role role = context.load(Role.class, roleId);
        context.getSession().delete(role);
        return new EmptyResult();
    }

}
