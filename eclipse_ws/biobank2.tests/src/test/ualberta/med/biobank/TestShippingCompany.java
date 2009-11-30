package test.ualberta.med.biobank;

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

}
