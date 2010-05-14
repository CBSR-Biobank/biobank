package edu.ualberta.med.biobank.test.wrappers;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import edu.ualberta.med.biobank.common.BiobankCheckException;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;
import edu.ualberta.med.biobank.common.wrappers.SourceVesselWrapper;
import edu.ualberta.med.biobank.common.wrappers.StudySourceVesselWrapper;
import edu.ualberta.med.biobank.common.wrappers.StudyWrapper;
import edu.ualberta.med.biobank.model.StudySourceVessel;
import edu.ualberta.med.biobank.test.TestDatabase;
import edu.ualberta.med.biobank.test.internal.SiteHelper;
import edu.ualberta.med.biobank.test.internal.SourceVesselHelper;
import edu.ualberta.med.biobank.test.internal.StudyHelper;
import edu.ualberta.med.biobank.test.internal.StudySourceVesselHelper;

public class TestStudySourceVessel extends TestDatabase {

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
    }

    @Test
    public void testGetSetSourceVessel() throws Exception {
        String name = "testGetSetSourceVessel" + r.nextInt();
        StudySourceVesselWrapper ssv = new StudySourceVesselWrapper(appService);
        Assert.assertNull(ssv.getSourceVessel());

        ssv.setSourceVessel(null);
        Assert.assertNull(ssv.getSourceVessel());

        SiteWrapper site = SiteHelper.addSite(name);
        StudyWrapper study = StudyHelper.addStudy(site, name);

        SourceVesselWrapper sourceVessel = SourceVesselHelper
            .addSourceVessel(name);
        ssv = StudySourceVesselHelper.addStudySourceVessel(study, sourceVessel);

        ssv.reload();
        Assert.assertEquals(sourceVessel, ssv.getSourceVessel());

        SourceVesselWrapper sourceVessel2 = SourceVesselHelper
            .addSourceVessel(name);
        ssv.setSourceVessel(sourceVessel2);
        ssv.persist();

        ssv.reload();
        Assert.assertEquals(sourceVessel2, ssv.getSourceVessel());
    }

    @Test
    public void testGetSetStudy() throws Exception {
        String name = "testGetSetStudy" + r.nextInt();
        SiteWrapper site = SiteHelper.addSite(name);
        StudyWrapper study = StudyHelper.addStudy(site, name);

        SourceVesselWrapper sourceVessel = SourceVesselHelper
            .addSourceVessel(name);
        StudySourceVesselWrapper ssv = StudySourceVesselHelper
            .addStudySourceVessel(study, sourceVessel);

        ssv.reload();
        Assert.assertEquals(study, ssv.getStudy());

        StudyWrapper study2 = StudyHelper.addStudy(site, name + "_2");
        ssv.setStudy(study2);
        ssv.persist();

        ssv.reload();
        Assert.assertEquals(study2, ssv.getStudy());
    }

    @Test
    public void testBasicGettersAndSetters() throws BiobankCheckException,
        Exception {
        String name = "testBasicGettersAndSetters" + r.nextInt();
        SiteWrapper site = SiteHelper.addSite(name);
        StudyWrapper study = StudyHelper.addStudy(site, name);

        SourceVesselWrapper sourceVessel = SourceVesselHelper
            .addSourceVessel(name);
        StudySourceVesselWrapper ssv = StudySourceVesselHelper
            .addStudySourceVessel(study, sourceVessel);

        testGettersAndSetters(ssv);
    }

    @Test
    public void testCompareTo() throws Exception {
        String name = "testCompareTo" + r.nextInt();

        SiteWrapper site = SiteHelper.addSite(name);
        StudyWrapper study = StudyHelper.addStudy(site, name);

        SourceVesselWrapper ss1 = SourceVesselHelper.addSourceVessel("QWERTY"
            + name);
        StudySourceVesselWrapper ssv1 = StudySourceVesselHelper
            .addStudySourceVessel(study, ss1);

        SourceVesselWrapper ss2 = SourceVesselHelper.addSourceVessel("ASDFG"
            + name);
        StudySourceVesselWrapper ssv2 = StudySourceVesselHelper
            .addStudySourceVessel(study, ss2);

        Assert.assertTrue(ssv1.compareTo(ssv2) > 0);
        Assert.assertTrue(ssv2.compareTo(ssv1) < 0);
    }

    @Test
    public void testResetAlreadyInDatabase() throws Exception {
        String name = "testResetAlreadyInDatabase" + r.nextInt();

        SiteWrapper site = SiteHelper.addSite(name);
        StudyWrapper study = StudyHelper.addStudy(site, name);

        SourceVesselWrapper ss1 = SourceVesselHelper.addSourceVessel(name);
        StudySourceVesselWrapper ssv1 = StudySourceVesselHelper
            .newStudySourceVessel(study, ss1);
        ssv1.setNeedOriginalVolume(true);
        ssv1.persist();

        Boolean oldNeedVolume = ssv1.getNeedOriginalVolume();
        ssv1.setNeedOriginalVolume(false);
        ssv1.reset();
        Assert.assertEquals(oldNeedVolume, ssv1.getNeedOriginalVolume());
    }

    @Test
    public void testResetNew() throws Exception {
        String name = "testResetNew" + r.nextInt();
        SiteWrapper site = SiteHelper.addSite(name);
        StudyWrapper study = StudyHelper.addStudy(site, name);

        SourceVesselWrapper ss1 = SourceVesselHelper.addSourceVessel(name);
        StudySourceVesselWrapper ssv1 = StudySourceVesselHelper
            .newStudySourceVessel(study, ss1);

        Boolean oldNeedVolume = ssv1.getNeedOriginalVolume();
        ssv1.setNeedOriginalVolume(false);
        ssv1.reset();
        Assert.assertEquals(oldNeedVolume, ssv1.getNeedOriginalVolume());
    }

    @Test
    public void testDelete() throws Exception {
        String name = "testDelete" + r.nextInt();
        SiteWrapper site = SiteHelper.addSite(name);
        StudyWrapper study = StudyHelper.addStudy(site, name);

        SourceVesselWrapper ss1 = SourceVesselHelper.addSourceVessel(name);
        StudySourceVesselWrapper ssv1 = StudySourceVesselHelper
            .addStudySourceVessel(study, ss1);

        int count = appService.search(StudySourceVessel.class,
            new StudySourceVessel()).size();
        ssv1.delete();
        int countAfter = appService.search(StudySourceVessel.class,
            new StudySourceVessel()).size();
        Assert.assertEquals(count - 1, countAfter);
    }
}
