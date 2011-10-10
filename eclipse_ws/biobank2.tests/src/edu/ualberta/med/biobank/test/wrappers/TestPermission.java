package edu.ualberta.med.biobank.test.wrappers;

import org.junit.Test;

import edu.ualberta.med.biobank.common.wrappers.PermissionWrapper;
import edu.ualberta.med.biobank.test.TestDatabase;

public class TestPermission extends TestDatabase {

    @Test
    public void testCreatePermission() throws Exception {
        String name = "testCreatePermission" + r.nextInt();
        PermissionWrapper rp = new PermissionWrapper(appService);
        rp.setClassName(name);

        rp.persist();
    }
}
