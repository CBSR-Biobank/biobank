package edu.ualberta.med.biobank.test.wrappers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import edu.ualberta.med.biobank.common.exception.BiobankCheckException;
import edu.ualberta.med.biobank.common.wrappers.ActivityStatusWrapper;
import edu.ualberta.med.biobank.common.wrappers.ClinicWrapper;
import edu.ualberta.med.biobank.common.wrappers.CollectionEventWrapper;
import edu.ualberta.med.biobank.common.wrappers.ContactWrapper;
import edu.ualberta.med.biobank.common.wrappers.PatientWrapper;
import edu.ualberta.med.biobank.common.wrappers.SpecimenTypeWrapper;
import edu.ualberta.med.biobank.common.wrappers.SpecimenWrapper;
import edu.ualberta.med.biobank.common.wrappers.StudyWrapper;
import edu.ualberta.med.biobank.model.Clinic;
import edu.ualberta.med.biobank.model.Contact;
import edu.ualberta.med.biobank.server.applicationservice.exceptions.BiobankSessionException;
import edu.ualberta.med.biobank.server.applicationservice.exceptions.DuplicatePropertySetException;
import edu.ualberta.med.biobank.server.applicationservice.exceptions.ValueNotSetException;
import edu.ualberta.med.biobank.test.TestDatabase;
import edu.ualberta.med.biobank.test.internal.ClinicHelper;
import edu.ualberta.med.biobank.test.internal.CollectionEventHelper;
import edu.ualberta.med.biobank.test.internal.ContactHelper;
import edu.ualberta.med.biobank.test.internal.DbHelper;
import edu.ualberta.med.biobank.test.internal.PatientHelper;
import edu.ualberta.med.biobank.test.internal.SpecimenHelper;
import edu.ualberta.med.biobank.test.internal.StudyHelper;

public class TestClinic extends TestDatabase {

    @Test
    public void testGettersAndSetters() throws BiobankCheckException, Exception {
        String name = "testGettersAndSetters" + r.nextInt();
        ClinicWrapper clinic = ClinicHelper.addClinic(name);
        testGettersAndSetters(clinic);
    }

    @Test
    public void testGetContactCollection() throws Exception {
        String name = "testGetContactCollection" + r.nextInt();
        ClinicWrapper clinic = ClinicHelper.addClinic(name);
        int nber = r.nextInt(5) + 1;
        for (int i = 0; i < nber; i++) {
            ContactHelper.addContact(clinic, name + i);
        }
        clinic.reload();
        List<ContactWrapper> contacts = clinic.getContactCollection(false);
        int sizeFound = contacts.size();

        Assert.assertEquals(nber, sizeFound);
    }

    @Test
    public void testGetContactCollectionBoolean() throws Exception {
        String name = "testGetContactCollectionBoolean" + r.nextInt();
        ClinicWrapper clinic = ClinicHelper.addClinic(name, true);

        List<ContactWrapper> contacts = clinic.getContactCollection(true);
        if (contacts.size() > 1) {
            for (int i = 0; i < (contacts.size() - 1); i++) {
                ContactWrapper contact1 = contacts.get(i);
                ContactWrapper contact2 = contacts.get(i + 1);
                Assert.assertTrue(contact1.compareTo(contact2) <= 0);
            }
        }
    }

    @Test
    public void testAddContacts() throws Exception {
        String name = "testAddContacts" + r.nextInt();
        ClinicWrapper clinic = ClinicHelper.addClinic(name);
        int nber = r.nextInt(5) + 1;
        for (int i = 0; i < nber; i++) {
            ContactHelper.addContact(clinic, name + i);
        }
        clinic.reload();
        ContactWrapper contact = ContactHelper.newContact(clinic, name + "NEW");
        clinic.addToContactCollection(Arrays.asList(contact));
        clinic.persist();

        clinic.reload();
        // one contact added
        Assert
            .assertEquals(nber + 1, clinic.getContactCollection(false).size());
    }

