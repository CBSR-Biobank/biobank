package edu.ualberta.med.biobank.test.action.security;

import java.util.HashSet;
import java.util.List;
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
import edu.ualberta.med.biobank.model.Role;
import edu.ualberta.med.biobank.model.User;
import edu.ualberta.med.biobank.test.AssertMore;
import edu.ualberta.med.biobank.test.action.TestAction;

public class TestGroupGetAllAction extends TestAction {
    @Test
    public void superAdminAccess() {
        exec(new GroupGetAllAction(new GroupGetAllInput()));
    }

    @Test
    public void adminAccess() {
        execAsAdmin(new GroupGetAllAction(new GroupGetAllInput()));
    }

    @Test
    public void normalAccess() {
        try {
            execAsNormal(new GroupGetAllAction(new GroupGetAllInput()));
            Assert.fail();
        } catch (AccessDeniedException e) {
        }
    }

    @Test
    public void managerAccess() {
        execAsManager(new GroupGetAllAction(new GroupGetAllInput()));
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
            (List<Group>) session.createCriteria(Group.class)
                .list());
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

        for (Group group : output.getAllManageableGroups()) {
            group.getId();
            group.getName();
            group.getDescription();
            group.getActivityStatus();

            for (Membership m : group.getMemberships()) {
                m.getCenter();
                m.getStudy();
                m.getRank();
                m.getLevel();

                for (Role r : m.getRoles()) {
                    r.getName();

                    AssertMore.assertInited(r.getPermissions());
                }
            }

            for (User u : group.getUsers()) {
                u.getId();
                u.getActivityStatus();
                u.getLogin();
                u.getEmail();
                u.getFullName();

                AssertMore.assertNotInited(u.getGroups());
                AssertMore.assertNotInited(u.getMemberships());
                AssertMore.assertNotInited(u.getComments());
            }
        }
    }
}