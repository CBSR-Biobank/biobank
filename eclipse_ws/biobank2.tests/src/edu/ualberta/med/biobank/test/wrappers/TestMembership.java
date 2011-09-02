package edu.ualberta.med.biobank.test.wrappers;

import java.util.Arrays;
import java.util.List;

import junit.framework.Assert;

import org.junit.Test;

import edu.ualberta.med.biobank.common.wrappers.BbRightWrapper;
import edu.ualberta.med.biobank.common.wrappers.ClinicWrapper;
import edu.ualberta.med.biobank.common.wrappers.MembershipWrapper;
import edu.ualberta.med.biobank.common.wrappers.PermissionWrapper;
import edu.ualberta.med.biobank.common.wrappers.PrivilegeWrapper;
import edu.ualberta.med.biobank.common.wrappers.RoleWrapper;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;
import edu.ualberta.med.biobank.common.wrappers.StudyWrapper;
import edu.ualberta.med.biobank.common.wrappers.UserWrapper;
import edu.ualberta.med.biobank.model.Role;
import edu.ualberta.med.biobank.test.TestDatabase;
import edu.ualberta.med.biobank.test.internal.ClinicHelper;
import edu.ualberta.med.biobank.test.internal.MembershipHelper;
import edu.ualberta.med.biobank.test.internal.RightHelper;
import edu.ualberta.med.biobank.test.internal.RoleHelper;
import edu.ualberta.med.biobank.test.internal.SiteHelper;
import edu.ualberta.med.biobank.test.internal.StudyHelper;
import edu.ualberta.med.biobank.test.internal.UserHelper;

public class TestMembership extends TestDatabase {

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
        PermissionWrapper rp = new PermissionWrapper(appService);
        rp.setRight(right);
        rp.addToPrivilegeCollection(Arrays.asList(read, update));
        rp.setRole(role1);
        role1.addToPermissionCollection(Arrays.asList(rp));
        role1.persist();
        RoleHelper.createdRoles.add(role1);

        RoleWrapper role2 = RoleHelper.newRole(name + "_2");
        rp = new PermissionWrapper(appService);
        rp.setRight(right);
        rp.addToPrivilegeCollection(Arrays.asList(delete, create));
        rp.setRole(role2);
        role2.addToPermissionCollection(Arrays.asList(rp));
        role2.persist();
        RoleHelper.createdRoles.add(role2);

        UserWrapper user = UserHelper.addUser(name, null, true);

        MembershipWrapper mwr = MembershipHelper
            .newMembership(user, null, null);
        mwr.addToRoleCollection(Arrays.asList(role1));
        mwr.persist();

        // another membership for the second role
        MembershipWrapper mwr2 = MembershipHelper.newMembership(user, null,
            null);
        mwr2.addToRoleCollection(Arrays.asList(role2));
        mwr2.persist();

