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
import test.ualberta.med.biobank.internal.ShipmentHelper;
import test.ualberta.med.biobank.internal.ShippingCompanyHelper;
import test.ualberta.med.biobank.internal.SiteHelper;
import test.ualberta.med.biobank.internal.StudyHelper;
import edu.ualberta.med.biobank.common.BiobankCheckException;
import edu.ualberta.med.biobank.common.formatters.DateFormatter;
import edu.ualberta.med.biobank.common.wrappers.ClinicWrapper;
import edu.ualberta.med.biobank.common.wrappers.ContactWrapper;
import edu.ualberta.med.biobank.common.wrappers.PatientVisitWrapper;
import edu.ualberta.med.biobank.common.wrappers.PatientWrapper;
import edu.ualberta.med.biobank.common.wrappers.ShipmentWrapper;
import edu.ualberta.med.biobank.common.wrappers.ShippingCompanyWrapper;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;
import edu.ualberta.med.biobank.common.wrappers.StudyWrapper;
import edu.ualberta.med.biobank.model.Shipment;

public class TestShipment extends TestDatabase {

    @Test
    public void testGettersAndSetters() throws Exception {
        String name = "testGettersAndSetters" + r.nextInt();
        SiteWrapper site = SiteHelper.addSite(name);
        ClinicWrapper clinic = ClinicHelper.addClinic(site, name);

        ShipmentWrapper shipment = ShipmentHelper.addShipmentWithRandomPatient(
            clinic, name);
        testGettersAndSetters(shipment);
    }

    @Test
    public void testGetSetClinic() throws Exception {
        String name = "testGetSetClinic" + r.nextInt();
        SiteWrapper site = SiteHelper.addSite(name);
        ClinicWrapper clinic = ClinicHelper.addClinic(site, name);
        ShipmentWrapper shipment = ShipmentHelper.addShipmentWithRandomPatient(
            clinic, name);

        ClinicWrapper clinic2 = ClinicHelper.addClinic(site, name + "CLINIC2");
        ContactWrapper contact = ContactHelper.addContact(clinic2, name
            + "CONTACT2");
        StudyWrapper study = clinic.getStudyCollection().get(0);
        study.setContactCollection(Arrays.asList(contact));
        study.persist();

        shipment.setClinic(clinic2);
        shipment.persist();
        clinic.reload();
        clinic2.reload();

        shipment.reload();

        Assert.assertEquals(clinic2, shipment.getClinic());

        Assert.assertTrue(clinic2.getShipmentCollection().contains(shipment));

        Assert.assertFalse(clinic.getShipmentCollection().contains(shipment));
    }

    @Test
    public void testGetPatientVisitCollection() throws Exception {
        String name = "testGetPatientVisitCollection" + r.nextInt();
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
        int nbClinic1Study1 = PatientVisitHelper.addPatientVisits(patient1,
            shipment1).size();
        PatientVisitHelper.addPatientVisits(patient1, shipment2).size();

        StudyWrapper study2 = StudyHelper.addStudy(site, name + "STUDY2");
        study2.setContactCollection(contacts);
        study2.persist();
        PatientWrapper patient2 = PatientHelper.addPatient(name + "_2", study2);
        ShipmentWrapper shipment3 = ShipmentHelper.addShipment(clinic1,
            patient2);
        ShipmentWrapper shipment4 = ShipmentHelper.addShipment(clinic2,
            patient2);
        int nbClinic1Study2 = PatientVisitHelper.addPatientVisits(patient2,
            shipment3).size();
        PatientVisitHelper.addPatientVisits(patient2, shipment4).size();

        shipment1.reload();
        shipment3.reload();
        Assert.assertEquals(nbClinic1Study1, shipment1
            .getPatientVisitCollection().size());
        Assert.assertEquals(nbClinic1Study2, shipment3
            .getPatientVisitCollection().size());
    }

