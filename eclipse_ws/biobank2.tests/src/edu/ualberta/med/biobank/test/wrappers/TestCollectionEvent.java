package edu.ualberta.med.biobank.test.wrappers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import edu.ualberta.med.biobank.common.exception.BiobankCheckException;
import edu.ualberta.med.biobank.common.formatters.DateFormatter;
import edu.ualberta.med.biobank.common.wrappers.ClinicWrapper;
import edu.ualberta.med.biobank.common.wrappers.CollectionEventWrapper;
import edu.ualberta.med.biobank.common.wrappers.ContactWrapper;
import edu.ualberta.med.biobank.common.wrappers.PatientWrapper;
import edu.ualberta.med.biobank.common.wrappers.ProcessingEventWrapper;
import edu.ualberta.med.biobank.common.wrappers.ShippingMethodWrapper;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;
import edu.ualberta.med.biobank.common.wrappers.StudyWrapper;
import edu.ualberta.med.biobank.model.CollectionEvent;
import edu.ualberta.med.biobank.test.TestDatabase;
import edu.ualberta.med.biobank.test.Utils;
import edu.ualberta.med.biobank.test.internal.ClinicHelper;
import edu.ualberta.med.biobank.test.internal.CollectionEventHelper;
import edu.ualberta.med.biobank.test.internal.ContactHelper;
import edu.ualberta.med.biobank.test.internal.DbHelper;
import edu.ualberta.med.biobank.test.internal.PatientHelper;
import edu.ualberta.med.biobank.test.internal.ProcessingEventHelper;
import edu.ualberta.med.biobank.test.internal.ShippingMethodHelper;
import edu.ualberta.med.biobank.test.internal.SiteHelper;
import edu.ualberta.med.biobank.test.internal.SourceVesselHelper;
import edu.ualberta.med.biobank.test.internal.StudyHelper;

public class TestCollectionEvent extends TestDatabase {

    @Test
    public void testGettersAndSetters() throws Exception {
        String name = "testGettersAndSetters" + r.nextInt();
        SiteWrapper site = SiteHelper.addSite(name);

        CollectionEventWrapper shipment = CollectionEventHelper
            .addCollectionEventWithRandomPatient(site, name);
        testGettersAndSetters(shipment);
    }

    @Test
    public void testGetSetShippingMethod() throws Exception {
        String name = "testGetSetShippingMethod" + r.nextInt();
        SiteWrapper site = SiteHelper.addSite(name);
        ShippingMethodWrapper company = ShippingMethodHelper
            .addShippingMethod(name);
        CollectionEventWrapper shipment = CollectionEventHelper
            .addCollectionEventWithRandomPatient(site, name);

        shipment.setShippingMethod(company);
        shipment.persist();

        shipment.reload();

        Assert.assertEquals(company, shipment.getShippingMethod());
    }

    @Test
    public void testGetPatientCollection() throws Exception {
        String name = "testGetPatientCollection" + r.nextInt();
        SiteWrapper site = SiteHelper.addSite(name);
        ClinicWrapper clinic1 = ClinicHelper.addClinic(name + "CLINIC1");
        ContactWrapper contact1 = ContactHelper.addContact(clinic1, name
            + "CONTACT1");
        ClinicWrapper clinic2 = ClinicHelper.addClinic(name + "CLINIC2");
        ContactWrapper contact2 = ContactHelper.addContact(clinic2, name
            + "CONTACT2");

        List<ContactWrapper> contacts = new ArrayList<ContactWrapper>();
        contacts.add(contact1);
        contacts.add(contact2);

        StudyWrapper study1 = StudyHelper.addStudy(name + "STUDY1");
        study1.addToContactCollection(contacts);
        study1.persist();
        PatientWrapper patient1 = PatientHelper.addPatient(name, study1);
        PatientWrapper patient2 = PatientHelper.addPatient(name + "_2", study1);

        StudyWrapper study2 = StudyHelper.addStudy(name + "STUDY2");
        study2.addToContactCollection(contacts);
        study2.persist();
        PatientWrapper patient3 = PatientHelper.addPatient(name + "_3", study2);

        CollectionEventWrapper shipment = CollectionEventHelper
            .newCollectionEvent(site,
                ShippingMethodWrapper.getShippingMethods(appService).get(0));
        shipment.addSourceVessels(Arrays.asList(
            SourceVesselHelper.addSourceVessel(patient1, Utils.getRandomDate(),
                0.1),
            SourceVesselHelper.addSourceVessel(patient2, Utils.getRandomDate(),
                0.1),
            SourceVesselHelper.addSourceVessel(patient3, Utils.getRandomDate(),
                0.1)));
        shipment.persist();

        shipment.reload();
        Assert.assertEquals(3, shipment.getPatientCollection().size());
    }