    @Test
    public void testRemoveContacts() throws Exception {
        String name = "testRemoveContacts" + r.nextInt();
        ClinicWrapper clinic = ClinicHelper.addClinic(name);
        int nber = r.nextInt(5) + 5;
        for (int i = 0; i < nber; i++) {
            ContactHelper.addContact(clinic, name + i);
        }
        clinic.reload();
        List<ContactWrapper> contacts = clinic.getContactCollection(false);
        ContactWrapper contact = DbHelper.chooseRandomlyInList(contacts);
        // direct deletion
        contact.delete();

        clinic.reload();
        // one contact less
        contacts = clinic.getContactCollection(false);
        Assert.assertEquals(nber - 1, contacts.size());

        contact = DbHelper.chooseRandomlyInList(contacts);
        // remove through clinic persist
        clinic.removeFromContactCollection(Arrays.asList(contact));
        clinic.persist();
        clinic.reload();
        contacts = clinic.getContactCollection(false);
        Assert.assertEquals(nber - 2, contacts.size());

        ClinicWrapper clinic2 = ClinicHelper.addClinic(name + "_2");
        ContactWrapper otherContact = ContactHelper.addContact(clinic2, name
            + "_clinic2");
        try {
            // try to remove but check should fail
            clinic.removeFromContactCollectionWithCheck(Arrays
                .asList(otherContact));
            Assert
                .fail("Should throw an exception if the contact is not in the list");
        } catch (BiobankCheckException bce) {
            Assert.assertTrue(true);
        }

        clinic.removeFromContactCollection(Arrays.asList(otherContact));
        Assert.assertTrue(true);

        contact = DbHelper.chooseRandomlyInList(contacts);
        try {
            // try to remove but check should fail
            clinic.removeFromContactCollectionWithCheck(Arrays.asList(contact));
            Assert.assertTrue(true);
        } catch (BiobankCheckException bce) {
            Assert
                .fail("Should not throw an exception : the contact is in the list");
        }
    }

    @Test
    public void testGetStudyCollection() throws Exception {
        String name = "testGetStudyCollection" + r.nextInt();
        ClinicWrapper clinic = ClinicHelper.addClinic(name, true);
        StudyWrapper study1 = StudyHelper.addStudy(name + "STUDY1");
        study1.addToContactCollection(Arrays.asList(DbHelper
            .chooseRandomlyInList(clinic.getContactCollection(false))));
        study1.persist();

        ClinicWrapper clinic2 = ClinicHelper.addClinic(name + "CLINIC2", true);
        StudyWrapper study2 = StudyHelper.addStudy(name + "STUDY2");
        study2
            .addToContactCollection(Arrays.asList(DbHelper
                .chooseRandomlyInList(clinic.getContactCollection(false)),
                DbHelper.chooseRandomlyInList(clinic2
                    .getContactCollection(false))));
        study2.persist();

        clinic.reload();

        Assert.assertEquals(2, clinic.getStudyCollection().size());
        Assert.assertEquals(1, clinic2.getStudyCollection().size());
    }

    @Test
    public void testGetStudyCollectionBoolean() throws Exception {
        String name = "testGetStudyCollectionBoolean" + r.nextInt();
        ClinicWrapper clinic = ClinicHelper.addClinic(name, true);
        StudyWrapper study1 = StudyHelper.addStudy(name + "STUDY1");
        study1.addToContactCollection(Arrays.asList(DbHelper
            .chooseRandomlyInList(clinic.getContactCollection(false))));
        study1.persist();
        StudyWrapper study2 = StudyHelper.addStudy(name + "STUDY2");
        study2.addToContactCollection(Arrays.asList(DbHelper
            .chooseRandomlyInList(clinic.getContactCollection(false))));
        study2.persist();

        clinic.reload();

        List<StudyWrapper> studies = clinic.getStudyCollection();
        if (studies.size() > 1) {
            for (int i = 0; i < (studies.size() - 1); i++) {
                StudyWrapper s1 = studies.get(i);
                StudyWrapper s2 = studies.get(i + 1);
                Assert.assertTrue(s1.compareTo(s2) <= 0);
            }
        }
    }

