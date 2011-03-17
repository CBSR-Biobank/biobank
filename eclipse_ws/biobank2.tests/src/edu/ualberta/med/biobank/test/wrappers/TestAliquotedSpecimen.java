package edu.ualberta.med.biobank.test.wrappers;

import java.util.List;

import junit.framework.Assert;

import org.junit.Test;

import edu.ualberta.med.biobank.common.wrappers.ActivityStatusWrapper;
import edu.ualberta.med.biobank.common.wrappers.AliquotedSpecimenWrapper;
import edu.ualberta.med.biobank.common.wrappers.PatientWrapper;
import edu.ualberta.med.biobank.common.wrappers.SpecimenTypeWrapper;
import edu.ualberta.med.biobank.common.wrappers.StudyWrapper;
import edu.ualberta.med.biobank.model.AliquotedSpecimen;
import edu.ualberta.med.biobank.model.SpecimenType;
import edu.ualberta.med.biobank.server.applicationservice.exceptions.ValueNotSetException;
import edu.ualberta.med.biobank.test.TestDatabase;
import edu.ualberta.med.biobank.test.internal.AliquotedSpecimenHelper;
import edu.ualberta.med.biobank.test.internal.DbHelper;
import edu.ualberta.med.biobank.test.internal.PatientHelper;
import edu.ualberta.med.biobank.test.internal.SpecimenTypeHelper;
import edu.ualberta.med.biobank.test.internal.StudyHelper;

public class TestAliquotedSpecimen extends TestDatabase {

    @Test
    public void testGettersAndSetters() throws Exception {
        String name = "testGettersAndSetters" + r.nextInt();
        StudyWrapper study = StudyHelper.addStudy(name);

        List<SpecimenTypeWrapper> types = SpecimenTypeWrapper
            .getAllSpecimenTypes(appService, false);
        AliquotedSpecimenWrapper sampleStorage = AliquotedSpecimenHelper
            .addAliquotedSpecimen(study, DbHelper.chooseRandomlyInList(types));
        testGettersAndSetters(sampleStorage);
    }

    @Test
    public void testGetSetStudy() throws Exception {
        String name = "testGetSetStudy" + r.nextInt();
        StudyWrapper study = StudyHelper.addStudy(name);

        List<SpecimenTypeWrapper> types = SpecimenTypeWrapper
            .getAllSpecimenTypes(appService, false);
        AliquotedSpecimenWrapper sampleStorage = AliquotedSpecimenHelper
            .addAliquotedSpecimen(study, DbHelper.chooseRandomlyInList(types));

        Assert.assertEquals(study, sampleStorage.getStudy());

        StudyWrapper newStudy = StudyHelper.addStudy(name + "NEW");
        sampleStorage.setStudy(newStudy);
        sampleStorage.persist();

        Assert.assertEquals(newStudy, sampleStorage.getStudy());
        Assert.assertFalse(study.equals(sampleStorage.getStudy()));

        sampleStorage = new AliquotedSpecimenWrapper(appService);
        Assert.assertNull(sampleStorage.getStudy());
    }

    @Test
    public void testGetSetSpecimenType() throws Exception {
        String name = "testGetSetSpecimenType" + r.nextInt();
        StudyWrapper study = StudyHelper.addStudy(name);

        List<SpecimenTypeWrapper> types = SpecimenTypeWrapper
            .getAllSpecimenTypes(appService, false);
        SpecimenTypeWrapper type = DbHelper.chooseRandomlyInList(types);
        AliquotedSpecimenWrapper sampleStorage = AliquotedSpecimenHelper
            .addAliquotedSpecimen(study, type);

        Assert.assertEquals(type, sampleStorage.getSpecimenType());

        SpecimenTypeWrapper newType = SpecimenTypeHelper.addSpecimenType(name);
        sampleStorage.setSpecimenType(newType);
        sampleStorage.persist();

        Assert.assertEquals(newType, sampleStorage.getSpecimenType());
        Assert.assertFalse(type.equals(sampleStorage.getSpecimenType()));

        sampleStorage = new AliquotedSpecimenWrapper(appService);
        Assert.assertNull(sampleStorage.getSpecimenType());
    }

    @Test
    public void testPersist() throws Exception {
        int oldTotal = appService.search(AliquotedSpecimen.class,
            new AliquotedSpecimen()).size();
        String name = "testPersist" + r.nextInt();
        StudyWrapper study = StudyHelper.addStudy(name);

        List<SpecimenTypeWrapper> types = SpecimenTypeWrapper
            .getAllSpecimenTypes(appService, false);
        AliquotedSpecimenHelper.addAliquotedSpecimen(study,
            DbHelper.chooseRandomlyInList(types));
        int newTotal = appService.search(AliquotedSpecimen.class,
            new AliquotedSpecimen()).size();
        Assert.assertEquals(oldTotal + 1, newTotal);
    }

