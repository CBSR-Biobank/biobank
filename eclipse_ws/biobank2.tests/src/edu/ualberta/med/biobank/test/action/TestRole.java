package edu.ualberta.med.biobank.test.action;

import java.util.HashSet;
import java.util.Set;

import org.hibernate.Transaction;
import org.junit.Test;
import org.springframework.util.Assert;

import edu.ualberta.med.biobank.common.action.IdResult;
import edu.ualberta.med.biobank.common.action.security.RoleSaveAction;
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
    public void testANameSwap() {
        Transaction tx = session.beginTransaction();

        String nameA = getMethodNameR();
        String nameB = getMethodNameR();

        Role roleA = new Role();
        roleA.setName(nameA);

        session.save(roleA);

        // Note that flush should be called. Inserts get queued up otherwise
        // (like our two role insert statements). Then before we insert roleB,
        // roleA was not actually flushed to the db, so it looks like there is
        // not a name conflict. Could be fixed with FlushMode.ALWAYS, but that
        // _could_ be inefficient. Either use ALWAYS, or have to be smart about
        // things. Same problem could happen with two duplicate elements in a
        // collection. You wouldn't know there's a problem until the db says so.
        // session.flush();

        Role roleB = new Role();
        roleB.setName(nameA);

        session.save(roleB);

        tx.commit();

        session.flush();
        session.clear();
    }

    @Test
    public void testRoleGetAll() {
        Role role1 = new Role();
        role1.setName(getMethodNameR());

        Role role2 = new Role();
        role2.setName(getMethodNameR());
    }
}
