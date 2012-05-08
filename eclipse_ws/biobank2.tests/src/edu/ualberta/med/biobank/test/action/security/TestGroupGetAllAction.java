package edu.ualberta.med.biobank.test.action.security;

import java.util.HashSet;
import java.util.Set;

import junit.framework.Assert;

import org.hibernate.Transaction;
import org.junit.Test;

import edu.ualberta.med.biobank.common.action.exception.AccessDeniedException;
import edu.ualberta.med.biobank.common.action.security.GroupGetAllAction;
import edu.ualberta.med.biobank.common.action.security.GroupGetAllInput;
import edu.ualberta.med.biobank.common.action.security.GroupGetAllOutput;
import edu.ualberta.med.biobank.model.Group;
import edu.ualberta.med.biobank.model.Membership;
import edu.ualberta.med.biobank.model.PermissionEnum;
import edu.ualberta.med.biobank.model.Rank;
import edu.ualberta.med.biobank.model.Role;
import edu.ualberta.med.biobank.model.Site;
import edu.ualberta.med.biobank.model.Study;
import edu.ualberta.med.biobank.model.User;
import edu.ualberta.med.biobank.test.AssertMore;
import edu.ualberta.med.biobank.test.Factory.Domain;
import edu.ualberta.med.biobank.test.action.ActionTest;

public class TestGroupGetAllAction extends ActionTest {
    @Test
    public void superAdminAccess() {
        exec(new GroupGetAllAction(new GroupGetAllInput()));
    }

    @Test
    public void adminAccess() {
        Transaction tx = session.beginTransaction();
        User user = factory.createUser();
        factory.createMembership(Domain.CENTER_STUDY, Rank.ADMINISTRATOR);
        tx.commit();

        execAs(user, new GroupGetAllAction(new GroupGetAllInput()));
    }

    @Test
    public void normalAccess() {
        Transaction tx = session.beginTransaction();
        User user = factory.createUser();
        tx.commit();

        try {
            execAs(user, new GroupGetAllAction(new GroupGetAllInput()));
            Assert.fail();
        } catch (AccessDeniedException e) {
        }
    }

    @Test
    public void managerAccess() {
        Transaction tx = session.beginTransaction();
        User user = factory.createUser();
        factory.createMembership(Domain.CENTER_STUDY, Rank.MANAGER);
        tx.commit();

        execAs(user, new GroupGetAllAction(new GroupGetAllInput()));
    }

    @Test
    public void upToDate() {
        Transaction tx = session.beginTransaction();
        Group newGroup = factory.createGroup();
        tx.commit();

        GroupGetAllOutput output;

        output = exec(new GroupGetAllAction(new GroupGetAllInput()));
        Set<Group> postInsertGroups = output.getAllManageableGroups();

        Assert.assertTrue("group not found",
            postInsertGroups.contains(newGroup));

        @SuppressWarnings("unchecked")
        Set<Group> dbGroups = new HashSet<Group>(
            session.createCriteria(Group.class).list());
        Assert.assertEquals("unexpected groups",
            postInsertGroups, dbGroups);

        tx = session.beginTransaction();
        session.delete(newGroup);
        tx.commit();

        output = exec(new GroupGetAllAction(new GroupGetAllInput()));
        Set<Group> postDeleteGroups = output.getAllManageableGroups();

        Assert.assertTrue("role not removed",
            !postDeleteGroups.contains(newGroup));
    }

    @Test
    public void inited() {
        Transaction tx = session.beginTransaction();
        Group g1 = factory.createGroup();
        for (int i = 0; i < 5; i++) {
            User u = factory.createUser();
            g1.getUsers().add(u);
            u.getGroups().add(g1);
            session.update(u);
        }
        tx.commit();

        GroupGetAllOutput output =
            exec(new GroupGetAllAction(new GroupGetAllInput()));

        Assert.assertFalse("expected at least one group",
            output.getAllManageableGroups().isEmpty());

        for (Group group : output.getAllManageableGroups()) {
            group.getId();
            group.getName();
            group.getDescription();
            group.getActivityStatus();

            for (Membership m : group.getMemberships()) {
                m.getDomain();
                m.isUserManager();
                m.isEveryPermission();

                for (Role r : m.getRoles()) {
                    r.getName();

                    AssertMore.assertInitialized(r.getPermissions());
                }
            }

            for (User u : group.getUsers()) {
                u.getId();
                u.getActivityStatus();
                u.getLogin();
                u.getEmail();
                u.getFullName();

                AssertMore.assertNotInitialized(u.getGroups());
                AssertMore.assertNotInitialized(u.getMemberships());
                AssertMore.assertNotInitialized(u.getComments());
            }
        }
    }

    @SuppressWarnings("unused")
    @Test
    public void domainSpecific() {
        Transaction tx = session.beginTransaction();
        Site site1 = factory.createSite();
        Study studyA = factory.createStudy();
        Group group1A = factory.createGroup();
        factory.createMembership();
        User man1A = factory.createUser();
        factory.createMembership(Domain.CENTER_STUDY, Rank.MANAGER);

        Membership man1Amembership = man1A.getMemberships().iterator().next();
        man1Amembership.getPermissions().add(PermissionEnum.CLINIC_READ);

        Site site2 = factory.createSite();
        Study studyB = factory.createStudy();
        Group group2B = factory.createGroup();
        factory.createMembership();
        User man2B = factory.createUser();
        factory.createMembership(Domain.CENTER_STUDY, Rank.MANAGER);
        tx.commit();

        GroupGetAllOutput actual;
        final Set<Group> expected = new HashSet<Group>();

        expected.clear();
        expected.add(group1A);
        actual = execAs(man1A, new GroupGetAllAction(new GroupGetAllInput()));

        Assert.assertEquals("wrong groups",
            expected, actual.getAllManageableGroups());

        expected.clear();
        actual = execAs(man2B, new GroupGetAllAction(new GroupGetAllInput()));

        Assert.assertEquals("wrong groups",
            expected, actual.getAllManageableGroups());
    }
}