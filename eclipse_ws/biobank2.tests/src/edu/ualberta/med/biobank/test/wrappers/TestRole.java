package edu.ualberta.med.biobank.test.wrappers;

import org.junit.Test;

import edu.ualberta.med.biobank.common.wrappers.RoleWrapper;
import edu.ualberta.med.biobank.test.TestDatabase;
import edu.ualberta.med.biobank.test.internal.RoleHelper;

public class TestRole extends TestDatabase {

    @Test
    public void testGettersAndSetters() throws Exception {
        String name = "testGettersAndSetters" + r.nextInt();
        RoleWrapper role = RoleHelper.addRole(name, true);
        testGettersAndSetters(role);
    }
}