    @Test
    public void testActivityStatus() throws Exception {
        String name = "testPersist" + r.nextInt();
        StudyWrapper study = StudyHelper.addStudy(name);

        List<SpecimenTypeWrapper> types = SpecimenTypeWrapper
            .getAllSpecimenTypes(appService, false);
        AliquotedSpecimenWrapper ss = AliquotedSpecimenHelper
            .newAliquotedSpecimen(study, DbHelper.chooseRandomlyInList(types));
        ss.setActivityStatus(null);

        try {
            ss.persist();
            Assert.fail("Should not be allowed : no activity status");
        } catch (ValueNotSetException e) {
            Assert.assertTrue(true);
        }

        ss.setActivityStatus(ActivityStatusWrapper
            .getActiveActivityStatus(appService));
        ss.persist();
    }

    @Test
    public void testDelete() throws Exception {
        String name = "testDelete" + r.nextInt();
        StudyWrapper study = StudyHelper.addStudy(name);

        List<SpecimenTypeWrapper> types = SpecimenTypeWrapper
            .getAllSpecimenTypes(appService, false);
        AliquotedSpecimenWrapper sampleStorage = AliquotedSpecimenHelper
            .addAliquotedSpecimen(study, DbHelper.chooseRandomlyInList(types));

        // object is in database
        AliquotedSpecimen ssInDB = ModelUtils.getObjectWithId(appService,
            AliquotedSpecimen.class, sampleStorage.getId());
        Assert.assertNotNull(ssInDB);

        sampleStorage.delete();

        ssInDB = ModelUtils.getObjectWithId(appService,
            AliquotedSpecimen.class, sampleStorage.getId());
        // object is not anymore in database
        Assert.assertNull(ssInDB);
    }

    @Test
    public void testResetAlreadyInDatabase() throws Exception {
        String name = "testResetAlreadyInDatabase" + r.nextInt();
        StudyWrapper study = StudyHelper.addStudy(name);

        List<SpecimenTypeWrapper> types = SpecimenTypeWrapper
            .getAllSpecimenTypes(appService, false);
        AliquotedSpecimenWrapper sampleStorage = AliquotedSpecimenHelper
            .addAliquotedSpecimen(study, DbHelper.chooseRandomlyInList(types));

        sampleStorage.reload();
        Double oldVolume = sampleStorage.getVolume();
        sampleStorage.setVolume(6.3);
        sampleStorage.reset();
        Assert.assertEquals(oldVolume, sampleStorage.getVolume());
    }

    @Test
    public void testResetNew() throws Exception {
        AliquotedSpecimenWrapper sampleStorage = new AliquotedSpecimenWrapper(
            appService);
        sampleStorage.setVolume(5.2);
        sampleStorage.reset();
        Assert.assertEquals(null, sampleStorage.getVolume());
    }

    @Test
    public void testCompareTo() throws Exception {
        String name = "testCompareTo" + r.nextInt();
        StudyWrapper study = StudyHelper.addStudy(name);

        SpecimenType type = new SpecimenType();
        type.setName("Plasma");
        type = (SpecimenType) appService.search(SpecimenType.class, type)
            .get(0);
        SpecimenTypeWrapper typeWrapperPlasma = new SpecimenTypeWrapper(
            appService, type);

        type = new SpecimenType();
        type.setName("Hair");
        type = (SpecimenType) appService.search(SpecimenType.class, type)
            .get(0);
        SpecimenTypeWrapper typeWrapperHair = new SpecimenTypeWrapper(
            appService, type);

        AliquotedSpecimenWrapper sampleStorage1 = AliquotedSpecimenHelper
            .addAliquotedSpecimen(study, typeWrapperPlasma);
        AliquotedSpecimenWrapper sampleStorage2 = AliquotedSpecimenHelper
            .addAliquotedSpecimen(study, typeWrapperHair);

        Assert.assertTrue(sampleStorage1.compareTo(sampleStorage2) > 0);
        Assert.assertTrue(sampleStorage2.compareTo(sampleStorage1) < 0);
    }

    @Test
    public void testStudyDeleteRemoveAliquotedSpecimens() throws Exception {
        String name = "testStudyDeleteRemoveAliquotedSpecimens" + r.nextInt();
        int nbAliquotedSpecimen = appService.search(AliquotedSpecimen.class,
            new AliquotedSpecimen()).size();
        StudyWrapper study1 = StudyHelper.addStudy(name);
        List<SpecimenTypeWrapper> types = SpecimenTypeWrapper
            .getAllSpecimenTypes(appService, false);
        AliquotedSpecimenHelper.addAliquotedSpecimen(study1,
            DbHelper.chooseRandomlyInList(types));
        study1.delete();
        Assert.assertEquals(nbAliquotedSpecimen,
            appService.search(AliquotedSpecimen.class, new AliquotedSpecimen())
                .size());

        StudyWrapper study = StudyHelper.addStudy("studyname" + r.nextInt());
        PatientWrapper patient = PatientHelper.addPatient("5684", study);
        study.persist();
        AliquotedSpecimenHelper.addAliquotedSpecimen(study,
            DbHelper.chooseRandomlyInList(types));
        study.reload();
        patient.delete();
        study.reload();
        study.delete();
        // FIXME this test is failing when the study has a patient. Should find
        // why !
        //
        // it seems like hibernate forgets about the cascade setting for
        // this association . NL
        Assert.assertEquals(nbAliquotedSpecimen,
            appService.search(AliquotedSpecimen.class, new AliquotedSpecimen())
                .size());
    }
}
