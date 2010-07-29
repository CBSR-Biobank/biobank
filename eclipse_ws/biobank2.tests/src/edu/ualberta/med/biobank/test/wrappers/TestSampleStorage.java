package edu.ualberta.med.biobank.test.wrappers;

import java.util.List;

import junit.framework.Assert;

import org.junit.Test;

import edu.ualberta.med.biobank.common.exception.BiobankCheckException;
import edu.ualberta.med.biobank.common.wrappers.ActivityStatusWrapper;
import edu.ualberta.med.biobank.common.wrappers.SampleStorageWrapper;
import edu.ualberta.med.biobank.common.wrappers.SampleTypeWrapper;
import edu.ualberta.med.biobank.common.wrappers.StudyWrapper;
import edu.ualberta.med.biobank.model.SampleStorage;
import edu.ualberta.med.biobank.model.SampleType;
import edu.ualberta.med.biobank.test.TestDatabase;
import edu.ualberta.med.biobank.test.internal.DbHelper;
import edu.ualberta.med.biobank.test.internal.SampleStorageHelper;
import edu.ualberta.med.biobank.test.internal.SampleTypeHelper;
import edu.ualberta.med.biobank.test.internal.StudyHelper;

public class TestSampleStorage extends TestDatabase {

    @Test
    public void testGettersAndSetters() throws Exception {
        String name = "testGettersAndSetters" + r.nextInt();
        StudyWrapper study = StudyHelper.addStudy(name);

        List<SampleTypeWrapper> types = SampleTypeWrapper.getAllSampleTypes(
            appService, false);
        SampleStorageWrapper sampleStorage = SampleStorageHelper
            .addSampleStorage(study, DbHelper.chooseRandomlyInList(types));
        testGettersAndSetters(sampleStorage);
    }

    @Test
    public void testGetSetStudy() throws Exception {
        String name = "testGetSetStudy" + r.nextInt();
        StudyWrapper study = StudyHelper.addStudy(name);

        List<SampleTypeWrapper> types = SampleTypeWrapper.getAllSampleTypes(
            appService, false);
        SampleStorageWrapper sampleStorage = SampleStorageHelper
            .addSampleStorage(study, DbHelper.chooseRandomlyInList(types));

        Assert.assertEquals(study, sampleStorage.getStudy());

        StudyWrapper newStudy = StudyHelper.addStudy(name + "NEW");
        sampleStorage.setStudy(newStudy);
        sampleStorage.persist();

        Assert.assertEquals(newStudy, sampleStorage.getStudy());
        Assert.assertFalse(study.equals(sampleStorage.getStudy()));

        sampleStorage = new SampleStorageWrapper(appService);
        Assert.assertNull(sampleStorage.getStudy());
    }

    @Test
    public void testGetSetSampleType() throws Exception {
        String name = "testGetSetSampleType" + r.nextInt();
        StudyWrapper study = StudyHelper.addStudy(name);

        List<SampleTypeWrapper> types = SampleTypeWrapper.getAllSampleTypes(
            appService, false);
        SampleTypeWrapper type = DbHelper.chooseRandomlyInList(types);
        SampleStorageWrapper sampleStorage = SampleStorageHelper
            .addSampleStorage(study, type);

        Assert.assertEquals(type, sampleStorage.getSampleType());

        SampleTypeWrapper newType = SampleTypeHelper.addSampleType(name);
        sampleStorage.setSampleType(newType);
        sampleStorage.persist();

        Assert.assertEquals(newType, sampleStorage.getSampleType());
        Assert.assertFalse(type.equals(sampleStorage.getSampleType()));

        sampleStorage = new SampleStorageWrapper(appService);
        Assert.assertNull(sampleStorage.getSampleType());
    }

    @Test
    public void testPersist() throws Exception {
        int oldTotal = appService.search(SampleStorage.class,
            new SampleStorage()).size();
        String name = "testPersist" + r.nextInt();
        StudyWrapper study = StudyHelper.addStudy(name);

        List<SampleTypeWrapper> types = SampleTypeWrapper.getAllSampleTypes(
            appService, false);
        SampleStorageHelper.addSampleStorage(study,
            DbHelper.chooseRandomlyInList(types));
        int newTotal = appService.search(SampleStorage.class,
            new SampleStorage()).size();
        Assert.assertEquals(oldTotal + 1, newTotal);
    }

    @Test
    public void testActivityStatus() throws Exception {
        String name = "testPersist" + r.nextInt();
        StudyWrapper study = StudyHelper.addStudy(name);

        List<SampleTypeWrapper> types = SampleTypeWrapper.getAllSampleTypes(
            appService, false);
        SampleStorageWrapper ss = SampleStorageHelper.newSampleStorage(study,
            DbHelper.chooseRandomlyInList(types));
        ss.setActivityStatus(null);

        try {
            ss.persist();
            Assert.fail("Should not be allowed : no activity status");
        } catch (BiobankCheckException bce) {
            Assert.assertTrue(true);
        }

        ss.setActivityStatus(ActivityStatusWrapper.getActivityStatus(
            appService, "Active"));
        ss.persist();
    }

