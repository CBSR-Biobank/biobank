package edu.ualberta.med.biobank.action.security;

import edu.ualberta.med.biobank.action.Action;
import edu.ualberta.med.biobank.action.ActionContext;
import edu.ualberta.med.biobank.action.EmptyResult;
import edu.ualberta.med.biobank.action.exception.ActionException;
import edu.ualberta.med.biobank.permission.Permission;
import edu.ualberta.med.biobank.permission.security.RoleManagementPermission;
import edu.ualberta.med.biobank.model.security.Role;

public class RoleDeleteAction implements Action<EmptyResult> {
    private static final long serialVersionUID = 1L;
    private static final Permission PERMISSION = new RoleManagementPermission();

    private final RoleDeleteInput input;

    public RoleDeleteAction(RoleDeleteInput input) {
        this.input = input;
    }

    @Override
    public boolean isAllowed(ActionContext context) throws ActionException {
        return PERMISSION.isAllowed(context);
    }

    @Override
    public EmptyResult run(ActionContext context) throws ActionException {
        Role role = context.load(Role.class, input.getRoleId());

        context.getSession().delete(role);

        return new EmptyResult();
    }
}
