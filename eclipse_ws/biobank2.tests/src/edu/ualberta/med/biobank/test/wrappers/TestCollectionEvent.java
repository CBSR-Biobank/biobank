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

        CollectionEventWrapper cevent = CollectionEventHelper
            .addCollectionEventWithRandomPatient(site, name);

        testGettersAndSetters(cevent);
    }

    @Test
    public void testGetSetShippingMethod() throws Exception {
        String name = "testGetSetShippingMethod" + r.nextInt();
        SiteWrapper site = SiteHelper.addSite(name);
        ShippingMethodWrapper company = ShippingMethodHelper
            .addShippingMethod(name);
        CollectionEventWrapper cevent = CollectionEventHelper
            .addCollectionEventWithRandomPatient(site, name);

        cevent.setShippingMethod(company);
        cevent.persist();

        cevent.reload();

        Assert.assertEquals(company, cevent.getShippingMethod());
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

        CollectionEventWrapper cevent = CollectionEventHelper
            .newCollectionEvent(site,
                ShippingMethodWrapper.getShippingMethods(appService).get(0));
        cevent.addToSourceVesselCollection(Arrays.asList(SourceVesselHelper
            .newSourceVessel(patient1, Utils.getRandomDate(), 0.1),
            SourceVesselHelper.newSourceVessel(patient2, Utils.getRandomDate(),
                0.1), SourceVesselHelper.newSourceVessel(patient3,
                Utils.getRandomDate(), 0.1)));
        cevent.persist();

        cevent.reload();
        Assert.assertEquals(3, cevent.getPatientCollection().size());
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

        CollectionEventWrapper cevent = CollectionEventHelper
            .newCollectionEvent(site,
                ShippingMethodWrapper.getShippingMethods(appService).get(0));
        cevent.addToSourceVesselCollection(Arrays.asList(SourceVesselHelper
            .newSourceVessel(patient1, Utils.getRandomDate(), 0.1),
            SourceVesselHelper.newSourceVessel(patient2, Utils.getRandomDate(),
                0.1), SourceVesselHelper.newSourceVessel(patient3,
                Utils.getRandomDate(), 0.1)));
        cevent.persist();

        cevent.reload();
        List<PatientWrapper> patients = cevent.getPatientCollection();
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
        CollectionEventWrapper cevent = CollectionEventHelper
            .addCollectionEvent(
                site,
                ShippingMethodWrapper.getShippingMethods(appService).get(0),
                SourceVesselHelper.newSourceVessel(firstPatient,
                    Utils.getRandomDate(), 0.1));
        cevent.reload();

        PatientWrapper patient = PatientHelper.addPatient(name + "NewPatient",
            study);
        cevent.addToSourceVesselCollection(Arrays.asList(SourceVesselHelper
            .newSourceVessel(patient, Utils.getRandomDate(), 0.1)));
        cevent.persist();

        cevent.reload();
        // one patient added
        Assert.assertEquals(2, cevent.getPatientCollection().size());
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
        CollectionEventWrapper cevent = CollectionEventHelper
            .addCollectionEvent(
                site,
                ShippingMethodWrapper.getShippingMethods(appService).get(0),
                SourceVesselHelper.newSourceVessel(patient1,
                    Utils.getRandomDate(), 0.1),
                SourceVesselHelper.newSourceVessel(patient2,
                    Utils.getRandomDate(), 0.1));
        cevent.reload();

        PatientWrapper patient = DbHelper.chooseRandomlyInList(cevent
            .getPatientCollection());

        cevent.removeFromSourceVesselCollection(patient
            .getSourceVesselCollection(false));
        cevent.persist();

        cevent.reload();
        // one patient removed
        Assert.assertEquals(1, cevent.getPatientCollection().size());

    }

    @Test
    public void testGetceventInSite() throws Exception {
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
        CollectionEventHelper.addCollectionEvent(site, method,
            SourceVesselHelper.newSourceVessel(patient1, Utils.getRandomDate(),
                0.1));
        CollectionEventWrapper ceventTest = CollectionEventHelper
            .addCollectionEvent(
                site,
                method,
                SourceVesselHelper.newSourceVessel(patient1,
                    Utils.getRandomDate(), 0.1));
        CollectionEventWrapper ceventWithDate = CollectionEventHelper
            .addCollectionEvent(
                site,
                method,
                SourceVesselHelper.newSourceVessel(patient1,
                    Utils.getRandomDate(), 0.1));

        String waybill = ceventTest.getWaybill();

        site.reload();
        List<CollectionEventWrapper> shipsFound = CollectionEventWrapper
            .getCollectionEvents(appService, waybill);

        Assert.assertEquals(1, shipsFound.size());
        Assert.assertEquals(ceventTest, shipsFound.get(0));

        // test for date
        shipsFound = CollectionEventWrapper.getCollectionEvents(appService,
            ceventWithDate.getDateReceived());

        Assert.assertEquals(1, shipsFound.size());
        Assert.assertEquals(ceventWithDate, shipsFound.get(0));

        CollectionEventWrapper cevent = CollectionEventHelper
            .newCollectionEvent(site,
                ShippingMethodWrapper.getShippingMethods(appService).get(0),
                name, Utils.getRandomDate());

        Assert.assertNull(cevent.getSourceCenter());
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

        CollectionEventWrapper cevent = CollectionEventHelper
            .newCollectionEvent(site,
                ShippingMethodWrapper.getShippingMethods(appService).get(0));
        cevent.addToSourceVesselCollection(Arrays.asList(SourceVesselHelper
            .newSourceVessel(patient1, Utils.getRandomDate(), 0.1),
            SourceVesselHelper.newSourceVessel(patient2, Utils.getRandomDate(),
                0.1), SourceVesselHelper.newSourceVessel(patient3,
                Utils.getRandomDate(), 0.1)));
        cevent.persist();

        cevent.reload();
        Assert.assertTrue(cevent.hasPatient(name + "_2"));
        Assert.assertFalse(cevent.hasPatient(name + "_4"));
    }

    @Test
    public void testPersist() throws Exception {
        String name = "testPersist" + r.nextInt();
        ClinicWrapper clinic = ClinicHelper.addClinic(name);
        StudyWrapper study = StudyHelper.addStudy(name);
        ContactWrapper contact = ContactHelper.addContact(clinic, name);
        study.addToContactCollection(Arrays.asList(contact));
        study.persist();
        ShippingMethodWrapper method = ShippingMethodWrapper
            .getShippingMethods(appService).get(0);
        PatientWrapper patient = PatientHelper.addPatient(name, study);
        CollectionEventWrapper cevent = CollectionEventHelper
            .newCollectionEvent(
                null,
                method,
                name,
                Utils.getRandomDate(),
                SourceVesselHelper.newSourceVessel(patient,
                    Utils.getRandomDate(), 0.1));

        try {
            cevent.persist();
            Assert.fail("cevent does not have a site");
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
        CollectionEventWrapper cevent = CollectionEventHelper
            .newCollectionEvent(
                site,
                ShippingMethodWrapper.getShippingMethods(appService).get(0),
                null,
                Utils.getRandomDate(),
                SourceVesselHelper.newSourceVessel(patient,
                    Utils.getRandomDate(), 0.1));

        try {
            cevent.persist();
            Assert.fail("cevent with waybill null");
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
        CollectionEventWrapper cevent = CollectionEventHelper
            .newCollectionEvent(
                site,
                ShippingMethodWrapper.getShippingMethods(appService).get(0),
                TestCommon.getNewWaybill(r),
                Utils.getRandomDate(),
                SourceVesselHelper.newSourceVessel(patient,
                    Utils.getRandomDate(), 0.1));

        try {
            cevent.persist();
            Assert.fail("cevent should not have a waybill");
        } catch (BiobankCheckException bce) {
            Assert.assertTrue(true);
        }

        // should not have any waybill
        cevent.setWaybill(null);
        cevent.persist();
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
        CollectionEventWrapper cevent = CollectionEventHelper
            .newCollectionEvent(
                site,
                method,
                name,
                Utils.getRandomDate(),
                SourceVesselHelper.newSourceVessel(patient,
                    Utils.getRandomDate(), 0.1));

        cevent.persist();

        CollectionEventWrapper cevent2 = CollectionEventHelper
            .newCollectionEvent(
                site,
                method,
                name,
                Utils.getRandomDate(),
                SourceVesselHelper.newSourceVessel(patient,
                    Utils.getRandomDate(), 0.1));
        try {
            cevent2.persist();
            Assert.fail("cevent with waybill '" + name
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
        CollectionEventWrapper cevent = CollectionEventHelper
            .newCollectionEvent(site,
                ShippingMethodWrapper.getShippingMethods(appService).get(0));
        try {
            cevent.persist();
            Assert
                .fail("cevent don't have any patient. An exception should be thrown.");
        } catch (BiobankCheckException bce) {
            Assert.assertTrue(true);
        }
    }

    @Test
    public void testPersistFailPatientNoInStudyOwnByClinic() throws Exception {
        String name = "testPersistFailPatientNoInStudyOwnByClinic"
            + r.nextInt();
        SiteWrapper site = SiteHelper.addSite(name);

        StudyWrapper study = StudyHelper.addStudy(name);
        PatientWrapper patient = PatientHelper.addPatient(name, study);
        CollectionEventWrapper cevent = CollectionEventHelper
            .newCollectionEvent(
                site,
                ShippingMethodWrapper.getShippingMethods(appService).get(0),
                name,
                Utils.getRandomDate(),
                SourceVesselHelper.newSourceVessel(patient,
                    Utils.getRandomDate(), 0.1));

        try {
            cevent.persist();
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
        CollectionEventHelper.addCollectionEvent(site, method,
            SourceVesselHelper.newSourceVessel(patient1, Utils.getRandomDate(),
                0.1));
        CollectionEventWrapper ceventTest = CollectionEventHelper
            .addCollectionEvent(
                site,
                method,
                SourceVesselHelper.newSourceVessel(patient1,
                    Utils.getRandomDate(), 0.1));
        CollectionEventHelper.addCollectionEvent(site, method,
            SourceVesselHelper.newSourceVessel(patient1, Utils.getRandomDate(),
                0.1));

        int countBefore = appService.search(CollectionEvent.class,
            new CollectionEvent()).size();

        ceventTest.delete();

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
        CollectionEventHelper.addCollectionEvent(site, method,
            SourceVesselHelper.newSourceVessel(patient1, Utils.getRandomDate(),
                0.1));
        CollectionEventWrapper ceventTest = CollectionEventHelper
            .addCollectionEvent(
                site,
                method,
                SourceVesselHelper.newSourceVessel(patient1,
                    Utils.getRandomDate(), 0.1));
        CollectionEventHelper.addCollectionEvent(site, method,
            SourceVesselHelper.newSourceVessel(patient1, Utils.getRandomDate(),
                0.1));

        ProcessingEventHelper.addProcessingEvent(site, patient1,
            Utils.getRandomDate(), Utils.getRandomDate());
        ceventTest.reload();

        try {
            ceventTest.delete();
            Assert.fail("one visit still there");
        } catch (BiobankCheckException bce) {
            Assert.assertTrue(true);
        }

        CollectionEventHelper.addCollectionEvent(site, ShippingMethodWrapper
            .getShippingMethods(appService).get(0), SourceVesselHelper
            .newSourceVessel(patient1, Utils.getRandomDate(), 0.1));

        int countBefore = appService.search(CollectionEvent.class,
            new CollectionEvent()).size();
        ceventTest.reload();
        ceventTest.delete();
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
        CollectionEventWrapper cevent1 = CollectionEventHelper
            .addCollectionEvent(
                site,
                method,
                SourceVesselHelper.newSourceVessel(patient1,
                    Utils.getRandomDate(), 0.1));
        cevent1.setDateReceived(DateFormatter.dateFormatter
            .parse("2010-02-01 23:00"));
        CollectionEventWrapper cevent2 = CollectionEventHelper
            .addCollectionEvent(
                site,
                method,
                SourceVesselHelper.newSourceVessel(patient1,
                    Utils.getRandomDate(), 0.1));
        cevent2.setDateReceived(DateFormatter.dateFormatter
            .parse("2009-12-01 23:00"));

        Assert.assertTrue(cevent1.compareTo(cevent2) > 0);
        Assert.assertTrue(cevent2.compareTo(cevent1) < 0);

        Assert.assertTrue(cevent1.compareTo(null) == 0);
        Assert.assertTrue(cevent2.compareTo(null) == 0);
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
        CollectionEventWrapper cevent1 = CollectionEventHelper
            .addCollectionEvent(
                site,
                ShippingMethodWrapper.getShippingMethods(appService).get(0),
                SourceVesselHelper.newSourceVessel(patient1,
                    Utils.getRandomDate(), 0.1));
        String oldWaybill = cevent1.getWaybill();
        cevent1.setWaybill("QQQQ");
        cevent1.reset();
        Assert.assertEquals(oldWaybill, cevent1.getWaybill());
    }

    @Test
    public void testResetNew() throws Exception {
        CollectionEventWrapper cevent = new CollectionEventWrapper(appService);
        cevent.setWaybill("titi");
        cevent.reset();
        Assert.assertEquals(null, cevent.getWaybill());
    }

    @Test
    public void testGetTodaycevents() throws Exception {
        String name = "testTodaycevents_" + r.nextInt();
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
        CollectionEventHelper.addCollectionEvent(site, method,
            SourceVesselHelper.newSourceVessel(patient1, Utils.getRandomDate(),
                0.1)); // another
        // day
        CollectionEventWrapper cevent2 = CollectionEventHelper
            .newCollectionEvent(
                site,
                method,
                "waybill_" + name + "_2",
                new Date(),
                SourceVesselHelper.newSourceVessel(patient1,
                    Utils.getRandomDate(), 0.1)); // today
        cevent2.persist();
        CollectionEventWrapper cevent3 = CollectionEventHelper
            .newCollectionEvent(
                site,
                method,
                "waybill_" + name + "_3",
                new Date(),
                SourceVesselHelper.newSourceVessel(patient2,
                    Utils.getRandomDate(), 0.1)); // today
        cevent3.persist();

        List<CollectionEventWrapper> ships = CollectionEventWrapper
            .getTodayCollectionEvents(appService);
        Assert.assertEquals(2, ships.size());
        Assert.assertTrue(ships.contains(cevent2));
        Assert.assertTrue(ships.contains(cevent3));
    }

    @Test
    public void testIsReceivedToday() throws Exception {
        String name = "testTodaycevents_" + r.nextInt();
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
        CollectionEventWrapper cevent1 = CollectionEventHelper
            .addCollectionEvent(
                site,
                method,
                SourceVesselHelper.newSourceVessel(patient1,
                    Utils.getRandomDate(), 0.1)); // another day
        CollectionEventWrapper cevent2 = CollectionEventHelper
            .newCollectionEvent(
                site,

                method,
                "waybill_" + name + "_2",
                new Date(),
                SourceVesselHelper.newSourceVessel(patient1,
                    Utils.getRandomDate(), 0.1)); // today
        cevent2.persist();
        CollectionEventWrapper cevent3 = CollectionEventHelper
            .newCollectionEvent(
                site,
                method,
                "waybill_" + name + "_3",
                new Date(),
                SourceVesselHelper.newSourceVessel(patient2,
                    Utils.getRandomDate(), 0.1)); // today
        cevent3.persist();

        Assert.assertFalse(cevent1.isReceivedToday());
        Assert.assertTrue(cevent2.isReceivedToday());
        Assert.assertTrue(cevent3.isReceivedToday());
    }
}