    @Test
    public void testPersist() throws Exception {
        String name = "testPersist" + r.nextInt();
        int oldTotal = appService.search(Clinic.class, new Clinic()).size();
        ClinicHelper.addClinic(name);

        int newTotal = appService.search(Clinic.class, new Clinic()).size();
        Assert.assertEquals(oldTotal + 1, newTotal);
    }

    @Test
    public void testPersistFailAddressNotNull() throws Exception {
        String name = "testPersistFailAddressNotNul" + r.nextInt();
        ClinicHelper.addClinic(name + "_1");
        int oldTotal = ClinicWrapper.getAllClinics(appService).size();

        ClinicWrapper clinic = new ClinicWrapper(appService);
        clinic.setName(name);
        clinic.setNameShort(name);
        clinic.setActivityStatus(ActivityStatusWrapper
            .getActiveActivityStatus(appService));
        try {
            clinic.persist();
            Assert.fail("Should not insert the clinic : no address");
        } catch (ValueNotSetException vnse) {
            Assert.assertTrue(true);
        }

        clinic.setCity("Vesoul");
        clinic.persist();
        Assert.assertEquals(oldTotal + 1,
            ClinicWrapper.getAllClinics(appService).size());
        clinic.delete();
    }

    @Test
    public void testPersistFailActivityStatusNull() throws Exception {
        String name = "testPersistFailActivityStatusNull" + r.nextInt();
        ClinicWrapper clinic = new ClinicWrapper(appService);
        clinic.setName(name);
        clinic.setNameShort(name);
        clinic.setCity("Rupt");

        try {
            clinic.persist();
            Assert.fail("Should not insert the clinic : no activity status");
        } catch (ValueNotSetException vnse) {
            Assert.assertTrue(true);
        }
        clinic.setActivityStatus(ActivityStatusWrapper
            .getActiveActivityStatus(appService));
        clinic.persist();
        clinic.delete();
    }

    @Test
    public void testPersistFailNameUnique() throws Exception {
        String name = "testPersistFailNameUnique" + r.nextInt();
        ClinicHelper.addClinic(name);
        int oldTotal = ClinicWrapper.getAllClinics(appService).size();

        ClinicWrapper clinic = ClinicHelper.newClinic(name);
        clinic.setNameShort(name + "_NS");
        try {
            clinic.persist();
            Assert
                .fail("Should not insert the clinic : same name already in database for this site");
        } catch (DuplicatePropertySetException e) {
            Assert.assertTrue(true);
        }
        clinic.setName(name + "_otherName");
        clinic.persist();
        int newTotal = ClinicWrapper.getAllClinics(appService).size();
        Assert.assertEquals(oldTotal + 1, newTotal);
        clinic.delete();
    }

    @Test
    public void testDelete() throws Exception {
        String name = "testDelete" + r.nextInt();
        ClinicWrapper clinic = ClinicHelper.addClinic(name, false, false);

        // object is in database
        Clinic clinicInDB = ModelUtils.getObjectWithId(appService,
            Clinic.class, clinic.getId());
        Assert.assertNotNull(clinicInDB);

        Integer clinicId = clinic.getId();

        clinic.delete();

        clinicInDB = ModelUtils.getObjectWithId(appService, Clinic.class,
            clinicId);
        // object is not anymore in database
        Assert.assertNull(clinicInDB);
    }

    @Test
    public void testDeleteWithContacts() throws Exception {
        String name = "testDeleteWithContacts" + r.nextInt();
        ClinicWrapper clinic = ClinicHelper.addClinic(name, false, false);
        int contactId = ContactHelper.addContact(clinic, name).getId();
        Contact contactInDB = ModelUtils.getObjectWithId(appService,
            Contact.class, contactId);
        Assert.assertNotNull(contactInDB);
        clinic.reload();

        clinic.delete();

        contactInDB = ModelUtils.getObjectWithId(appService, Contact.class,
            contactId);
        Assert.assertNull(contactInDB);
    }

