package edu.ualberta.med.biobank.test.wrappers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import edu.ualberta.med.biobank.common.wrappers.SampleSourceWrapper;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;
import edu.ualberta.med.biobank.common.wrappers.StudyWrapper;
import edu.ualberta.med.biobank.test.TestDatabase;
import edu.ualberta.med.biobank.test.internal.SampleSourceHelper;
import edu.ualberta.med.biobank.test.internal.SiteHelper;
import edu.ualberta.med.biobank.test.internal.StudyHelper;
import gov.nih.nci.system.applicationservice.ApplicationException;

public class TestSampleSource extends TestDatabase {

    SampleSourceWrapper ssw;
    SiteWrapper defaultSite;

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
        ssw = SampleSourceHelper.addSampleSource("SampleSourceName");
        defaultSite = SiteHelper.addSite("Default");
    }

    @Test
    public void testGetStudyCollection() throws Exception {
        List<StudyWrapper> studies = new ArrayList<StudyWrapper>();
        for (int i = 0; i < 3; i++) {
            StudyWrapper newStudy = StudyHelper.newStudy(defaultSite,
                "newStudy" + i);
            newStudy.addSampleSources(Arrays.asList(ssw));
            newStudy.persist();
            studies.add(newStudy);
        }
        ssw.reload();
        List<StudyWrapper> foundStudies = new ArrayList<StudyWrapper>(ssw
            .getStudyCollection(true));
        Assert.assertEquals(studies.size(), foundStudies.size());
        for (StudyWrapper study : studies) {
            Assert.assertTrue(foundStudies.contains(study));
        }
    }

    @Test
    public void testCompareTo() throws Exception {
        SampleSourceWrapper newSampleSource = SampleSourceHelper
            .addSampleSource(ssw.getName() + "1");
        Assert.assertTrue(newSampleSource.compareTo(ssw) > 0);
        Assert.assertTrue(ssw.compareTo(newSampleSource) < 0);
        newSampleSource.setName(ssw.getName());
        Assert.assertTrue(newSampleSource.compareTo(ssw) == 0);
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
        SampleSourceWrapper ssw = SampleSourceHelper
            .newSampleSource("testResetNew");
        ssw.setName("toto");
        ssw.reset();
        Assert.assertEquals(null, ssw.getName());
    }

    @Test
    public void testGetAllSampleSources() throws ApplicationException {
        List<SampleSourceWrapper> list = SampleSourceWrapper
            .getAllSampleSources(appService);
        Assert.assertTrue(list.contains(ssw));

    }
}
