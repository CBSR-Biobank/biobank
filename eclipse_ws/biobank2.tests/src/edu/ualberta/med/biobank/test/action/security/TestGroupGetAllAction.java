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
import edu.ualberta.med.biobank.common.action.security.RoleGetAllAction;
import edu.ualberta.med.biobank.common.action.security.RoleGetAllInput;
import edu.ualberta.med.biobank.common.action.security.RoleGetAllOutput;
import edu.ualberta.med.biobank.model.Group;
import edu.ualberta.med.biobank.model.PermissionEnum;
import edu.ualberta.med.biobank.model.Role;
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
