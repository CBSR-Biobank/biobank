package edu.ualberta.med.biobank.test.wrappers;

import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;
import edu.ualberta.med.biobank.common.wrappers.SourceVesselWrapper;
import edu.ualberta.med.biobank.test.TestDatabase;
import edu.ualberta.med.biobank.test.internal.SiteHelper;
import edu.ualberta.med.biobank.test.internal.SourceVesselHelper;
import gov.nih.nci.system.applicationservice.ApplicationException;

public class TestSourceVessel extends TestDatabase {

    SourceVesselWrapper ssw;
    SiteWrapper defaultSite;

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
        ssw = SourceVesselHelper.addSourceVessel("SourceVesselName");
        defaultSite = SiteHelper.addSite("Default");
    }

    @Test
    public void testCompareTo() throws Exception {
        SourceVesselWrapper newSourceVessel = SourceVesselHelper
            .addSourceVessel(ssw.getName() + "1");
        Assert.assertTrue(newSourceVessel.compareTo(ssw) > 0);
        Assert.assertTrue(ssw.compareTo(newSourceVessel) < 0);
        newSourceVessel.setName(ssw.getName());
        Assert.assertTrue(newSourceVessel.compareTo(ssw) == 0);
    }

    @Test
    public void testResetAlreadyInDatabase() throws Exception {
        String old = ssw.getName();
        ssw.setName("toto");
        ssw.reset();
        Assert.assertEquals(old, ssw.getName());
    }

    @Test
    public void testResetNew() throws Exception {
        SourceVesselWrapper ssw = SourceVesselHelper
            .newSourceVessel("testResetNew");
        ssw.setName("toto");
        ssw.reset();
        Assert.assertEquals(null, ssw.getName());
    }

    @Test
    public void testGetAllSourceVessels() throws ApplicationException {
        List<SourceVesselWrapper> list = SourceVesselWrapper
            .getAllSourceVessels(appService);
        Assert.assertTrue(list.contains(ssw));

    }
}
