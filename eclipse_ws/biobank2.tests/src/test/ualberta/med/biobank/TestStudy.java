package test.ualberta.med.biobank;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import test.ualberta.med.biobank.internal.ClinicHelper;
import test.ualberta.med.biobank.internal.ContactHelper;
import test.ualberta.med.biobank.internal.DbHelper;
import test.ualberta.med.biobank.internal.PatientHelper;
import test.ualberta.med.biobank.internal.PatientVisitHelper;
import test.ualberta.med.biobank.internal.SampleSourceHelper;
import test.ualberta.med.biobank.internal.SampleStorageHelper;
import test.ualberta.med.biobank.internal.SampleTypeHelper;
import test.ualberta.med.biobank.internal.SiteHelper;
import test.ualberta.med.biobank.internal.StudyHelper;
import edu.ualberta.med.biobank.common.BiobankCheckException;
import edu.ualberta.med.biobank.common.wrappers.ClinicWrapper;
import edu.ualberta.med.biobank.common.wrappers.ContactWrapper;
import edu.ualberta.med.biobank.common.wrappers.PatientWrapper;
import edu.ualberta.med.biobank.common.wrappers.SampleSourceWrapper;
import edu.ualberta.med.biobank.common.wrappers.SampleStorageWrapper;
import edu.ualberta.med.biobank.common.wrappers.SampleTypeWrapper;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;
import edu.ualberta.med.biobank.common.wrappers.StudyWrapper;
import edu.ualberta.med.biobank.model.Study;

public class TestStudy extends TestDatabase {

    @Test
    public void testGettersAndSetters() throws Exception {
        String name = "testGettersAndSetters" + r.nextInt();
        SiteWrapper site = SiteHelper.addSite(name);
        StudyWrapper study = StudyHelper.addStudy(site, name);
        testGettersAndSetters(study);
    }

    @Test
    public void testSetGetSite() throws Exception {
        String name = "testGetSite" + r.nextInt();
        SiteWrapper site = SiteHelper.addSite(name);
        StudyWrapper study = StudyHelper.addStudy(site, name);

        SiteWrapper site2 = SiteHelper.addSite(name + "SecondSite");
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
        String name = "testGetContactCollection" + r.nextInt();
        SiteWrapper site = SiteHelper.addSite(name);
        StudyWrapper study = StudyHelper.addStudy(site, name);
        int nber = ContactHelper.addContactsToStudy(study, name);

        List<ContactWrapper> contacts = study.getContactCollection();
        int sizeFound = contacts.size();

        Assert.assertEquals(nber, sizeFound);
    }