    @Test
    public void testGetPatientCollectionBoolean() throws Exception {
        String name = "testGetPatientCollectionBoolean" + r.nextInt();
        SiteWrapper site = SiteHelper.addSite(name);
        ClinicWrapper clinic1 = ClinicHelper.addClinic(name + "CLINIC1");
        ContactWrapper contact1 = ContactHelper.addContact(clinic1, name
            + "CONTACT1");
        ClinicWrapper clinic2 = ClinicHelper.addClinic(name + "CLINIC2");
        ContactWrapper contact2 = ContactHelper.addContact(clinic2, name
            + "CONTACT2");

        List<ContactWrapper> contacts = new ArrayList<ContactWrapper>();
        contacts.add(contact1);
        contacts.add(contact2);

        StudyWrapper study1 = StudyHelper.addStudy(name + "STUDY1");
        study1.addToContactCollection(contacts);
        study1.persist();
        PatientWrapper patient1 = PatientHelper.addPatient("QWERTY" + name,
            study1);
        PatientWrapper patient2 = PatientHelper.addPatient("ASDFG" + name,
            study1);

        StudyWrapper study2 = StudyHelper.addStudy(name + "STUDY2");
        study2.addToContactCollection(contacts);
        study2.persist();
        PatientWrapper patient3 = PatientHelper.addPatient("ZXCVB" + name,
            study2);

        CollectionEventWrapper shipment = CollectionEventHelper
            .newCollectionEvent(site,
                ShippingMethodWrapper.getShippingMethods(appService).get(0));
        shipment.addSourceVessels(Arrays.asList(
            SourceVesselHelper.addSourceVessel(patient1, Utils.getRandomDate(),
                0.1),
            SourceVesselHelper.addSourceVessel(patient2, Utils.getRandomDate(),
                0.1),
            SourceVesselHelper.addSourceVessel(patient3, Utils.getRandomDate(),
                0.1)));
        shipment.persist();

        shipment.reload();
        List<PatientWrapper> patients = shipment.getPatientCollection();
        if (patients.size() > 1) {
            for (int i = 0; i < patients.size() - 1; i++) {
                PatientWrapper p1 = patients.get(i);
                PatientWrapper p2 = patients.get(i + 1);
                Assert.assertTrue(p1.compareTo(p2) <= 0);
            }
        }
    }

    @Test
    public void testAddPatients() throws Exception {
        String name = "testAddPatients" + r.nextInt();
        SiteWrapper site = SiteHelper.addSite(name);
        ClinicWrapper clinic = ClinicHelper.addClinic(name);
        StudyWrapper study = StudyHelper.addStudy(name);
        ContactWrapper contact = ContactHelper.addContact(clinic, name);
        study.addToContactCollection(Arrays.asList(contact));
        study.persist();
        PatientWrapper firstPatient = PatientHelper.addPatient(name, study);
        CollectionEventWrapper shipment = CollectionEventHelper
            .addCollectionEvent(
                site,
                ShippingMethodWrapper.getShippingMethods(appService).get(0),
                SourceVesselHelper.addSourceVessel(firstPatient, Utils.getRandomDate(),
                    0.1));
        shipment.reload();

        PatientWrapper patient = PatientHelper.addPatient(name + "NewPatient",
            study);
        shipment.addSourceVessels(Arrays.asList(SourceVesselHelper
            .addSourceVessel(patient, Utils.getRandomDate(), 0.1)));
        shipment.persist();

        shipment.reload();
        // one patient added
        Assert.assertEquals(2, shipment.getPatientCollection().size());
    }

