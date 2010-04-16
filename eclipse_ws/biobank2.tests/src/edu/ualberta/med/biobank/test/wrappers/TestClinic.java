package edu.ualberta.med.biobank.test.wrappers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import edu.ualberta.med.biobank.common.BiobankCheckException;
import edu.ualberta.med.biobank.common.wrappers.ActivityStatusWrapper;
import edu.ualberta.med.biobank.common.wrappers.ClinicWrapper;
import edu.ualberta.med.biobank.common.wrappers.ContactWrapper;
import edu.ualberta.med.biobank.common.wrappers.PatientWrapper;
import edu.ualberta.med.biobank.common.wrappers.ShipmentWrapper;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;
import edu.ualberta.med.biobank.common.wrappers.StudyWrapper;
import edu.ualberta.med.biobank.model.Clinic;
import edu.ualberta.med.biobank.model.Contact;
import edu.ualberta.med.biobank.test.TestDatabase;
import edu.ualberta.med.biobank.test.Utils;
import edu.ualberta.med.biobank.test.internal.ClinicHelper;
import edu.ualberta.med.biobank.test.internal.ContactHelper;
import edu.ualberta.med.biobank.test.internal.DbHelper;
import edu.ualberta.med.biobank.test.internal.PatientHelper;
import edu.ualberta.med.biobank.test.internal.PatientVisitHelper;
import edu.ualberta.med.biobank.test.internal.ShipmentHelper;
import edu.ualberta.med.biobank.test.internal.SiteHelper;
import edu.ualberta.med.biobank.test.internal.StudyHelper;

public class TestClinic extends TestDatabase {

    @Test
    public void testGettersAndSetters() throws BiobankCheckException, Exception {
        String name = "testGettersAndSetters" + r.nextInt();
        SiteWrapper site = SiteHelper.addSite(name);
        ClinicWrapper clinic = ClinicHelper.addClinic(site, name);

        testGettersAndSetters(clinic);
    }

    @Test
    public void testGetSetSite() throws Exception {
        String name = "testGetSite" + r.nextInt();
        SiteWrapper site = SiteHelper.addSite(name);
        ClinicWrapper clinic = ClinicHelper.addClinic(site, name);
        SiteWrapper site2 = SiteHelper.addSite(name + "SITE2");

        clinic.setSite(site2);
        clinic.persist();

        clinic.reload();

        Assert.assertFalse(site.equals(clinic.getSite()));

        Assert.assertEquals(site2, clinic.getSite());
    }

    @Test
    public void testGetContactCollection() throws Exception {
        String name = "testGetContactCollection" + r.nextInt();
        SiteWrapper site = SiteHelper.addSite(name);
        ClinicWrapper clinic = ClinicHelper.addClinic(site, name);
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
        SiteWrapper site = SiteHelper.addSite(name);
        ClinicWrapper clinic = ClinicHelper.addClinic(site, name, true);

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
        SiteWrapper site = SiteHelper.addSite(name);
        ClinicWrapper clinic = ClinicHelper.addClinic(site, name);
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
        SiteWrapper site = SiteHelper.addSite(name);
        ClinicWrapper clinic = ClinicHelper.addClinic(site, name);
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
        SiteWrapper site = SiteHelper.addSite(name);
        ClinicWrapper clinic = ClinicHelper.addClinic(site, name, true);
        StudyWrapper study1 = StudyHelper.addStudy(site, name + "STUDY1");
        study1.addContacts(Arrays.asList(DbHelper.chooseRandomlyInList(clinic
            .getContactCollection())));
        study1.persist();

        ClinicWrapper clinic2 = ClinicHelper.addClinic(site, name + "CLINIC2",
            true);
        StudyWrapper study2 = StudyHelper.addStudy(site, name + "STUDY2");
        study2.addContacts(Arrays.asList(DbHelper.chooseRandomlyInList(clinic
            .getContactCollection()), DbHelper.chooseRandomlyInList(clinic2
            .getContactCollection())));
        study2.persist();

        clinic.reload();

        Assert.assertEquals(2, clinic.getStudyCollection().size());
        Assert.assertEquals(1, clinic2.getStudyCollection().size());
    }

    @Test
    public void testGetStudyCollectionBoolean() throws Exception {
        String name = "testGetStudyCollectionBoolean" + r.nextInt();
        SiteWrapper site = SiteHelper.addSite(name);
        ClinicWrapper clinic = ClinicHelper.addClinic(site, name, true);
        StudyWrapper study1 = StudyHelper.addStudy(site, name + "STUDY1");
        study1.addContacts(Arrays.asList(DbHelper.chooseRandomlyInList(clinic
            .getContactCollection())));
        study1.persist();
        StudyWrapper study2 = StudyHelper.addStudy(site, name + "STUDY2");
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
        SiteWrapper site = SiteHelper.addSite(name);
        ClinicHelper.addClinic(site, name);

        int newTotal = appService.search(Clinic.class, new Clinic()).size();
        Assert.assertEquals(oldTotal + 1, newTotal);
    }