        mwr.reload();
        List<PrivilegeWrapper> privilegesForRight = mwr.getPrivilegesForRight(
            right, null, null);
        // make sure retrieve privileges for this Membership only
        Assert.assertEquals(2, privilegesForRight.size());
        Assert.assertTrue(privilegesForRight.contains(read));
        Assert.assertTrue(privilegesForRight.contains(update));
        Assert.assertFalse(privilegesForRight.contains(delete));
        Assert.assertFalse(privilegesForRight.contains(create));
    }

    @Test
    public void testGetPrivilegesForRightCenterSpecific() throws Exception {
        String name = "testGetPrivilegesForRightCenterSpecific" + r.nextInt();

        PrivilegeWrapper read = PrivilegeWrapper.getReadPrivilege(appService);
        PrivilegeWrapper update = PrivilegeWrapper
            .getUpdatePrivilege(appService);
        PrivilegeWrapper delete = PrivilegeWrapper
            .getDeletePrivilege(appService);
        PrivilegeWrapper create = PrivilegeWrapper
            .getCreatePrivilege(appService);

        BbRightWrapper right = RightHelper.addRight(name, name, true);

        RoleWrapper role1 = RoleHelper.newRole(name + "_1");
        PermissionWrapper rp = new PermissionWrapper(appService);
        rp.setRight(right);
        rp.addToPrivilegeCollection(Arrays.asList(read, update));
        rp.setRole(role1);
        role1.addToPermissionCollection(Arrays.asList(rp));
        role1.persist();
        RoleHelper.createdRoles.add(role1);

        RoleWrapper role2 = RoleHelper.newRole(name + "_2");
        rp = new PermissionWrapper(appService);
        rp.setRight(right);
        rp.addToPrivilegeCollection(Arrays.asList(delete, create));
        rp.setRole(role2);
        role2.addToPermissionCollection(Arrays.asList(rp));
        role2.persist();
        RoleHelper.createdRoles.add(role2);

        UserWrapper user = UserHelper.addUser(name, null, true);

        SiteWrapper site = SiteHelper.addSite(name);
        SiteWrapper site2 = SiteHelper.addSite(name + "_2");
        StudyWrapper study = StudyHelper.addStudy(name);

        MembershipWrapper mwr = MembershipHelper
            .newMembership(user, site, null);
        mwr.addToRoleCollection(Arrays.asList(role1));
        mwr.persist();

        mwr.reload();
        // right is specific to site, not specific to any study
        Assert.assertEquals(0, mwr.getPrivilegesForRight(right, null, null)
            .size());
        Assert.assertEquals(0, mwr.getPrivilegesForRight(right, site2, null)
            .size());
        Assert.assertEquals(0, mwr.getPrivilegesForRight(right, null, study)
            .size());

        List<PrivilegeWrapper> privilegesForRight = mwr.getPrivilegesForRight(
            right, site, null);
        Assert.assertEquals(2, privilegesForRight.size());
        Assert.assertTrue(privilegesForRight.contains(read));
        Assert.assertTrue(privilegesForRight.contains(update));
        Assert.assertEquals(2, mwr.getPrivilegesForRight(right, site, study)
            .size());
    }

    @Test
    public void testCanDeleteMembershipRoleUsingARole() throws Exception {
        String name = "testCanDeleteMembershipRoleUsingARole" + r.nextInt();
        UserWrapper user = UserHelper.addUser(name, null, true);

        BbRightWrapper right = RightHelper.addRight(name, name, true);

        RoleWrapper role1 = RoleHelper.newRole(name + "_1");
        PermissionWrapper rp = new PermissionWrapper(appService);
        rp.setRight(right);
        rp.addToPrivilegeCollection(Arrays.asList(PrivilegeWrapper
            .getReadPrivilege(appService)));
        rp.setRole(role1);
        role1.addToPermissionCollection(Arrays.asList(rp));
        role1.persist();
        RoleHelper.createdRoles.add(role1);

        MembershipWrapper mwr = MembershipHelper
            .newMembership(user, null, null);
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

    /**
     * Test unique constraint on principal/study/center
     */
    @Test
    public void testUniqueConstraint() throws Exception {
        String name = "testUniqueConstraint" + r.nextInt();

        StudyWrapper s = StudyHelper.addStudy(name);
        ClinicWrapper c = ClinicHelper.addClinic(name);
        UserWrapper u = UserHelper.addUser(name, null, true);

        MembershipHelper.addMembership(u, c, s);

        try {
            MembershipHelper.addMembership(u, c, s);
            Assert.fail("Should not be able to insert");
        } catch (Exception ex) {
            Assert.assertTrue("Should not be able to insert", true);
        }
        try {
            MembershipHelper.addMembership(u, c, null);
            Assert.assertTrue("Should be able to insert", true);
        } catch (Exception ex) {
            Assert.fail("Should be able to insert");
        }
        try {
            MembershipHelper.addMembership(u, c, null);
            Assert.fail("Should not be able to insert");
        } catch (Exception ex) {
            Assert.assertTrue("Should not be able to insert", true);
        }

        MembershipHelper.addMembership(u, null, null);
        try {
            MembershipHelper.addMembership(u, null, null);
            Assert.fail("Should not be able to insert");
        } catch (Exception ex) {
            Assert.assertTrue("Should not be able to insert", true);
        }
    }
}
