package edu.ualberta.med.biobank.test.action.security;

import java.util.List;

import junit.framework.Assert;

import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;
import org.junit.Test;

import edu.ualberta.med.biobank.common.action.exception.AccessDeniedException;
import edu.ualberta.med.biobank.common.action.exception.ModelNotFoundException;
import edu.ualberta.med.biobank.common.action.security.RoleDeleteAction;
import edu.ualberta.med.biobank.common.action.security.RoleDeleteInput;
import edu.ualberta.med.biobank.model.Role;
import edu.ualberta.med.biobank.model.User;
import edu.ualberta.med.biobank.test.action.TestAction;

public class TestRoleDeleteAction extends TestAction {
    @Test
    public void superAdminAccess() {
        Transaction tx = session.beginTransaction();
        Role role = factory.createRole();
        tx.commit();

        exec(new RoleDeleteAction(new RoleDeleteInput(role)));
    }

    @Test
    public void adminAccess() {
        Transaction tx = session.beginTransaction();
        Role role = factory.createRole();
        User admin = factory.createAdmin();
        tx.commit();

        try {
            execAs(admin, new RoleDeleteAction(new RoleDeleteInput(role)));
            Assert.fail();
        } catch (AccessDeniedException e) {
        }
    }

    @Test
    public void normalAccess() {
        Transaction tx = session.beginTransaction();
        Role role = factory.createRole();
        User user = factory.createUser();
        tx.commit();

        try {
            execAs(user, new RoleDeleteAction(new RoleDeleteInput(role)));
            Assert.fail();
        } catch (AccessDeniedException e) {
        }
    }

    @Test
    public void managerAccess() {
        Transaction tx = session.beginTransaction();
        Role role = factory.createRole();
        User manager = factory.createManager();
        tx.commit();

        try {
            execAs(manager, new RoleDeleteAction(new RoleDeleteInput(role)));
            Assert.fail();
        } catch (AccessDeniedException e) {
        }
    }

    @Test
    public void transientRole() {
        Role role = new Role();
        role.setId(0);

        try {
            exec(new RoleDeleteAction(new RoleDeleteInput(role)));
            Assert.fail();
        } catch (ModelNotFoundException e) {
        }
    }

    @Test
    public void deleted() {
        Transaction tx = session.beginTransaction();
        Role role = factory.createRole();
        tx.commit();

        exec(new RoleDeleteAction(new RoleDeleteInput(role)));

        List<?> results = session.createCriteria(Role.class)
            .add(Restrictions.idEq(role.getId()))
            .list();

        Assert.assertTrue("role not deleted", results.isEmpty());
    }
}
