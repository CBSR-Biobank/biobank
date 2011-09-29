package edu.ualberta.med.biobank.test.wrappers;

import java.util.Arrays;

import org.junit.Assert;
import org.junit.Test;

import edu.ualberta.med.biobank.common.wrappers.MembershipWrapper;
import edu.ualberta.med.biobank.common.wrappers.RoleWrapper;
import edu.ualberta.med.biobank.common.wrappers.UserWrapper;
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

}
