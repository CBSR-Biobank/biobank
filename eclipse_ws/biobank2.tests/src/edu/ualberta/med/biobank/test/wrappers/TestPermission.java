package edu.ualberta.med.biobank.test.wrappers;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import edu.ualberta.med.biobank.common.wrappers.BbRightWrapper;
import edu.ualberta.med.biobank.common.wrappers.PermissionWrapper;
import edu.ualberta.med.biobank.common.wrappers.PrivilegeWrapper;
import edu.ualberta.med.biobank.server.applicationservice.exceptions.BiobankSessionException;
import edu.ualberta.med.biobank.test.TestDatabase;

public class TestPermission extends TestDatabase {

    @Test
    public void testCreatePermission() throws Exception {
        List<BbRightWrapper> rights = BbRightWrapper.getAllRights(appService);

        PermissionWrapper rp = new PermissionWrapper(appService);
        BbRightWrapper right = rights.get(0);
        rp.setRight(right);
        rp.addToPrivilegeCollection(right
            .getAvailablePrivilegeCollection(false));

        rp.persist();
    }

    @Test
    public void testCreatePermissionFail() throws Exception {
        List<BbRightWrapper> rights = BbRightWrapper.getAllRights(appService);

        PermissionWrapper rp = new PermissionWrapper(appService);
        BbRightWrapper right = rights.get(0);
        rp.setRight(right);
        rp.addToPrivilegeCollection(PrivilegeWrapper
            .getAllPrivileges(appService));

        try {
            rp.persist();
            Assert
                .fail("Should fail because there are privileges that are not available for the right");
        } catch (BiobankSessionException bse) {
            Assert.assertTrue(true);
        }
    }
}
