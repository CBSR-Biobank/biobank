package edu.ualberta.med.biobank.test.wrappers;

import java.util.Arrays;
import java.util.List;

import junit.framework.Assert;

import org.junit.Test;

import edu.ualberta.med.biobank.common.wrappers.BbGroupWrapper;
import edu.ualberta.med.biobank.common.wrappers.BbRightWrapper;
import edu.ualberta.med.biobank.common.wrappers.MembershipWrapper;
import edu.ualberta.med.biobank.common.wrappers.PermissionWrapper;
import edu.ualberta.med.biobank.common.wrappers.PrivilegeWrapper;
import edu.ualberta.med.biobank.common.wrappers.RoleWrapper;
import edu.ualberta.med.biobank.common.wrappers.UserWrapper;
import edu.ualberta.med.biobank.model.BbGroup;
import edu.ualberta.med.biobank.model.Membership;
import edu.ualberta.med.biobank.model.Permission;
import edu.ualberta.med.biobank.model.Role;
import edu.ualberta.med.biobank.test.TestDatabase;
import edu.ualberta.med.biobank.test.internal.GroupHelper;
import edu.ualberta.med.biobank.test.internal.RoleHelper;
import edu.ualberta.med.biobank.test.internal.UserHelper;

public class TestGroup extends TestDatabase {

    @Test
    public void testGettersAndSetters() throws Exception {
        String name = "testGettersAndSetters" + r.nextInt();
        BbGroupWrapper g = GroupHelper.addGroup(name, true);
        testGettersAndSetters(g);
    }

    @Test
    public void addUsers() throws Exception {
        String name = "addUsers" + r.nextInt();
        BbGroupWrapper group = GroupHelper.addGroup(name, true);

        Assert.assertEquals(0, group.getUserCollection(false).size());

        UserWrapper user1 = UserHelper.addUser(name + "_1", null, true);
        UserWrapper user2 = UserHelper.addUser(name + "_2", null, true);

        group.addToUserCollection(Arrays.asList(user1, user2));
        group.persist();
        group.reload();
        user1.reload();
        user2.reload();
        Assert.assertEquals(2, group.getUserCollection(false).size());
        Assert.assertEquals(1, user1.getGroupCollection(false).size());
        Assert.assertEquals(1, user2.getGroupCollection(false).size());
    }

    @Test
    public void testCascadeWithMembershipRole() throws Exception {
        String name = "testCascadeWithMembershipRole" + r.nextInt();

        BbGroupWrapper group = GroupHelper.addGroup(name, false);
        Assert.assertEquals(0, group.getMembershipCollection(false).size());

        RoleWrapper role = RoleHelper.addRole(name, true);
        Integer idRole = role.getId();

        MembershipWrapper mrw = new MembershipWrapper(appService);
        mrw.setPrincipal(group);
        mrw.addToRoleCollection(Arrays.asList(role));
        group.addToMembershipCollection(Arrays.asList(mrw));
        group.persist();

        Assert.assertEquals(1, group.getMembershipCollection(false).size());
        MembershipWrapper membership = group.getMembershipCollection(false)
            .get(0);
        Integer idMembership = membership.getId();
        Assert.assertEquals(group, membership.getPrincipal());
        Assert.assertEquals(1, membership.getRoleCollection(false).size());
        Assert.assertEquals(role, membership.getRoleCollection(false).get(0));

        // delete group
        Integer idGroup = group.getId();
        group.delete();

        // check group is deleted
        BbGroup dbGroup = ModelUtils.getObjectWithId(appService, BbGroup.class,
            idGroup);
        Assert.assertNull(dbGroup);

        // check membership deleted
        Membership dbMembership = ModelUtils.getObjectWithId(appService,
            Membership.class, idMembership);
        Assert.assertNull(dbMembership);

        // check role no deleted
        Role dbRole = ModelUtils
            .getObjectWithId(appService, Role.class, idRole);
        Assert.assertNotNull(dbRole);
    }

    @Test
    public void testCascadeWithMembershipRight() throws Exception {
        String name = "testCascadeWithMembershipRight" + r.nextInt();

        BbGroupWrapper group = GroupHelper.addGroup(name, false);
        Assert.assertEquals(0, group.getMembershipCollection(false).size());

        MembershipWrapper mrw = new MembershipWrapper(appService);
        mrw.setPrincipal(group);

        PermissionWrapper rp = new PermissionWrapper(appService);
        rp.setMembership(mrw);
        BbRightWrapper right = BbRightWrapper.getAllRights(appService).get(0);
        rp.setRight(right);
        List<PrivilegeWrapper> privileges = PrivilegeWrapper
            .getAllPrivileges(appService);
        rp.addToPrivilegeCollection(privileges);
        mrw.addToPermissionCollection(Arrays.asList(rp));
        group.addToMembershipCollection(Arrays.asList(mrw));
        group.persist();

        // check everything is added
        Assert.assertEquals(1, group.getMembershipCollection(false).size());
        MembershipWrapper membership = group.getMembershipCollection(false)
            .get(0);
        Integer idMembership = membership.getId();
        Assert.assertEquals(group, membership.getPrincipal());
        Assert
            .assertEquals(1, membership.getPermissionCollection(false).size());
        rp = membership.getPermissionCollection(false).get(0);
        Integer idRp = rp.getId();
        Assert.assertEquals(right, rp.getRight());
        Assert.assertEquals(privileges.size(), rp.getPrivilegeCollection(false)
            .size());

        // delete group
        Integer idGroup = group.getId();
        group.delete();

        // check group is deleted
        BbGroup dbGroup = ModelUtils.getObjectWithId(appService, BbGroup.class,
            idGroup);
        Assert.assertNull(dbGroup);

        // check membership deleted
        Membership dbMembership = ModelUtils.getObjectWithId(appService,
            Membership.class, idMembership);
        Assert.assertNull(dbMembership);

        // check permission deleted
        Permission dbPermission = ModelUtils.getObjectWithId(appService,
            Permission.class, idRp);
        Assert.assertNull(dbPermission);
    }
}
