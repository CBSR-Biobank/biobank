package edu.ualberta.med.biobank.test.wrappers;

import java.math.BigDecimal;
import java.util.List;

import junit.framework.Assert;

import org.junit.Test;

import edu.ualberta.med.biobank.common.wrappers.AliquotedSpecimenWrapper;
import edu.ualberta.med.biobank.common.wrappers.PatientWrapper;
import edu.ualberta.med.biobank.common.wrappers.SpecimenTypeWrapper;
import edu.ualberta.med.biobank.common.wrappers.StudyWrapper;
import edu.ualberta.med.biobank.common.wrappers.base.AliquotedSpecimenBaseWrapper;
import edu.ualberta.med.biobank.model.ActivityStatus;
import edu.ualberta.med.biobank.model.AliquotedSpecimen;
import edu.ualberta.med.biobank.model.SpecimenType;
import edu.ualberta.med.biobank.server.applicationservice.exceptions.ValueNotSetException;
import edu.ualberta.med.biobank.test.TestDatabase;
import edu.ualberta.med.biobank.test.internal.AliquotedSpecimenHelper;
import edu.ualberta.med.biobank.test.internal.DbHelper;
import edu.ualberta.med.biobank.test.internal.PatientHelper;
import edu.ualberta.med.biobank.test.internal.SpecimenTypeHelper;
import edu.ualberta.med.biobank.test.internal.StudyHelper;

@Deprecated
public class TestAliquotedSpecimen extends TestDatabase {

    @Test
    public void testGettersAndSetters() throws Exception {
        String name = "testGettersAndSetters" + r.nextInt();
        StudyWrapper study = StudyHelper.addStudy(name);

        List<SpecimenTypeWrapper> types = SpecimenTypeWrapper
            .getAllSpecimenTypes(appService, false);
        AliquotedSpecimenWrapper aliquotedSpec = AliquotedSpecimenHelper
            .addAliquotedSpecimen(study, DbHelper.chooseRandomlyInList(types));
        testGettersAndSetters(aliquotedSpec);
    }

    @Test
    public void testGetSetStudy() throws Exception {
        String name = "testGetSetStudy" + r.nextInt();
        StudyWrapper study = StudyHelper.addStudy(name);

        List<SpecimenTypeWrapper> types = SpecimenTypeWrapper
            .getAllSpecimenTypes(appService, false);
        AliquotedSpecimenWrapper aliquotedSpec = AliquotedSpecimenHelper
            .addAliquotedSpecimen(study, DbHelper.chooseRandomlyInList(types));

        Assert.assertEquals(study, aliquotedSpec.getStudy());

        StudyWrapper newStudy = StudyHelper.addStudy(name + "NEW");
        aliquotedSpec.setStudy(newStudy);
        aliquotedSpec.persist();

        Assert.assertEquals(newStudy, aliquotedSpec.getStudy());
        Assert.assertFalse(study.equals(aliquotedSpec.getStudy()));

        aliquotedSpec = new AliquotedSpecimenWrapper(appService);
        Assert.assertNull(aliquotedSpec.getStudy());
    }

    @Test
    public void testGetSetSpecimenType() throws Exception {
        String name = "testGetSetSpecimenType" + r.nextInt();
        StudyWrapper study = StudyHelper.addStudy(name);

        List<SpecimenTypeWrapper> types = SpecimenTypeWrapper
            .getAllSpecimenTypes(appService, false);
        SpecimenTypeWrapper type = DbHelper.chooseRandomlyInList(types);
        AliquotedSpecimenWrapper aliquotedSpec = AliquotedSpecimenHelper
            .addAliquotedSpecimen(study, type);

        Assert.assertEquals(type, aliquotedSpec.getSpecimenType());

        SpecimenTypeWrapper newType = SpecimenTypeHelper.addSpecimenType(name);
        aliquotedSpec.setSpecimenType(newType);
        aliquotedSpec.persist();

        Assert.assertEquals(newType, aliquotedSpec.getSpecimenType());
        Assert.assertFalse(type.equals(aliquotedSpec.getSpecimenType()));

        aliquotedSpec = new AliquotedSpecimenWrapper(appService);
        Assert.assertNull(aliquotedSpec.getSpecimenType());
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
        String name = "testActivityStatus" + r.nextInt();
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

        ss.setActivityStatus(ActivityStatus.ACTIVE);
        ss.persist();
        Assert.assertTrue(ActivityStatus.ACTIVE == ss.getActivityStatus());


        ss.setActivityStatus(ActivityStatus.FLAGGED);
        ss.persist();
        Assert.assertTrue(ActivityStatus.FLAGGED == ss.getActivityStatus());
        Assert.assertFalse(ActivityStatus.ACTIVE == ss.getActivityStatus());
    }