    @Test
    public void testGetContactCollectionBoolean() throws Exception {
        String name = "testGetContactCollectionBoolean" + r.nextInt();
        SiteWrapper site = SiteHelper.addSite(name);
        StudyWrapper study = StudyHelper.addStudy(site, name);
        ContactHelper.addContactsToStudy(study, name);

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
        String name = "testAddInContactCollection" + r.nextInt();
        SiteWrapper site = SiteHelper.addSite(name);
        StudyWrapper study = StudyHelper.addStudy(site, name);
        int nber = ContactHelper.addContactsToStudy(study, name);
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
        String name = "testAddInContactCollectionFromClinicAlreadyChoosen"
            + r.nextInt();
        SiteWrapper site = SiteHelper.addSite(name);
        StudyWrapper study = StudyHelper.addStudy(site, name);
        ContactHelper.addContactsToStudy(study, name);
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
    public void testGetSampleStorageCollection() throws Exception {
        String name = "testGetSampleStorageCollection" + r.nextInt();
        SiteWrapper site = SiteHelper.addSite(name);
        StudyWrapper study = StudyHelper.addStudy(site, name);
        int nber = SampleStorageHelper.addSampleStorages(study, name);

        List<SampleStorageWrapper> storages = study
            .getSampleStorageCollection();
        int sizeFound = storages.size();

        Assert.assertEquals(nber, sizeFound);
    }

    @Test
    public void testGetSampleStorageCollectionBoolean() throws Exception {
        String name = "testGetSampleStorageCollectionBoolean" + r.nextInt();
        SiteWrapper site = SiteHelper.addSite(name);
        StudyWrapper study = StudyHelper.addStudy(site, name);
        SampleStorageHelper.addSampleStorages(study, name);

        List<SampleStorageWrapper> storages = study
            .getSampleStorageCollection(true);
        if (storages.size() > 1) {
            for (int i = 0; i < storages.size() - 1; i++) {
                SampleStorageWrapper storage1 = storages.get(i);
                SampleStorageWrapper storage2 = storages.get(i + 1);
                Assert.assertTrue(storage1.compareTo(storage2) <= 0);
            }
        }
    }

    @Test
    public void testAddInSampleStorageCollection() throws Exception {
        String name = "testAddInSampleStorageCollection" + r.nextInt();
        SiteWrapper site = SiteHelper.addSite(name);
        StudyWrapper study = StudyHelper.addStudy(site, name);
        int nber = SampleStorageHelper.addSampleStorages(study, name);

        List<SampleStorageWrapper> storages = study
            .getSampleStorageCollection();
        SampleTypeWrapper type = SampleTypeHelper.addSampleType(site, name);
        SampleStorageWrapper newStorage = SampleStorageHelper.newSampleStorage(
            study, type);
        storages.add(newStorage);
        study.setSampleStorageCollection(storages);
        study.persist();

        study.reload();
        // one storage added
        Assert
            .assertEquals(nber + 1, study.getSampleStorageCollection().size());
    }

    @Test
    public void testGetSampleSourceCollection() throws Exception {
        String name = "testGetSampleSourceCollection" + r.nextInt();
        SiteWrapper site = SiteHelper.addSite(name);
        StudyWrapper study = StudyHelper.addStudy(site, name);
        int nber = SampleSourceHelper.addSampleSources(study, name);

        List<SampleSourceWrapper> storages = study.getSampleSourceCollection();
        int sizeFound = storages.size();

        Assert.assertEquals(nber, sizeFound);
    }

    @Test
    public void testGetSampleSourceCollectionBoolean() throws Exception {
        String name = "testGetSampleSourceCollectionBoolean" + r.nextInt();
        SiteWrapper site = SiteHelper.addSite(name);
        StudyWrapper study = StudyHelper.addStudy(site, name);
        SampleSourceHelper.addSampleSources(study, name);

        List<SampleSourceWrapper> sources = study
            .getSampleSourceCollection(true);
        if (sources.size() > 1) {
            for (int i = 0; i < sources.size() - 1; i++) {
                SampleSourceWrapper source1 = sources.get(i);
                SampleSourceWrapper source2 = sources.get(i + 1);
                Assert.assertTrue(source1.compareTo(source2) <= 0);
            }
        }
    }

    @Test
    public void testAddInSampleSourceCollection() throws Exception {
        String name = "testAddInSampleSourceCollection" + r.nextInt();
        SiteWrapper site = SiteHelper.addSite(name);
        StudyWrapper study = StudyHelper.addStudy(site, name);
        int nber = SampleSourceHelper.addSampleSources(study, name);

        List<SampleSourceWrapper> sources = study.getSampleSourceCollection();
        SampleSourceWrapper source = SampleSourceHelper.addSampleSource(name);
        sources.add(source);
        study.setSampleSourceCollection(sources);
        study.persist();

        study.reload();
        // one storage added
        Assert.assertEquals(nber + 1, study.getSampleSourceCollection().size());
    }

    @Test
    public void testGetPvInfoLabels() throws Exception {
        String name = "testGetPvInfoLabels" + r.nextInt();
        SiteWrapper site = SiteHelper.addSite(name);
        StudyWrapper study = StudyHelper.addStudy(site, name);

        study.setPvInfoAllowedValues("Worksheet", null);
        study.setPvInfoAllowedValues("Consent", null);
        study.persist();

        study.reload();
        Assert.assertEquals(2, study.getPvInfoLabels().length);
    }

    @Test
    public void testGetPvInfoType() throws Exception {
        String name = "testGetPvInfoType" + r.nextInt();
        SiteWrapper site = SiteHelper.addSite(name);
        StudyWrapper study = StudyHelper.addStudy(site, name);

        study.setPvInfoAllowedValues("Worksheet", null);
        study.setPvInfoAllowedValues("Consent", null);
        study.persist();

        study.reload();
        Integer typeId = site.getPvInfoType("Consent");
        Assert.assertEquals(typeId, study.getPvInfoType("Consent"));
    }

    @Test
    public void testGetPvInfoAllowedValues() throws Exception {
        String name = "testGetPvInfoType" + r.nextInt();
        SiteWrapper site = SiteHelper.addSite(name);
        StudyWrapper study = StudyHelper.addStudy(site, name);

        String pvInfoLabel = "Visit Type";
        study.setPvInfoAllowedValues("Worksheet", null);
        String[] values = new String[] { "toto", "titi", "tata" };
        study.setPvInfoAllowedValues(pvInfoLabel, values);
        study.persist();

        study.reload();
        String[] valuesFound = study.getPvInfoAllowedValues(pvInfoLabel);
        List<String> valuesList = Arrays.asList(values);
        Assert.assertTrue(valuesFound.length == values.length);
        for (String s : valuesFound) {
            Assert.assertTrue(valuesList.contains(s));
        }
    }

    @Test
    public void testGetClinicCollection() throws Exception {
        String name = "testGetClinicCollection" + r.nextInt();
        SiteWrapper site = SiteHelper.addSite(name);
        StudyWrapper study = StudyHelper.addStudy(site, name);
        int nber = ContactHelper.addContactsToStudy(study, name);

        List<ClinicWrapper> clinics = study.getClinicCollection();
        int sizeFound = clinics.size();

        Assert.assertEquals(nber, sizeFound);
    }

    @Test
    public void testGetPatientCollection() throws Exception {
        String name = "testGetPatientCollection" + r.nextInt();
        SiteWrapper site = SiteHelper.addSite(name);
        StudyWrapper study = StudyHelper.addStudy(site, name);
        int nber = PatientHelper.addPatients(name, study);

        List<PatientWrapper> patients = study.getPatientCollection();
        int sizeFound = patients.size();

        Assert.assertEquals(nber, sizeFound);
    }

    @Test
    public void testGetPatientCollectionBoolean() throws Exception {
        String name = "testGetPatientCollectionBoolean" + r.nextInt();
        SiteWrapper site = SiteHelper.addSite(name);
        StudyWrapper study = StudyHelper.addStudy(site, name);
        PatientHelper.addPatients(name, study);

        List<PatientWrapper> patients = study.getPatientCollection(true);
        if (patients.size() > 1) {
            for (int i = 0; i < patients.size() - 1; i++) {
                PatientWrapper patient1 = patients.get(i);
                PatientWrapper patient2 = patients.get(i + 1);
                Assert.assertTrue(patient1.compareTo(patient2) <= 0);
            }
        }
    }

    @Test
    public void testAddInPatientCollection() throws Exception {
        String name = "testAddInPatientCollection" + r.nextInt();
        SiteWrapper site = SiteHelper.addSite(name);
        StudyWrapper study = StudyHelper.addStudy(site, name);
        int nber = PatientHelper.addPatients(name, study);

        List<PatientWrapper> patients = study.getPatientCollection();
        PatientWrapper newPatient = PatientHelper.newPatient(name
            + "newPatient");
        newPatient.setStudy(study);
        patients.add(newPatient);
        study.setPatientCollection(patients);
        study.persist();

        study.reload();
        // one patient added
        Assert.assertEquals(nber + 1, study.getPatientCollection().size());
    }

    @Test
    public void testGetPatientCountForClinic() throws Exception {
        String name = "testGetPatientCountForClinic" + r.nextInt();
        SiteWrapper site = SiteHelper.addSite(name);

        ClinicWrapper clinic1 = ClinicHelper.addClinic(site, name + "CLINIC1");
        ContactWrapper contact1 = ContactHelper.addContact(clinic1, name
            + "CONTACT1");

        ClinicWrapper clinic2 = ClinicHelper.addClinic(site, name + "CLINIC2");
        ContactWrapper contact2 = ContactHelper.addContact(clinic2, name
            + "CONTACT2");

        List<ContactWrapper> contacts = new ArrayList<ContactWrapper>();
        contacts.add(contact1);
        contacts.add(contact2);

        StudyWrapper study1 = StudyHelper.addStudy(site, name + "STUDY1");
        study1.setContactCollection(contacts);
        study1.persist();
        PatientWrapper patient1 = PatientHelper.addPatient(name + "PATIENT1",
            study1);
        // clinic 1 = 1 patient pour study 1
        PatientVisitHelper.addPatientVisits(patient1, clinic1);
        PatientVisitHelper.addPatientVisits(patient1, clinic2);
        PatientWrapper patient2 = PatientHelper.addPatient(name + "PATIENT2",
            study1);
        // clinic 2 = 2 patients pour study 1
        PatientVisitHelper.addPatientVisits(patient2, clinic2);

        StudyWrapper study2 = StudyHelper.addStudy(site, name + "STUDY2");
        study2.setContactCollection(contacts);
        study2.persist();
        PatientWrapper patient3 = PatientHelper.addPatient(name + "PATIENT3",
            study2);
        // clinic 1 = 1 patient pour study2
        PatientVisitHelper.addPatientVisits(patient3, clinic1);
        // clinic 2= 1 patient pour study2
        PatientVisitHelper.addPatientVisits(patient3, clinic2);

        study1.reload();
        Assert.assertEquals(1, study1.getPatientCountForClinic(clinic1));
        Assert.assertEquals(2, study1.getPatientCountForClinic(clinic2));
    }

    @Test
    public void testGetPatientVisitCountForClinic() throws Exception {
        String name = "testGetPatientVisitCountForClinic" + r.nextInt();
        SiteWrapper site = SiteHelper.addSite(name);

        ClinicWrapper clinic1 = ClinicHelper.addClinic(site, name + "CLINIC1");
        ContactWrapper contact1 = ContactHelper.addContact(clinic1, name
            + "CONTACT1");

        ClinicWrapper clinic2 = ClinicHelper.addClinic(site, name + "CLINIC2");
        ContactWrapper contact2 = ContactHelper.addContact(clinic2, name
            + "CONTACT2");

        List<ContactWrapper> contacts = new ArrayList<ContactWrapper>();
        contacts.add(contact1);
        contacts.add(contact2);

        StudyWrapper study1 = StudyHelper.addStudy(site, name + "STUDY1");
        study1.setContactCollection(contacts);
        study1.persist();
        PatientWrapper patient1 = PatientHelper.addPatient(name, study1);
        int nber = PatientVisitHelper.addPatientVisits(patient1, clinic1);
        int nber2 = PatientVisitHelper.addPatientVisits(patient1, clinic2);

        StudyWrapper study2 = StudyHelper.addStudy(site, name + "STUDY2");
        study2.setContactCollection(contacts);
        study2.persist();
        PatientWrapper patient2 = PatientHelper.addPatient(name, study2);
        PatientVisitHelper.addPatientVisits(patient2, clinic1);
        PatientVisitHelper.addPatientVisits(patient2, clinic2);

        study1.reload();
        Assert
            .assertEquals(nber, study1.getPatientVisitCountForClinic(clinic1));
        Assert.assertEquals(nber2, study1
            .getPatientVisitCountForClinic(clinic2));
    }

    @Test
    public void testGetPatientVisitCount() throws Exception {
        String name = "testGetPatientVisitCount" + r.nextInt();
        SiteWrapper site = SiteHelper.addSite(name);

        ClinicWrapper clinic1 = ClinicHelper.addClinic(site, name + "CLINIC1");
        ContactWrapper contact1 = ContactHelper.addContact(clinic1, name
            + "CONTACT1");

        ClinicWrapper clinic2 = ClinicHelper.addClinic(site, name + "CLINIC2");
        ContactWrapper contact2 = ContactHelper.addContact(clinic2, name
            + "CONTACT2");

        List<ContactWrapper> contacts = new ArrayList<ContactWrapper>();
        contacts.add(contact1);
        contacts.add(contact2);

        StudyWrapper study1 = StudyHelper.addStudy(site, name + "STUDY1");
        study1.setContactCollection(contacts);
        study1.persist();
        PatientWrapper patient1 = PatientHelper.addPatient(name, study1);
        int nber = PatientVisitHelper.addPatientVisits(patient1, clinic1);
        int nber2 = PatientVisitHelper.addPatientVisits(patient1, clinic2);

        StudyWrapper study2 = StudyHelper.addStudy(site, name + "STUDY2");
        study2.setContactCollection(contacts);
        study2.persist();
        PatientWrapper patient2 = PatientHelper.addPatient(name, study2);
        PatientVisitHelper.addPatientVisits(patient2, clinic1);
        PatientVisitHelper.addPatientVisits(patient2, clinic2);

        study1.reload();
        Assert.assertEquals(nber + nber2, study1.getPatientVisitCount());
    }

    @Test
    public void testPersist() throws Exception {
        int oldTotal = appService.search(Study.class, new Study()).size();
        String name = "testPersist" + r.nextInt();
        SiteWrapper site = SiteHelper.addSite(name);
        StudyHelper.addStudy(site, name);
        int newTotal = appService.search(Study.class, new Study()).size();
        Assert.assertEquals(oldTotal + 1, newTotal);
    }

    @Test
    public void testPersistFail() throws Exception {
        String name = "testPersistFail" + r.nextInt();
        SiteWrapper site = SiteHelper.addSite(name);
        StudyHelper.addStudy(site, name);

        try {
            StudyHelper.addStudy(site, name);
            Assert
                .fail("Should not insert the study : same name already in database");
        } catch (BiobankCheckException bce) {
            Assert.assertTrue(true);
        }
    }

    @Test
    public void testDelete() throws Exception {
        StudyWrapper study = StudyHelper.addStudy(SiteHelper
            .addSite("testDelete"), "testDelete");

        // object is in database
        Study studyInDB = ModelUtils.getObjectWithId(appService, Study.class,
            study.getId());
        Assert.assertNotNull(studyInDB);

        study.delete();

        studyInDB = ModelUtils.getObjectWithId(appService, Study.class, study
            .getId());
        // object is not anymore in database
        Assert.assertNull(studyInDB);
    }

    @Test
    public void testDeleteWithPatient() throws Exception {
        String name = "testDelete" + r.nextInt();
        StudyWrapper study = StudyHelper.addStudy(SiteHelper.addSite(name),
            name);
        PatientHelper.addPatient(name, study);
        study.reload();
        try {
            study.delete();
            Assert
                .fail("Should not delete : patients need to be removed first");
        } catch (BiobankCheckException bce) {
            Assert.assertTrue(true);
        }
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
