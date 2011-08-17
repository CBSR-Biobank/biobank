package edu.ualberta.med.biobank.test.wrappers;

import java.util.List;

import org.junit.Test;

import edu.ualberta.med.biobank.common.wrappers.BbRightWrapper;
import edu.ualberta.med.biobank.common.wrappers.PrivilegeWrapper;
import edu.ualberta.med.biobank.common.wrappers.RightPrivilegeWrapper;
import edu.ualberta.med.biobank.test.TestDatabase;

public class TestRightPrivilege extends TestDatabase {

    @Test
    public void createRightPrivilege() throws Exception {
        List<PrivilegeWrapper> privileges = PrivilegeWrapper
            .getAllPrivileges(appService);
        List<BbRightWrapper> rights = BbRightWrapper.getAllRights(appService);

        RightPrivilegeWrapper rp = new RightPrivilegeWrapper(appService);
        rp.setRight(rights.get(0));
        rp.addToPrivilegeCollection(privileges);

        rp.persist();
    }
}
