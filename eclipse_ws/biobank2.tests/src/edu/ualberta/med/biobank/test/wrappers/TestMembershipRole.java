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
import edu.ualberta.med.biobank.test.TestDatabase;
import edu.ualberta.med.biobank.test.internal.MembershipHelper;
import edu.ualberta.med.biobank.test.internal.RightHelper;
import edu.ualberta.med.biobank.test.internal.RoleHelper;
import edu.ualberta.med.biobank.test.internal.UserHelper;

public class TestMembershipRole extends TestDatabase {

    @Test
    public void testGetPrivilegesForRight() throws Exception {
        String name = "testGetPrivilegesForRight" + r.nextInt();
        UserWrapper user = UserHelper.addUser(name, null, true);

        RoleWrapper role = RoleHelper.newRole(name);
        List<PrivilegeWrapper> privileges = PrivilegeWrapper
            .getAllPrivileges(appService);
        BbRightWrapper right = RightHelper.addRight(name, name, true);
        // for each right, give all privileges
        RightPrivilegeWrapper rp = new RightPrivilegeWrapper(appService);
        rp.setRight(right);
        rp.addToPrivilegeCollection(privileges);
        rp.setRole(role);
        role.addToRightPrivilegeCollection(Arrays.asList(rp));
        role.persist();
        RoleHelper.createdRoles.add(role);

        MembershipRoleWrapper mwr = MembershipHelper.newMembershipRole(user,
            null, null);
        mwr.addToRoleCollection(Arrays.asList(role));
        user.persist();

        user.reload();
        mwr.persist();
        List<PrivilegeWrapper> privilegesForRight = mwr
            .getPrivilegesForRight(right);
        Assert.assertEquals(privileges.size(), privilegesForRight.size());
    }
}