    @Test
    public void testDeleteWithContactsLinkedToStudy() throws Exception {
        String name = "testDeleteWithContacts" + r.nextInt();
        ClinicWrapper clinic = ClinicHelper.addClinic(name);
        ContactWrapper contact = ContactHelper.addContact(clinic, name);

        StudyWrapper study = StudyHelper.addStudy(name);
        study.addToContactCollection(Arrays.asList(contact));
        study.persist();

        clinic.reload();
        contact.reload();

        try {
            clinic.delete();
            Assert
                .fail("Can't remove a clinic if a study linked to one of its contacts still exists");
        } catch (BiobankSessionException e) {
            Assert.assertTrue(true);
        }
    }

    @Test
    public void testDeleteWithCollectionEvents() throws Exception {
        String name = "testDeleteWithCollectionEvents" + r.nextInt();
        ClinicWrapper clinic = ClinicHelper.addClinic(name);
        ContactWrapper contact = ContactHelper.addContact(clinic, name);

        StudyWrapper study = StudyHelper.addStudy(name);
        study.addToContactCollection(Arrays.asList(contact));
        study.persist();
        PatientWrapper patient = PatientHelper.addPatient(name, study);
        SpecimenWrapper sv = SpecimenHelper.newSpecimen(SpecimenTypeWrapper
            .getAllSpecimenTypes(appService, false).get(0));

        CollectionEventHelper.addCollectionEvent(clinic, patient, 1, sv);

        clinic.reload();
        try {
            clinic.delete();
            Assert.fail("Can't remove a clinic if shipments linked to it");
        } catch (BiobankSessionException e) {
            Assert.assertTrue(true);
        }
    }

    @Test
    public void testResetAlreadyInDatabase() throws Exception {
        String name = "testResetAlreadyInDatabase" + r.nextInt();
        ClinicWrapper clinic = ClinicHelper.addClinic(name);
        clinic.reload();
        String oldName = clinic.getName();
        clinic.setName("toto");
        clinic.reset();
        Assert.assertEquals(oldName, clinic.getName());
    }

    @Test
    public void testResetNew() throws Exception {
        String name = "testResetAlreadyInDatabase" + r.nextInt();
        ClinicWrapper clinic = ClinicHelper.newClinic(name);
        clinic.reset();
        Assert.assertEquals(null, clinic.getName());
    }

    @Test
    public void testCompareTo() throws Exception {
        String name = "testCompareTo" + r.nextInt();
        ClinicWrapper clinic1 = ClinicHelper.addClinic("QWERTY" + name);
        ClinicWrapper clinic2 = ClinicHelper.addClinic("ASDFG" + name);

        Assert.assertTrue(clinic1.compareTo(clinic2) > 0);
        Assert.assertTrue(clinic2.compareTo(clinic1) < 0);
    }

    @Test
    public void testGetContact() throws Exception {
        String name = "testGetContact" + r.nextInt();
        ClinicWrapper clinic = ClinicHelper.addClinic(name);
        ContactWrapper contact1 = ContactHelper.addContact(clinic, name);
        String name1 = contact1.getName();
        ContactWrapper contact2 = ContactHelper.addContact(clinic, name);
        String name2 = contact2.getName();
        clinic.reload();

        Assert.assertEquals(contact1, clinic.getContact(name1));
        Assert.assertEquals(contact2, clinic.getContact(name2));
        Assert.assertNull(clinic.getContact(name + " **"));
    }

