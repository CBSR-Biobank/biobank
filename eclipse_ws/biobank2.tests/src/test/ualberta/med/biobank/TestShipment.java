package test.ualberta.med.biobank;

import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import test.ualberta.med.biobank.internal.ClinicHelper;
import test.ualberta.med.biobank.internal.ContactHelper;
import test.ualberta.med.biobank.internal.PatientHelper;
import test.ualberta.med.biobank.internal.PatientVisitHelper;
import test.ualberta.med.biobank.internal.ShipmentHelper;
import test.ualberta.med.biobank.internal.ShippingCompanyHelper;
import test.ualberta.med.biobank.internal.SiteHelper;
import test.ualberta.med.biobank.internal.StudyHelper;
import edu.ualberta.med.biobank.common.BiobankCheckException;
import edu.ualberta.med.biobank.common.wrappers.ClinicWrapper;
import edu.ualberta.med.biobank.common.wrappers.ContactWrapper;
import edu.ualberta.med.biobank.common.wrappers.PatientVisitWrapper;
import edu.ualberta.med.biobank.common.wrappers.PatientWrapper;
import edu.ualberta.med.biobank.common.wrappers.ShipmentWrapper;
import edu.ualberta.med.biobank.common.wrappers.ShippingCompanyWrapper;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;
import edu.ualberta.med.biobank.common.wrappers.StudyWrapper;

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
        study.setContactCollection(Arrays
            .asList(new ContactWrapper[] { contact }));
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
        int nbClinic2Study1 = PatientVisitHelper.addPatientVisits(patient1,
            shipment2).size();

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
        int nbClinic2Study2 = PatientVisitHelper.addPatientVisits(patient2,
            shipment4).size();

        shipment1.reload();
        Assert.assertEquals(nbClinic1Study1 + nbClinic1Study2, shipment1
            .getPatientVisitCollection().size());
        clinic2.reload();
        Assert.assertEquals(nbClinic2Study1 + nbClinic2Study2, shipment3
            .getPatientVisitCollection().size());
    }

    @Test
    public void testAddInPatientVisitCollection() throws Exception {
        String name = "testAddInPatientVisitCollection" + r.nextInt();
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
        String name = "testGetPattestGetPatientCollectionientVisitCollection"
            + r.nextInt();
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
        shipment.setPatientCollection(Arrays.asList(new PatientWrapper[] {
            patient1, patient2, patient3 }));
        shipment.persist();

        shipment.reload();
        Assert.assertEquals(3, shipment.getPatientCollection().size());
    }

    @Test
    public void testAddInPatientCollection() throws Exception {
        String name = "testAddInPatientCollection" + r.nextInt();
        SiteWrapper site = SiteHelper.addSite(name);
        ClinicWrapper clinic = ClinicHelper.addClinic(site, name);
        StudyWrapper study = StudyHelper.addStudy(clinic.getSite(), name);
        ContactWrapper contact = ContactHelper.addContact(clinic, name);
        study.setContactCollection(Arrays
            .asList(new ContactWrapper[] { contact }));
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
    public void testGetShipmentInSite() {
        fail("Not yet implemented");
    }

    @Test
    public void testPersist() throws Exception {
        String name = "testPersist" + r.nextInt();
        SiteWrapper site = SiteHelper.addSite(name);
        ClinicWrapper clinic = ClinicHelper.addClinic(site, name);
        StudyWrapper study = StudyHelper.addStudy(clinic.getSite(), name);
        ContactWrapper contact = ContactHelper.addContact(clinic, name);
        study.setContactCollection(Arrays
            .asList(new ContactWrapper[] { contact }));
        study.persist();
        PatientWrapper patient = PatientHelper.addPatient(name, study);
        ShipmentWrapper shipment = ShipmentHelper.newShipment(clinic, name,
            Utils.getRandomDate(), patient);

        shipment.persist();
    }

    @Test
    public void testPersistFail() throws Exception {
        String name = "testPersistFail" + r.nextInt();
        SiteWrapper site = SiteHelper.addSite(name);
        ClinicWrapper clinic = ClinicHelper.addClinic(site, name);
        StudyWrapper study = StudyHelper.addStudy(clinic.getSite(), name);
        ContactWrapper contact = ContactHelper.addContact(clinic, name);
        study.setContactCollection(Arrays
            .asList(new ContactWrapper[] { contact }));
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

        ShipmentWrapper shipment3 = ShipmentHelper.newShipment(clinic);
        try {
            shipment3.persist();
            Assert
                .fail("shipment don't have any patient. An exception should be thrown.");
        } catch (BiobankCheckException bce) {
            Assert.assertTrue(true);
        }

        // TODO test also clinic/study/patient not ok
    }

    public void testDelete() {
        fail("Not yet implemented");
    }

    public void testDeleteFail() {
        fail("Not yet implemented : should check the deleteChecks");
    }
}