    @Test
    public void testRemoveSourceVessels() throws Exception {
        String name = "testRemovePatients" + r.nextInt();
        SiteWrapper site = SiteHelper.addSite(name);
        ClinicWrapper clinic = ClinicHelper.addClinic(name);
        StudyWrapper study = StudyHelper.addStudy(name);
        ContactWrapper contact = ContactHelper.addContact(clinic, name);
        study.addToContactCollection(Arrays.asList(contact));
        study.persist();
        PatientWrapper patient1 = PatientHelper.addPatient(name, study);
        PatientWrapper patient2 = PatientHelper.addPatient(name + "_2", study);
        CollectionEventWrapper shipment = CollectionEventHelper
            .addCollectionEvent(
                site,
                ShippingMethodWrapper.getShippingMethods(appService).get(0),
                SourceVesselHelper.addSourceVessel(patient1, Utils.getRandomDate(),
                    0.1),
                SourceVesselHelper.addSourceVessel(patient2, Utils.getRandomDate(),
                    0.1));
        shipment.reload();

        PatientWrapper patient = DbHelper.chooseRandomlyInList(shipment
            .getPatientCollection());

        shipment.removeSourceVessels(patient.getSourceVesselCollection());
        shipment.persist();

        shipment.reload();
        // one patient removed
        Assert.assertEquals(1, shipment.getPatientCollection().size());

    }

    @Test
    public void testGetShipmentInSite() throws Exception {
        String name = "testSetPatientCollectionRemove" + r.nextInt();
        SiteWrapper site = SiteHelper.addSite(name);
        ClinicWrapper clinic = ClinicHelper.addClinic(name);
        StudyWrapper study = StudyHelper.addStudy(name);
        ContactWrapper contact = ContactHelper.addContact(clinic, name);
        study.addToContactCollection(Arrays.asList(contact));
        study.persist();
        PatientWrapper patient1 = PatientHelper.addPatient(name, study);
        ShippingMethodWrapper method = ShippingMethodWrapper
            .getShippingMethods(appService).get(0);
        CollectionEventHelper.addCollectionEvent(
            site,
            method,
            SourceVesselHelper.addSourceVessel(patient1, Utils.getRandomDate(),
                0.1));
        CollectionEventWrapper shipmentTest = CollectionEventHelper
            .addCollectionEvent(
                site,
                method,
                SourceVesselHelper.addSourceVessel(patient1, Utils.getRandomDate(),
                    0.1));
        CollectionEventWrapper shipmentWithDate = CollectionEventHelper
            .addCollectionEvent(
                site,
                method,
                SourceVesselHelper.addSourceVessel(patient1, Utils.getRandomDate(),
                    0.1));

        String waybill = shipmentTest.getWaybill();

        site.reload();
        List<CollectionEventWrapper> shipsFound = CollectionEventWrapper
            .getCollectionEvents(appService, waybill);

        Assert.assertEquals(1, shipsFound.size());
        Assert.assertEquals(shipmentTest, shipsFound.get(0));

        // test for date
        shipsFound = CollectionEventWrapper.getCollectionEvents(appService,
            shipmentWithDate.getDateReceived());

        Assert.assertEquals(1, shipsFound.size());
        Assert.assertEquals(shipmentWithDate, shipsFound.get(0));

        CollectionEventWrapper shipment = CollectionEventHelper
            .newCollectionEvent(site,
                ShippingMethodWrapper.getShippingMethods(appService).get(0),
                name, Utils.getRandomDate());

        Assert.assertNull(shipment.getSourceCenter());
    }

    @Test
    public void testHasPatient() throws Exception {
        String name = "testHasPatient" + r.nextInt();
        SiteWrapper site = SiteHelper.addSite(name);
        ClinicWrapper clinic1 = ClinicHelper.addClinic(name + "CLINIC1");
        ContactWrapper contact1 = ContactHelper.addContact(clinic1, name
            + "CONTACT1");
        ClinicWrapper clinic2 = ClinicHelper.addClinic(name + "CLINIC2");
        ContactWrapper contact2 = ContactHelper.addContact(clinic2, name
            + "CONTACT2");

        List<ContactWrapper> contacts = new ArrayList<ContactWrapper>();
        contacts.add(contact1);
        contacts.add(contact2);

        StudyWrapper study1 = StudyHelper.addStudy(name + "STUDY1");
        study1.addToContactCollection(contacts);
        study1.persist();
        PatientWrapper patient1 = PatientHelper.addPatient(name, study1);
        PatientWrapper patient2 = PatientHelper.addPatient(name + "_2", study1);

        StudyWrapper study2 = StudyHelper.addStudy(name + "STUDY2");
        study2.addToContactCollection(contacts);
        study2.persist();
        PatientWrapper patient3 = PatientHelper.addPatient(name + "_3", study2);
        PatientHelper.addPatient(name + "_4", study2);

        CollectionEventWrapper shipment = CollectionEventHelper
            .newCollectionEvent(site,
                ShippingMethodWrapper.getShippingMethods(appService).get(0));
        shipment.addSourceVessels(Arrays.asList(
            SourceVesselHelper.addSourceVessel(patient1, Utils.getRandomDate(),
                0.1),
            SourceVesselHelper.addSourceVessel(patient2, Utils.getRandomDate(),
                0.1),
            SourceVesselHelper.addSourceVessel(patient3, Utils.getRandomDate(),
                0.1)));
        shipment.persist();

        shipment.reload();
        Assert.assertTrue(shipment.hasPatient(name + "_2"));
        Assert.assertFalse(shipment.hasPatient(name + "_4"));
    }

