package edu.ualberta.med.biobank.test.wrappers;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import edu.ualberta.med.biobank.common.exception.BiobankCheckException;
import edu.ualberta.med.biobank.common.wrappers.ResearchGroupWrapper;
import edu.ualberta.med.biobank.common.wrappers.StudyWrapper;
import edu.ualberta.med.biobank.model.ActivityStatus;
import edu.ualberta.med.biobank.model.ResearchGroup;
import edu.ualberta.med.biobank.model.Study;
import edu.ualberta.med.biobank.server.applicationservice.exceptions.DuplicatePropertySetException;
import edu.ualberta.med.biobank.server.applicationservice.exceptions.ValueNotSetException;
import edu.ualberta.med.biobank.test.TestDatabase;
import edu.ualberta.med.biobank.test.internal.ResearchGroupHelper;
import edu.ualberta.med.biobank.test.internal.StudyHelper;

@Deprecated
public class TestResearchGroup extends TestDatabase {

    @Test
    public void testGettersAndSetters() throws BiobankCheckException, Exception {
        String name = "testGettersAndSetters" + r.nextInt();
        ResearchGroupWrapper rg = ResearchGroupHelper.addResearchGroup(name,
            true);
        testGettersAndSetters(rg);
    }

    @Test
    public void testPersist() throws Exception {
        String name = "testPersist" + r.nextInt();
        int oldTotal = appService.search(ResearchGroup.class,
            new ResearchGroup()).size();
        ResearchGroupHelper.addResearchGroup(name, true);

        int newTotal = appService.search(ResearchGroup.class,
            new ResearchGroup()).size();
        Assert.assertEquals(oldTotal + 1, newTotal);
    }

    @Test
    public void testPersistFailStudyInUse() throws Exception {
        String name = "testPersistFailStudyInUse" + r.nextInt();
        ResearchGroupWrapper rg = ResearchGroupHelper.addResearchGroup(name
            + "_1", true);

        ResearchGroupWrapper researchGroup = ResearchGroupHelper
            .newResearchGroup(name, false);
        //researchGroup.setStudy(rg.getStudy());
        researchGroup.addToStudyCollection(rg.getStudyCollection());
        researchGroup.setName(name);
        researchGroup.setNameShort(name);
        researchGroup.setActivityStatus(ActivityStatus.ACTIVE);

        researchGroup.setCity("Vesoul");
        try {
            researchGroup.persist();
            Assert.fail("Should not use another research group's study");
        } catch (Exception e) {
            Assert.assertTrue(true);
        }
    }

    @Test
    public void testPersistFailAddressNotNull() throws Exception {
        String name = "testPersistFailAddressNotNul" + r.nextInt();
        ResearchGroupHelper.addResearchGroup(name + "_1", true);
        int oldTotal = ResearchGroupWrapper.getAllResearchGroups(appService)
            .size();

        ResearchGroupWrapper researchGroup = new ResearchGroupWrapper(
            appService);
        researchGroup.setName(name);
        researchGroup.setNameShort(name);
        researchGroup.setActivityStatus(ActivityStatus.ACTIVE);
        try {
            researchGroup.persist();
            Assert.fail("Should not insert the researchGroup : no address");
        } catch (ValueNotSetException vnse) {
            Assert.assertTrue(true);
        }

        researchGroup.setCity("Vesoul");
        researchGroup.persist();
        Assert.assertEquals(oldTotal + 1, ResearchGroupWrapper
            .getAllResearchGroups(appService).size());
        researchGroup.delete();
    }

    @Test
    public void testPersistFailActivityStatusNull() throws Exception {
        String name = "testPersistFailActivityStatusNull" + r.nextInt();
        ResearchGroupWrapper researchGroup = new ResearchGroupWrapper(
            appService);
        researchGroup.setName(name);
        researchGroup.setNameShort(name);
        researchGroup.setCity("Rupt");

        try {
            researchGroup.persist();
            Assert
                .fail("Should not insert the researchGroup : no activity status");
        } catch (ValueNotSetException vnse) {
            Assert.assertTrue(true);
        }
        researchGroup.setActivityStatus(ActivityStatus.ACTIVE);
        researchGroup.persist();
        researchGroup.delete();
    }

    @Test
    public void testPersistFailNameUnique() throws Exception {
        String name = "testPersistFailNameUnique" + r.nextInt();
        ResearchGroupHelper.addResearchGroup(name, true);
        int oldTotal = ResearchGroupWrapper.getAllResearchGroups(appService)
            .size();

        ResearchGroupWrapper researchGroup = ResearchGroupHelper
            .newResearchGroup(name, false);
        researchGroup.setNameShort(name + "_NS");
        try {
            researchGroup.persist();
            Assert
                .fail("Should not insert the researchGroup : same name already in database for this site");
        } catch (DuplicatePropertySetException e) {
            Assert.assertTrue(true);
        }
        researchGroup.setName(name + "_otherName");
        researchGroup.persist();
        int newTotal = ResearchGroupWrapper.getAllResearchGroups(appService)
            .size();
        Assert.assertEquals(oldTotal + 1, newTotal);
        researchGroup.delete();
    }

