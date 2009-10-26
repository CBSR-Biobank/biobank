package test.ualberta.med.biobank;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import test.ualberta.med.biobank.internal.SampleSourceHelper;
import test.ualberta.med.biobank.internal.SiteHelper;
import test.ualberta.med.biobank.internal.StudyHelper;
import edu.ualberta.med.biobank.common.wrappers.SampleSourceWrapper;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;
import edu.ualberta.med.biobank.common.wrappers.StudyWrapper;
import edu.ualberta.med.biobank.model.Study;

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
    public void TestGetSetStudyCollection() throws Exception {
        List<StudyWrapper> studies = new ArrayList<StudyWrapper>();
        List<StudyWrapper> oldStudies = new ArrayList<StudyWrapper>(studies);

        // simple add
        StudyWrapper newStudy = StudyHelper.addStudy(defaultSite, "newStudy");
        studies.add(newStudy);
        ssw.setStudyCollection(new ArrayList<StudyWrapper>(studies));
        ssw.persist();
        // check if study is attached in db and compare with get
        Study dbStudy = ModelUtils.getObjectWithId(appService, Study.class,
            newStudy.getId());
        studies = new ArrayList<StudyWrapper>(ssw.getStudyCollection(false));
        boolean found = false;
        for (StudyWrapper study : studies) {
            if (study.getId().equals(dbStudy.getId()))
                found = true;
        }
        Assert.assertTrue(found);

        // simple delete

        studies.remove(studies.size() - 1);
        ssw.setStudyCollection(studies);
        ssw.persist();

        for (int i = 0; i < studies.size(); i++) {
            Assert.assertTrue(studies.get(i).getId().equals(
                oldStudies.get(i).getId()));
        }

        // add three, delete middle

        StudyWrapper newStudy2 = StudyHelper.addStudy(defaultSite, "newStudy2");
        StudyWrapper newStudy3 = StudyHelper.addStudy(defaultSite, "newStudy3");

        int middle = studies.size() + 1;
        studies.add(newStudy);
        studies.add(newStudy2);
        studies.add(newStudy3);

        ssw.setStudyCollection(studies);
        ssw.persist();
        studies.remove(middle);
        ssw.setStudyCollection(studies);
        ssw.persist();
        studies = new ArrayList<StudyWrapper>(ssw.getStudyCollection(true));

        for (int i = 0; i < studies.size() - 2; i++) {
            Assert.assertTrue(studies.get(i).getId().equals(
                oldStudies.get(i).getId()));
        }
        Assert.assertTrue(studies.get(studies.size() - 2).getId().equals(
            newStudy.getId()));
        Assert.assertTrue(studies.get(studies.size() - 1).getId().equals(
            newStudy3.getId()));
    }

    @Test
    public void TestCompareTo() throws Exception {
        SampleSourceWrapper newSampleSource = SampleSourceHelper
            .addSampleSource(ssw.getName() + "1");
        Assert.assertTrue(newSampleSource.compareTo(ssw) > 0);
        Assert.assertTrue(ssw.compareTo(newSampleSource) < 0);
        newSampleSource.setName(ssw.getName());
        Assert.assertTrue(newSampleSource.compareTo(ssw) == 0);
    }
}