    @Test
    public void testPersist() throws Exception {
        String name = "testPersist" + r.nextInt();
        SiteWrapper site = SiteHelper.addSite(name);
        ClinicWrapper clinic = ClinicHelper.addClinic(name);
        StudyWrapper study = StudyHelper.addStudy(name);
        ContactWrapper contact = ContactHelper.addContact(clinic, name);
        study.addToContactCollection(Arrays.asList(contact));
        study.persist();
        ShippingMethodWrapper method = ShippingMethodWrapper
            .getShippingMethods(appService).get(0);
        PatientWrapper patient = PatientHelper.addPatient(name, study);
        CollectionEventWrapper shipment = CollectionEventHelper
            .newCollectionEvent(
                null,
                method,
                name,
                Utils.getRandomDate(),
                SourceVesselHelper.addSourceVessel(patient, Utils.getRandomDate(),
                    0.1));

        try {
            shipment.persist();
            Assert.fail("shipment does not have a site");
        } catch (BiobankCheckException bce) {
            Assert.assertTrue(true);
        }
    }

    @Test
    public void testPersistFailWaybillNull() throws Exception {
        String name = "testPersistFailWaybillNull" + r.nextInt();
        SiteWrapper site = SiteHelper.addSite(name);
        ClinicWrapper clinic = ClinicHelper.addClinic(name);
        StudyWrapper study = StudyHelper.addStudy(name);
        ContactWrapper contact = ContactHelper.addContact(clinic, name);
        study.addToContactCollection(Arrays.asList(contact));
        study.persist();
        PatientWrapper patient = PatientHelper.addPatient(name, study);
        CollectionEventWrapper shipment = CollectionEventHelper
            .newCollectionEvent(
                site,
                ShippingMethodWrapper.getShippingMethods(appService).get(0),
                null,
                Utils.getRandomDate(),
                SourceVesselHelper.addSourceVessel(patient, Utils.getRandomDate(),
                    0.1));

        try {
            shipment.persist();
            Assert.fail("shipment with waybill null");
        } catch (BiobankCheckException bce) {
            Assert.assertTrue(true);
        }
    }

    @Test
    public void testPersistFailNoNeedWaybill() throws Exception {
        String name = "testPersistFailNoNeedWaybill" + r.nextInt();
        SiteWrapper site = SiteHelper.addSite(name);
        ClinicWrapper clinic = ClinicHelper.newClinic(name);
        clinic.persist();
        ClinicHelper.createdClinics.add(clinic);

        StudyWrapper study = StudyHelper.addStudy(name);
        ContactWrapper contact = ContactHelper.addContact(clinic, name);
        study.addToContactCollection(Arrays.asList(contact));
        study.persist();
        PatientWrapper patient = PatientHelper.addPatient(name, study);
        CollectionEventWrapper shipment = CollectionEventHelper
            .newCollectionEvent(
                site,
                ShippingMethodWrapper.getShippingMethods(appService).get(0),
                TestCommon.getNewWaybill(r),
                Utils.getRandomDate(),
                SourceVesselHelper.addSourceVessel(patient, Utils.getRandomDate(),
                    0.1));

        try {
            shipment.persist();
            Assert.fail("shipment should not have a waybill");
        } catch (BiobankCheckException bce) {
            Assert.assertTrue(true);
        }

        // should not have any waybill
        shipment.setWaybill(null);
        shipment.persist();
    }

