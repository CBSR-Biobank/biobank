package edu.ualberta.med.biobank.test.wrappers;

import org.junit.Assert;
import org.junit.Test;

import edu.ualberta.med.biobank.common.exception.BiobankCheckException;
import edu.ualberta.med.biobank.common.wrappers.SourceVesselTypeWrapper;
import edu.ualberta.med.biobank.common.wrappers.StudySourceVesselWrapper;
import edu.ualberta.med.biobank.common.wrappers.StudyWrapper;
import edu.ualberta.med.biobank.model.StudySourceVessel;
import edu.ualberta.med.biobank.test.TestDatabase;
import edu.ualberta.med.biobank.test.Utils;
import edu.ualberta.med.biobank.test.internal.SourceVesselTypeHelper;
import edu.ualberta.med.biobank.test.internal.StudyHelper;
import edu.ualberta.med.biobank.test.internal.StudySourceVesselHelper;

public class TestStudySourceVessel extends TestDatabase {

    @Test
    public void testBasicGettersAndSetters() throws BiobankCheckException,
        Exception {
        String name = "testBasicGettersAndSetters" + r.nextInt();
        StudyWrapper study = StudyHelper.addStudy(name);

        SourceVesselTypeWrapper sourceVessel = SourceVesselTypeHelper
            .addSourceVesselType(name);
        StudySourceVesselWrapper ssv = StudySourceVesselHelper
            .addStudySourceVessel(study, sourceVessel, true, true);

        testGettersAndSetters(ssv);
    }

    @Test
    public void testGetSetSourceVessel() throws Exception {
        String name = "testGetSetSourceVessel" + r.nextInt();
        StudySourceVesselWrapper ssv = new StudySourceVesselWrapper(appService);
        Assert.assertNull(ssv.getSourceVesselType());

        ssv.setSourceVesselType(null);
        Assert.assertNull(ssv.getSourceVesselType());

        StudyWrapper study = StudyHelper.addStudy(name);

        SourceVesselTypeWrapper svType = SourceVesselTypeHelper
            .addSourceVesselType("newST" + Utils.getRandomString(11));
        ssv = StudySourceVesselHelper.addStudySourceVessel(study, svType, true,
            true);

        ssv.reload();
        Assert.assertEquals(svType, ssv.getSourceVesselType());
    }

    @Test
    public void testGetSetStudy() throws Exception {
        String name = "testGetSetStudy" + r.nextInt();
        StudyWrapper study = StudyHelper.addStudy(name);

        SourceVesselTypeWrapper svType = SourceVesselTypeHelper
            .addSourceVesselType(name);
        StudySourceVesselWrapper ssv = StudySourceVesselHelper
            .addStudySourceVessel(study, svType, true, true);

        ssv.reload();
        Assert.assertEquals(study, ssv.getStudy());

        StudyWrapper study2 = StudyHelper.addStudy(name + "_2");
        ssv.setStudy(study2);
        ssv.persist();

        ssv.reload();
        Assert.assertEquals(study2, ssv.getStudy());
    }

    @Test
    public void testCompareTo() throws Exception {
        String name = "testCompareTo" + r.nextInt();

        StudyWrapper study = StudyHelper.addStudy(name);

        SourceVesselTypeWrapper svType1 = SourceVesselTypeHelper
            .addSourceVesselType("A" + name);
        StudySourceVesselWrapper ssv1 = StudySourceVesselHelper
            .addStudySourceVessel(study, svType1, true, true);

        SourceVesselTypeWrapper svType2 = SourceVesselTypeHelper
            .addSourceVesselType("B" + name);
        StudySourceVesselWrapper ssv2 = StudySourceVesselHelper
            .addStudySourceVessel(study, svType2, true, true);

        Assert.assertTrue(ssv1.compareTo(ssv2) < 0);
        Assert.assertTrue(ssv2.compareTo(ssv1) > 0);
    }

    @Test
    public void testResetAlreadyInDatabase() throws Exception {
        String name = "testResetAlreadyInDatabase" + r.nextInt();

        StudyWrapper study = StudyHelper.addStudy(name);

        SourceVesselTypeWrapper ss1 = SourceVesselTypeHelper
            .addSourceVesselType(name);
        StudySourceVesselWrapper ssv1 = StudySourceVesselHelper
            .newStudySourceVessel(study, ss1, true, true);
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
        StudyWrapper study = StudyHelper.addStudy(name);

        SourceVesselTypeWrapper svType1 = SourceVesselTypeHelper
            .addSourceVesselType(name);
        StudySourceVesselWrapper ssv1 = StudySourceVesselHelper
            .newStudySourceVessel(study, svType1, true, true);

        ssv1.reset();
        Assert.assertEquals(null, ssv1.getNeedOriginalVolume());
        Assert.assertEquals(null, ssv1.getNeedTimeDrawn());
    }

    @Test
    public void testDelete() throws Exception {
        String name = "testDelete" + r.nextInt();
        StudyWrapper study = StudyHelper.addStudy(name);

        SourceVesselTypeWrapper ss1 = SourceVesselTypeHelper
            .addSourceVesselType(name);
        StudySourceVesselWrapper ssv1 = StudySourceVesselHelper
            .addStudySourceVessel(study, ss1, true, true);

        int count = appService.search(StudySourceVessel.class,
            new StudySourceVessel()).size();
        ssv1.delete();
        int countAfter = appService.search(StudySourceVessel.class,
            new StudySourceVessel()).size();
        Assert.assertEquals(count - 1, countAfter);
    }
}
