package test.ualberta.med.biobank;

import static org.junit.Assert.fail;

import org.junit.Assert;
import org.junit.Test;

import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;
import edu.ualberta.med.biobank.common.wrappers.StudyWrapper;
import edu.ualberta.med.biobank.model.Study;

public class TestStudy extends TestDatabase {

    @Test
    public void testGettersAndSetters() throws Exception {
        SiteWrapper site = addSite("testGettersAndSetters");
        StudyWrapper study = addStudy(site, "testGettersAndSetters");
        testGettersAndSetters(study);
    }

    @Test
    public void testSetGetSite() throws Exception {
        SiteWrapper site = addSite("testGetSite");
        StudyWrapper study = addStudy(site, "testGetSite");

        SiteWrapper site2 = addSite("testGetSite-2-");
        study.setSite(site2);
        study.persist();

        study.reload();
        site.reload();
        site2.reload();

        Assert.assertEquals(site2, study.getSite());

        Assert.assertTrue(site2.getStudyCollection().contains(study));

        Assert.assertFalse(site.getStudyCollection().contains(study));
    }

    // @Test
    // public void testGetContactCollection() {
    // SiteWrapper site = addSite("testGetContactCollection");
    // StudyWrapper study = addStudy(site, "testGetContactCollection");
    // addContactsToStudy(study, "testGetContactCollection");
    //
    // List<ContactWrapper> contacts = study.getContactCollection();
    // int sizeFound = contacts.size();
    //
    // Assert.assertEquals(studiesNber, sizeFound);
    // }
    //
    // private void addContactsToStudy(StudyWrapper study, String name)
    // throws Exception {
    // SiteWrapper site = study.getSite();
    // int nberClinics = addClinics(site, name);
    // site.reload();
    // int nber = r.nextInt(nberClinics) + 1;
    // ClinicWrapper clinic =
    // for (int i = 0; i < nber; i++) {
    // ClinicWrapper clini=
    // }
    //
    // }

    // @Test
    // public void testGetContactCollectionBoolean() {
    // SiteWrapper site = addSite("testGetStudyCollectionBoolean");
    // addStudies(site, "testGetStudyCollectionBoolean");
    //
    // List<StudyWrapper> studiesSorted = site.getStudyCollection(true);
    // if (studiesSorted.size() > 1) {
    // for (int i = 0; i < studiesSorted.size() - 1; i++) {
    // StudyWrapper study1 = studiesSorted.get(i);
    // StudyWrapper study2 = studiesSorted.get(i + 1);
    // Assert.assertTrue(study1.compareTo(study2) <= 0);
    // }
    // }
    // }

    @Test
    public void testSetContactCollectionCollectionOfContactBoolean() {
        fail("Not yet implemented");
    }

    @Test
    public void testSetContactCollectionListOfContactWrapper() {
        fail("Not yet implemented");
    }

    @Test
    public void testGetSampleStorageCollectionBoolean() {
        fail("Not yet implemented");
    }

    @Test
    public void testGetSampleStorageCollection() {
        fail("Not yet implemented");
    }

    @Test
    public void testSetSampleStorageCollectionCollectionOfSampleStorageBoolean() {
        fail("Not yet implemented");
    }

    @Test
    public void testSetSampleStorageCollectionListOfSampleStorageWrapper() {
        fail("Not yet implemented");
    }

    @Test
    public void testGetSampleSourceCollectionBoolean() {
        fail("Not yet implemented");
    }

    @Test
    public void testGetSampleSourceCollection() {
        fail("Not yet implemented");
    }

    @Test
    public void testSetSampleSourceCollectionCollectionOfSampleSourceBoolean() {
        fail("Not yet implemented");
    }

    @Test
    public void testSetSampleSourceCollectionListOfSampleSourceWrapper() {
        fail("Not yet implemented");
    }

    @Test
    public void testGetPvInfoLabels() {
        fail("Not yet implemented");
    }

    @Test
    public void testGetPvInfo() {
        fail("Not yet implemented");
    }

    @Test
    public void testGetPvInfoType() {
        fail("Not yet implemented");
    }

    @Test
    public void testGetPvInfoAllowedValues() {
        fail("Not yet implemented");
    }

    @Test
    public void testSetPvInfoAllowedValues() {
        fail("Not yet implemented");
    }

    @Test
    public void testGetClinicCollection() {
        fail("Not yet implemented");
    }

    @Test
    public void testGetPatientCollectionBoolean() {
        fail("Not yet implemented");
    }

    @Test
    public void testGetPatientCollection() {
        fail("Not yet implemented");
    }

    @Test
    public void testSetPatientCollectionCollectionOfPatientBoolean() {
        fail("Not yet implemented");
    }

    @Test
    public void testSetPatientCollectionListOfPatientWrapper() {
        fail("Not yet implemented");
    }

    @Test
    public void testGetPatientCountForClinic() {
        fail("Not yet implemented");
    }

    @Test
    public void testGetPatientVisitCountForClinic() {
        fail("Not yet implemented");
    }

    @Test
    public void testGetPatientVisitCount() {
        fail("Not yet implemented");
    }

    @Test
    public void testDelete() throws Exception {
        StudyWrapper study = addStudy(addSite("testDelete"), "testDelete");
        // object is in database
        Assert.assertNotNull(study);
        study.delete();
        Study studyInDB = ModelUtils.getObjectWithId(appService, Study.class,
            study.getId());
        // object is not anymore in database
        Assert.assertNull(studyInDB);
    }

    @Test
    public void testResetAlreadyInDatabase() throws Exception {
        StudyWrapper study = addStudy(addSite("testResetAlreadyInDatabase"),
            "testResetAlreadyInDatabase");
        study.reload();
        String oldName = study.getName();
        study.setName("toto");
        study.reset();
        Assert.assertEquals(oldName, study.getName());
    }

    @Test
    public void testResetNew() throws Exception {
        StudyWrapper newStudy = new StudyWrapper(appService);
        newStudy.setName("titi");
        newStudy.reset();
        Assert.assertEquals(null, newStudy.getName());
    }

}
