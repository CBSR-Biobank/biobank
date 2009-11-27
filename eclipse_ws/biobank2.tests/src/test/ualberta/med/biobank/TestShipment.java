package test.ualberta.med.biobank;

import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import test.ualberta.med.biobank.internal.ClinicHelper;
import test.ualberta.med.biobank.internal.ContactHelper;
import test.ualberta.med.biobank.internal.PatientHelper;
import test.ualberta.med.biobank.internal.PatientVisitHelper;
import test.ualberta.med.biobank.internal.ShipmentHelper;
import test.ualberta.med.biobank.internal.ShptSampleSourceHelper;
import test.ualberta.med.biobank.internal.SiteHelper;
import test.ualberta.med.biobank.internal.StudyHelper;
import edu.ualberta.med.biobank.common.wrappers.ClinicWrapper;
import edu.ualberta.med.biobank.common.wrappers.ContactWrapper;
import edu.ualberta.med.biobank.common.wrappers.PatientWrapper;
import edu.ualberta.med.biobank.common.wrappers.ShipmentWrapper;
import edu.ualberta.med.biobank.common.wrappers.ShptSampleSourceWrapper;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;
import edu.ualberta.med.biobank.common.wrappers.StudyWrapper;

public class TestShipment extends TestDatabase {

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
    }

    @Test
    public void testGettersAndSetters() throws Exception {
        String name = "testGettersAndSetters" + r.nextInt();
        SiteWrapper site = SiteHelper.addSite(name);
        ClinicWrapper clinic = ClinicHelper.addClinic(site, name);

        ShipmentWrapper shipment = ShipmentHelper.addShipmentWithRandomObjects(
            clinic, name);
        testGettersAndSetters(shipment);
    }

    @Test
    public void testGetSetClinic() throws Exception {
        String name = "testGetSetClinic" + r.nextInt();
        SiteWrapper site = SiteHelper.addSite(name);
        ClinicWrapper clinic = ClinicHelper.addClinic(site, name);
        ShipmentWrapper shipment = ShipmentHelper.addShipmentWithRandomObjects(
            clinic, name);

        ClinicWrapper clinic2 = ClinicHelper.addClinic(site, name + "CLINIC2");

        shipment.setClinic(clinic2);
        shipment.persist();

        shipment.reload();

        Assert.assertFalse(clinic.equals(shipment.getClinic()));

        Assert.assertEquals(clinic2, shipment.getClinic());
    }

    @Test
    public void testGetShptSampleSourceCollection() throws Exception {
        String name = "testGetShptSampleSourceCollection" + r.nextInt();
        SiteWrapper site = SiteHelper.addSite(name);
        ClinicWrapper clinic = ClinicHelper.addClinic(site, name);
        ShipmentWrapper shipment = ShipmentHelper.addShipmentWithRandomObjects(
            clinic, name);
        int nber = r.nextInt(5) + 1;
        ShptSampleSourceHelper.addShptSampleSources(name, shipment, nber);

        shipment.reload();
        List<ShptSampleSourceWrapper> sssList = shipment
            .getShptSampleSourceCollection();
        int sizeFound = sssList.size();

        Assert.assertEquals(nber + 1, sizeFound);
    }

    @Test
    public void testGetShptSampleSourceCollectionBoolean() throws Exception {
        String name = "testGetShptSampleSourceCollectionBoolean" + r.nextInt();
        SiteWrapper site = SiteHelper.addSite(name);
        ClinicWrapper clinic = ClinicHelper.addClinic(site, name);
        ShipmentWrapper shipment = ShipmentHelper.addShipmentWithRandomObjects(
            clinic, name);

        ShptSampleSourceHelper.addShptSampleSource("c" + name, shipment);
        ShptSampleSourceHelper.addShptSampleSource("a" + name, shipment);
        ShptSampleSourceHelper.addShptSampleSource("d" + name, shipment);
        shipment.reload();

        List<ShptSampleSourceWrapper> sssList = shipment
            .getShptSampleSourceCollection(true);
        if (sssList.size() > 1) {
            for (int i = 0; i < sssList.size() - 1; i++) {
                ShptSampleSourceWrapper sss1 = sssList.get(i);
                ShptSampleSourceWrapper sss2 = sssList.get(i + 1);
                Assert.assertTrue(sss1.compareTo(sss2) <= 0);
            }
        }
    }

    @Test
    public void testAddShptSampleSourceCollection() throws Exception {
        String name = "testAddShptSampleSourceCollection" + r.nextInt();
        SiteWrapper site = SiteHelper.addSite(name);
        ClinicWrapper clinic = ClinicHelper.addClinic(site, name);
        ShipmentWrapper shipment = ShipmentHelper.addShipmentWithRandomObjects(
            clinic, name);
        int nber = r.nextInt(5) + 1;
        ShptSampleSourceHelper.addShptSampleSources(name, shipment, nber);

        shipment.reload();
        List<ShptSampleSourceWrapper> sssList = shipment
            .getShptSampleSourceCollection();
        ShptSampleSourceWrapper sss = ShptSampleSourceHelper
            .newShptSampleSource(name + "NEW", shipment);
        sssList.add(sss);
        shipment.setShptSampleSourceCollection(sssList);
        shipment.persist();

        shipment.reload();
        // one shptSS added (plus the one added
        Assert.assertEquals(nber + 2, shipment.getShptSampleSourceCollection()
            .size());
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
        ShipmentWrapper shipment1 = ShipmentHelper.addShipmentWithShptSampleSource(clinic1,
            patient1);
        ShipmentWrapper shipment2 = ShipmentHelper.addShipmentWithShptSampleSource(clinic2,
            patient1);
        int nbClinic1Study1 = PatientVisitHelper.addPatientVisits(patient1,
            shipment1).size();
        int nbClinic2Study1 = PatientVisitHelper.addPatientVisits(patient1,
            shipment2).size();

        StudyWrapper study2 = StudyHelper.addStudy(site, name + "STUDY2");
        study2.setContactCollection(contacts);
        study2.persist();
        PatientWrapper patient2 = PatientHelper.addPatient(name, study2);
        ShipmentWrapper shipment3 = ShipmentHelper.addShipmentWithShptSampleSource(clinic1,
            patient2);
        ShipmentWrapper shipment4 = ShipmentHelper.addShipmentWithShptSampleSource(clinic2,
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
    public void testSetPatientVisitCollectionCollectionOfPatientVisitBoolean() {
        fail("Not yet implemented");
    }

    @Test
    public void testSetPatientVisitCollectionCollectionOfPatientVisitWrapper() {
        fail("Not yet implemented");
    }

    @Test
    public void testGetShipmentInSite() {
        fail("Not yet implemented");
    }

    @Test
    public void testPersist() {
        fail("Not yet implemented");
    }

    @Test
    public void testPersistFail() {
        fail("Not yet implemented : should check the persistChecks");
    }

    public void testDelete() {
        fail("Not yet implemented");
    }

    public void testDeleteFail() {
        fail("Not yet implemented : should check the deleteChecks");
    }
}
