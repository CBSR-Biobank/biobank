package test.ualberta.med.biobank.wrappers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import test.ualberta.med.biobank.TestDatabase;
import test.ualberta.med.biobank.Utils;
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
import edu.ualberta.med.biobank.common.wrappers.PatientVisitWrapper;
import edu.ualberta.med.biobank.common.wrappers.PatientWrapper;
import edu.ualberta.med.biobank.common.wrappers.SampleSourceWrapper;
import edu.ualberta.med.biobank.common.wrappers.SampleStorageWrapper;
import edu.ualberta.med.biobank.common.wrappers.SampleTypeWrapper;
import edu.ualberta.med.biobank.common.wrappers.ShipmentWrapper;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;
import edu.ualberta.med.biobank.common.wrappers.StudyWrapper;
import edu.ualberta.med.biobank.model.Study;

public class TestStudy extends TestDatabase {

    private static List<PatientVisitWrapper> studyAddPatientVisits(
        StudyWrapper study) throws Exception {
        String name = study.getName();
        SiteWrapper site = study.getSite();
        ClinicWrapper clinic = ClinicHelper.addClinic(site, name + "CLINIC1");
        ContactWrapper contact = ContactHelper.addContact(clinic, name
            + "CONTACT1");
        List<ContactWrapper> contacts = new ArrayList<ContactWrapper>();
        contacts.add(contact);
        study.addContacts(contacts);
        study.persist();
        study.reload();
        PatientWrapper patient = PatientHelper.addPatient(name, study);
        ShipmentWrapper shipment = ShipmentHelper.addShipment(clinic, patient);
        return PatientVisitHelper.addPatientVisits(patient, shipment);

    }

    // the methods to skip in the getters and setters test
    private static final List<String> GETTER_SKIP_METHODS = Arrays
        .asList("getStudyPvAttrLocked");

