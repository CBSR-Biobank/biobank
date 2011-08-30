package edu.ualberta.med.biobank.test.wrappers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import edu.ualberta.med.biobank.common.wrappers.BbRightWrapper;
import edu.ualberta.med.biobank.common.wrappers.MembershipRoleWrapper;
import edu.ualberta.med.biobank.common.wrappers.PrivilegeWrapper;
import edu.ualberta.med.biobank.common.wrappers.RightPrivilegeWrapper;
import edu.ualberta.med.biobank.common.wrappers.RoleWrapper;
import edu.ualberta.med.biobank.common.wrappers.UserWrapper;
import edu.ualberta.med.biobank.model.RightPrivilege;
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
    public void testCascadeWithRightPrivilege() throws Exception {
        String name = "testCascadeWithRightPrivilege" + r.nextInt();
        RoleWrapper role = new RoleWrapper(appService);
        role.setName(name);

        List<PrivilegeWrapper> privileges = PrivilegeWrapper
            .getAllPrivileges(appService);
        List<BbRightWrapper> rights = BbRightWrapper.getAllRights(appService);

        List<RightPrivilegeWrapper> rpList = new ArrayList<RightPrivilegeWrapper>();
        for (BbRightWrapper right : rights) {
            // for each right, give all privileges
            RightPrivilegeWrapper rp = new RightPrivilegeWrapper(appService);
            rp.setRight(right);
            rp.addToPrivilegeCollection(privileges);
            rp.setRole(role);
            rpList.add(rp);
        }
        role.addToRightPrivilegeCollection(rpList);
        role.persist();

        // checking cascade worked well:

        // supposed to have same number of rigthPrivilege than number of rights
        Assert.assertEquals(rights.size(),
            role.getRightPrivilegeCollection(false).size());
        List<Integer> rpIdList = new ArrayList<Integer>();
        int nberPriviliges = privileges.size();
        for (RightPrivilegeWrapper rp : role.getRightPrivilegeCollection(false)) {
            // each rightprivilege is supposed to have save number of privileges
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
            RightPrivilege dbRp = ModelUtils.getObjectWithId(appService,
                RightPrivilege.class, id);
            Assert.assertNull(dbRp);
        }
    }

    @Test
    public void testDeleteIsUsedInMembership() throws Exception {
        String name = "addMembershipsWithRole" + r.nextInt();
        UserWrapper user = UserHelper.addUser(name, null, true);

        RoleWrapper role = RoleHelper.addRole(name, false);

        MembershipRoleWrapper mwr = MembershipHelper.newMembershipRole(user,
            null, null);
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
    public void testRemoveFromRightPrivilegeCollection() throws Exception {
        String name = "testRemoveFromRightPrivilegeCollection" + r.nextInt();
        RoleWrapper role = new RoleWrapper(appService);
        role.setName(name);

        RightPrivilegeWrapper rp = new RightPrivilegeWrapper(appService);
        rp.setRight(BbRightWrapper.getAllRights(appService).get(0));
        rp.addToPrivilegeCollection(Arrays.asList(PrivilegeWrapper
            .getReadPrivilege(appService)));
        rp.setRole(role);
        role.addToRightPrivilegeCollection(Arrays.asList(rp));
        role.persist();

        role.reload();
        Integer idRp = rp.getId();
        role.removeFromRightPrivilegeCollection(Arrays.asList(rp));
        role.persist();

        Assert.assertNull(ModelUtils.getObjectWithId(appService,
            RightPrivilege.class, idRp));
    }
}