    @Test
    public void testSetPatientVisitCollectionAdd() throws Exception {
        String name = "testSetPatientVisitCollectionAdd" + r.nextInt();
        SiteWrapper site = SiteHelper.addSite(name);
        ClinicWrapper clinic = ClinicHelper.addClinic(site, name);
        ShipmentWrapper shipment = ShipmentHelper.addShipmentWithRandomPatient(
            clinic, name);
        int nber = r.nextInt(3) + 1;
        for (int i = 0; i < nber; i++) {
            PatientVisitHelper.addPatientVisit(shipment.getPatientCollection()
                .get(0), shipment, Utils.getRandomDate());
        }
        shipment.reload();

        List<PatientVisitWrapper> visits = shipment.getPatientVisitCollection();
        PatientVisitWrapper visit = PatientVisitHelper.newPatientVisit(shipment
            .getPatientCollection().get(0), shipment, Utils.getRandomDate());
        visits.add(visit);
        shipment.setPatientVisitCollection(visits);
        shipment.persist();

        shipment.reload();
        // one visit added
        Assert.assertEquals(nber + 1, shipment.getPatientVisitCollection()
            .size());
    }

    @Test
    public void testGetSetShippingCompany() throws Exception {
        String name = "testGetSetShippingCompany" + r.nextInt();
        SiteWrapper site = SiteHelper.addSite(name);
        ClinicWrapper clinic = ClinicHelper.addClinic(site, name);
        ShippingCompanyWrapper company = ShippingCompanyHelper
            .addShippingCompany(name);
        ShipmentWrapper shipment = ShipmentHelper.addShipmentWithRandomPatient(
            clinic, name);

        shipment.setShippingCompany(company);
        shipment.persist();

        shipment.reload();

        Assert.assertEquals(company, shipment.getShippingCompany());
    }

    @Test
    public void testGetPatientCollection() throws Exception {
        String name = "testGetPatientCollection" + r.nextInt();
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
        PatientWrapper patient2 = PatientHelper.addPatient(name + "_2", study1);

        StudyWrapper study2 = StudyHelper.addStudy(site, name + "STUDY2");
        study2.setContactCollection(contacts);
        study2.persist();
        PatientWrapper patient3 = PatientHelper.addPatient(name + "_3", study2);

        ShipmentWrapper shipment = ShipmentHelper.newShipment(clinic1);
        shipment.setPatientCollection(Arrays.asList(patient1, patient2,
            patient3));
        shipment.persist();

        shipment.reload();
        Assert.assertEquals(3, shipment.getPatientCollection().size());
    }

    @Test
    public void testGetPatientCollectionBoolean() throws Exception {
        String name = "testGetPatientCollectionBoolean" + r.nextInt();
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
        PatientWrapper patient1 = PatientHelper.addPatient("QWERTY" + name,
            study1);
        PatientWrapper patient2 = PatientHelper.addPatient("ASDFG" + name,
            study1);

        StudyWrapper study2 = StudyHelper.addStudy(site, name + "STUDY2");
        study2.setContactCollection(contacts);
        study2.persist();
        PatientWrapper patient3 = PatientHelper.addPatient("ZXCVB" + name,
            study2);

        ShipmentWrapper shipment = ShipmentHelper.newShipment(clinic1);
        shipment.setPatientCollection(Arrays.asList(patient1, patient2,
            patient3));
        shipment.persist();

        shipment.reload();
        List<PatientWrapper> patients = shipment.getPatientCollection(true);
        if (patients.size() > 1) {
            for (int i = 0; i < patients.size() - 1; i++) {
                PatientWrapper p1 = patients.get(i);
                PatientWrapper p2 = patients.get(i + 1);
                Assert.assertTrue(p1.compareTo(p2) <= 0);
            }
        }
    }

