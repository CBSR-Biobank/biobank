package edu.ualberta.med.biobank.test.wrappers;

import java.util.Arrays;

import junit.framework.Assert;

import org.junit.Test;

import edu.ualberta.med.biobank.common.wrappers.ClinicWrapper;
import edu.ualberta.med.biobank.common.wrappers.MembershipWrapper;
import edu.ualberta.med.biobank.common.wrappers.RoleWrapper;
import edu.ualberta.med.biobank.common.wrappers.StudyWrapper;
import edu.ualberta.med.biobank.common.wrappers.UserWrapper;
import edu.ualberta.med.biobank.model.PermissionEnum;
import edu.ualberta.med.biobank.model.Role;
import edu.ualberta.med.biobank.server.applicationservice.exceptions.DuplicatePropertySetException;
import edu.ualberta.med.biobank.test.TestDatabase;
import edu.ualberta.med.biobank.test.internal.ClinicHelper;
import edu.ualberta.med.biobank.test.internal.MembershipHelper;
import edu.ualberta.med.biobank.test.internal.RoleHelper;
import edu.ualberta.med.biobank.test.internal.StudyHelper;
import edu.ualberta.med.biobank.test.internal.UserHelper;

@Deprecated
public class TestMembership extends TestDatabase {

    @Test
    public void testCanDeleteMembershipRoleUsingARole() throws Exception {
        String name = "testCanDeleteMembershipRoleUsingARole" + r.nextInt();
        UserWrapper user = UserHelper.addUser(name, null, true);

        RoleWrapper role1 = RoleHelper.newRole(name + "_1");
        role1.addToPermissionCollection(Arrays.asList(PermissionEnum.ADMINISTRATION));
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
        } catch (DuplicatePropertySetException ex) {
            Assert.assertTrue("Should not be able to insert", true);
        }
        try {
            MembershipHelper.addMembership(u, c, null);
            Assert.assertTrue("Should be able to insert", true);
        } catch (DuplicatePropertySetException ex) {
            Assert.fail("Should be able to insert");
        }
        try {
            MembershipHelper.addMembership(u, c, null);
            Assert.fail("Should not be able to insert");
        } catch (DuplicatePropertySetException ex) {
            Assert.assertTrue("Should not be able to insert", true);
        }

        MembershipHelper.addMembership(u, null, null);
        try {
            MembershipHelper.addMembership(u, null, null);
            Assert.fail("Should not be able to insert");
        } catch (DuplicatePropertySetException ex) {
            Assert.assertTrue("Should not be able to insert", true);
        }
    }
}