    @Test
    public void testPersistFailWaybillExists() throws Exception {
        String name = "testPersistFailWaybillExists" + r.nextInt();
        SiteWrapper site = SiteHelper.addSite(name);
        ClinicWrapper clinic = ClinicHelper.addClinic(name);
        StudyWrapper study = StudyHelper.addStudy(name);
        ContactWrapper contact = ContactHelper.addContact(clinic, name);
        study.addToContactCollection(Arrays.asList(contact));
        study.persist();
        ShippingMethodWrapper method = ShippingMethodWrapper
            .getShippingMethods(appService).get(0);
        PatientWrapper patient = PatientHelper.addPatient(name, study);
        CollectionEventWrapper shipment = CollectionEventHelper
            .newCollectionEvent(
                site,
                method,
                name,
                Utils.getRandomDate(),
                SourceVesselHelper.addSourceVessel(patient, Utils.getRandomDate(),
                    0.1));

        shipment.persist();

        CollectionEventWrapper shipment2 = CollectionEventHelper
            .newCollectionEvent(
                site,
                method,
                name,
                Utils.getRandomDate(),
                SourceVesselHelper.addSourceVessel(patient, Utils.getRandomDate(),
                    0.1));
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
        ClinicWrapper clinic = ClinicHelper.addClinic(name);
        StudyWrapper study = StudyHelper.addStudy(name);
        ContactWrapper contact = ContactHelper.addContact(clinic, name);
        study.addToContactCollection(Arrays.asList(contact));
        study.persist();
        CollectionEventWrapper shipment = CollectionEventHelper
            .newCollectionEvent(site,
                ShippingMethodWrapper.getShippingMethods(appService).get(0));
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
        ClinicWrapper clinic = ClinicHelper.addClinic(name);

        StudyWrapper study = StudyHelper.addStudy(name);
        PatientWrapper patient = PatientHelper.addPatient(name, study);
        CollectionEventWrapper shipment = CollectionEventHelper
            .newCollectionEvent(
                site,
                ShippingMethodWrapper.getShippingMethods(appService).get(0),
                name,
                Utils.getRandomDate(),
                SourceVesselHelper.addSourceVessel(patient, Utils.getRandomDate(),
                    0.1));

        try {
            shipment.persist();
            Assert
                .fail("patient should be part of the study that has contact with the clinic");
        } catch (BiobankCheckException bce) {
            Assert.assertTrue(true);
        }
    }

    @Test
    public void testDelete() throws Exception {
        String name = "testDelete" + r.nextInt();
        SiteWrapper site = SiteHelper.addSite(name);
        ClinicWrapper clinic = ClinicHelper.addClinic(name);
        StudyWrapper study = StudyHelper.addStudy(name);
        ContactWrapper contact = ContactHelper.addContact(clinic, name);
        study.addToContactCollection(Arrays.asList(contact));
        study.persist();
        ShippingMethodWrapper method = ShippingMethodWrapper
            .getShippingMethods(appService).get(0);
        PatientWrapper patient1 = PatientHelper.addPatient(name, study);
        CollectionEventHelper.addCollectionEvent(
            site,
            method,
            SourceVesselHelper.addSourceVessel(patient1, Utils.getRandomDate(),
                0.1));
        CollectionEventWrapper shipmentTest = CollectionEventHelper
            .addCollectionEvent(
                site,
                method,
                SourceVesselHelper.addSourceVessel(patient1, Utils.getRandomDate(),
                    0.1));
        CollectionEventHelper.addCollectionEvent(
            site,
            method,
            SourceVesselHelper.addSourceVessel(patient1, Utils.getRandomDate(),
                0.1));

        int countBefore = appService.search(CollectionEvent.class,
            new CollectionEvent()).size();

        shipmentTest.delete();

        int countAfter = appService.search(CollectionEvent.class,
            new CollectionEvent()).size();

        Assert.assertEquals(countBefore - 1, countAfter);
    }