    @Test
    public void testSetPatientCollectionAdd() throws Exception {
        String name = "testSetPatientCollectionAdd" + r.nextInt();
        SiteWrapper site = SiteHelper.addSite(name);
        ClinicWrapper clinic = ClinicHelper.addClinic(site, name);
        StudyWrapper study = StudyHelper.addStudy(clinic.getSite(), name);
        ContactWrapper contact = ContactHelper.addContact(clinic, name);
        study.setContactCollection(Arrays.asList(contact));
        study.persist();
        PatientWrapper firstPatient = PatientHelper.addPatient(name, study);
        ShipmentWrapper shipment = ShipmentHelper.addShipment(clinic,
            firstPatient);
        shipment.reload();

        List<PatientWrapper> patients = shipment.getPatientCollection();
        PatientWrapper patient = PatientHelper.addPatient(name + "NewPatient",
            study);
        patients.add(patient);
        shipment.setPatientCollection(patients);
        shipment.persist();

        shipment.reload();
        // one patient added
        Assert.assertEquals(2, shipment.getPatientCollection().size());
    }

    @Test
    public void testSetPatientCollectionRemove() throws Exception {
        String name = "testSetPatientCollectionRemove" + r.nextInt();
        SiteWrapper site = SiteHelper.addSite(name);
        ClinicWrapper clinic = ClinicHelper.addClinic(site, name);
        StudyWrapper study = StudyHelper.addStudy(clinic.getSite(), name);
        ContactWrapper contact = ContactHelper.addContact(clinic, name);
        study.setContactCollection(Arrays.asList(contact));
        study.persist();
        PatientWrapper patient1 = PatientHelper.addPatient(name, study);
        PatientWrapper patient2 = PatientHelper.addPatient(name + "_2", study);
        ShipmentWrapper shipment = ShipmentHelper.addShipment(clinic, patient1,
            patient2);
        shipment.reload();

        List<PatientWrapper> patients = shipment.getPatientCollection();
        PatientWrapper patient = DbHelper.chooseRandomlyInList(patients);
        patients.remove(patient);
        shipment.setPatientCollection(patients);
        shipment.persist();

        shipment.reload();
        // one patient removed
        Assert.assertEquals(1, shipment.getPatientCollection().size());
    }

    @Test
    public void testGetShipmentInSite() throws Exception {
        String name = "testSetPatientCollectionRemove" + r.nextInt();
        SiteWrapper site = SiteHelper.addSite(name);
        ClinicWrapper clinic = ClinicHelper.addClinic(site, name);
        StudyWrapper study = StudyHelper.addStudy(clinic.getSite(), name);
        ContactWrapper contact = ContactHelper.addContact(clinic, name);
        study.setContactCollection(Arrays.asList(contact));
        study.persist();
        PatientWrapper patient1 = PatientHelper.addPatient(name, study);
        ShipmentHelper.addShipment(clinic, patient1);
        ShipmentWrapper shipmentTest = ShipmentHelper.addShipment(clinic,
            patient1);
        ShipmentHelper.addShipment(clinic, patient1);

        String waybill = shipmentTest.getWaybill();

        site.reload();
        List<ShipmentWrapper> shipsFound = ShipmentWrapper.getShipmentsInSite(
            appService, waybill, site);

        Assert.assertEquals(1, shipsFound.size());
        Assert.assertEquals(shipmentTest, shipsFound.get(0));
    }

    @Test
    public void testPersist() throws Exception {
        String name = "testPersist" + r.nextInt();
        SiteWrapper site = SiteHelper.addSite(name);
        ClinicWrapper clinic = ClinicHelper.addClinic(site, name);
        StudyWrapper study = StudyHelper.addStudy(clinic.getSite(), name);
        ContactWrapper contact = ContactHelper.addContact(clinic, name);
        study.setContactCollection(Arrays.asList(contact));
        study.persist();
        PatientWrapper patient = PatientHelper.addPatient(name, study);
        ShipmentWrapper shipment = ShipmentHelper.newShipment(clinic, name,
            Utils.getRandomDate(), patient);

        shipment.persist();
    }

