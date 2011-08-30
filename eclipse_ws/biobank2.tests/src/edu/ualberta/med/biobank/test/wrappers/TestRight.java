package edu.ualberta.med.biobank.test.wrappers;

import org.junit.Assert;
import org.junit.Test;

import edu.ualberta.med.biobank.common.wrappers.BbRightWrapper;
import edu.ualberta.med.biobank.test.TestDatabase;
import edu.ualberta.med.biobank.test.internal.RightHelper;

public class TestRight extends TestDatabase {

    @Test
    public void testGetRightWithKeyDesc() throws Exception {
        String name = "testGetRightWithKeyDesc" + r.nextInt();

        BbRightWrapper r = RightHelper.addRight(name + "Name", name, true);

        BbRightWrapper found = BbRightWrapper.getRightWithKeyDesc(appService,
            name);
        Assert.assertNotNull(found);
        Assert.assertEquals(r, found);
    }
}