    @Test
    public void testDeleteNoMoreVisits() throws Exception {
        String name = "testDeleteNoMoreVisits" + r.nextInt();
        SiteWrapper site = SiteHelper.addSite(name);
        ClinicWrapper clinic = ClinicHelper.addClinic(name);
        StudyWrapper study = StudyHelper.addStudy(name);
        ContactWrapper contact = ContactHelper.addContact(clinic, name);
        study.addToContactCollection(Arrays.asList(contact));
        study.persist();
        ShippingMethodWrapper method = ShippingMethodWrapper
            .getShippingMethods(appService).get(0);
        PatientWrapper patient1 = PatientHelper.addPatient(name, study);
        CollectionEventHelper.addCollectionEvent(
            site,
            method,
            SourceVesselHelper.addSourceVessel(patient1, Utils.getRandomDate(),
                0.1));
        CollectionEventWrapper shipmentTest = CollectionEventHelper
            .addCollectionEvent(
                site,
                method,
                SourceVesselHelper.addSourceVessel(patient1, Utils.getRandomDate(),
                    0.1));
        CollectionEventHelper.addCollectionEvent(
            site,
            method,
            SourceVesselHelper.addSourceVessel(patient1, Utils.getRandomDate(),
                0.1));

        ProcessingEventWrapper visit = ProcessingEventHelper
            .addProcessingEvent(site, patient1, Utils.getRandomDate(),
                Utils.getRandomDate());
        shipmentTest.reload();

        try {
            shipmentTest.delete();
            Assert.fail("one visit still there");
        } catch (BiobankCheckException bce) {
            Assert.assertTrue(true);
        }

        CollectionEventWrapper shipment2 = CollectionEventHelper
            .addCollectionEvent(
                site,
                ShippingMethodWrapper.getShippingMethods(appService).get(0),
                SourceVesselHelper.addSourceVessel(patient1, Utils.getRandomDate(),
                    0.1));

        int countBefore = appService.search(CollectionEvent.class,
            new CollectionEvent()).size();
        shipmentTest.reload();
        shipmentTest.delete();
        int countAfter = appService.search(CollectionEvent.class,
            new CollectionEvent()).size();
        Assert.assertEquals(countBefore - 1, countAfter);
    }

    @Test
    public void testCompareTo() throws Exception {
        String name = "testCompareTo" + r.nextInt();
        SiteWrapper site = SiteHelper.addSite(name);
        ClinicWrapper clinic = ClinicHelper.addClinic(name);
        StudyWrapper study = StudyHelper.addStudy(name);
        ContactWrapper contact = ContactHelper.addContact(clinic, name);
        study.addToContactCollection(Arrays.asList(contact));
        study.persist();
        ShippingMethodWrapper method = ShippingMethodWrapper
            .getShippingMethods(appService).get(0);
        PatientWrapper patient1 = PatientHelper.addPatient(name, study);
        CollectionEventWrapper shipment1 = CollectionEventHelper
            .addCollectionEvent(
                site,
                method,
                SourceVesselHelper.addSourceVessel(patient1, Utils.getRandomDate(),
                    0.1));
        shipment1.setDateReceived(DateFormatter.dateFormatter
            .parse("2010-02-01 23:00"));
        CollectionEventWrapper shipment2 = CollectionEventHelper
            .addCollectionEvent(
                site,
                method,
                SourceVesselHelper.addSourceVessel(patient1, Utils.getRandomDate(),
                    0.1));
        shipment2.setDateReceived(DateFormatter.dateFormatter
            .parse("2009-12-01 23:00"));

        Assert.assertTrue(shipment1.compareTo(shipment2) > 0);
        Assert.assertTrue(shipment2.compareTo(shipment1) < 0);

        Assert.assertTrue(shipment1.compareTo(null) == 0);
        Assert.assertTrue(shipment2.compareTo(null) == 0);
    }

    @Test
    public void testResetAlreadyInDatabase() throws Exception {
        String name = "testResetAlreadyInDatabase" + r.nextInt();
        SiteWrapper site = SiteHelper.addSite(name);
        ClinicWrapper clinic = ClinicHelper.addClinic(name);
        StudyWrapper study = StudyHelper.addStudy(name);
        ContactWrapper contact = ContactHelper.addContact(clinic, name);
        study.addToContactCollection(Arrays.asList(contact));
        study.persist();
        PatientWrapper patient1 = PatientHelper.addPatient(name, study);
        CollectionEventWrapper shipment1 = CollectionEventHelper
            .addCollectionEvent(
                site,
                ShippingMethodWrapper.getShippingMethods(appService).get(0),
                SourceVesselHelper.addSourceVessel(patient1, Utils.getRandomDate(),
                    0.1));
        String oldWaybill = shipment1.getWaybill();
        shipment1.setWaybill("QQQQ");
        shipment1.reset();
        Assert.assertEquals(oldWaybill, shipment1.getWaybill());
    }

