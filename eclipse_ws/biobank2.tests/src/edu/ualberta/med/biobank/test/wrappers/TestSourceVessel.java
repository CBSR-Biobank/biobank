package edu.ualberta.med.biobank.test.wrappers;

import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import edu.ualberta.med.biobank.common.wrappers.PatientWrapper;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;
import edu.ualberta.med.biobank.common.wrappers.SourceVesselWrapper;
import edu.ualberta.med.biobank.common.wrappers.StudyWrapper;
import edu.ualberta.med.biobank.test.TestDatabase;
import edu.ualberta.med.biobank.test.Utils;
import edu.ualberta.med.biobank.test.internal.CollectionEventHelper;
import edu.ualberta.med.biobank.test.internal.PatientHelper;
import edu.ualberta.med.biobank.test.internal.ShippingMethodHelper;
import edu.ualberta.med.biobank.test.internal.SiteHelper;
import edu.ualberta.med.biobank.test.internal.SourceVesselHelper;
import edu.ualberta.med.biobank.test.internal.SourceVesselTypeHelper;
import edu.ualberta.med.biobank.test.internal.StudyHelper;
import gov.nih.nci.system.applicationservice.ApplicationException;

public class TestSourceVessel extends TestDatabase {

    SourceVesselWrapper ssw;
    SiteWrapper defaultSite;
    PatientWrapper p1;

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
        StudyWrapper study = StudyHelper.addStudy(Utils.getRandomString(11));
        p1 = PatientHelper.addPatient(Utils.getRandomNumericString(11), study);
        ssw = SourceVesselHelper
            .newSourceVessel(p1, Utils.getRandomDate(), 0.1);
        defaultSite = SiteHelper.addSite("Default");
        CollectionEventHelper.addCollectionEvent(defaultSite,
            ShippingMethodHelper.addShippingMethod(Utils.getRandomString(11)),
            ssw);
        ssw.reload();
    }

    @Test
    public void testCompareTo() throws Exception {
        Assert.assertTrue(ssw.compareTo(ssw) == 0);
    }

    @Test
    public void testResetAlreadyInDatabase() throws Exception {
        String old = ssw.getSourceVesselType().getName();
        ssw.setSourceVesselType(SourceVesselTypeHelper
            .addSourceVesselType(Utils.getRandomString(11)));
        ssw.reset();
        Assert.assertEquals(old, ssw.getSourceVesselType().getName());
    }

    @Test
    public void testResetNew() throws Exception {
        SourceVesselWrapper ssw = SourceVesselHelper.newSourceVessel(p1,
            Utils.getRandomDate(), 0.1);
        ssw.reset();
        Assert.assertEquals(null, ssw.getSourceVesselType().getName());
    }

    @Test
    public void testGetAllSourceVessels() throws ApplicationException {
        List<SourceVesselWrapper> list = SourceVesselWrapper
            .getAllSourceVessels(appService);
        Assert.assertTrue(list.contains(ssw));

    }
}