    @Test
    public void testDelete() throws Exception {
        String name = "testDelete" + r.nextInt();
        StudyWrapper study = StudyHelper.addStudy(name);

        List<SpecimenTypeWrapper> types = SpecimenTypeWrapper
            .getAllSpecimenTypes(appService, false);
        AliquotedSpecimenWrapper aliquotedSpec = AliquotedSpecimenHelper
            .addAliquotedSpecimen(study, DbHelper.chooseRandomlyInList(types));

        // object is in database
        AliquotedSpecimen ssInDB = ModelUtils.getObjectWithId(appService,
            AliquotedSpecimen.class, aliquotedSpec.getId());
        Assert.assertNotNull(ssInDB);

        Integer aliquotedSpecId = aliquotedSpec.getId();

        aliquotedSpec.delete();

        ssInDB = ModelUtils.getObjectWithId(appService,
            AliquotedSpecimen.class, aliquotedSpecId);
        // object is not anymore in database
        Assert.assertNull(ssInDB);
    }

    @Test
    public void testResetAlreadyInDatabase() throws Exception {
        String name = "testResetAlreadyInDatabase" + r.nextInt();
        StudyWrapper study = StudyHelper.addStudy(name);

        List<SpecimenTypeWrapper> types = SpecimenTypeWrapper
            .getAllSpecimenTypes(appService, false);
        AliquotedSpecimenWrapper aliquotedSpec = AliquotedSpecimenHelper
            .addAliquotedSpecimen(study, DbHelper.chooseRandomlyInList(types));

        aliquotedSpec.reload();
        BigDecimal oldVolume = aliquotedSpec.getVolume();
        aliquotedSpec.setVolume(new BigDecimal(6.3));
        aliquotedSpec.reset();
        Assert.assertEquals(oldVolume, aliquotedSpec.getVolume());
    }

    @Test
    public void testResetNew() throws Exception {
        AliquotedSpecimenWrapper aliquotedSpec = new AliquotedSpecimenWrapper(
            appService);
        aliquotedSpec.setVolume(new BigDecimal(5.2));
        aliquotedSpec.reset();
        Assert.assertEquals(null, aliquotedSpec.getVolume());
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

        AliquotedSpecimenWrapper aliquotedSpec1 = AliquotedSpecimenHelper
            .addAliquotedSpecimen(study, typeWrapperPlasma);
        AliquotedSpecimenWrapper aliquotedSpec2 = AliquotedSpecimenHelper
            .addAliquotedSpecimen(study, typeWrapperHair);

        Assert.assertTrue(aliquotedSpec1.compareTo(aliquotedSpec2) > 0);
        Assert.assertTrue(aliquotedSpec2.compareTo(aliquotedSpec1) < 0);

        AliquotedSpecimenBaseWrapper as = new AliquotedSpecimenBaseWrapper(
            appService);
        as.setSpecimenType(typeWrapperHair);
        Assert.assertEquals(0, aliquotedSpec2.compareTo(as));
    }

    @Test
    public void testStudyDeleteRemoveAliquotedSpecimens() throws Exception {
        String name = "testStudyDeleteRemoveAliquotedSpecimens" + r.nextInt();
        int nbAliquotedSpecimen = appService.search(AliquotedSpecimen.class,
            new AliquotedSpecimen()).size();
        StudyWrapper study1 = StudyHelper.addStudy(name, false);
        List<SpecimenTypeWrapper> types = SpecimenTypeWrapper
            .getAllSpecimenTypes(appService, false);
        AliquotedSpecimenHelper.addAliquotedSpecimen(study1,
            DbHelper.chooseRandomlyInList(types));
        study1.delete();
        Assert.assertEquals(nbAliquotedSpecimen,
            appService.search(AliquotedSpecimen.class, new AliquotedSpecimen())
                .size());

        StudyWrapper study = StudyHelper.addStudy("studyname" + r.nextInt(),
            false);
        PatientWrapper patient = PatientHelper.addPatient("5684" + r.nextInt(),
            study);
        AliquotedSpecimenHelper.addAliquotedSpecimen(study,
            DbHelper.chooseRandomlyInList(types));
        study.reload();

        Assert.assertEquals(nbAliquotedSpecimen + 1,
            appService.search(AliquotedSpecimen.class, new AliquotedSpecimen())
                .size());

        patient.delete();
        study.reload();
        study.delete();
        Assert.assertEquals(nbAliquotedSpecimen,
            appService.search(AliquotedSpecimen.class, new AliquotedSpecimen())
                .size());
    }

    @Test
    public void testToString() throws Exception {
        String name = "testToString" + r.nextInt();
        StudyWrapper study = StudyHelper.addStudy(name);

        List<SpecimenTypeWrapper> types = SpecimenTypeWrapper
            .getAllSpecimenTypes(appService, false);
        AliquotedSpecimenWrapper aliquotedSpec = AliquotedSpecimenHelper
            .addAliquotedSpecimen(study, DbHelper.chooseRandomlyInList(types));
        String s = aliquotedSpec.toString();
        Assert.assertTrue((s != null) && !s.isEmpty());
    }

}
