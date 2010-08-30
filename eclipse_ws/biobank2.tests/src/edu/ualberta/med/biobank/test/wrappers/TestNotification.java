package edu.ualberta.med.biobank.test.wrappers;

import org.junit.Test;

import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;
import edu.ualberta.med.biobank.test.TestDatabase;
import edu.ualberta.med.biobank.test.internal.SiteHelper;

public class TestNotification extends TestDatabase {

    @Test
    public void testGettersAndSetters() throws Exception {
        String name = "testGettersAndSetters" + r.nextInt();
        SiteWrapper site = SiteHelper.addSite(name);

    }

    @Test
    public void testCompatreTo() throws Exception {

    }

    @Test
    public void testReset() throws Exception {

    }

    @Test
    public void testReload() throws Exception {

    }

    @Test
    public void testGetWrappedClass() throws Exception {

    }

    @Test
    public void testDelete() throws Exception {

    }

    @Test
    public void testPersist() throws Exception {

    }
}
