package edu.ualberta.med.biobank.common.action.security;

import java.util.HashSet;
import java.util.Set;

import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.ActionContext;
import edu.ualberta.med.biobank.common.action.IdResult;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.common.permission.Permission;
import edu.ualberta.med.biobank.common.permission.security.RoleManagementPermission;
import edu.ualberta.med.biobank.model.PermissionEnum;
import edu.ualberta.med.biobank.model.Role;

public class RoleSaveAction implements Action<IdResult> {
    private static final long serialVersionUID = 1L;
    private static final Permission PERMISSION = new RoleManagementPermission();

    private final Integer roleId;
    private final String name;
    private final Set<PermissionEnum> permissions;

    public RoleSaveAction(Role role) {
        this.roleId = role.getId();
        this.name = role.getName();
        this.permissions = new HashSet<PermissionEnum>(role.getPermissions());
    }

    @Override
    public boolean isAllowed(ActionContext context) throws ActionException {
        return PERMISSION.isAllowed(context);
    }

    @Override
    public IdResult run(ActionContext context) throws ActionException {
        Role role = context.load(Role.class, roleId, new Role());

        role.setName(name);
        role.getPermissions().clear();
        role.getPermissions().addAll(permissions);

        context.getSession().saveOrUpdate(role);

        return new IdResult(role.getId());
    }
}
