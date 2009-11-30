package test.ualberta.med.biobank;

import junit.framework.Assert;

import org.junit.Test;

import test.ualberta.med.biobank.internal.ShippingCompanyHelper;
import edu.ualberta.med.biobank.common.wrappers.ShippingCompanyWrapper;

public class TestShippingCompany extends TestDatabase {
    @Test
    public void testGettersAndSetters() throws Exception {
        String name = "testGettersAndSetters" + r.nextInt();

        ShippingCompanyWrapper company = ShippingCompanyHelper
            .addShippingCompany(name);
        testGettersAndSetters(company);
    }

    @Test
    public void testGetSetShipmentCollection() {
        Assert.fail("not implemented yet");
    }

    @Test
    public void testPersist() {
        Assert.fail("not implemented yet");
    }

    @Test
    public void testDelete() {
        Assert.fail("not implemented yet");
    }

    @Test
    public void testDeleteFail() {
        Assert.fail("not implemented yet");
    }
}
