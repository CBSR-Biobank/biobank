package edu.ualberta.med.biobank.common.action.security;

import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.ActionContext;
import edu.ualberta.med.biobank.common.action.IdResult;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.common.permission.Permission;
import edu.ualberta.med.biobank.common.permission.security.RoleManagementPermission;
import edu.ualberta.med.biobank.model.Role;

public class RoleSaveAction implements Action<IdResult> {
    private static final long serialVersionUID = 1L;
    private static final Permission PERMISSION = new RoleManagementPermission();

    private final RoleSaveInput input;

    public RoleSaveAction(RoleSaveInput input) {
        this.input = input;
    }

    @Override
    public boolean isAllowed(ActionContext context) throws ActionException {
        return PERMISSION.isAllowed(context);
    }

    @Override
    public IdResult run(ActionContext context) throws ActionException {
        Role role = context.load(Role.class, input.getRoleId(), new Role());

        role.setName(input.getName());

        role.getPermissions().clear();
        role.getPermissions().addAll(input.getPermissions());

        context.getSession().saveOrUpdate(role);

        return new IdResult(role.getId());
    }
}
