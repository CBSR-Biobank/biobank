package edu.ualberta.med.biobank.action.security;

import java.util.List;

import junit.framework.Assert;

import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;
import org.junit.Test;

import edu.ualberta.med.biobank.action.ActionTest;
import edu.ualberta.med.biobank.action.exception.AccessDeniedException;
import edu.ualberta.med.biobank.model.security.Group;
import edu.ualberta.med.biobank.model.security.Role;
import edu.ualberta.med.biobank.model.security.User;

public class TestGroupDeleteAction extends ActionTest {
    @Test
    public void normalAccess() {
        Transaction tx = session.beginTransaction();
        Group group = factory.createGroup();
        User user = factory.createUser();
        factory.buildMembership().setGlobal().create();
        tx.commit();

        try {
            execAs(user, new GroupDeleteAction(new GroupDeleteInput(group)));
            Assert.fail("only user managers should be able to delete a group");
        } catch (AccessDeniedException e) {
        }
    }

    @Test
    public void asGlobalUserManager() {
        Transaction tx = session.beginTransaction();
        Group group = factory.createGroup();
        User user = factory.createUser();
        factory.buildMembership().setGlobal().setUserManager(true).create();
        tx.commit();

        execAs(user, new GroupDeleteAction(new GroupDeleteInput(group)));

        List<?> results = session.createCriteria(Role.class)
            .add(Restrictions.idEq(group.getId()))
            .list();

        Assert.assertTrue("group not deleted", results.isEmpty());
    }
}
