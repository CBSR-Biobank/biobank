package edu.ualberta.med.biobank.test.action.security;

import junit.framework.Assert;

import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;
import org.junit.Test;

import edu.ualberta.med.biobank.common.action.IdResult;
import edu.ualberta.med.biobank.common.action.exception.AccessDeniedException;
import edu.ualberta.med.biobank.common.action.security.RoleSaveAction;
import edu.ualberta.med.biobank.common.action.security.RoleSaveInput;
import edu.ualberta.med.biobank.model.PermissionEnum;
import edu.ualberta.med.biobank.model.Rank;
import edu.ualberta.med.biobank.model.Role;
import edu.ualberta.med.biobank.model.User;
import edu.ualberta.med.biobank.test.Factory.Domain;
import edu.ualberta.med.biobank.test.action.TestAction;

public class TestRoleSaveAction extends TestAction {
    @Test
    public void superAdminAccess() {
        Transaction tx = session.beginTransaction();
        Role role = factory.createRole();
        tx.commit();

        exec(new RoleSaveAction(new RoleSaveInput(role)));
    }

    @Test
    public void adminAccess() {
        Transaction tx = session.beginTransaction();
        Role role = factory.createRole();
        User user = factory.createUser();
        factory.createMembership(Domain.CENTER_STUDY, Rank.ADMINISTRATOR);
        tx.commit();

        try {
            execAs(user, new RoleSaveAction(new RoleSaveInput(role)));
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
            execAs(user, new RoleSaveAction(new RoleSaveInput(role)));
            Assert.fail();
        } catch (AccessDeniedException e) {
        }
    }

    @Test
    public void managerAccess() {
        Transaction tx = session.beginTransaction();
        Role role = factory.createRole();
        User manager = factory.createUser();
        factory.createMembership(Domain.CENTER_STUDY, Rank.MANAGER);
        tx.commit();

        try {
            execAs(manager, new RoleSaveAction(new RoleSaveInput(role)));
            Assert.fail();
        } catch (AccessDeniedException e) {
        }
    }

    @Test
    public void insert() {
        Role role = new Role();
        role.setName(getMethodNameR());
        role.getPermissions().addAll(PermissionEnum.valuesList());

        IdResult result = exec(new RoleSaveAction(new RoleSaveInput(role)));

        Object o = session.createCriteria(Role.class)
            .add(Restrictions.idEq(result.getId()))
            .uniqueResult();

        Assert.assertTrue(o instanceof Role);

        Role saved = (Role) o;

        Assert.assertTrue("name not inserted properly",
            role.getName().equals(saved.getName()));

        Assert.assertTrue("permissions not inserted properly",
            role.getPermissions().equals(saved.getPermissions()));
    }

    @Test
    public void update() {
        Transaction tx = session.beginTransaction();
        Role role = factory.createRole();
        role.getPermissions().add(PermissionEnum.REPORTS);
        session.update(role);
        tx.commit();

        role.setName(factory.getName(Role.class));
        role.getPermissions().clear();

        IdResult result = exec(new RoleSaveAction(new RoleSaveInput(role)));

        Assert.assertEquals("id should not change",
            role.getId(), result.getId());

        Object o = session.createCriteria(Role.class)
            .add(Restrictions.idEq(role.getId()))
            .uniqueResult();

        Assert.assertTrue(o instanceof Role);

        Role saved = (Role) o;

        Assert.assertEquals("name not updated properly",
            role.getName(), saved.getName());

        Assert.assertEquals("permissions not updated properly",
            role.getPermissions(), saved.getPermissions());
    }
}
