package edu.ualberta.med.biobank.test.wrappers;

import org.junit.Test;

import edu.ualberta.med.biobank.common.wrappers.BbGroupWrapper;
import edu.ualberta.med.biobank.test.TestDatabase;

public class TestGroup extends TestDatabase {

    @Test
    public void createGroup() throws Exception {
        String name = "createGroup" + r.nextInt();
        BbGroupWrapper group = new BbGroupWrapper(appService);
        group.setName(name);
        group.persist();
    }
}