    @Test
    public void testDelete() throws Exception {
        String name = "testDelete" + r.nextInt();
        StudyWrapper study = StudyHelper.addStudy(name);

        List<SampleTypeWrapper> types = SampleTypeWrapper.getAllSampleTypes(
            appService, false);
        SampleStorageWrapper sampleStorage = SampleStorageHelper
            .addSampleStorage(study, DbHelper.chooseRandomlyInList(types));

        // object is in database
        SampleStorage ssInDB = ModelUtils.getObjectWithId(appService,
            SampleStorage.class, sampleStorage.getId());
        Assert.assertNotNull(ssInDB);

        sampleStorage.delete();

        ssInDB = ModelUtils.getObjectWithId(appService, SampleStorage.class,
            sampleStorage.getId());
        // object is not anymore in database
        Assert.assertNull(ssInDB);
    }

    @Test
    public void testResetAlreadyInDatabase() throws Exception {
        String name = "testResetAlreadyInDatabase" + r.nextInt();
        StudyWrapper study = StudyHelper.addStudy(name);

        List<SampleTypeWrapper> types = SampleTypeWrapper.getAllSampleTypes(
            appService, false);
        SampleStorageWrapper sampleStorage = SampleStorageHelper
            .addSampleStorage(study, DbHelper.chooseRandomlyInList(types));

        sampleStorage.reload();
        Double oldVolume = sampleStorage.getVolume();
        sampleStorage.setVolume(6.3);
        sampleStorage.reset();
        Assert.assertEquals(oldVolume, sampleStorage.getVolume());
    }

    @Test
    public void testResetNew() throws Exception {
        SampleStorageWrapper sampleStorage = new SampleStorageWrapper(
            appService);
        sampleStorage.setVolume(5.2);
        sampleStorage.reset();
        Assert.assertEquals(null, sampleStorage.getVolume());
    }

    @Test
    public void testCompareTo() throws Exception {
        String name = "testCompareTo" + r.nextInt();
        StudyWrapper study = StudyHelper.addStudy(name);

        SampleType type = new SampleType();
        type.setName("Plasma");
        type = (SampleType) appService.search(SampleType.class, type).get(0);
        SampleTypeWrapper typeWrapperPlasma = new SampleTypeWrapper(appService,
            type);

        type = new SampleType();
        type.setName("Hair");
        type = (SampleType) appService.search(SampleType.class, type).get(0);
        SampleTypeWrapper typeWrapperHair = new SampleTypeWrapper(appService,
            type);

        SampleStorageWrapper sampleStorage1 = SampleStorageHelper
            .addSampleStorage(study, typeWrapperPlasma);
        SampleStorageWrapper sampleStorage2 = SampleStorageHelper
            .addSampleStorage(study, typeWrapperHair);

        Assert.assertTrue(sampleStorage1.compareTo(sampleStorage2) > 0);
        Assert.assertTrue(sampleStorage2.compareTo(sampleStorage1) < 0);
    }

    // @Test
    // public void testStudyDeleteRemoveSampleStorages() throws Exception {
    // String name = "testStudyDeleteRemoveSampleStorages" + r.nextInt();
    // int nbSampleStorage = appService.search(SampleStorage.class,
    // new SampleStorage()).size();
    // SiteWrapper site = SiteHelper.addSite(name);
    //
    // StudyWrapper study1 = StudyHelper.addStudy(name);
    // List<SampleTypeWrapper> types = SampleTypeWrapper.getGlobalSampleTypes(
    // appService, false);
    // SampleStorageHelper.addSampleStorage(study1, DbHelper
    // .chooseRandomlyInList(types));
    // study1.delete();
    // Assert.assertEquals(nbSampleStorage, appService.search(
    // SampleStorage.class, new SampleStorage()).size());
    //
    // StudyWrapper study = StudyHelper.addStudy("studyname"
    // + r.nextInt());
    // PatientWrapper patient = PatientHelper.addPatient("5684", study);
    // study.persist();
    // SampleStorageHelper.addSampleStorage(study, DbHelper
    // .chooseRandomlyInList(types));
    // study.reload();
    // patient.delete();
    // study.reload();
    // study.delete();
    // // FIXME this test is failing when the study has a patient. Should find
    // // why !
    // //
    // // it seems like hibernate forgets about the cascade setting for
    // // this association . NL
    // Assert.assertEquals(nbSampleStorage, appService.search(
    // SampleStorage.class, new SampleStorage()).size());
    // }
}