    @Test
    public void testDelete() throws Exception {
        String name = "testDelete" + r.nextInt();
        ResearchGroupWrapper researchGroup = ResearchGroupHelper
            .addResearchGroup(name, false);

        // object is in database
        ResearchGroup researchGroupInDB = ModelUtils.getObjectWithId(
            appService, ResearchGroup.class, researchGroup.getId());
        Assert.assertNotNull(researchGroupInDB);

        researchGroup.delete();

        researchGroupInDB = ModelUtils.getObjectWithId(appService,
            ResearchGroup.class, researchGroup.getId());
        // object is not anymore in database
        Assert.assertNull(researchGroupInDB);
    }

    @Test
    public void testDeleteWithStudy() throws Exception {
        String name = "testDeleteWithStudy" + r.nextInt();
        ResearchGroupWrapper researchGroup = ResearchGroupHelper
            .addResearchGroup(name, false);
        //int studyId = researchGroup.getStudy().getId();
        int studyId = researchGroup.getStudyCollection().get(0).getId();
        Study studyInDB = ModelUtils.getObjectWithId(appService, Study.class,
            studyId);
        Assert.assertNotNull(studyInDB);
        researchGroup.reload();

        researchGroup.delete();

        studyInDB = ModelUtils
            .getObjectWithId(appService, Study.class, studyId);
        Assert.assertNotNull(studyInDB);
    }

    @Test
    public void testResetAlreadyInDatabase() throws Exception {
        String name = "testResetAlreadyInDatabase" + r.nextInt();
        ResearchGroupWrapper researchGroup = ResearchGroupHelper
            .addResearchGroup(name, true);
        researchGroup.reload();
        String oldName = researchGroup.getName();
        researchGroup.setName("toto");
        researchGroup.reset();
        Assert.assertEquals(oldName, researchGroup.getName());
    }

    @Test
    public void testResetNew() throws Exception {
        String name = "testResetAlreadyInDatabase" + r.nextInt();
        ResearchGroupWrapper researchGroup = ResearchGroupHelper
            .newResearchGroup(name, false);
        researchGroup.reset();
        Assert.assertEquals(null, researchGroup.getName());
    }

    @Test
    public void testCompareTo() throws Exception {
        String name = "testCompareTo" + r.nextInt();
        ResearchGroupWrapper researchGroup1 = ResearchGroupHelper
            .addResearchGroup("QWERTY" + name, true);
        ResearchGroupWrapper researchGroup2 = ResearchGroupHelper
            .addResearchGroup("ASDFG" + name, true);

        Assert.assertTrue(researchGroup1.compareTo(researchGroup2) > 0);
        Assert.assertTrue(researchGroup2.compareTo(researchGroup1) < 0);
    }

    @Test
    public void testGetPatientCount() throws Exception {
        // FIXME: this test doesn't make sense for research group
        ResearchGroupWrapper rg = ResearchGroupHelper.addResearchGroup("testz",
            true);
        Assert.assertTrue(rg.getPatientCount() == 0);
    }

    @Test
    public void testGetPatientCountForStudy() throws Exception {
        // FIXME: this test doesn't make sense for research group
        ResearchGroupWrapper rg = ResearchGroupHelper.addResearchGroup("testx",
            true);
        Assert.assertTrue(rg.getPatientCountForStudy(null) == 0);
    }

    @Test
    public void testGetCount() throws Exception {
        String name = "testGetCount" + r.nextInt();
        ResearchGroupHelper.addResearchGroups(name, r.nextInt(10) + 3);
        // don't use the above number, just in case researchGroups of others
        // test cases
        // where not removed
        int total = appService.search(ResearchGroup.class, new ResearchGroup())
            .size();
        Assert.assertEquals(total, ResearchGroupWrapper.getCount(appService));
    }

    @Test
    public void testCollectionEventCount() throws Exception {
        // FIXME: this test doesn't make sense for research group
        ResearchGroupWrapper rg = ResearchGroupHelper.addResearchGroup("testy",
            true);
        Assert.assertTrue(rg.getCollectionEventCount() == 0);
    }

    @Test
    public void testGetAvailStudies() throws Exception {
        List<StudyWrapper> studies = StudyHelper.addStudies("testg", 5);

        //Assert.assertEquals(5, ResearchGroupWrapper.getAvailStudies(appService).size());

        String name = "testGetCount" + r.nextInt();
        ResearchGroupWrapper rg1 = ResearchGroupHelper.newResearchGroup(name,
            true);
        //rg1.setStudy(studies.get(0));
        rg1.addToStudyCollection(studies);
        rg1.persist();

        //Assert.assertEquals(4, ResearchGroupWrapper.getAvailStudies(appService).size());

    }
}
