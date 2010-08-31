package edu.ualberta.med.biobank.test.wrappers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import edu.ualberta.med.biobank.common.exception.BiobankCheckException;
import edu.ualberta.med.biobank.common.wrappers.ActivityStatusWrapper;
import edu.ualberta.med.biobank.common.wrappers.ClinicShipmentWrapper;
import edu.ualberta.med.biobank.common.wrappers.ClinicWrapper;
import edu.ualberta.med.biobank.common.wrappers.ContactWrapper;
import edu.ualberta.med.biobank.common.wrappers.PatientWrapper;
import edu.ualberta.med.biobank.common.wrappers.ShippingMethodWrapper;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;
import edu.ualberta.med.biobank.common.wrappers.StudyWrapper;
import edu.ualberta.med.biobank.model.Clinic;
import edu.ualberta.med.biobank.model.Contact;
import edu.ualberta.med.biobank.test.TestDatabase;
import edu.ualberta.med.biobank.test.Utils;
import edu.ualberta.med.biobank.test.internal.ClinicHelper;
import edu.ualberta.med.biobank.test.internal.ClinicShipmentHelper;
import edu.ualberta.med.biobank.test.internal.ContactHelper;
import edu.ualberta.med.biobank.test.internal.DbHelper;
import edu.ualberta.med.biobank.test.internal.PatientHelper;
import edu.ualberta.med.biobank.test.internal.PatientVisitHelper;
import edu.ualberta.med.biobank.test.internal.SiteHelper;
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
        List<ContactWrapper> contacts = clinic.getContactCollection();
        int sizeFound = contacts.size();

        Assert.assertEquals(nber, sizeFound);
    }

    @Test
    public void testGetContactCollectionBoolean() throws Exception {
        String name = "testGetContactCollectionBoolean" + r.nextInt();
        ClinicWrapper clinic = ClinicHelper.addClinic(name, true);

        List<ContactWrapper> contacts = clinic.getContactCollection(true);
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
        ClinicWrapper clinic = ClinicHelper.addClinic(name);
        int nber = r.nextInt(5) + 1;
        for (int i = 0; i < nber; i++) {
            ContactHelper.addContact(clinic, name + i);
        }
        clinic.reload();
        ContactWrapper contact = ContactHelper.newContact(clinic, name + "NEW");
        clinic.addContacts(Arrays.asList(contact));
        clinic.persist();

        clinic.reload();
        // one contact added
        Assert.assertEquals(nber + 1, clinic.getContactCollection().size());
    }

    @Test
    public void testRemoveContacts() throws Exception {
        String name = "testRemoveContacts" + r.nextInt();
        ClinicWrapper clinic = ClinicHelper.addClinic(name);
        int nber = r.nextInt(5) + 1;
        for (int i = 0; i < nber; i++) {
            ContactHelper.addContact(clinic, name + i);
        }
        clinic.reload();
        List<ContactWrapper> contacts = clinic.getContactCollection();
        ContactWrapper contact = DbHelper.chooseRandomlyInList(contacts);
        clinic.removeContacts(Arrays.asList(contact));
        clinic.persist();

        clinic.reload();
        // one contact added
        Assert.assertEquals(nber - 1, clinic.getContactCollection().size());
    }

    @Test
    public void testGetStudyCollection() throws Exception {
        String name = "testGetStudyCollection" + r.nextInt();
        ClinicWrapper clinic = ClinicHelper.addClinic(name, true);
        StudyWrapper study1 = StudyHelper.addStudy(name + "STUDY1");
        study1.addContacts(Arrays.asList(DbHelper.chooseRandomlyInList(clinic
            .getContactCollection())));
        study1.persist();

        ClinicWrapper clinic2 = ClinicHelper.addClinic(name + "CLINIC2", true);
        StudyWrapper study2 = StudyHelper.addStudy(name + "STUDY2");
        study2.addContacts(Arrays.asList(
            DbHelper.chooseRandomlyInList(clinic.getContactCollection()),
            DbHelper.chooseRandomlyInList(clinic2.getContactCollection())));
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
        study1.addContacts(Arrays.asList(DbHelper.chooseRandomlyInList(clinic
            .getContactCollection())));
        study1.persist();
        StudyWrapper study2 = StudyHelper.addStudy(name + "STUDY2");
        study2.addContacts(Arrays.asList(DbHelper.chooseRandomlyInList(clinic
            .getContactCollection())));
        study2.persist();

        clinic.reload();

        List<StudyWrapper> studies = clinic.getStudyCollection();
        if (studies.size() > 1) {
            for (int i = 0; i < studies.size() - 1; i++) {
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
        clinic.setActivityStatus(ActivityStatusWrapper.getActivityStatus(
            appService, "Active"));
        try {
            clinic.persist();
            Assert.fail("Should not insert the clinic : no address");
        } catch (BiobankCheckException bce) {
            Assert.assertTrue(true);
        }

        clinic.setCity("Vesoul");
        clinic.persist();
        ClinicHelper.createdClinics.add(clinic);
        int newTotal = ClinicWrapper.getAllClinics(appService).size();
        Assert.assertEquals(oldTotal + 1, newTotal);
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
        } catch (BiobankCheckException bce) {
            Assert.assertTrue(true);
        }
        clinic.setActivityStatus(ActivityStatusWrapper.getActivityStatus(
            appService, "Active"));
        clinic.persist();
        ClinicHelper.createdClinics.add(clinic);
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
        } catch (BiobankCheckException bce) {
            Assert.assertTrue(true);
        }
        clinic.setName(name + "_otherName");
        clinic.persist();
        ClinicHelper.createdClinics.add(clinic);
        int newTotal = ClinicWrapper.getAllClinics(appService).size();
        Assert.assertEquals(oldTotal + 1, newTotal);
    }

    @Test
    public void testDelete() throws Exception {
        String name = "testDelete" + r.nextInt();
        ClinicWrapper clinic = ClinicHelper.addClinic(name, false, false);

        // object is in database
        Clinic clinicInDB = ModelUtils.getObjectWithId(appService,
            Clinic.class, clinic.getId());
        Assert.assertNotNull(clinicInDB);

        clinic.delete();

        clinicInDB = ModelUtils.getObjectWithId(appService, Clinic.class,
            clinic.getId());
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
        study.addContacts(Arrays.asList(contact));
        study.persist();

        clinic.reload();
        contact.reload();

        try {
            clinic.delete();
            Assert
                .fail("Can't remove a clinic if a study linked to one of its contacts still exists");
        } catch (BiobankCheckException bce) {
            Assert.assertTrue(true);
        }
    }

    @Test
    public void testDeleteWithShipments() throws Exception {
        String name = "testDeleteWithShipments" + r.nextInt();
        SiteWrapper site = SiteHelper.addSite(name);
        ClinicWrapper clinic = ClinicHelper.addClinic(name);
        ContactHelper.addContact(clinic, name);

        ClinicShipmentHelper.addShipmentWithRandomPatient(site, clinic, name);

        clinic.reload();
        try {
            clinic.delete();
            Assert.fail("Can't remove a clinic if shipments linked to it");
        } catch (BiobankCheckException bce) {
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
    public void testGetShipmentCollection() throws Exception {
        String name = "testGetShipmentCollection" + r.nextInt();
        SiteWrapper site = SiteHelper.addSite(name);
        ClinicWrapper clinic = ClinicHelper.addClinic(name);
        ContactWrapper contact = ContactHelper.addContact(clinic, name);

        StudyWrapper study = StudyHelper.addStudy(name);
        study.addContacts(Arrays.asList(contact));
        study.persist();
        PatientWrapper patient1 = PatientHelper.addPatient(name, study);

        StudyWrapper study2 = StudyHelper.addStudy(name + "_2");
        study2.addContacts(Arrays.asList(contact));
        study2.persist();
        PatientWrapper patient2 = PatientHelper.addPatient(name + "_2", study2);

        ShippingMethodWrapper method = ShippingMethodWrapper
            .getShippingMethods(appService).get(0);
        ClinicShipmentHelper.addShipment(site, clinic, method, patient1);
        ClinicShipmentHelper.addShipment(site, clinic, method, patient2);

        clinic.reload();
        List<ClinicShipmentWrapper> ships = clinic.getShipmentCollection();
        int sizeFound = ships.size();

        Assert.assertEquals(2, sizeFound);
    }

    @Test
    public void testAddShipments() throws Exception {
        String name = "testAddShipments" + r.nextInt();
        SiteWrapper site = SiteHelper.addSite(name);
        ClinicWrapper clinic = ClinicHelper.addClinic(name);
        ContactWrapper contact = ContactHelper.addContact(clinic, name);

        StudyWrapper study = StudyHelper.addStudy(name);
        study.addContacts(Arrays.asList(contact));
        study.persist();
        PatientWrapper patient1 = PatientHelper.addPatient(name, study);

        StudyWrapper study2 = StudyHelper.addStudy(name + "_2");
        study2.addContacts(Arrays.asList(contact));
        study2.persist();
        PatientWrapper patient2 = PatientHelper.addPatient(name + "_2", study2);

        ShippingMethodWrapper method = ShippingMethodWrapper
            .getShippingMethods(appService).get(0);
        ClinicShipmentHelper.addShipment(site, clinic, method, patient1);
        ClinicShipmentHelper.addShipment(site, clinic, method, patient2);

        clinic.reload();

        ClinicShipmentWrapper shipment = ClinicShipmentHelper.newShipment(site,
            clinic, method);
        ClinicShipmentWrapper shipment2 = ClinicShipmentHelper.newShipment(
            site, clinic, method);
        clinic.addShipments(Arrays.asList(shipment, shipment2));
        clinic.persist();
        clinic.reload();
        Assert.assertEquals(4, clinic.getShipmentCollection().size());
    }

    @Test
    public void testGetShipmentCount() throws Exception {
        String name = "testGetShipmentCount" + r.nextInt();
        SiteWrapper site = SiteHelper.addSite(name);
        ClinicWrapper clinic = ClinicHelper.addClinic(name);
        ContactWrapper contact = ContactHelper.addContact(clinic, name);

        StudyWrapper study = StudyHelper.addStudy(name);
        study.addContacts(Arrays.asList(contact));
        study.persist();
        PatientWrapper patient1 = PatientHelper.addPatient(name, study);

        StudyWrapper study2 = StudyHelper.addStudy(name + "_2");
        study2.addContacts(Arrays.asList(contact));
        study2.persist();
        PatientWrapper patient2 = PatientHelper.addPatient(name + "_2", study2);

        Assert.assertEquals(0, clinic.getShipmentCount());

        ShippingMethodWrapper method = ShippingMethodWrapper
            .getShippingMethods(appService).get(0);
        ClinicShipmentWrapper shipment1 = ClinicShipmentHelper.addShipment(
            site, clinic, method, patient1);
        ClinicShipmentWrapper shipment2 = ClinicShipmentHelper.addShipment(
            site, clinic, method, patient2);

        Assert.assertEquals(2, clinic.getShipmentCount());

        clinic.reload();
        shipment1.delete();
        shipment2.delete();
        clinic.reload();

        Assert.assertEquals(0, clinic.getShipmentCount());
    }

    @Test
    public void testGetPatientVisitCollection() throws Exception {
        String name = "testGetPatientVisitCollection" + r.nextInt();
        SiteWrapper site = SiteHelper.addSite(name);
        ClinicWrapper clinic = ClinicHelper.addClinic(name);
        ContactWrapper contact = ContactHelper.addContact(clinic, name);

        ShippingMethodWrapper method = ShippingMethodWrapper
            .getShippingMethods(appService).get(0);

        StudyWrapper study = StudyHelper.addStudy(name);
        study.addContacts(Arrays.asList(contact));
        study.persist();
        PatientWrapper patient1 = PatientHelper.addPatient(name, study);
        PatientWrapper patient2 = PatientHelper.addPatient(name + "_2", study);
        ClinicShipmentWrapper shipment1 = ClinicShipmentHelper.addShipment(
            site, clinic, method, patient1, patient2);
        PatientVisitHelper.addPatientVisit(patient1, shipment1,
            Utils.getRandomDate(), Utils.getRandomDate());
        PatientVisitHelper.addPatientVisit(patient2, shipment1,
            Utils.getRandomDate(), Utils.getRandomDate());

        StudyWrapper study2 = StudyHelper.addStudy(name + "_2");
        study2.addContacts(Arrays.asList(contact));
        study2.persist();
        PatientWrapper patient3 = PatientHelper.addPatient(name + "_3", study2);
        ClinicShipmentWrapper shipment2 = ClinicShipmentHelper.addShipment(
            site, clinic, method, patient3);
        PatientVisitHelper.addPatientVisit(patient3, shipment2,
            Utils.getRandomDate(), Utils.getRandomDate());

        clinic.reload();
        Assert.assertEquals(3, clinic.getPatientVisitCollection().size());
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
    public void testGetShipmentWithDate() throws Exception {
        String name = "testGetShipmentWithDate" + r.nextInt();
        SiteWrapper site = SiteHelper.addSite(name);
        ClinicWrapper clinic = ClinicHelper.addClinic(name);
        ContactWrapper contact = ContactHelper.addContact(clinic, name);

        StudyWrapper study = StudyHelper.addStudy(name);
        study.addContacts(Arrays.asList(contact));
        study.persist();
        PatientWrapper patient1 = PatientHelper.addPatient(name, study);

        StudyWrapper study2 = StudyHelper.addStudy(name + "_2");
        study2.addContacts(Arrays.asList(contact));
        study2.persist();
        PatientWrapper patient2 = PatientHelper.addPatient(name + "_2", study2);

        ShippingMethodWrapper method = ShippingMethodWrapper
            .getShippingMethods(appService).get(0);
        ClinicShipmentWrapper shipment1 = ClinicShipmentHelper.addShipment(
            site, clinic, method, patient1);
        Date date1 = shipment1.getDateReceived();
        ClinicShipmentWrapper shipment2 = ClinicShipmentHelper.addShipment(
            site, clinic, method, patient2);
        Date date2 = shipment2.getDateReceived();

        clinic.reload();

        ClinicShipmentWrapper shipFound = clinic.getShipment(date1);
        Assert.assertEquals(shipment1, shipFound);
        Assert.assertFalse(shipment2.equals(shipFound));

        shipFound = clinic.getShipment(date2);
        Assert.assertEquals(shipment2, shipFound);
        Assert.assertFalse(shipment1.equals(shipFound));

        shipFound = clinic.getShipment(new Date());
        Assert.assertNull(shipFound);
    }

    @Test
    public void testGetShipmentWithDateAndPatient() throws Exception {
        String name = "testGetShipmentWithDateAndPatient" + r.nextInt();
        SiteWrapper site = SiteHelper.addSite(name);
        ClinicWrapper clinic = ClinicHelper.addClinic(name);
        ContactWrapper contact = ContactHelper.addContact(clinic, name);

        StudyWrapper study = StudyHelper.addStudy(name);
        study.addContacts(Arrays.asList(contact));
        study.persist();
        PatientWrapper patient1 = PatientHelper.addPatient(name, study);

        StudyWrapper study2 = StudyHelper.addStudy(name + "_2");
        study2.addContacts(Arrays.asList(contact));
        study2.persist();
        PatientWrapper patient2 = PatientHelper.addPatient(name + "_2", study2);

        ShippingMethodWrapper method = ShippingMethodWrapper
            .getShippingMethods(appService).get(0);
        ClinicShipmentWrapper shipment1 = ClinicShipmentHelper.addShipment(
            site, clinic, method, patient1, patient2);
        Date date1 = shipment1.getDateReceived();
        ClinicShipmentWrapper shipment2 = ClinicShipmentHelper.addShipment(
            site, clinic, method, patient2);
        Date date2 = shipment2.getDateReceived();

        clinic.reload();

        ClinicShipmentWrapper shipFound = clinic.getShipment(date1,
            patient1.getPnumber());
        Assert.assertEquals(shipment1, shipFound);
        Assert.assertFalse(shipment2.equals(shipFound));

        shipFound = clinic.getShipment(date1, patient2.getPnumber());
        Assert.assertEquals(shipment1, shipFound);
        Assert.assertFalse(shipment2.equals(shipFound));

        shipFound = clinic.getShipment(date2, patient2.getPnumber());
        Assert.assertEquals(shipment2, shipFound);
        Assert.assertFalse(shipment1.equals(shipFound));

        shipFound = clinic.getShipment(date2, patient1.getPnumber());
        Assert.assertNull(shipFound);

        shipFound = clinic.getShipment(new Date(), patient1.getPnumber());
        Assert.assertNull(shipFound);
    }

    @Test
    public void testGetPatientCount() throws Exception {
        String name = "testGetPatientCount" + r.nextInt();
        SiteWrapper site = SiteHelper.addSite(name);
        ClinicWrapper clinic1 = ClinicHelper.addClinic(name);
        ContactWrapper contact1 = ContactHelper.addContact(clinic1, name);
        ClinicWrapper clinic2 = ClinicHelper.addClinic(name + "_2");
        ContactWrapper contact2 = ContactHelper
            .addContact(clinic2, name + "_2");

        StudyWrapper study = StudyHelper.addStudy(name);
        study.addContacts(Arrays.asList(contact1, contact2));
        study.persist();

        PatientWrapper patient;
        List<ClinicWrapper> clinics = Arrays.asList(clinic1, clinic2);
        Map<ClinicWrapper, List<PatientWrapper>> patientMap = new HashMap<ClinicWrapper, List<PatientWrapper>>();

        for (ClinicWrapper clinic : clinics) {
            patientMap.put(clinic, new ArrayList<PatientWrapper>());
        }

        // add patients
        for (int i = 0, n = r.nextInt(10) + 1; i < n; ++i) {
            patient = PatientHelper.addPatient(name + "_p" + i, study);
            ClinicWrapper clinic = clinics.get(i & 1);
            patientMap.get(clinic).add(patient);
            ClinicShipmentHelper.addShipment(site, clinic,
                ShippingMethodWrapper.getShippingMethods(appService).get(0),
                patient);
            Assert.assertEquals(patientMap.get(clinic).size(),
                clinic.getPatientCount());
        }

        // delete patients
        for (ClinicWrapper clinic : clinics) {
            while (patientMap.get(clinic).size() > 0) {
                patient = patientMap.get(clinic).get(0);
                patient.reload();
                if (patient.getShipmentCollection() != null) {
                    for (ClinicShipmentWrapper s : patient
                        .getShipmentCollection()) {
                        s.delete();
                    }
                    patient.reload();
                }
                patient.delete();
                patientMap.get(clinic).remove(0);
                clinic.reload();
                Assert.assertEquals(patientMap.get(clinic).size(),
                    clinic.getPatientCount());
            }
        }
    }
}