    @Test
    public void testPersistFailWaybillNull() throws Exception {
        String name = "testPersistFailWaybillNull" + r.nextInt();
        SiteWrapper site = SiteHelper.addSite(name);
        ClinicWrapper clinic = ClinicHelper.addClinic(site, name);
        StudyWrapper study = StudyHelper.addStudy(clinic.getSite(), name);
        ContactWrapper contact = ContactHelper.addContact(clinic, name);
        study.setContactCollection(Arrays.asList(contact));
        study.persist();
        PatientWrapper patient = PatientHelper.addPatient(name, study);
        ShipmentWrapper shipment = ShipmentHelper.newShipment(clinic, null,
            Utils.getRandomDate(), patient);

        try {
            shipment.persist();
            Assert.fail("shipment with waybill null");
        } catch (BiobankCheckException bce) {
            Assert.assertTrue(true);
        }
    }

    @Test
    public void testPersistFailWaybillExists() throws Exception {
        String name = "testPersistFailWaybillExists" + r.nextInt();
        SiteWrapper site = SiteHelper.addSite(name);
        ClinicWrapper clinic = ClinicHelper.addClinic(site, name);
        StudyWrapper study = StudyHelper.addStudy(clinic.getSite(), name);
        ContactWrapper contact = ContactHelper.addContact(clinic, name);
        study.setContactCollection(Arrays.asList(contact));
        study.persist();
        PatientWrapper patient = PatientHelper.addPatient(name, study);
        ShipmentWrapper shipment = ShipmentHelper.newShipment(clinic, name,
            Utils.getRandomDate(), patient);

        shipment.persist();

        ShipmentWrapper shipment2 = ShipmentHelper.newShipment(clinic, name,
            Utils.getRandomDate(), patient);
        try {
            shipment2.persist();
            Assert.fail("shipment with waybill '" + name
                + "' already exists. An exception should be thrown.");
        } catch (BiobankCheckException bce) {
            Assert.assertTrue(true);
        }
    }

    @Test
    public void testPersistFailNoPatient() throws Exception {
        String name = "testPersistFailNoPatient" + r.nextInt();
        SiteWrapper site = SiteHelper.addSite(name);
        ClinicWrapper clinic = ClinicHelper.addClinic(site, name);
        StudyWrapper study = StudyHelper.addStudy(clinic.getSite(), name);
        ContactWrapper contact = ContactHelper.addContact(clinic, name);
        study.setContactCollection(Arrays.asList(contact));
        study.persist();
        ShipmentWrapper shipment = ShipmentHelper.newShipment(clinic);
        try {
            shipment.persist();
            Assert
                .fail("shipment don't have any patient. An exception should be thrown.");
        } catch (BiobankCheckException bce) {
            Assert.assertTrue(true);
        }
    }

    @Test
    public void testPersistFailPatientNoInStudyOwnByClinic() throws Exception {
        String name = "testPersistFailPatientNoInStudyOwnByClinic"
            + r.nextInt();
        SiteWrapper site = SiteHelper.addSite(name);
        ClinicWrapper clinic = ClinicHelper.addClinic(site, name);

        StudyWrapper study = StudyHelper.addStudy(clinic.getSite(), name);
        PatientWrapper patient = PatientHelper.addPatient(name, study);
        ShipmentWrapper shipment = ShipmentHelper.newShipment(clinic, name,
            Utils.getRandomDate(), patient);

        try {
            shipment.persist();
            Assert
                .fail("patient should be part of the sutdy that has contact with the clinic");
        } catch (BiobankCheckException bce) {
            Assert.assertTrue(true);
        }
    }

    @Test
    public void testDelete() throws Exception {
        String name = "testDelete" + r.nextInt();
        SiteWrapper site = SiteHelper.addSite(name);
        ClinicWrapper clinic = ClinicHelper.addClinic(site, name);
        StudyWrapper study = StudyHelper.addStudy(clinic.getSite(), name);
        ContactWrapper contact = ContactHelper.addContact(clinic, name);
        study.setContactCollection(Arrays.asList(contact));
        study.persist();
        PatientWrapper patient1 = PatientHelper.addPatient(name, study);
        ShipmentHelper.addShipment(clinic, patient1);
        ShipmentWrapper shipmentTest = ShipmentHelper.addShipment(clinic,
            patient1);
        ShipmentHelper.addShipment(clinic, patient1);

        int countBefore = appService.search(Shipment.class, new Shipment())
            .size();

        shipmentTest.delete();

        int countAfter = appService.search(Shipment.class, new Shipment())
            .size();

        Assert.assertEquals(countBefore - 1, countAfter);
    }