    @Test
    public void testGetPatientCount() throws Exception {
        String name = "testGetPatientCount" + r.nextInt();
        ClinicWrapper clinic1 = ClinicHelper.addClinic(name);
        ContactWrapper contact1 = ContactHelper.addContact(clinic1, name);
        ClinicWrapper clinic2 = ClinicHelper.addClinic(name + "_2");
        ContactWrapper contact2 = ContactHelper
            .addContact(clinic2, name + "_2");

        StudyWrapper study = StudyHelper.addStudy(name);
        study.addToContactCollection(Arrays.asList(contact1, contact2));
        study.persist();

        PatientWrapper patient;
        List<ClinicWrapper> clinics = Arrays.asList(clinic1, clinic2);
        Map<Integer, List<PatientWrapper>> patientMap = new HashMap<Integer, List<PatientWrapper>>();
        for (ClinicWrapper clinic : clinics) {
            patientMap.put(clinic.getId(), new ArrayList<PatientWrapper>());
        }

        // add patients
        for (int i = 0, n = r.nextInt(10) + 1; i < n; ++i) {
            patient = PatientHelper.addPatient(name + "_p" + i, study);
            ClinicWrapper clinic = clinics.get(i & 1);
            List<PatientWrapper> patientsForClinic = patientMap.get(clinic
                .getId());
            patientsForClinic.add(patient);
            SpecimenWrapper sv = SpecimenHelper.newSpecimen(SpecimenTypeWrapper
                .getAllSpecimenTypes(appService, false).get(0));
            CollectionEventHelper.addCollectionEvent(clinic, patient, i, sv);
            Assert.assertEquals(Long.valueOf(patientsForClinic.size()),
                clinic.getPatientCount());
        }

        // delete patients
        for (ClinicWrapper clinic : clinics) {
            List<PatientWrapper> patientsForClinic = patientMap.get(clinic
                .getId());
            while (patientsForClinic.size() > 0) {
                patient = patientsForClinic.get(0);
                patient.reload();
                DbHelper.deleteCollectionEvents(patient
                    .getCollectionEventCollection(false));
                patient.reload();
                patient.delete();
                patientsForClinic.remove(0);
                clinic.reload();
                Assert.assertEquals(Long.valueOf(patientsForClinic.size()),
                    clinic.getPatientCount());
            }
        }
    }

    @Test
    public void testGetPatientCountForStudy() throws Exception {
        String name = "testGetPatientCountForStudy" + r.nextInt();
        ClinicWrapper clinic1 = ClinicHelper.addClinic(name);
        ContactWrapper contact1 = ContactHelper.addContact(clinic1, name);
        ClinicWrapper clinic2 = ClinicHelper.addClinic(name + "_2");
        ContactWrapper contact2 = ContactHelper
            .addContact(clinic2, name + "_2");

        StudyWrapper study1 = StudyHelper.addStudy(name);
        study1.addToContactCollection(Arrays.asList(contact1));
        study1.persist();

        StudyWrapper study2 = StudyHelper.addStudy(name + "_2");
        study2.addToContactCollection(Arrays.asList(contact2));
        study2.persist();

        List<ClinicWrapper> clinics = Arrays.asList(clinic1, clinic2);
        List<StudyWrapper> studies = Arrays.asList(study1, study2);

        // ClinicID = {StudyID = patientCOunt}
        Map<Integer, Map<Integer, List<PatientWrapper>>> patientMap = new HashMap<Integer, Map<Integer, List<PatientWrapper>>>();

        for (ClinicWrapper clinic : clinics) {
            Map<Integer, List<PatientWrapper>> studyMap = new HashMap<Integer, List<PatientWrapper>>();
            for (StudyWrapper study : studies) {
                studyMap.put(study.getId(), new ArrayList<PatientWrapper>());
            }
            patientMap.put(clinic.getId(), studyMap);
        }
        // add patients
        for (int i = 0, n = r.nextInt(10) + 3; i < n; ++i) {
            StudyWrapper study = studies.get(i & 1);
            ClinicWrapper clinic = clinics.get(i & 1);
            PatientWrapper patient = PatientHelper.addPatient(name + "_p" + i,
                study);
            Map<Integer, List<PatientWrapper>> studyMap = patientMap.get(clinic
                .getId());
            List<PatientWrapper> patientsForClinicForStudy = studyMap.get(study
                .getId());
            patientsForClinicForStudy.add(patient);
            SpecimenWrapper sv = SpecimenHelper.newSpecimen(SpecimenTypeWrapper
                .getAllSpecimenTypes(appService, false).get(0));
            CollectionEventHelper.addCollectionEvent(clinic, patient, i, sv);
            Assert.assertEquals(patientsForClinicForStudy.size(),
                clinic.getPatientCountForStudy(study));
        }

        // delete patients
        for (ClinicWrapper clinic : clinics) {
            Map<Integer, List<PatientWrapper>> studyMap = patientMap.get(clinic
                .getId());
            for (StudyWrapper study : studies) {
                List<PatientWrapper> patientsForClinicForStudy = studyMap
                    .get(study.getId());
                while (patientsForClinicForStudy.size() > 0) {
                    PatientWrapper patient = patientsForClinicForStudy.get(0);
                    patient.reload();
                    DbHelper.deleteCollectionEvents(patient
                        .getCollectionEventCollection(false));
                    patient.reload();
                    patient.delete();
                    patientsForClinicForStudy.remove(0);
                    clinic.reload();
                    Assert.assertEquals(
                        Long.valueOf(patientsForClinicForStudy.size()),
                        clinic.getPatientCount());
                }
            }
        }
    }

