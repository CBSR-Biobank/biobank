package edu.ualberta.med.biobank.test.action;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hibernate.criterion.Restrictions;
import org.junit.Test;
import org.springframework.util.Assert;

import edu.ualberta.med.biobank.common.action.IdResult;
import edu.ualberta.med.biobank.common.action.security.RoleSaveAction;
import edu.ualberta.med.biobank.common.permission.Permission;
import edu.ualberta.med.biobank.model.PermissionEnum;
import edu.ualberta.med.biobank.model.Role;

public class TestRole extends TestAction {
    @Test
    public void testRoleSaveWithoutPermissions() {
        RoleSaveAction action = new RoleSaveAction();
        action.setName(getMethodNameR());

        IdResult result = EXECUTOR.exec(action);

        Assert.notNull(session.get(Role.class, result.getId()));
    }

    @Test
    public void testRoleSaveWithPermissions() {
        RoleSaveAction action = new RoleSaveAction();
        action.setName(getMethodNameR());

        Set<PermissionEnum> permissions = new HashSet<PermissionEnum>();
        permissions.add(PermissionEnum.ADMINISTRATION);
        permissions.add(PermissionEnum.CLINIC_CREATE);

        action.setPermissions(permissions);

        IdResult result = EXECUTOR.exec(action);

        Role role = (Role) session.get(Role.class, result.getId());

        Assert.notNull(role);
        Assert.isTrue(role.getPermissionCollection().containsAll(permissions),
            "Permissions not saved.");
    }

    @Test
    public void testRoleUpdate() {
        RoleSaveAction action = new RoleSaveAction();
        action.setName(getMethodNameR());

        Set<PermissionEnum> permissions = new HashSet<PermissionEnum>();
        permissions.add(PermissionEnum.ADMINISTRATION);

        action.setPermissions(permissions);

        IdResult result = EXECUTOR.exec(action);
        Integer roleId = result.getId();

        // update
        action.setId(roleId);

        permissions.clear();
        permissions.add(PermissionEnum.CLINIC_CREATE);

        action.setPermissions(permissions);

        EXECUTOR.exec(action);

        // check
        Role role = (Role) session.get(Role.class, result.getId());

        Assert.notNull(role);
        Assert.isTrue(role.getPermissionCollection().equals(permissions),
            "Permissions not saved.");
    }

    @Test
    public void testRoleSaveDuplicateName() {
        RoleSaveAction action = new RoleSaveAction();
        action.setName(getMethodNameR());

        EXECUTOR.exec(action);
        EXECUTOR.exec(action);
    }

    @Test
    public void testRoleGetAll() {
        Role role1 = new Role();
        role1.setName(getMethodNameR());

        Role role2 = new Role();
        role2.setName(getMethodNameR());
    }
}
