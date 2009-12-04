package test.ualberta.med.biobank;

import java.util.List;

import junit.framework.Assert;

import org.junit.Test;

import test.ualberta.med.biobank.internal.DbHelper;
import test.ualberta.med.biobank.internal.SampleStorageHelper;
import test.ualberta.med.biobank.internal.SampleTypeHelper;
import test.ualberta.med.biobank.internal.SiteHelper;
import test.ualberta.med.biobank.internal.StudyHelper;
import edu.ualberta.med.biobank.common.wrappers.SampleStorageWrapper;
import edu.ualberta.med.biobank.common.wrappers.SampleTypeWrapper;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;
import edu.ualberta.med.biobank.common.wrappers.StudyWrapper;
import edu.ualberta.med.biobank.model.SampleStorage;
import edu.ualberta.med.biobank.model.SampleType;

public class TestSampleStorage extends TestDatabase {

    @Test
    public void testGettersAndSetters() throws Exception {
        String name = "testGettersAndSetters" + r.nextInt();
        SiteWrapper site = SiteHelper.addSite(name);
        StudyWrapper study = StudyHelper.addStudy(site, name);

        List<SampleTypeWrapper> types = SampleTypeWrapper.getGlobalSampleTypes(
            appService, false);
        SampleStorageWrapper sampleStorage = SampleStorageHelper
            .addSampleStorage(study, DbHelper.chooseRandomlyInList(types));
        testGettersAndSetters(sampleStorage);
    }

    @Test
    public void testGetSetStudy() throws Exception {
        String name = "testGetSetStudy" + r.nextInt();
        SiteWrapper site = SiteHelper.addSite(name);
        StudyWrapper study = StudyHelper.addStudy(site, name);

        List<SampleTypeWrapper> types = SampleTypeWrapper.getGlobalSampleTypes(
            appService, false);
        SampleStorageWrapper sampleStorage = SampleStorageHelper
            .addSampleStorage(study, DbHelper.chooseRandomlyInList(types));

        Assert.assertEquals(study, sampleStorage.getStudy());

        StudyWrapper newStudy = StudyHelper.addStudy(site, name + "NEW");
        sampleStorage.setStudy(newStudy);
        sampleStorage.persist();

        Assert.assertEquals(newStudy, sampleStorage.getStudy());
        Assert.assertFalse(study.equals(sampleStorage.getStudy()));
    }

    @Test
    public void testGetSetSampleType() throws Exception {
        String name = "testGetSetSampleType" + r.nextInt();
        SiteWrapper site = SiteHelper.addSite(name);
        StudyWrapper study = StudyHelper.addStudy(site, name);

        List<SampleTypeWrapper> types = SampleTypeWrapper.getGlobalSampleTypes(
            appService, false);
        SampleTypeWrapper type = DbHelper.chooseRandomlyInList(types);
        SampleStorageWrapper sampleStorage = SampleStorageHelper
            .addSampleStorage(study, type);

        Assert.assertEquals(type, sampleStorage.getSampleType());

        SampleTypeWrapper newType = SampleTypeHelper.addSampleType(site, name);
        sampleStorage.setSampleType(newType);
        sampleStorage.persist();

        Assert.assertEquals(newType, sampleStorage.getSampleType());
        Assert.assertFalse(type.equals(sampleStorage.getSampleType()));
    }

    @Test
    public void testPersist() throws Exception {
        int oldTotal = appService.search(SampleStorage.class,
            new SampleStorage()).size();
        String name = "testPersist" + r.nextInt();
        SiteWrapper site = SiteHelper.addSite(name);
        StudyWrapper study = StudyHelper.addStudy(site, name);

        List<SampleTypeWrapper> types = SampleTypeWrapper.getGlobalSampleTypes(
            appService, false);
        SampleStorageHelper.addSampleStorage(study, DbHelper
            .chooseRandomlyInList(types));
        int newTotal = appService.search(SampleStorage.class,
            new SampleStorage()).size();
        Assert.assertEquals(oldTotal + 1, newTotal);
    }

    @Test
    public void testDelete() throws Exception {
        String name = "testDelete" + r.nextInt();
        SiteWrapper site = SiteHelper.addSite(name);
        StudyWrapper study = StudyHelper.addStudy(site, name);

        List<SampleTypeWrapper> types = SampleTypeWrapper.getGlobalSampleTypes(
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
        SiteWrapper site = SiteHelper.addSite(name);
        StudyWrapper study = StudyHelper.addStudy(site, name);

        List<SampleTypeWrapper> types = SampleTypeWrapper.getGlobalSampleTypes(
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
        SiteWrapper site = SiteHelper.addSite(name);
        StudyWrapper study = StudyHelper.addStudy(site, name);

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
}
