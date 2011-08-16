package edu.ualberta.med.biobank.test.wrappers;

import java.util.Arrays;

import junit.framework.Assert;

import org.junit.Test;

import edu.ualberta.med.biobank.common.wrappers.BbGroupWrapper;
import edu.ualberta.med.biobank.common.wrappers.UserWrapper;
import edu.ualberta.med.biobank.test.TestDatabase;
import edu.ualberta.med.biobank.test.internal.GroupHelper;
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
}
