package edu.ualberta.med.biobank.action.security;

import java.util.HashSet;
import java.util.Set;

import junit.framework.Assert;

import org.hibernate.Transaction;
import org.junit.Test;

import edu.ualberta.med.biobank.action.exception.AccessDeniedException;
import edu.ualberta.med.biobank.action.security.RoleGetAllAction;
import edu.ualberta.med.biobank.action.security.RoleGetAllInput;
import edu.ualberta.med.biobank.action.security.RoleGetAllOutput;
import edu.ualberta.med.biobank.model.security.Role;
import edu.ualberta.med.biobank.model.security.User;
import edu.ualberta.med.biobank.model.type.PermissionEnum;
import edu.ualberta.med.biobank.action.ActionTest;

public class TestRoleGetAllAction extends ActionTest {
    @Test
    public void superAdminAccess() {
        exec(new RoleGetAllAction(new RoleGetAllInput()));
    }

    @Test
    public void adminAccess() {
        Transaction tx = session.beginTransaction();
        User user = factory.createUser();
        factory.buildMembership().setUserManager(true).create();
        tx.commit();

        try {
            execAs(user, new RoleGetAllAction(new RoleGetAllInput()));
            Assert.fail();
        } catch (AccessDeniedException e) {
        }
    }

    @Test
    public void normalAccess() {
        Transaction tx = session.beginTransaction();
        User user = factory.createUser();
        tx.commit();

        try {
            execAs(user, new RoleGetAllAction(new RoleGetAllInput()));
            Assert.fail();
        } catch (AccessDeniedException e) {
        }
    }

    @Test
    public void managerAccess() {
        Transaction tx = session.beginTransaction();
        User manager = factory.createUser();
        tx.commit();

        try {
            execAs(manager, new RoleGetAllAction(new RoleGetAllInput()));
            Assert.fail();
        } catch (AccessDeniedException e) {
        }
    }

    @Test
    public void upToDate() {
        Transaction tx = session.beginTransaction();
        Role newRole = factory.createRole();
        tx.commit();

        RoleGetAllOutput output;

        output = exec(new RoleGetAllAction(new RoleGetAllInput()));
        Set<Role> postInsertActionRoles = output.getAllRoles();

        Assert.assertTrue("role not found",
            postInsertActionRoles.contains(newRole));

        @SuppressWarnings("unchecked")
        Set<Role> dbRoles =
            new HashSet<Role>(session.createCriteria(Role.class)
                .list());
        Assert.assertEquals("unexpected roles",
            postInsertActionRoles, dbRoles);

        tx = session.beginTransaction();
        session.delete(newRole);
        tx.commit();

        output = exec(new RoleGetAllAction(new RoleGetAllInput()));
        Set<Role> postDeleteActionRoles = output.getAllRoles();

        Assert.assertTrue("role not removed",
            !postDeleteActionRoles.contains(newRole));
    }

    @Test
    public void inited() {
        Transaction tx = session.beginTransaction();
        Role r1 = factory.createRole();
        r1.getPermissions().addAll(PermissionEnum.valuesList());
        session.update(r1);

        Role r2 = factory.createRole();
        r2.getPermissions().add(PermissionEnum.REPORTS);
        session.update(r2);

        factory.createRole();
        tx.commit();

        RoleGetAllOutput output =
            exec(new RoleGetAllAction(new RoleGetAllInput()));

        for (Role role : output.getAllRoles()) {
            role.getId();
            role.getName();

            for (PermissionEnum perm : role.getPermissions()) {
                perm.getId();
                perm.getName();
            }
        }
    }
}