    @Test
    public void testPersistFailAddressNotNull() throws Exception {
        String name = "testPersistFailAddressNotNul" + r.nextInt();
        SiteWrapper site = SiteHelper.addSite(name);
        ClinicHelper.addClinic(site, name + "_1");
        int oldTotal = site.getClinicCollection().size();

        ClinicWrapper clinic = new ClinicWrapper(appService);
        clinic.setName(name);
        clinic.setNameShort(name);
        clinic.setSite(site);
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
        site.reload();
        int newTotal = site.getClinicCollection().size();
        Assert.assertEquals(oldTotal + 1, newTotal);
    }

    @Test
    public void testPersistFailSiteNotNull() throws Exception {
        String name = "testPersistFailSiteNotNul" + r.nextInt();
        ClinicWrapper clinic = new ClinicWrapper(appService);
        clinic.setName(name);
        clinic.setNameShort(name);
        clinic.setCity("Rupt");
        clinic.setActivityStatus(ActivityStatusWrapper.getActivityStatus(
            appService, "Active"));

        try {
            clinic.persist();
            Assert.fail("Should not insert the clinic : no site");
        } catch (BiobankCheckException bce) {
            Assert.assertTrue(true);
        }
        SiteWrapper site = SiteHelper.addSite(name);
        clinic.setSite(site);
        clinic.persist();
    }

    @Test
    public void testPersistFailActivityStatusNull() throws Exception {
        String name = "testPersistFailSiteNotNul" + r.nextInt();
        SiteWrapper site = SiteHelper.addSite(name);
        ClinicWrapper clinic = new ClinicWrapper(appService);
        clinic.setSite(site);
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
    }

    @Test
    public void testPersistFailNameUnique() throws Exception {
        String name = "testPersistFailNameUnique" + r.nextInt();
        SiteWrapper site = SiteHelper.addSite(name);
        ClinicHelper.addClinic(site, name);
        int oldTotal = site.getClinicCollection().size();

        ClinicWrapper clinic = ClinicHelper.newClinic(site, name);
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
        site.reload();
        int newTotal = site.getClinicCollection().size();
        Assert.assertEquals(oldTotal + 1, newTotal);

        SiteWrapper site2 = SiteHelper.addSite(name + "SITE2");
        ClinicHelper.addClinic(site2, name + "_site2");
        int oldTotalSite2 = site2.getClinicCollection().size();
        // can insert same name in different site
        clinic = ClinicHelper.newClinic(site2, name);
        clinic.persist();
        site.reload();
        site2.reload();
        // only one clinic added
        Assert.assertEquals(oldTotal + 1, site.getClinicCollection().size());
        Assert.assertEquals(oldTotalSite2 + 1, site2.getClinicCollection()
            .size());
    }

    @Test
    public void testDelete() throws Exception {
        String name = "testDelete" + r.nextInt();
        SiteWrapper site = SiteHelper.addSite(name);
        ClinicWrapper clinic = ClinicHelper.addClinic(site, name);

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
        SiteWrapper site = SiteHelper.addSite(name);
        ClinicWrapper clinic = ClinicHelper.addClinic(site, name);
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
        SiteWrapper site = SiteHelper.addSite(name);
        ClinicWrapper clinic = ClinicHelper.addClinic(site, name);
        ContactWrapper contact = ContactHelper.addContact(clinic, name);

        StudyWrapper study = StudyHelper.addStudy(site, name);
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
        ClinicWrapper clinic = ClinicHelper.addClinic(site, name);
        ContactHelper.addContact(clinic, name);

        ShipmentHelper.addShipmentWithRandomPatient(clinic, name);

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
        SiteWrapper site = SiteHelper.addSite(name);
        ClinicWrapper clinic = ClinicHelper.addClinic(site, name);
        clinic.reload();
        String oldName = clinic.getName();
        clinic.setName("toto");
        clinic.reset();
        Assert.assertEquals(oldName, clinic.getName());
    }

    @Test
    public void testResetNew() throws Exception {
        String name = "testResetAlreadyInDatabase" + r.nextInt();
        SiteWrapper site = SiteHelper.addSite(name);
        ClinicWrapper clinic = ClinicHelper.newClinic(site, name);
        clinic.reset();
        Assert.assertEquals(null, clinic.getName());
    }

