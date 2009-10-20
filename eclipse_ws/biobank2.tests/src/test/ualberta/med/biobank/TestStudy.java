package test.ualberta.med.biobank;

import static org.junit.Assert.fail;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import test.ualberta.med.biobank.internal.DbHelper;
import test.ualberta.med.biobank.internal.SiteHelper;
import test.ualberta.med.biobank.internal.StudyHelper;
import edu.ualberta.med.biobank.common.BiobankCheckException;
import edu.ualberta.med.biobank.common.wrappers.ClinicWrapper;
import edu.ualberta.med.biobank.common.wrappers.ContactWrapper;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;
import edu.ualberta.med.biobank.common.wrappers.StudyWrapper;
import edu.ualberta.med.biobank.model.Study;

public class TestStudy extends TestDatabase {

    @Test
    public void testGettersAndSetters() throws Exception {
        SiteWrapper site = SiteHelper.addSite("testGettersAndSetters");
        StudyWrapper study = StudyHelper
            .addStudy(site, "testGettersAndSetters");
        testGettersAndSetters(study);
    }

    @Test
    public void testSetGetSite() throws Exception {
        SiteWrapper site = SiteHelper.addSite("testGetSite");
        StudyWrapper study = StudyHelper.addStudy(site, "testGetSite");

        SiteWrapper site2 = SiteHelper.addSite("testGetSite-2-");
        study.setSite(site2);
        study.persist();

        study.reload();
        site.reload();
        site2.reload();

        Assert.assertEquals(site2, study.getSite());

        Assert.assertTrue(site2.getStudyCollection().contains(study));

        Assert.assertFalse(site.getStudyCollection().contains(study));
    }

    @Test
    public void testGetContactCollection() throws Exception {
        SiteWrapper site = SiteHelper.addSite("testGetContactCollection");
        StudyWrapper study = StudyHelper.addStudy(site,
            "testGetContactCollection");
        int nber = StudyHelper.addContactsToStudy(study,
            "testGetContactCollection");

        List<ContactWrapper> contacts = study.getContactCollection();
        int sizeFound = contacts.size();

        Assert.assertEquals(nber, sizeFound);
    }

    @Test
    public void testGetContactCollectionBoolean() throws Exception {
        SiteWrapper site = SiteHelper
            .addSite("testGetContactCollectionBoolean");
        StudyWrapper study = StudyHelper.addStudy(site,
            "testGetContactCollectionBoolean");
        StudyHelper
            .addContactsToStudy(study, "testGetContactCollectionBoolean");

        List<ContactWrapper> contacts = study.getContactCollection(true);
        if (contacts.size() > 1) {
            for (int i = 0; i < contacts.size() - 1; i++) {
                ContactWrapper contact1 = contacts.get(i);
                ContactWrapper contact2 = contacts.get(i + 1);
                Assert.assertTrue(contact1.compareTo(contact2) <= 0);
            }
        }
    }

    @Test
    public void testAddInContactCollection() throws Exception {
        SiteWrapper site = SiteHelper.addSite("testAddInContactCollection");
        StudyWrapper study = StudyHelper.addStudy(site,
            "testAddInContactCollection");
        int nber = StudyHelper.addContactsToStudy(study,
            "testAddInContactCollection");
        site.reload();

        // get a clinic not yet added
        List<ContactWrapper> contacts = study.getContactCollection();
        List<ClinicWrapper> clinics = site.getClinicCollection();
        for (ContactWrapper contact : contacts) {
            clinics.remove(contact.getClinicWrapper());
        }
        ClinicWrapper clinicNotAdded = DbHelper.chooseRandomlyInList(clinics);
        ContactWrapper contactToAdd = DbHelper
            .chooseRandomlyInList(clinicNotAdded.getContactCollection());
        contacts.add(contactToAdd);
        study.setContactCollection(contacts);
        study.persist();

        study.reload();
        // one contact added
        Assert.assertEquals(nber + 1, study.getContactCollection().size());
    }

    @Test
    public void testAddInContactCollectionFromClinicAlreadyChoosen()
        throws Exception {
        SiteWrapper site = SiteHelper
            .addSite("testAddInContactCollectionFromClinicAlreadyChoosen");
        StudyWrapper study = StudyHelper.addStudy(site,
            "testAddInContactCollectionFromClinicAlreadyChoosen");
        int nber = StudyHelper.addContactsToStudy(study,
            "testAddInContactCollectionFromClinicAlreadyChoosen");
        site.reload();

        // get a clinic already added
        List<ContactWrapper> contacts = study.getContactCollection();
        ClinicWrapper clinicUsed = null;
        ContactWrapper contactUsed = null;
        for (ContactWrapper contact : contacts) {
            if (contact.getClinicWrapper().getContactCollection().size() > 1) {
                clinicUsed = contact.getClinicWrapper();
                contactUsed = contact;
                break;
            }
        }
        if (clinicUsed != null) {
            // get a different contact
            ContactWrapper newContact = null;
            for (ContactWrapper contact : clinicUsed.getContactCollection()) {
                if (!contact.equals(contactUsed)) {
                    newContact = contact;
                    break;
                }
            }
            contacts.add(newContact);
            study.setContactCollection(contacts);
            try {
                study.persist();
                Assert
                    .fail("Exception expected - should not be able to add more than one contact from the same clinic");
            } catch (BiobankCheckException bce) {
                Assert.assertTrue(true);
            }
        } else {
            Assert.fail("Was not able to perform test");
        }
    }

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
        StudyWrapper study = StudyHelper.addStudy(SiteHelper
            .addSite("testDelete"), "testDelete");
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
        StudyWrapper study = StudyHelper.addStudy(SiteHelper
            .addSite("testResetAlreadyInDatabase"),
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
