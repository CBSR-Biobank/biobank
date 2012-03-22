package edu.ualberta.med.biobank.test.action.security;

import java.util.List;

import junit.framework.Assert;

import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;
import org.junit.Test;

import edu.ualberta.med.biobank.common.action.exception.AccessDeniedException;
import edu.ualberta.med.biobank.common.action.exception.ModelNotFoundException;
import edu.ualberta.med.biobank.common.action.security.GroupDeleteAction;
import edu.ualberta.med.biobank.common.action.security.GroupDeleteInput;
import edu.ualberta.med.biobank.model.Group;
import edu.ualberta.med.biobank.model.Rank;
import edu.ualberta.med.biobank.model.Role;
import edu.ualberta.med.biobank.model.User;
import edu.ualberta.med.biobank.test.Factory.Domain;
import edu.ualberta.med.biobank.test.action.TestAction;

public class TestGroupDeleteAction extends TestAction {
    @Test
    public void superAdminAccess() {
        Transaction tx = session.beginTransaction();
        Group group = factory.createGroup();
        tx.commit();

        exec(new GroupDeleteAction(new GroupDeleteInput(group)));
    }

    @Test
    public void adminAccess() {
        Transaction tx = session.beginTransaction();
        Group group = factory.createGroup();
        User user = factory.createUser();
        factory.createMembership(Domain.CENTER_STUDY, Rank.ADMINISTRATOR);
        tx.commit();

        execAs(user, new GroupDeleteAction(new GroupDeleteInput(group)));
    }

    @Test
    public void normalAccess() {
        Transaction tx = session.beginTransaction();
        Group group = factory.createGroup();
        User user = factory.createUser();
        tx.commit();

        try {
            execAs(user, new GroupDeleteAction(new GroupDeleteInput(group)));
            Assert.fail();
        } catch (AccessDeniedException e) {
        }
    }

    @Test
    public void managerAccess() {
        Transaction tx = session.beginTransaction();
        Group group = factory.createGroup();
        User user = factory.createUser();
        factory.createMembership(Domain.CENTER_STUDY, Rank.MANAGER);
        tx.commit();

        execAs(user, new GroupDeleteAction(new GroupDeleteInput(group)));
    }

    @Test
    public void transientGroup() {
        Group group = new Group();
        group.setId(0);

        try {
            exec(new GroupDeleteAction(new GroupDeleteInput(group)));
            Assert.fail();
        } catch (ModelNotFoundException e) {
        }
    }

    @Test
    public void deleted() {
        Transaction tx = session.beginTransaction();
        Group group = factory.createGroup();
        tx.commit();

        exec(new GroupDeleteAction(new GroupDeleteInput(group)));

        List<?> results = session.createCriteria(Role.class)
            .add(Restrictions.idEq(group.getId()))
            .list();

        Assert.assertTrue("group not deleted", results.isEmpty());
    }
}
