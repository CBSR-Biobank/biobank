package edu.ualberta.med.biobank.test.wrappers;

import java.util.Arrays;

import junit.framework.Assert;

import org.acegisecurity.AccessDeniedException;
import org.junit.Test;

import edu.ualberta.med.biobank.common.exception.BiobankCheckException;
import edu.ualberta.med.biobank.common.wrappers.BbGroupWrapper;
import edu.ualberta.med.biobank.common.wrappers.MembershipRoleWrapper;
import edu.ualberta.med.biobank.common.wrappers.MembershipWrapper;
import edu.ualberta.med.biobank.common.wrappers.RoleWrapper;
import edu.ualberta.med.biobank.common.wrappers.UserWrapper;
import edu.ualberta.med.biobank.model.Site;
import edu.ualberta.med.biobank.model.User;
import edu.ualberta.med.biobank.server.applicationservice.BiobankSecurityUtil;
import edu.ualberta.med.biobank.test.AllTests;
import edu.ualberta.med.biobank.test.TestDatabase;
import edu.ualberta.med.biobank.test.internal.GroupHelper;
import edu.ualberta.med.biobank.test.internal.MembershipHelper;
import edu.ualberta.med.biobank.test.internal.RoleHelper;
import edu.ualberta.med.biobank.test.internal.UserHelper;
import gov.nih.nci.security.SecurityServiceProvider;
import gov.nih.nci.security.UserProvisioningManager;

public class TestUser extends TestDatabase {

    @Test
    public void createUser() throws BiobankCheckException, Exception {
        String name = "createUser" + r.nextInt();
        String password = "123";
        UserWrapper user = UserHelper.addUser(name, password, true);

        // check biobank user
        User dbUser = ModelUtils.getObjectWithId(appService, User.class,
            user.getId());
        Assert.assertNotNull(dbUser);
        Assert.assertEquals(name, dbUser.getLogin());
        Assert.assertNotNull(dbUser.getCsmUserId());

        // check csm user
        UserProvisioningManager upm = SecurityServiceProvider
            .getUserProvisioningManager(BiobankSecurityUtil.APPLICATION_CONTEXT_NAME);

        gov.nih.nci.security.authorization.domainobjects.User csmUser = upm
            .getUser(name);
        Assert.assertNotNull(csmUser);
        Assert.assertNotNull(csmUser.getPassword());
        Assert.assertFalse(csmUser.getPassword().isEmpty());

        // check user can connect
        AllTests.connect(name, password);
        // check user can access a biobank object
        try {
            appService.search(Site.class, new Site());
        } catch (AccessDeniedException ade) {
            Assert.fail("User should be able to access any object");
        }
        // reconnect to testuser
        AllTests.connectTestUser();
    }

    @Test
    public void updateUser() throws BiobankCheckException, Exception {
        String name = "updateUser" + r.nextInt();
        String password = "123";
        UserWrapper user = UserHelper.addUser(name, password, true);

        user.reload();
        Assert.assertNull(user.getEmail());
        String email = "toto@gmail.com";
        user.setEmail(email);
        user.persist();

        user.reload();
        Assert.assertEquals(email, user.getEmail());
    }

    @Test
    public void deleteUser() throws BiobankCheckException, Exception {
        String name = "deleteUser" + r.nextInt();
        UserWrapper user = UserHelper.addUser(name, null, false);

        User dbUser = ModelUtils.getObjectWithId(appService, User.class,
            user.getId());
        Assert.assertNotNull(dbUser);
        UserProvisioningManager upm = SecurityServiceProvider
            .getUserProvisioningManager(BiobankSecurityUtil.APPLICATION_CONTEXT_NAME);
        gov.nih.nci.security.authorization.domainobjects.User csmUser = upm
            .getUser(name);
        Assert.assertNotNull(csmUser);

        user.delete();
        Assert.assertNull(ModelUtils.getObjectWithId(appService, User.class,
            user.getId()));
        csmUser = upm.getUser(name);
        Assert.assertNull(csmUser);
    }

    @Test
    public void testGettersAndSetters() throws BiobankCheckException, Exception {
        String name = "testGettersAndSetters" + r.nextInt();
        UserWrapper user = UserHelper.addUser(name, null, true);
        testGettersAndSetters(user,
            Arrays.asList("getPassword", "getCsmUserId"));
    }

    @Test
    public void deleteWhenHasGroupRelation() throws Exception {
        String name = "deleteWhenHasGroupRelation" + r.nextInt();
        UserWrapper user1 = UserHelper.addUser(name + "_1", null, false);
        UserWrapper user2 = UserHelper.addUser(name + "_2", null, true);

        BbGroupWrapper group = GroupHelper.addGroup(name, true);
        group.addToUserCollection(Arrays.asList(user1, user2));
        group.persist();
        user1.reload();
        user2.reload();

        Assert.assertEquals(2, group.getUserCollection(false).size());

        // deletedependencies should remove the relation in the correlation
        // table
        user1.delete();

        group.reload();
        Assert.assertEquals(1, group.getUserCollection(false).size());
    }

    @Test
    public void addMembershipsWithNoObject() throws Exception {
        String name = "addMembershipsWithNoObject" + r.nextInt();
        UserWrapper user = UserHelper.addUser(name, null, true);

        UserHelper.addMembershipRole(user, null, null);

        user.reload();
        Assert.assertEquals(1, user.getMembershipCollection(false).size());
    }

    @Test
    public void addMembershipsWithRole() throws Exception {
        String name = "addMembershipsWithRole" + r.nextInt();
        UserWrapper user = UserHelper.addUser(name, null, true);

        RoleWrapper role = RoleHelper.addRole(name, true);

        MembershipRoleWrapper mwr = MembershipHelper.newMembershipRole(user,
            null, null);
        mwr.addToRoleCollection(Arrays.asList(role));
        user.persist();

        user.reload();
        Assert.assertEquals(1, user.getMembershipCollection(false).size());
        MembershipWrapper<?> mw = user.getMembershipCollection(false).get(0);
        Assert.assertTrue(mw instanceof MembershipRoleWrapper);
        Assert.assertEquals(1,
            ((MembershipRoleWrapper) mw).getRoleCollection(false).size());
    }
}