    @Test
    public void testGetShipmentCollection() throws Exception {
        String name = "testGetShipmentCollection" + r.nextInt();
        SiteWrapper site = SiteHelper.addSite(name);
        ClinicWrapper clinic = ClinicHelper.addClinic(site, name);
        ContactWrapper contact = ContactHelper.addContact(clinic, name);

        StudyWrapper study = StudyHelper.addStudy(clinic.getSite(), name);
        study.addContacts(Arrays.asList(contact));
        study.persist();
        PatientWrapper patient1 = PatientHelper.addPatient(name, study);

        StudyWrapper study2 = StudyHelper.addStudy(clinic.getSite(), name
            + "_2");
        study2.addContacts(Arrays.asList(contact));
        study2.persist();
        PatientWrapper patient2 = PatientHelper.addPatient(name + "_2", study2);

        ShipmentHelper.addShipment(clinic, patient1);
        ShipmentHelper.addShipment(clinic, patient2);

        clinic.reload();
        List<ShipmentWrapper> ships = clinic.getShipmentCollection();
        int sizeFound = ships.size();

        Assert.assertEquals(2, sizeFound);
    }

    @Test
    public void testAddShipments() throws Exception {
        String name = "testAddShipments" + r.nextInt();
        SiteWrapper site = SiteHelper.addSite(name);
        ClinicWrapper clinic = ClinicHelper.addClinic(site, name);
        ContactWrapper contact = ContactHelper.addContact(clinic, name);

        StudyWrapper study = StudyHelper.addStudy(clinic.getSite(), name);
        study.addContacts(Arrays.asList(contact));
        study.persist();
        PatientWrapper patient1 = PatientHelper.addPatient(name, study);

        StudyWrapper study2 = StudyHelper.addStudy(clinic.getSite(), name
            + "_2");
        study2.addContacts(Arrays.asList(contact));
        study2.persist();
        PatientWrapper patient2 = PatientHelper.addPatient(name + "_2", study2);

        ShipmentHelper.addShipment(clinic, patient1);
        ShipmentHelper.addShipment(clinic, patient2);

        clinic.reload();

        ShipmentWrapper shipment = ShipmentHelper.newShipment(clinic);
        ShipmentWrapper shipment2 = ShipmentHelper.newShipment(clinic);
        clinic.addShipments(Arrays.asList(shipment, shipment2));
        clinic.persist();
        clinic.reload();
        Assert.assertEquals(4, clinic.getShipmentCollection().size());
    }

    @Test
    public void testHasShipments() throws Exception {
        String name = "testHasShipments" + r.nextInt();
        SiteWrapper site = SiteHelper.addSite(name);
        ClinicWrapper clinic = ClinicHelper.addClinic(site, name);
        ContactWrapper contact = ContactHelper.addContact(clinic, name);

        StudyWrapper study = StudyHelper.addStudy(clinic.getSite(), name);
        study.addContacts(Arrays.asList(contact));
        study.persist();
        PatientWrapper patient1 = PatientHelper.addPatient(name, study);

        StudyWrapper study2 = StudyHelper.addStudy(clinic.getSite(), name
            + "_2");
        study2.addContacts(Arrays.asList(contact));
        study2.persist();
        PatientWrapper patient2 = PatientHelper.addPatient(name + "_2", study2);

        Assert.assertFalse(clinic.sendsShipments());

        ShipmentWrapper shipment1 = ShipmentHelper
            .addShipment(clinic, patient1);
        ShipmentWrapper shipment2 = ShipmentHelper
            .addShipment(clinic, patient2);

        Assert.assertTrue(clinic.sendsShipments());

        clinic.reload();
        shipment1.delete();
        shipment2.delete();
        clinic.reload();

        Assert.assertFalse(clinic.sendsShipments());
    }

    @Test
    public void testGetPatientVisitCollection() throws Exception {
        String name = "testGetPatientVisitCollection" + r.nextInt();
        SiteWrapper site = SiteHelper.addSite(name);
        ClinicWrapper clinic = ClinicHelper.addClinic(site, name);
        ContactWrapper contact = ContactHelper.addContact(clinic, name);

        StudyWrapper study = StudyHelper.addStudy(clinic.getSite(), name);
        study.addContacts(Arrays.asList(contact));
        study.persist();
        PatientWrapper patient1 = PatientHelper.addPatient(name, study);
        PatientWrapper patient2 = PatientHelper.addPatient(name + "_2", study);
        ShipmentWrapper shipment1 = ShipmentHelper.addShipment(clinic,
            patient1, patient2);
        PatientVisitHelper.addPatientVisit(patient1, shipment1, Utils
            .getRandomDate(), Utils.getRandomDate());
        PatientVisitHelper.addPatientVisit(patient2, shipment1, Utils
            .getRandomDate(), Utils.getRandomDate());

        StudyWrapper study2 = StudyHelper.addStudy(clinic.getSite(), name
            + "_2");
        study2.addContacts(Arrays.asList(contact));
        study2.persist();
        PatientWrapper patient3 = PatientHelper.addPatient(name + "_3", study2);
        ShipmentWrapper shipment2 = ShipmentHelper
            .addShipment(clinic, patient3);
        PatientVisitHelper.addPatientVisit(patient3, shipment2, Utils
            .getRandomDate(), Utils.getRandomDate());

        clinic.reload();
        Assert.assertEquals(3, clinic.getPatientVisitCollection().size());
    }