    @Test
    public void testDeleteNoMoreVisits() throws Exception {
        String name = "testDeleteNoMoreVisits" + r.nextInt();
        SiteWrapper site = SiteHelper.addSite(name);
        ClinicWrapper clinic = ClinicHelper.addClinic(site, name);
        StudyWrapper study = StudyHelper.addStudy(clinic.getSite(), name);
        ContactWrapper contact = ContactHelper.addContact(clinic, name);
        study.setContactCollection(Arrays.asList(contact));
        study.persist();
        PatientWrapper patient1 = PatientHelper.addPatient(name, study);
        ShipmentHelper.addShipment(clinic, patient1);
        ShipmentWrapper shipmentTest = ShipmentHelper.addShipment(clinic,
            patient1);
        ShipmentHelper.addShipment(clinic, patient1);

        PatientVisitWrapper visit = PatientVisitHelper.addPatientVisit(
            patient1, shipmentTest, Utils.getRandomDate());
        shipmentTest.reload();

        try {
            shipmentTest.delete();
            Assert.fail("one visit still there");
        } catch (BiobankCheckException bce) {
            Assert.assertTrue(true);
        }

        ShipmentWrapper shipment2 = ShipmentHelper
            .addShipment(clinic, patient1);
        visit.setShipment(shipment2);
        visit.persist();

        int countBefore = appService.search(Shipment.class, new Shipment())
            .size();
        shipmentTest.reload();
        shipmentTest.delete();
        int countAfter = appService.search(Shipment.class, new Shipment())
            .size();
        Assert.assertEquals(countBefore - 1, countAfter);
    }

    @Test
    public void testCompareTo() throws Exception {
        String name = "testCompareTo" + r.nextInt();
        SiteWrapper site = SiteHelper.addSite(name);
        ClinicWrapper clinic = ClinicHelper.addClinic(site, name);
        StudyWrapper study = StudyHelper.addStudy(clinic.getSite(), name);
        ContactWrapper contact = ContactHelper.addContact(clinic, name);
        study.setContactCollection(Arrays.asList(contact));
        study.persist();
        PatientWrapper patient1 = PatientHelper.addPatient(name, study);
        ShipmentWrapper shipment1 = ShipmentHelper
            .addShipment(clinic, patient1);
        shipment1.setDateShipped(DateFormatter.dateFormatter
            .parse("2010-02-01 23:00"));
        ShipmentWrapper shipment2 = ShipmentHelper
            .addShipment(clinic, patient1);
        shipment2.setDateShipped(DateFormatter.dateFormatter
            .parse("2009-12-01 23:00"));

        Assert.assertTrue(shipment1.compareTo(shipment2) > 0);
        Assert.assertTrue(shipment2.compareTo(shipment1) < 0);
    }

    @Test
    public void testResetAlreadyInDatabase() throws Exception {
        String name = "testResetAlreadyInDatabase" + r.nextInt();
        SiteWrapper site = SiteHelper.addSite(name);
        ClinicWrapper clinic = ClinicHelper.addClinic(site, name);
        StudyWrapper study = StudyHelper.addStudy(clinic.getSite(), name);
        ContactWrapper contact = ContactHelper.addContact(clinic, name);
        study.setContactCollection(Arrays.asList(contact));
        study.persist();
        PatientWrapper patient1 = PatientHelper.addPatient(name, study);
        ShipmentWrapper shipment1 = ShipmentHelper
            .addShipment(clinic, patient1);
        String oldWaybill = shipment1.getWaybill();
        shipment1.setWaybill("QQQQ");
        shipment1.reset();
        Assert.assertEquals(oldWaybill, shipment1.getWaybill());
    }

    @Test
    public void testResetNew() throws Exception {
        ShipmentWrapper shipment = new ShipmentWrapper(appService);
        shipment.setWaybill("titi");
        shipment.reset();
        Assert.assertEquals(null, shipment.getWaybill());
    }

}
