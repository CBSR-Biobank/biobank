package edu.ualberta.med.biobank.test.action;

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

import edu.ualberta.med.biobank.common.action.IdResult;
import edu.ualberta.med.biobank.common.action.security.RoleSaveAction;
import edu.ualberta.med.biobank.common.permission.PermissionEnum;
import edu.ualberta.med.biobank.model.Role;

public class TestRole extends TestAction {
    @Test
    public void testRoleSaveWithoutPermissions() {
        RoleSaveAction action = new RoleSaveAction();
        action.setName(getMethodNameR());

        IdResult result = actionExecutor.exec(action);

        // session.get(Role.class, id)
    }

    @Test
    public void testRoleSaveWithPermissions() {
        RoleSaveAction action = new RoleSaveAction();
        action.setName(getMethodNameR());

        Set<PermissionEnum> permissions = new HashSet<PermissionEnum>();
        permissions.add(PermissionEnum.ADMINISTRATION);
        permissions.add(PermissionEnum.CLINIC_CREATE);

        action.setPermissions(permissions);

        IdResult result = actionExecutor.exec(action);
    }

    @Test
    public void testRoleSaveDuplicateName() {
        RoleSaveAction action = new RoleSaveAction();
        action.setName(getMethodNameR());

        actionExecutor.exec(action);
        actionExecutor.exec(action);
    }

    @Test
    public void testRoleGetAll() {
        Role role1 = new Role();
        role1.setName(getMethodNameR());

        Role role2 = new Role();
        role2.setName(getMethodNameR());
    }
}