    @Test
    public void testCompareTo() throws Exception {
        String name = "testCompareTo" + r.nextInt();
        SiteWrapper site = SiteHelper.addSite(name);
        ClinicWrapper clinic1 = ClinicHelper.addClinic(site, "QWERTY" + name);
        ClinicWrapper clinic2 = ClinicHelper.addClinic(site, "ASDFG" + name);

        Assert.assertTrue(clinic1.compareTo(clinic2) > 0);
        Assert.assertTrue(clinic2.compareTo(clinic1) < 0);
    }

    @Test
    public void testGetContact() throws Exception {
        String name = "testGetContact" + r.nextInt();
        SiteWrapper site = SiteHelper.addSite(name);
        ClinicWrapper clinic = ClinicHelper.addClinic(site, name);
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
        ClinicWrapper clinic = ClinicHelper.addClinic(site, name);
        ContactWrapper contact = ContactHelper.addContact(clinic, name);

        StudyWrapper study = StudyHelper.addStudy(clinic.getSite(), name);
        study.addContacts(Arrays.asList(contact));
        study.persist();
        PatientWrapper patient1 = PatientHelper.addPatient(name, study);

        StudyWrapper study2 = StudyHelper.addStudy(clinic.getSite(), name
            + "_2");
        study2.addContacts(Arrays.asList(contact));
        study2.persist();
        PatientWrapper patient2 = PatientHelper.addPatient(name + "_2", study2);

        ShipmentWrapper shipment1 = ShipmentHelper
            .addShipment(clinic, patient1);
        Date date1 = shipment1.getDateReceived();
        ShipmentWrapper shipment2 = ShipmentHelper
            .addShipment(clinic, patient2);
        Date date2 = shipment2.getDateReceived();

        clinic.reload();

        ShipmentWrapper shipFound = clinic.getShipment(date1);
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
        ClinicWrapper clinic = ClinicHelper.addClinic(site, name);
        ContactWrapper contact = ContactHelper.addContact(clinic, name);

        StudyWrapper study = StudyHelper.addStudy(clinic.getSite(), name);
        study.addContacts(Arrays.asList(contact));
        study.persist();
        PatientWrapper patient1 = PatientHelper.addPatient(name, study);

        StudyWrapper study2 = StudyHelper.addStudy(clinic.getSite(), name
            + "_2");
        study2.addContacts(Arrays.asList(contact));
        study2.persist();
        PatientWrapper patient2 = PatientHelper.addPatient(name + "_2", study2);

        ShipmentWrapper shipment1 = ShipmentHelper.addShipment(clinic,
            patient1, patient2);
        Date date1 = shipment1.getDateReceived();
        ShipmentWrapper shipment2 = ShipmentHelper
            .addShipment(clinic, patient2);
        Date date2 = shipment2.getDateReceived();

        clinic.reload();

        ShipmentWrapper shipFound = clinic.getShipment(date1, patient1
            .getPnumber());
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
        ClinicWrapper clinic1 = ClinicHelper.addClinic(site, name);
        ContactWrapper contact1 = ContactHelper.addContact(clinic1, name);
        ClinicWrapper clinic2 = ClinicHelper.addClinic(site, name + "_2");
        ContactWrapper contact2 = ContactHelper
            .addContact(clinic2, name + "_2");

        StudyWrapper study = StudyHelper.addStudy(clinic1.getSite(), name);
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
            ShipmentHelper.addShipment(clinic, patient);
            Assert.assertEquals(patientMap.get(clinic).size(), clinic
                .getPatientCount());
        }

        // delete patients
        for (ClinicWrapper clinic : clinics) {
            while (patientMap.get(clinic).size() > 0) {
                patient = patientMap.get(clinic).get(0);
                patient.reload();
                if (patient.getShipmentCollection() != null) {
                    for (ShipmentWrapper s : patient.getShipmentCollection()) {
                        s.delete();
                    }
                    patient.reload();
                }
                patient.delete();
                patientMap.get(clinic).remove(0);
                Assert.assertEquals(patientMap.get(clinic).size(), clinic
                    .getPatientCount());
            }
        }
    }
}
