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
    private Set<PermissionEnum> permissionEnums = new HashSet<PermissionEnum>();

    public void setId(Integer roleId) {
        this.roleId = roleId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPermissions(Set<PermissionEnum> permissions) {
        this.permissionEnums = permissions;
    }

    @Override
    public boolean isAllowed(ActionContext context) throws ActionException {
        return new UserManagementPermission().isAllowed(context);
    }

    @Override
    public IdResult run(ActionContext context) throws ActionException {
        // TODO: should just use Hibernate to map a Role to a list of
        // PermissionEnum-s, see
        // https://community.jboss.org/wiki/UserTypeForPersistingAnEnumWithAVARCHARColumn
        // but this is probably only possible once we have access to directly
        // manipulate the beans/ pojos
        Set<Integer> permissionIds = PermissionEnum.getIds(permissionEnums);
        Map<Integer, Permission> permissions =
            context.load(Permission.class, permissionIds);

        Role role = context.load(Role.class, roleId, new Role());
        role.setName(name);
        role.getPermissionCollection().clear();
        role.getPermissionCollection().addAll(permissions.values());

        context.getSession().saveOrUpdate(role);

        return new IdResult(role.getId());
    }

}
