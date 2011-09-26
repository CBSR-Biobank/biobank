package edu.ualberta.med.biobank.test.wrappers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import edu.ualberta.med.biobank.common.wrappers.BbRightWrapper;
import edu.ualberta.med.biobank.common.wrappers.MembershipWrapper;
import edu.ualberta.med.biobank.common.wrappers.PermissionWrapper;
import edu.ualberta.med.biobank.common.wrappers.PrivilegeWrapper;
import edu.ualberta.med.biobank.common.wrappers.RoleWrapper;
import edu.ualberta.med.biobank.common.wrappers.UserWrapper;
import edu.ualberta.med.biobank.model.Permission;
import edu.ualberta.med.biobank.model.Role;
import edu.ualberta.med.biobank.server.applicationservice.exceptions.BiobankSessionException;
import edu.ualberta.med.biobank.test.TestDatabase;
import edu.ualberta.med.biobank.test.internal.MembershipHelper;
import edu.ualberta.med.biobank.test.internal.RoleHelper;
import edu.ualberta.med.biobank.test.internal.UserHelper;

public class TestRole extends TestDatabase {

    @Test
    public void testGettersAndSetters() throws Exception {
        String name = "testGettersAndSetters" + r.nextInt();
        RoleWrapper role = RoleHelper.addRole(name, true);
        testGettersAndSetters(role);
    }

    @Test
    public void testCascadeWithPermission() throws Exception {
        String name = "testCascadeWithPermission" + r.nextInt();
        RoleWrapper role = new RoleWrapper(appService);
        role.setName(name);

        List<PrivilegeWrapper> privileges = PrivilegeWrapper
            .getAllPrivileges(appService);
        List<BbRightWrapper> rights = BbRightWrapper.getAllRights(appService);

        List<PermissionWrapper> rpList = new ArrayList<PermissionWrapper>();
        for (BbRightWrapper right : rights) {
            // for each right, give all privileges
            PermissionWrapper rp = new PermissionWrapper(appService);
            rp.setRight(right);
            rp.addToPrivilegeCollection(privileges);
            rp.setRole(role);
            rpList.add(rp);
        }
        role.addToPermissionCollection(rpList);
        role.persist();

        // checking cascade worked well:

        // supposed to have same number of rigthPrivilege than number of rights
        Assert.assertEquals(rights.size(), role.getPermissionCollection(false)
            .size());
        List<Integer> rpIdList = new ArrayList<Integer>();
        int nberPriviliges = privileges.size();
        for (PermissionWrapper rp : role.getPermissionCollection(false)) {
            // each permission is supposed to have same number of privileges
            // than the total number of privileges
            Assert.assertEquals(nberPriviliges, rp
                .getPrivilegeCollection(false).size());
            rpIdList.add(rp.getId());
        }

        Integer idRole = role.getId();
        // delete Role
        role.delete();

        // check role deleted
        Role dbRole = ModelUtils
            .getObjectWithId(appService, Role.class, idRole);
        Assert.assertNull(dbRole);
        // check right privileges also deleted:
        for (Integer id : rpIdList) {
            Permission dbRp = ModelUtils.getObjectWithId(appService,
                Permission.class, id);
            Assert.assertNull(dbRp);
        }
    }

    @Test
    public void testDeleteIsUsedInMembership() throws Exception {
        String name = "addMembershipsWithRole" + r.nextInt();
        UserWrapper user = UserHelper.addUser(name, null, true);

        RoleWrapper role = RoleHelper.addRole(name, false);

        MembershipWrapper mwr = MembershipHelper
            .newMembership(user, null, null);
        mwr.addToRoleCollection(Arrays.asList(role));
        user.persist();

        user.reload();
        Assert.assertEquals(1, user.getMembershipCollection(false).size());

        role.reload();
        try {
            role.delete();
        } catch (BiobankSessionException bse) {
            Assert.assertTrue(
                "Can't delete because it is still used in memberships", true);
        } catch (Exception e) {
            Assert
                .fail("If try to delete when used in a membership, the query will fail because of foreign keys problems");
        }

        user.getMembershipCollection(false).get(0).delete();
        // should be able to delete it now
        role.delete();
    }

    @Test
    public void testRemoveFromPermissionCollection() throws Exception {
        String name = "testRemoveFromPermissionCollection" + r.nextInt();
        RoleWrapper role = new RoleWrapper(appService);
        role.setName(name);

        PermissionWrapper rp = new PermissionWrapper(appService);
        rp.setRight(BbRightWrapper.getAllRights(appService).get(0));
        rp.addToPrivilegeCollection(Arrays.asList(PrivilegeWrapper
            .getReadPrivilege(appService)));
        rp.setRole(role);
        role.addToPermissionCollection(Arrays.asList(rp));
        role.persist();

        role.reload();
        Integer idRp = rp.getId();
        role.removeFromPermissionCollection(Arrays.asList(rp));
        role.persist();

        Assert.assertNull(ModelUtils.getObjectWithId(appService,
            Permission.class, idRp));
    }
}