    @Test
    public void testGetCount() throws Exception {
        String name = "testGetCount" + r.nextInt();
        ClinicHelper.addClinics(name, r.nextInt(10) + 3, true);
        // don't use the above number, just in case clinics of others test cases
        // where not removed
        int total = appService.search(Clinic.class, new Clinic()).size();
        Assert.assertEquals(total, ClinicWrapper.getCount(appService));
    }

    @Test
    public void testCollectionEventCount() throws Exception {
        String name = "testCollectionEventCount" + r.nextInt();
        ClinicWrapper clinic1 = ClinicHelper.addClinic(name);
        ContactWrapper contact1 = ContactHelper.addContact(clinic1, name);
        ClinicWrapper clinic2 = ClinicHelper.addClinic(name + "_2");
        ContactWrapper contact2 = ContactHelper
            .addContact(clinic2, name + "_2");

        StudyWrapper study1 = StudyHelper.addStudy(name);
        study1.addToContactCollection(Arrays.asList(contact1));
        study1.persist();

        StudyWrapper study2 = StudyHelper.addStudy(name + "_2");
        study2.addToContactCollection(Arrays.asList(contact2));
        study2.persist();

        List<ClinicWrapper> clinics = Arrays.asList(clinic1, clinic2);
        List<StudyWrapper> studies = Arrays.asList(study1, study2);

        // ClinicID = {StudyID = patientCOunt}
        Map<Integer, Map<Integer, List<CollectionEventWrapper>>> cEventMap = new HashMap<Integer, Map<Integer, List<CollectionEventWrapper>>>();

        for (ClinicWrapper clinic : clinics) {
            Map<Integer, List<CollectionEventWrapper>> studyMap = new HashMap<Integer, List<CollectionEventWrapper>>();
            for (StudyWrapper study : studies) {
                studyMap.put(study.getId(),
                    new ArrayList<CollectionEventWrapper>());
            }
            cEventMap.put(clinic.getId(), studyMap);
        }
        // add patients and collection events
        for (int i = 0, n = r.nextInt(10) + 3; i < n; ++i) {
            StudyWrapper study = studies.get(i & 1);
            ClinicWrapper clinic = clinics.get(i & 1);
            Map<Integer, List<CollectionEventWrapper>> studyMap = cEventMap
                .get(clinic.getId());
            List<CollectionEventWrapper> cEventForClinicForStudy = studyMap
                .get(study.getId());

            PatientWrapper patient = PatientHelper.addPatient(name + "_" + i,
                study);
            for (int eventNb = 0; eventNb < (r.nextInt(10) + 3); eventNb++) {
                SpecimenWrapper originSpecimen = SpecimenHelper
                    .newSpecimen(SpecimenTypeWrapper.getAllSpecimenTypes(
                        appService, false).get(0));
                CollectionEventWrapper cEvent = CollectionEventHelper
                    .addCollectionEvent(clinic, patient, eventNb,
                        originSpecimen);
                cEventForClinicForStudy.add(cEvent);
            }
            // count specific to study
            Assert.assertEquals(cEventForClinicForStudy.size(),
                clinic.getCollectionEventCountForStudy(study));
            int clinicTotal = 0;
            for (StudyWrapper s : studies)
                clinicTotal += studyMap.get(s.getId()).size();
            // count for the whole clinic
            Assert.assertEquals(clinicTotal, clinic.getCollectionEventCount());
        }

    }
}
