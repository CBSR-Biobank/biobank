package edu.ualberta.med.biobank.test.wrappers;

import java.util.Arrays;
import java.util.List;

import junit.framework.Assert;

import org.junit.Test;

import edu.ualberta.med.biobank.common.wrappers.BbRightWrapper;
import edu.ualberta.med.biobank.common.wrappers.MembershipRoleWrapper;
import edu.ualberta.med.biobank.common.wrappers.PrivilegeWrapper;
import edu.ualberta.med.biobank.common.wrappers.RightPrivilegeWrapper;
import edu.ualberta.med.biobank.common.wrappers.RoleWrapper;
import edu.ualberta.med.biobank.common.wrappers.UserWrapper;
import edu.ualberta.med.biobank.model.Role;
import edu.ualberta.med.biobank.test.TestDatabase;
import edu.ualberta.med.biobank.test.internal.MembershipHelper;
import edu.ualberta.med.biobank.test.internal.RightHelper;
import edu.ualberta.med.biobank.test.internal.RoleHelper;
import edu.ualberta.med.biobank.test.internal.UserHelper;

public class TestMembershipRole extends TestDatabase {

    @Test
    public void testGetPrivilegesForRight() throws Exception {
        String name = "testGetPrivilegesForRight" + r.nextInt();

        PrivilegeWrapper read = PrivilegeWrapper.getReadPrivilege(appService);
        PrivilegeWrapper update = PrivilegeWrapper
            .getUpdatePrivilege(appService);
        PrivilegeWrapper delete = PrivilegeWrapper
            .getDeletePrivilege(appService);
        PrivilegeWrapper create = PrivilegeWrapper
            .getCreatePrivilege(appService);

        BbRightWrapper right = RightHelper.addRight(name, name, true);

        RoleWrapper role1 = RoleHelper.newRole(name + "_1");
        RightPrivilegeWrapper rp = new RightPrivilegeWrapper(appService);
        rp.setRight(right);
        rp.addToPrivilegeCollection(Arrays.asList(read, update));
        rp.setRole(role1);
        role1.addToRightPrivilegeCollection(Arrays.asList(rp));
        role1.persist();
        RoleHelper.createdRoles.add(role1);

        RoleWrapper role2 = RoleHelper.newRole(name + "_2");
        rp = new RightPrivilegeWrapper(appService);
        rp.setRight(right);
        rp.addToPrivilegeCollection(Arrays.asList(delete, create));
        rp.setRole(role2);
        role2.addToRightPrivilegeCollection(Arrays.asList(rp));
        role2.persist();
        RoleHelper.createdRoles.add(role2);

        UserWrapper user = UserHelper.addUser(name, null, true);

        MembershipRoleWrapper mwr = MembershipHelper.newMembershipRole(user,
            null, null);
        mwr.addToRoleCollection(Arrays.asList(role1));
        mwr.persist();

        // another membership for the second role
        MembershipRoleWrapper mwr2 = MembershipHelper.newMembershipRole(user,
            null, null);
        mwr2.addToRoleCollection(Arrays.asList(role2));
        mwr2.persist();

        mwr.reload();
        List<PrivilegeWrapper> privilegesForRight = mwr
            .getPrivilegesForRight(right);
        // make sure retrieve privileges for this Membership only
        Assert.assertEquals(2, privilegesForRight.size());
        Assert.assertTrue(privilegesForRight.contains(read));
        Assert.assertTrue(privilegesForRight.contains(update));
        Assert.assertFalse(privilegesForRight.contains(delete));
        Assert.assertFalse(privilegesForRight.contains(create));
    }

    @Test
    public void testCanDeleteMembershipRoleUsingARole() throws Exception {
        String name = "testCanDeleteMembershipRoleUsingARole" + r.nextInt();
        UserWrapper user = UserHelper.addUser(name, null, true);

        BbRightWrapper right = RightHelper.addRight(name, name, true);

        RoleWrapper role1 = RoleHelper.newRole(name + "_1");
        RightPrivilegeWrapper rp = new RightPrivilegeWrapper(appService);
        rp.setRight(right);
        rp.addToPrivilegeCollection(Arrays.asList(PrivilegeWrapper
            .getReadPrivilege(appService)));
        rp.setRole(role1);
        role1.addToRightPrivilegeCollection(Arrays.asList(rp));
        role1.persist();
        RoleHelper.createdRoles.add(role1);

        MembershipRoleWrapper mwr = MembershipHelper.newMembershipRole(user,
            null, null);
        mwr.addToRoleCollection(Arrays.asList(role1));
        mwr.persist();

        mwr.reload();
        Integer idRole = role1.getId();
        Assert.assertEquals(1, mwr.getRoleCollection(false).size());
        try {
            mwr.delete();
            Assert
                .assertTrue("Can delete a membership role using a role", true);
        } catch (Exception ex) {
            Assert.fail("Should be able to delete the membership role");
        }
        Assert.assertNotNull(ModelUtils.getObjectWithId(appService, Role.class,
            idRole));
    }
}