    @Test
    public void testGettersAndSetters() throws Exception {
        String name = "testGettersAndSetters" + r.nextInt();
        SiteWrapper site = SiteHelper.addSite(name);
        StudyWrapper study = StudyHelper.addStudy(site, name);
        testGettersAndSetters(study, GETTER_SKIP_METHODS);
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
    public void testAddContacts() throws Exception {
        String name = "testAddContacts" + r.nextInt();
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
        study.addContacts(Arrays.asList(contactToAdd));
        study.persist();

        study.reload();
        // one contact added
        Assert.assertEquals(nber + 1, study.getContactCollection().size());
    }

    @Test
    public void testRemoveContacts() throws Exception {
        String name = "testRemoveContacts" + r.nextInt();
        SiteWrapper site = SiteHelper.addSite(name);
        StudyWrapper study = StudyHelper.addStudy(site, name);
        int nber = ContactHelper.addContactsToStudy(study, name);
        site.reload();

        // get a clinic not yet added
        List<ContactWrapper> contacts = study.getContactCollection();
        ContactWrapper contact = DbHelper.chooseRandomlyInList(contacts);
        // don't have to delete contact because this is a *..* relation
        study.removeContacts(Arrays.asList(contact));
        study.persist();

        study.reload();
        // one contact added
        Assert.assertEquals(nber - 1, study.getContactCollection().size());
    }

    @Test
    public void testContactsNotAssoc() throws Exception {
        String name = "testContactsNotAssoc" + r.nextInt();
        SiteWrapper site = SiteHelper.addSite(name);
        StudyWrapper study1 = StudyHelper.addStudy(site, name);
        StudyWrapper study2 = StudyHelper.addStudy(site, name + "_2");
        site.reload();

        ClinicWrapper clinic = ClinicHelper.addClinic(site, "CL1");
        int contactCount = ContactHelper.addContactsToClinic(clinic, "CL1-CT",
            5, 10);

        Assert.assertEquals(contactCount, study1.getContactsNotAssoc().size());

        List<ContactWrapper> contacts = clinic.getContactCollection();
        Assert.assertNotNull(contacts);

        // associate all contacts with study1
        for (int i = 0; i < contactCount; ++i) {
            study1.addContacts(Arrays.asList(contacts.get(i)));
            study1.persist();
            study1.reload();
            Assert.assertEquals(contactCount - i - 1, study1
                .getContactsNotAssoc().size());
        }

        // move all contacts to study2
        for (int i = 0; i < contactCount; ++i) {
            study1.removeContacts(Arrays.asList(contacts.get(i)));
            study1.persist();
            study1.reload();
            study2.addContacts(Arrays.asList(contacts.get(i)));
            study2.persist();
            study2.reload();
            Assert.assertEquals(i + 1, study1.getContactsNotAssoc().size());
        }

        // remove contacts one by one
        while (contacts.size() > 0) {
            ContactWrapper contact = contacts.get(0);
            contact.reload();
            study2.removeContacts(Arrays.asList(contact));
            study2.persist();
            study2.reload();
            contact.delete();
            contacts.remove(0);
            Assert.assertEquals(contacts.size(), study1.getContactsNotAssoc()
                .size());
            contacts = clinic.getContactCollection();
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
    public void testAddSampleStorages() throws Exception {
        String name = "testAddSampleStorages" + r.nextInt();
        SiteWrapper site = SiteHelper.addSite(name);
        StudyWrapper study = StudyHelper.addStudy(site, name);
        int nber = SampleStorageHelper.addSampleStorages(study, name);

        SampleTypeWrapper type = SampleTypeHelper.addSampleType(site, name);
        SampleStorageWrapper newStorage = SampleStorageHelper.newSampleStorage(
            study, type);
        study.addSampleStorages(Arrays.asList(newStorage));
        study.persist();

        study.reload();
        // one storage added
        Assert
            .assertEquals(nber + 1, study.getSampleStorageCollection().size());
    }

    @Test
    public void testRemoveSampleStorages() throws Exception {
        String name = "testRemoveSampleStorages" + r.nextInt();
        SiteWrapper site = SiteHelper.addSite(name);
        StudyWrapper study = StudyHelper.addStudy(site, name);
        int nber = SampleStorageHelper.addSampleStorages(study, name);

        List<SampleStorageWrapper> storages = study
            .getSampleStorageCollection();
        SampleStorageWrapper storage = DbHelper.chooseRandomlyInList(storages);
        study.removeSampleStorages(Arrays.asList(storage));
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
    public void testAddSampleSources() throws Exception {
        String name = "testAddSampleSources" + r.nextInt();
        SiteWrapper site = SiteHelper.addSite(name);
        StudyWrapper study = StudyHelper.addStudy(site, name);
        int nber = SampleSourceHelper.addSampleSources(study, name);

        SampleSourceWrapper source = SampleSourceHelper.addSampleSource(name);
        study.addSampleSources(Arrays.asList(source));
        study.persist();

        study.reload();
        // one storage added
        Assert.assertEquals(nber + 1, study.getSampleSourceCollection().size());
    }

    @Test
    public void testRemoveSampleSources() throws Exception {
        String name = "testRemoveSampleSources" + r.nextInt();
        SiteWrapper site = SiteHelper.addSite(name);
        StudyWrapper study = StudyHelper.addStudy(site, name);
        int nber = SampleSourceHelper.addSampleSources(study, name);

        List<SampleSourceWrapper> sources = study.getSampleSourceCollection();
        SampleSourceWrapper source = DbHelper.chooseRandomlyInList(sources);
        // don't have to delete the storage thanks to
        // deleteSampleSourceDifference method
        SampleSourceHelper.createdSampleSources.remove(source);
        study.removeSampleSources(Arrays.asList(source));
        study.persist();

        study.reload();
        // one storage added
        Assert.assertEquals(nber - 1, study.getSampleSourceCollection().size());
    }

    @Test
    public void testSetStudyPvAttr() throws Exception {
        String name = "testSetStudyPvAttr" + r.nextInt();
        SiteWrapper site = SiteHelper.addSite(name);
        StudyWrapper study = StudyHelper.addStudy(site, name);

        List<String> types = SiteWrapper.getPvAttrTypeNames(appService);
        Assert.assertTrue(types.contains("text"));
        Assert.assertTrue(types.contains("select_single"));

        study.setStudyPvAttr("Worksheet", "text");
        study.setStudyPvAttr("Visit Type", "select_single", new String[] {
            "toto", "titi", "tata" });
        study.persist();
        study.reload();

        // set non existing type, expect exception
        try {
            study.setStudyPvAttr(Utils.getRandomString(10, 15), Utils
                .getRandomString(10, 15));
            Assert.fail("call should generate an exception");
        } catch (Exception e) {
            Assert.assertTrue(true);
        }

        Assert.assertEquals(2, study.getStudyPvAttrLabels().length);

        study.deleteStudyPvAttr("Worksheet");
        study.persist();
        Assert.assertEquals(1, study.getStudyPvAttrLabels().length);

        study.deleteStudyPvAttr("Visit Type");
        study.persist();
        Assert.assertEquals(0, study.getStudyPvAttrLabels().length);

        // add patient visit that uses the attribute and try to delete
        study.setStudyPvAttr("Worksheet", "text");
        study.persist();
        study.reload();
        List<PatientVisitWrapper> visits = studyAddPatientVisits(study);
        PatientVisitWrapper visit = visits.get(0);
        visit.setPvAttrValue("Worksheet", Utils.getRandomString(10, 15));
        visit.persist();

        // delete non existing label, expect exception
        try {
            study.deleteStudyPvAttr("Worksheet");
            Assert.fail("call should generate an exception");
        } catch (Exception e) {
            Assert.assertTrue(true);
        }
    }

    @Test
    public void testGetStudyPvAttrLabels() throws Exception {
        String name = "testGetSetStudyPvAttrLabels" + r.nextInt();
        SiteWrapper site = SiteHelper.addSite(name);
        StudyWrapper study = StudyHelper.addStudy(site, name);

        study.setStudyPvAttr("Worksheet", "text");
        study.setStudyPvAttr("Consent", "select_multiple", new String[] { "a",
            "b" });
        Assert.assertEquals(2, study.getStudyPvAttrLabels().length);

        // test still ok after persist
        study.persist();
        study.reload();
        Assert.assertEquals(2, study.getStudyPvAttrLabels().length);
    }

    @Test
    public void testGetStudyPvAttrType() throws Exception {
        String name = "testGetStudyPvAttrType" + r.nextInt();
        SiteWrapper site = SiteHelper.addSite(name);
        StudyWrapper study = StudyHelper.addStudy(site, name);

        study.setStudyPvAttr("Worksheet", "text");
        study.setStudyPvAttr("Visit Type", "select_single", new String[] {
            "toto", "titi", "tata" });
        study.persist();

        List<String> labels = Arrays.asList(study.getStudyPvAttrLabels());
        Assert.assertEquals(2, labels.size());
        Assert.assertTrue(labels.contains("Worksheet"));
        Assert.assertTrue(labels.contains("Visit Type"));
        Assert.assertEquals("text", study.getStudyPvAttrType("Worksheet"));
        Assert.assertEquals("select_single", study
            .getStudyPvAttrType("Visit Type"));

        // get non existing label, expect exception
        try {
            study.getStudyPvAttrType(Utils.getRandomString(10, 20));
            Assert.fail("call should generate an exception");
        } catch (Exception e) {
            Assert.assertTrue(true);
        }
    }

    @Test
    public void testGetStudyPvAttrPermissible() throws Exception {
        String name = "testGetStudyPvAttrType" + r.nextInt();
        SiteWrapper site = SiteHelper.addSite(name);
        StudyWrapper study = StudyHelper.addStudy(site, name);

        study.setStudyPvAttr("Worksheet", "text");
        String pvInfoLabel = "Visit Type";

        for (int i = 0; i < 4; ++i) {
            String[] values;

            switch (i) {
            case 0:
                values = new String[] { "toto", "titi", "tata" };
                break;
            case 1:
                values = new String[] { "toto", "titi" };
                break;
            case 2:
                values = new String[] { "toto" };
                break;
            case 3:
            default:
                values = null;
            }
            study.setStudyPvAttr(pvInfoLabel, "select_single", values);
            study.persist();

            study.reload();
            if (values != null) {
                String[] valuesFound = study
                    .getStudyPvAttrPermissible(pvInfoLabel);
                List<String> valuesList = Arrays.asList(values);
                Assert.assertTrue(valuesFound.length == values.length);
                for (String s : valuesFound) {
                    Assert.assertTrue(valuesList.contains(s));
                }
            } else {
                try {
                    // this label should have been removed
                    study.getStudyPvAttrPermissible(pvInfoLabel);
                    Assert.fail("call should generate an exception");
                } catch (Exception e) {
                    Assert.assertTrue(true);
                }
            }
        }
    }

    @Test
    public void testGetStudyPvAttrLocked() throws Exception {
        String name = "testGetStudyPvAttrType" + r.nextInt();
        SiteWrapper site = SiteHelper.addSite(name);
        StudyWrapper study = StudyHelper.addStudy(site, name);

        study.setStudyPvAttr("Worksheet", "text");
        study.persist();
        study.reload();

        // attributes are not locked by default
        Assert.assertEquals(false, study.getStudyPvAttrLocked("Worksheet")
            .booleanValue());

        // lock the attribute
        study.setStudyPvAttrLocked("Worksheet", true);
        Assert.assertEquals(true, study.getStudyPvAttrLocked("Worksheet")
            .booleanValue());

        // get lock for non existing label, expect exception
        try {
            study.getStudyPvAttrLocked(Utils.getRandomString(10, 20));
            Assert.fail("call should generate an exception");
        } catch (Exception e) {
            Assert.assertTrue(true);
        }

        // set lock for non existing label, expect exception
        try {
            study.setStudyPvAttrLocked(Utils.getRandomString(10, 20), false);
            Assert.fail("call should generate an exception");
        } catch (Exception e) {
            Assert.assertTrue(true);
        }
        // add patient visit that uses the locked attribute
        study.setStudyPvAttr("Worksheet", "text");
        study.setStudyPvAttrLocked("Worksheet", true);
        study.persist();
        study.reload();
        List<PatientVisitWrapper> visits = studyAddPatientVisits(study);
        PatientVisitWrapper visit = visits.get(0);
        visit.reload();

        try {
            visit.setPvAttrValue("Worksheet", Utils.getRandomString(10, 15));
            Assert.fail("call should generate an exception");
        } catch (Exception e) {
            Assert.assertTrue(true);
        }
    }

    @Test
    public void testRemoveStudyPvAttr() throws Exception {
        String name = "testRemoveStudyPvAttr" + r.nextInt();
        SiteWrapper site = SiteHelper.addSite(name);
        StudyWrapper study = StudyHelper.addStudy(site, name);

        int sizeOrig = study.getStudyPvAttrLabels().length;
        List<String> types = SiteWrapper.getPvAttrTypeNames(appService);
        if (types.size() < 2) {
            Assert.fail("Can't test without PvAttrTypes");
        }

        study.setStudyPvAttr(name, "text");
        study.setStudyPvAttr(name + "_2", "number");
        study.persist();

        study.reload();
        Assert.assertEquals(sizeOrig + 2, study.getStudyPvAttrLabels().length);
        study.deleteStudyPvAttr(name);
        Assert.assertEquals(sizeOrig + 1, study.getStudyPvAttrLabels().length);
        site.persist();

        site.reload();
        Assert.assertEquals(sizeOrig + 1, study.getStudyPvAttrLabels().length);
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
    public void testAddPatients() throws Exception {
        String name = "testAddPatients" + r.nextInt();
        SiteWrapper site = SiteHelper.addSite(name);
        StudyWrapper study = StudyHelper.addStudy(site, name);
        int nber = PatientHelper.addPatients(name, study);

        PatientWrapper newPatient = PatientHelper.newPatient(name
            + "newPatient");
        newPatient.setStudy(study);
        study.addPatients(Arrays.asList(newPatient));
        study.persist();

        study.reload();
        // one patient added
        Assert.assertEquals(nber + 1, study.getPatientCollection().size());
    }

    @Test
    public void testRemovePatients() throws Exception {
        String name = "testRemovePatients" + r.nextInt();
        SiteWrapper site = SiteHelper.addSite(name);
        StudyWrapper study = StudyHelper.addStudy(site, name);
        int nber = PatientHelper.addPatients(name, study);

        List<PatientWrapper> patients = study.getPatientCollection();
        PatientWrapper patient = DbHelper.chooseRandomlyInList(patients);
        study.removePatients(Arrays.asList(patient));
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
        study1.addContacts(contacts);
        study1.persist();
        PatientWrapper patient1 = PatientHelper.addPatient(name + "PATIENT1",
            study1);
        ShipmentWrapper shipment1 = ShipmentHelper.addShipment(clinic1,
            patient1);
        PatientWrapper patient2 = PatientHelper.addPatient(name + "PATIENT2",
            study1);
        ShipmentWrapper shipment2 = ShipmentHelper.addShipment(clinic2,
            patient1, patient2);
        // clinic 1 = 1 patient for study 1
        PatientVisitHelper.addPatientVisits(patient1, shipment1);
        PatientVisitHelper.addPatientVisits(patient1, shipment2);
        // clinic 2 = 2 patients for study 1
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

        StudyWrapper study1 = StudyHelper.addStudy(site, name + "STUDY1");
        study1.addContacts(Arrays.asList(contact1, contact2));
        study1.persist();

        StudyWrapper study2 = StudyHelper.addStudy(site, name + "STUDY2");
        study2.addContacts(Arrays.asList(contact2));
        study2.persist();

        PatientWrapper patient1 = PatientHelper.addPatient(name, study1);
        PatientWrapper patient2 = PatientHelper
            .addPatient(name + "_p2", study2);
        PatientWrapper patient3 = PatientHelper
            .addPatient(name + "_p3", study1);

        ShipmentWrapper shipment1 = ShipmentHelper.addShipment(clinic1,
            patient1, patient3);
        ShipmentWrapper shipment2 = ShipmentHelper.addShipment(clinic2,
            patient1, patient2);

        // shipment1 has patient visits for patient1 and patient3
        int nber = PatientVisitHelper.addPatientVisits(patient1, shipment1)
            .size();
        int nber2 = PatientVisitHelper.addPatientVisits(patient3, shipment1)
            .size();

        // shipment 2 has patient visits for patient1 and patient2
        int nber3 = PatientVisitHelper.addPatientVisits(patient1, shipment2)
            .size();
        int nber4 = PatientVisitHelper.addPatientVisits(patient2, shipment2)
            .size();

        study1.reload();
        clinic1.reload();
        clinic2.reload();

        Assert.assertEquals(nber + nber2, study1
            .getPatientVisitCountForClinic(clinic1));
        Assert.assertEquals(nber3, study1
            .getPatientVisitCountForClinic(clinic2));
        Assert.assertEquals(nber4, study2
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
        study1.addContacts(contacts);
        study1.persist();
        PatientWrapper patient1 = PatientHelper.addPatient(name, study1);

        StudyWrapper study2 = StudyHelper.addStudy(site, name + "STUDY2");
        study2.addContacts(contacts);
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
    public void testLinkedToClinic() throws Exception {
        String name = "testLinkedToClinic" + r.nextInt();
        SiteWrapper site = SiteHelper.addSite(name);

        ClinicWrapper clinic1 = ClinicHelper.addClinic(site, name + "CLINIC1");
        ContactWrapper contact1 = ContactHelper.addContact(clinic1, name
            + "CONTACT1");

        ClinicWrapper clinic2 = ClinicHelper.addClinic(site, name + "CLINIC2");
        ContactWrapper contact2 = ContactHelper.addContact(clinic2, name
            + "CONTACT2");

        StudyWrapper study1 = StudyHelper.addStudy(site, name + "STUDY1");
        study1.addContacts(Arrays.asList(contact1));
        study1.persist();

        StudyWrapper study2 = StudyHelper.addStudy(site, name + "STUDY2");
        study2.addContacts(Arrays.asList(contact2));
        study2.persist();

        Assert.assertTrue(study1.isLinkedToClinic(clinic1));
        Assert.assertFalse(study1.isLinkedToClinic(clinic2));

        Assert.assertFalse(study2.isLinkedToClinic(clinic1));
        Assert.assertTrue(study2.isLinkedToClinic(clinic2));
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
    public void testPersitCheckNameNotEmpty() throws Exception {
        String name = "testPersitCheckNameNotEmpty" + r.nextInt();
        SiteWrapper site = SiteHelper.addSite(name);
        StudyWrapper s1 = StudyHelper.newStudy(site, null);
        try {
            s1.persist();
            Assert.fail("Should not insert the study : name empty");
        } catch (BiobankCheckException bce) {
            Assert.assertTrue(true);
        }
    }

    @Test
    public void testPersitCheckNameShortNotEmpty() throws Exception {
        String name = "testPersitCheckNameShortNotEmpty" + r.nextInt();
        SiteWrapper site = SiteHelper.addSite(name);
        StudyWrapper s1 = StudyHelper.newStudy(site, name);
        s1.setNameShort(null);
        try {
            s1.persist();
            Assert.fail("Should not insert the study : name short empty");
        } catch (BiobankCheckException bce) {
            Assert.assertTrue(true);
        }
    }

    @Test
    public void testPersitCheckStudyShortNameUnique() throws Exception {
        String name = "testCheckStudyShortNameUnique" + r.nextInt();
        SiteWrapper site = SiteHelper.addSite(name);
        StudyWrapper s1 = StudyHelper.newStudy(site, name);
        s1.setNameShort(name);
        s1.persist();

        StudyWrapper s2 = StudyHelper.newStudy(site, name + "_2");
        s2.setNameShort(name);
        try {
            s2.persist();
            Assert
                .fail("Should not insert the study : same short name already in database");
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

        study.addContacts(Arrays.asList(contact));
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

    @Test
    public void testHasClinic() throws Exception {
        String name = "testHasClinic" + r.nextInt();
        SiteWrapper site = SiteHelper.addSite(name);

        ClinicWrapper clinic1 = ClinicHelper.addClinic(site, name);
        ContactWrapper contact1 = ContactHelper.addContact(clinic1, name);
        ClinicWrapper clinic2 = ClinicHelper.addClinic(site, name + "_2");
        ContactHelper.addContact(clinic2, name);

        StudyWrapper study = StudyHelper.addStudy(site, name);
        study.addContacts(Arrays.asList(contact1));
        study.persist();

        study.reload();

        Assert.assertTrue(study.hasClinic(clinic1.getName()));
        Assert.assertFalse(study.hasClinic(clinic2.getName()));
    }

    @Test
    public void testGetPatient() throws Exception {
        String name = "testGetPatient" + r.nextInt();
        SiteWrapper site = SiteHelper.addSite(name);

        StudyWrapper study = StudyHelper.addStudy(site, name);
        PatientWrapper patient1 = PatientHelper.addPatient(name + "_1", study);
        PatientWrapper patient2 = PatientHelper.addPatient(name + "_2", study);

        StudyWrapper study2 = StudyHelper.addStudy(site, name + "_2");
        PatientWrapper patient3 = PatientHelper.addPatient(name + "_3", study2);

        study.reload();
        Assert.assertEquals(patient1, study.getPatient(name + "_1"));
        Assert.assertEquals(patient2, study.getPatient(name + "_2"));
        Assert.assertEquals(patient3, study2.getPatient(name + "_3"));
        Assert.assertNull(study.getPatient(name + "_3"));
        Assert.assertNull(study2.getPatient(name + "_1"));
    }
}
