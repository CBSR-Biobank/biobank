package edu.ualberta.med.biobank.common.action.security;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.ActionContext;
import edu.ualberta.med.biobank.common.action.IdResult;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.common.permission.PermissionEnum;
import edu.ualberta.med.biobank.common.permission.security.UserManagementPermission;
import edu.ualberta.med.biobank.model.Permission;
import edu.ualberta.med.biobank.model.Role;

public class RoleSaveAction implements Action<IdResult> {
    private static final long serialVersionUID = 1L;

    private Integer roleId;
    private String name;
    private Set<PermissionEnum> permissions = new HashSet<PermissionEnum>();

    public void setId(Integer roleId) {
        this.roleId = roleId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPermissions(Set<PermissionEnum> permissions) {
        this.permissions = permissions;
    }

    @Override
    public boolean isAllowed(ActionContext context) throws ActionException {
        return new UserManagementPermission().isAllowed(context);
    }

    @Override
    public IdResult run(ActionContext context) throws ActionException {
        Role role = context.load(Role.class, roleId, new Role());
        role.setName(name);
        role.getPermissionCollection().clear();
        role.getPermissionCollection().addAll(permissions);

        context.getSession().saveOrUpdate(role);

        return new IdResult(role.getId());
    }

}
