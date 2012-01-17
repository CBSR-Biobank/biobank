package edu.ualberta.med.biobank.common.action.security;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.ActionContext;
import edu.ualberta.med.biobank.common.action.IdResult;
import edu.ualberta.med.biobank.common.action.check.UniquePreCheck;
import edu.ualberta.med.biobank.common.action.check.ValueProperty;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.common.action.exception.NullPropertyException;
import edu.ualberta.med.biobank.common.peer.RolePeer;
import edu.ualberta.med.biobank.common.permission.security.UserManagementPermission;
import edu.ualberta.med.biobank.common.util.SetDifference;
import edu.ualberta.med.biobank.model.Permission;
import edu.ualberta.med.biobank.model.Role;

public class RoleSaveAction implements Action<IdResult> {
    private static final long serialVersionUID = 1L;

    private Integer roleId;
    private String name;
    private Set<Integer> permissionIds;

    public void setId(Integer roleId) {
        this.roleId = roleId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPermissionIds(Set<Integer> permissionIds) {
        this.permissionIds = permissionIds;
    }

    @Override
    public boolean isAllowed(ActionContext context) throws ActionException {
        return new UserManagementPermission().isAllowed(null);
    }

    @Override
    public IdResult run(ActionContext context) throws ActionException {
        if (name == null) {
            throw new NullPropertyException(Role.class, RolePeer.NAME);
        }
        if (permissionIds == null) {
            throw new NullPropertyException(Role.class,
                "permission ids cannot be null");
        }

        // check for duplicate name
        List<ValueProperty<Role>> uniqueValProps =
            new ArrayList<ValueProperty<Role>>();
        uniqueValProps.add(new ValueProperty<Role>(RolePeer.NAME, name));
        new UniquePreCheck<Role>(Role.class, roleId, uniqueValProps).run(
            null);

        Role role = context.get(Role.class, roleId, new Role());
        role.setId(roleId);

        // Role to Permission association is unidirectional.
        Map<Integer, Permission> permissions =
            context.load(Permission.class, permissionIds);

        SetDifference<Permission> permissionsDiff =
            new SetDifference<Permission>(
                role.getPermissionCollection(), permissions.values());
        role.setPermissionCollection(permissionsDiff.getAddSet());

        context.getSession().saveOrUpdate(role);
        context.getSession().flush();

        // TODO Auto-generated method stub
        return new IdResult(role.getId());
    }

}
