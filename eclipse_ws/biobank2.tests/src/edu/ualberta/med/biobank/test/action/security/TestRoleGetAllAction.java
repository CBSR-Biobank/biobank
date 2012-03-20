package edu.ualberta.med.biobank.test.action.security;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import junit.framework.Assert;

import org.hibernate.Transaction;
import org.junit.Test;

import edu.ualberta.med.biobank.common.action.exception.AccessDeniedException;
import edu.ualberta.med.biobank.common.action.security.RoleGetAllAction;
import edu.ualberta.med.biobank.common.action.security.RoleGetAllInput;
import edu.ualberta.med.biobank.common.action.security.RoleGetAllOutput;
import edu.ualberta.med.biobank.model.Role;
import edu.ualberta.med.biobank.model.User;
import edu.ualberta.med.biobank.test.action.TestAction;

public class TestRoleGetAllAction extends TestAction {
    @Test
    public void superAdminAccess() {
        exec(new RoleGetAllAction(new RoleGetAllInput()));
    }

    @Test
    public void adminAccess() {
        Transaction tx = session.beginTransaction();
        User user = factory.createAdmin();
        tx.commit();

        try {
            getExecutor().setUserId(user.getId());
            exec(new RoleGetAllAction(new RoleGetAllInput()));
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
            getExecutor().setUserId(user.getId());
            exec(new RoleGetAllAction(new RoleGetAllInput()));
            Assert.fail();
        } catch (AccessDeniedException e) {
        }
    }

    @Test
    public void managerAccess() {
        Transaction tx = session.beginTransaction();
        User user = factory.createUserManager();
        tx.commit();

        try {
            getExecutor().setUserId(user.getId());
            exec(new RoleGetAllAction(new RoleGetAllInput()));
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
        Set<Role> dbRoles = new HashSet<Role>(
            (List<Role>) session.createCriteria(Role.class)
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
}
