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
import test.ualberta.med.biobank.internal.ShipmentHelper;
import test.ualberta.med.biobank.internal.SiteHelper;
import test.ualberta.med.biobank.internal.StudyHelper;
import edu.ualberta.med.biobank.common.BiobankCheckException;
import edu.ualberta.med.biobank.common.wrappers.ClinicWrapper;
import edu.ualberta.med.biobank.common.wrappers.ContactWrapper;
import edu.ualberta.med.biobank.common.wrappers.PatientWrapper;
import edu.ualberta.med.biobank.common.wrappers.SampleSourceWrapper;
import edu.ualberta.med.biobank.common.wrappers.SampleStorageWrapper;
import edu.ualberta.med.biobank.common.wrappers.SampleTypeWrapper;
import edu.ualberta.med.biobank.common.wrappers.ShipmentWrapper;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;
import edu.ualberta.med.biobank.common.wrappers.StudyWrapper;
import edu.ualberta.med.biobank.common.wrappers.internal.PvInfoPossibleWrapper;
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
    public void testSetContactCollectionAdd() throws Exception {
        String name = "testSetContactCollectionAdd" + r.nextInt();
        SiteWrapper site = SiteHelper.addSite(name);
        StudyWrapper study = StudyHelper.addStudy(site, name);
        int nber = ContactHelper.addContactsToStudy(study, name);
        site.reload();

        // get a clinic not yet added
        List<ContactWrapper> contacts = study.getContactCollection();
        List<ClinicWrapper> clinics = site.getClinicCollection();
        for (ContactWrapper contact : contacts) {
            clinics.remove(contact.getClinic());
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
    public void testSetContactCollectionRemove() throws Exception {
        String name = "testSetContactCollectionRemove" + r.nextInt();
        SiteWrapper site = SiteHelper.addSite(name);
        StudyWrapper study = StudyHelper.addStudy(site, name);
        int nber = ContactHelper.addContactsToStudy(study, name);
        site.reload();

        // get a clinic not yet added
        List<ContactWrapper> contacts = study.getContactCollection();
        ContactWrapper contact = DbHelper.chooseRandomlyInList(contacts);
        contacts.remove(contact);
        // don't have to delete contact because this is a *..* relation
        study.setContactCollection(contacts);
        study.persist();

        study.reload();
        // one contact added
        Assert.assertEquals(nber - 1, study.getContactCollection().size());
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
    public void testSetSampleStorageCollectionAdd() throws Exception {
        String name = "testSetSampleStorageCollectionAdd" + r.nextInt();
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
    public void testSetSampleStorageCollectionRemove() throws Exception {
        String name = "testSetSampleStorageCollectionRemove" + r.nextInt();
        SiteWrapper site = SiteHelper.addSite(name);
        StudyWrapper study = StudyHelper.addStudy(site, name);
        int nber = SampleStorageHelper.addSampleStorages(study, name);

        List<SampleStorageWrapper> storages = study
            .getSampleStorageCollection();
        SampleStorageWrapper storage = DbHelper.chooseRandomlyInList(storages);
        storages.remove(storage);
        // don't have to delete the storage thanks to
        // deleteSampleStorageDifference method
        study.setSampleStorageCollection(storages);
        study.persist();

        study.reload();
        // one storage added
        Assert
            .assertEquals(nber - 1, study.getSampleStorageCollection().size());
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
    public void testSetSampleSourceCollectionAdd() throws Exception {
        String name = "testSetSampleSourceCollectionAdd" + r.nextInt();
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
    public void testSetSampleSourceCollectionRemove() throws Exception {
        String name = "testSetSampleSourceCollectionRemove" + r.nextInt();
        SiteWrapper site = SiteHelper.addSite(name);
        StudyWrapper study = StudyHelper.addStudy(site, name);
        int nber = SampleSourceHelper.addSampleSources(study, name);

        List<SampleSourceWrapper> sources = study.getSampleSourceCollection();
        SampleSourceWrapper source = DbHelper.chooseRandomlyInList(sources);
        sources.remove(source);
        // don't have to delete the storage thanks to
        // deleteSampleSourceDifference method
        SampleSourceHelper.createdSampleSources.remove(source);
        study.setSampleSourceCollection(sources);
        study.persist();

        study.reload();
        // one storage added
        Assert.assertEquals(nber - 1, study.getSampleSourceCollection().size());
    }

    @Test
    public void testSetPvInfo() throws Exception {
        String name = "testGetSetPvInfoLabels" + r.nextInt();
        SiteWrapper site = SiteHelper.addSite(name);
        StudyWrapper study = StudyHelper.addStudy(site, name);

        study.setPvInfo("Worksheet");
        study.setPvInfo("Visit Type", new String[] { "toto", "titi", "tata" });
        study.persist();

        Assert.assertEquals(2, study.getPvInfoLabels().length);
    }

    @Test
    public void testGetSetPvInfoLabels() throws Exception {
        String name = "testGetSetPvInfoLabels" + r.nextInt();
        SiteWrapper site = SiteHelper.addSite(name);
        StudyWrapper study = StudyHelper.addStudy(site, name);

        study.setPvInfo("Worksheet");
        study.setPvInfo("Consent");
        Assert.assertEquals(2, study.getPvInfoLabels().length);

        // test still ok after persist
        study.persist();
        study.reload();
        Assert.assertEquals(2, study.getPvInfoLabels().length);

        study.setPvInfoLabels(new String[] { "Consent" });
        Assert.assertEquals(1, study.getPvInfoLabels().length);

        // test still ok after persist
        study.persist();
        study.reload();
        Assert.assertEquals(1, study.getPvInfoLabels().length);
    }

    @Test
    public void testGetPvInfoType() throws Exception {
        String name = "testGetPvInfoType" + r.nextInt();
        SiteWrapper site = SiteHelper.addSite(name);
        StudyWrapper study = StudyHelper.addStudy(site, name);

        List<PvInfoPossibleWrapper> possibles = PvInfoPossibleWrapper
            .getGlobalPvInfoPossible(appService, false);
        if (possibles.size() < 2) {
            Assert
                .fail("no pv info possible available to execute this test case");
        }

        study.setPvInfo(possibles.get(0).getLabel());
        PvInfoPossibleWrapper possible = possibles.get(1);
        study.setPvInfo(possible.getLabel());
        study.persist();

        study.reload();
        Integer typeId = study.getPvInfoType(possible.getLabel());
        Assert.assertEquals(possible.getPvInfoType().getId(), typeId);
    }

    @Test
    public void testGetPvInfoAllowedValues() throws Exception {
        String name = "testGetPvInfoType" + r.nextInt();
        SiteWrapper site = SiteHelper.addSite(name);
        StudyWrapper study = StudyHelper.addStudy(site, name);

        study.setPvInfo("Worksheet");
        String pvInfoLabel = "Visit Type";
        String[] values = new String[] { "toto", "titi", "tata" };
        study.setPvInfo(pvInfoLabel, values);
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
    public void testSetPatientCollectionAdd() throws Exception {
        String name = "testSetPatientCollectionAdd" + r.nextInt();
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
    public void testSetPatientCollectionRemove() throws Exception {
        String name = "testSetPatientCollectionRemove" + r.nextInt();
        SiteWrapper site = SiteHelper.addSite(name);
        StudyWrapper study = StudyHelper.addStudy(site, name);
        int nber = PatientHelper.addPatients(name, study);

        List<PatientWrapper> patients = study.getPatientCollection();
        PatientWrapper patient = DbHelper.chooseRandomlyInList(patients);
        patients.remove(patient);
        study.setPatientCollection(patients);
        try {
            study.persist();
            Assert.fail("a patient is missing and is not deleted");
        } catch (BiobankCheckException bce) {
            Assert.assertTrue(true);
        }

        patient.delete();
        study.persist();

        study.reload();
        // one patient added
        Assert.assertEquals(nber - 1, study.getPatientCollection().size());
    }

    @Test
    public void testHasPatients() throws Exception {
        String name = "testHasPatients" + r.nextInt();
        SiteWrapper site = SiteHelper.addSite(name);
        StudyWrapper study = StudyHelper.addStudy(site, name);

        Assert.assertFalse(study.hasPatients());

        PatientHelper.addPatients(name, study);
        Assert.assertTrue(study.hasPatients());
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
        ShipmentWrapper shipment1 = ShipmentHelper.addShipment(clinic1,
            patient1);
        PatientWrapper patient2 = PatientHelper.addPatient(name + "PATIENT2",
            study1);
        ShipmentWrapper shipment2 = ShipmentHelper.addShipment(clinic2,
            patient1, patient2);
        // clinic 1 = 1 patient pour study 1
        PatientVisitHelper.addPatientVisits(patient1, shipment1);
        PatientVisitHelper.addPatientVisits(patient1, shipment2);
        // clinic 2 = 2 patients pour study 1
        PatientVisitHelper.addPatientVisits(patient2, shipment2);

        study1.reload();
        clinic1.reload();
        clinic2.reload();
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
        ShipmentWrapper shipment1 = ShipmentHelper.addShipment(clinic1,
            patient1);
        ShipmentWrapper shipment2 = ShipmentHelper.addShipment(clinic2,
            patient1);
        int nber = PatientVisitHelper.addPatientVisits(patient1, shipment1)
            .size();
        int nber2 = PatientVisitHelper.addPatientVisits(patient1, shipment2)
            .size();

        study1.reload();
        clinic1.reload();
        clinic2.reload();
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

        StudyWrapper study2 = StudyHelper.addStudy(site, name + "STUDY2");
        study2.setContactCollection(contacts);
        study2.persist();
        PatientWrapper patient2 = PatientHelper.addPatient(name + "2", study2);

        ShipmentWrapper shipment1 = ShipmentHelper.addShipment(clinic1,
            patient1, patient2);
        ShipmentWrapper shipment2 = ShipmentHelper.addShipment(clinic2,
            patient1, patient2);
        int nber = PatientVisitHelper.addPatientVisits(patient1, shipment1)
            .size();
        int nber2 = PatientVisitHelper.addPatientVisits(patient1, shipment2)
            .size();
        PatientVisitHelper.addPatientVisits(patient2, shipment1);
        PatientVisitHelper.addPatientVisits(patient2, shipment2);

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
    public void testPersistFailCheckStudyNameUnique() throws Exception {
        String name = "testPersistFailCheckStudyNameUnique" + r.nextInt();
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
    public void testPersistFailCheckContactsFromSameSite() throws Exception {
        String name = "testPersistFailCheckContactsFromSameSite";
        SiteWrapper site = SiteHelper.addSite(name);
        StudyWrapper study = StudyHelper.addStudy(site, name);

        SiteWrapper site2 = SiteHelper.addSite(name + "_2");
        ClinicWrapper clinic = ClinicHelper.addClinic(site2, name);
        ContactWrapper contact = ContactHelper.addContact(clinic, name);

        study.setContactCollection(Arrays
            .asList(new ContactWrapper[] { contact }));
        try {
            study.persist();
            Assert.fail("Contact should be in same site");
        } catch (BiobankCheckException bce) {
            Assert.assertTrue(true);
        }
    }

    @Test
    public void testDelete() throws Exception {
        String name = "testDelete" + r.nextInt();
        StudyWrapper study = StudyHelper.addStudy(SiteHelper.addSite(name),
            name);

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
    public void testDeleteFailNoMorePatient() throws Exception {
        String name = "testDeleteFailNoMorePatient" + r.nextInt();
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
        String name = "testResetAlreadyInDatabase" + r.nextInt();
        StudyWrapper study = StudyHelper.addStudy(SiteHelper.addSite(name),
            name);
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

    @Test
    public void testCompareTo() throws Exception {
        String name = "testCompareTo" + r.nextInt();
        SiteWrapper site = SiteHelper.addSite(name);
        StudyWrapper study = StudyHelper.addStudy(site, "WERTY" + name);
        StudyWrapper study2 = StudyHelper.addStudy(site, "AASDF" + name);

        Assert.assertTrue(study.compareTo(study2) > 0);
        Assert.assertTrue(study2.compareTo(study) < 0);
    }
}
