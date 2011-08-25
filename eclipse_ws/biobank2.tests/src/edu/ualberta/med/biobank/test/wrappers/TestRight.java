package edu.ualberta.med.biobank.test.wrappers;

import org.junit.Assert;
import org.junit.Test;

import edu.ualberta.med.biobank.common.exception.BiobankFailedQueryException;
import edu.ualberta.med.biobank.common.wrappers.BbRightWrapper;
import edu.ualberta.med.biobank.test.TestDatabase;
import edu.ualberta.med.biobank.test.internal.RightHelper;

public class TestRight extends TestDatabase {

    @Test
    public void testGetRightWithKeyDesc() throws Exception {
        String name = "testGetRightWithKeyDesc" + r.nextInt();

        BbRightWrapper r = RightHelper.addRight(name + "Name", name, false);

        BbRightWrapper found = BbRightWrapper.getRightWithKeyDesc(appService,
            name);
        Assert.assertNotNull(found);
        Assert.assertEquals(r, found);

        found.delete();
        try {
            found = BbRightWrapper.getRightWithKeyDesc(appService, name);
            Assert
                .fail("exception should be thrownsince this right doesn't exists anymore");
        } catch (BiobankFailedQueryException bfqe) {
            Assert.assertTrue("exception should be thrown", true);
        }
    }
}