    @Test
    public void testResetNew() throws Exception {
        CollectionEventWrapper shipment = new CollectionEventWrapper(appService);
        shipment.setWaybill("titi");
        shipment.reset();
        Assert.assertEquals(null, shipment.getWaybill());
    }

    @Test
    public void testGetTodayShipments() throws Exception {
        String name = "testTodayShipments_" + r.nextInt();
        SiteWrapper site = SiteHelper.addSite(name);

        ClinicWrapper clinic1 = ClinicHelper.addClinic(name + "_1");
        ClinicWrapper clinic2 = ClinicHelper.addClinic(name + "_2");
        StudyWrapper study = StudyHelper.addStudy(name);
        ContactWrapper contact1 = ContactHelper.addContact(clinic1, name);
        ContactWrapper contact2 = ContactHelper.addContact(clinic2, name);
        study.addToContactCollection(Arrays.asList(contact1, contact2));
        study.persist();

        PatientWrapper patient1 = PatientHelper.addPatient(name + "_1", study);
        PatientWrapper patient2 = PatientHelper.addPatient(name + "_2", study);

        ShippingMethodWrapper method = ShippingMethodWrapper
            .getShippingMethods(appService).get(0);
        CollectionEventHelper.addCollectionEvent(
            site,
            method,
            SourceVesselHelper.addSourceVessel(patient1, Utils.getRandomDate(),
                0.1)); // another
        // day
        CollectionEventWrapper shipment2 = CollectionEventHelper
            .newCollectionEvent(
                site,
                method,
                "waybill_" + name + "_2",
                new Date(),
                SourceVesselHelper.addSourceVessel(patient1, Utils.getRandomDate(),
                    0.1)); // today
        shipment2.persist();
        CollectionEventWrapper shipment3 = CollectionEventHelper
            .newCollectionEvent(
                site,
                method,
                "waybill_" + name + "_3",
                new Date(),
                SourceVesselHelper.addSourceVessel(patient2, Utils.getRandomDate(),
                    0.1)); // today
        shipment3.persist();

        List<CollectionEventWrapper> ships = CollectionEventWrapper
            .getTodayCollectionEvents(appService);
        Assert.assertEquals(2, ships.size());
        Assert.assertTrue(ships.contains(shipment2));
        Assert.assertTrue(ships.contains(shipment3));
    }

    @Test
    public void testIsReceivedToday() throws Exception {
        String name = "testTodayShipments_" + r.nextInt();
        SiteWrapper site = SiteHelper.addSite(name);

        ClinicWrapper clinic1 = ClinicHelper.addClinic(name + "_1");
        ClinicWrapper clinic2 = ClinicHelper.addClinic(name + "_2");
        StudyWrapper study = StudyHelper.addStudy(name);
        ContactWrapper contact1 = ContactHelper.addContact(clinic1, name);
        ContactWrapper contact2 = ContactHelper.addContact(clinic2, name);
        study.addToContactCollection(Arrays.asList(contact1, contact2));
        study.persist();

        PatientWrapper patient1 = PatientHelper.addPatient(name + "_1", study);
        PatientWrapper patient2 = PatientHelper.addPatient(name + "_2", study);

        ShippingMethodWrapper method = ShippingMethodWrapper
            .getShippingMethods(appService).get(0);
        CollectionEventWrapper shipment1 = CollectionEventHelper
            .addCollectionEvent(
                site,
                method,
                SourceVesselHelper.addSourceVessel(patient1, Utils.getRandomDate(),
                    0.1)); // another day
        CollectionEventWrapper shipment2 = CollectionEventHelper
            .newCollectionEvent(
                site,

                method,
                "waybill_" + name + "_2",
                new Date(),
                SourceVesselHelper.addSourceVessel(patient1, Utils.getRandomDate(),
                    0.1)); // today
        shipment2.persist();
        CollectionEventWrapper shipment3 = CollectionEventHelper
            .newCollectionEvent(
                site,
                method,
                "waybill_" + name + "_3",
                new Date(),
                SourceVesselHelper.addSourceVessel(patient2, Utils.getRandomDate(),
                    0.1)); // today
        shipment3.persist();

        Assert.assertFalse(shipment1.isReceivedToday());
        Assert.assertTrue(shipment2.isReceivedToday());
        Assert.assertTrue(shipment3.isReceivedToday());
    }
}
