package test.ualberta.med.biobank;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import test.ualberta.med.biobank.internal.ClinicHelper;
import test.ualberta.med.biobank.internal.ContactHelper;
import test.ualberta.med.biobank.internal.ContainerHelper;
import test.ualberta.med.biobank.internal.ContainerTypeHelper;
import test.ualberta.med.biobank.internal.PatientHelper;
import test.ualberta.med.biobank.internal.PatientVisitHelper;
import test.ualberta.med.biobank.internal.SampleHelper;
import test.ualberta.med.biobank.internal.ShipmentHelper;
import test.ualberta.med.biobank.internal.SiteHelper;
import test.ualberta.med.biobank.internal.StudyHelper;
import edu.ualberta.med.biobank.common.BiobankCheckException;
import edu.ualberta.med.biobank.common.wrappers.ClinicWrapper;
import edu.ualberta.med.biobank.common.wrappers.ContactWrapper;
import edu.ualberta.med.biobank.common.wrappers.ContainerTypeWrapper;
import edu.ualberta.med.biobank.common.wrappers.ContainerWrapper;
import edu.ualberta.med.biobank.common.wrappers.PatientVisitWrapper;
import edu.ualberta.med.biobank.common.wrappers.PatientWrapper;
import edu.ualberta.med.biobank.common.wrappers.SampleTypeWrapper;
import edu.ualberta.med.biobank.common.wrappers.SampleWrapper;
import edu.ualberta.med.biobank.common.wrappers.ShipmentWrapper;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;
import edu.ualberta.med.biobank.common.wrappers.StudyWrapper;
import edu.ualberta.med.biobank.model.Patient;

public class TestPatient extends TestDatabase {

    private Map<String, ContainerWrapper> containerMap;

    private Map<String, ContainerTypeWrapper> containerTypeMap;

    private SiteWrapper site;

    private StudyWrapper study;

    private ClinicWrapper clinic;

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
        site = SiteHelper.addSite("Site - Patient Test "
            + Utils.getRandomString(10));
        study = StudyHelper.addStudy(site, "Study - Patient Test "
            + Utils.getRandomString(10));
        containerMap = new HashMap<String, ContainerWrapper>();
        containerTypeMap = new HashMap<String, ContainerTypeWrapper>();
    }

    private void addClinic(PatientWrapper patient) throws Exception {
        clinic = ClinicHelper.addClinic(site, "Clinic - Patient Test "
            + Utils.getRandomString(10));
        ContactWrapper contact = ContactHelper.addContact(clinic,
            "Contact - Patient Test");
        study.setContactCollection(Arrays.asList(contact));
        study.persist();
    }

    private void addContainerTypes() throws Exception {
        // first add container types
        ContainerTypeWrapper topType, childType;

        List<SampleTypeWrapper> allSampleTypes = SampleTypeWrapper
            .getGlobalSampleTypes(appService, true);

        childType = ContainerTypeHelper.newContainerType(site,
            "Child L1 Container Type", "CCTL1", 3, 4, 5, false);
        childType.setSampleTypeCollection(allSampleTypes);
        childType.persist();
        containerTypeMap.put("ChildCtL1", childType);

        topType = ContainerTypeHelper.newContainerType(site,
            "Top Container Type", "TCT", 2, 3, 10, true);
        topType.setChildContainerTypeCollection(Arrays.asList(containerTypeMap
            .get("ChildCtL1")));
        topType.persist();
        containerTypeMap.put("TopCT", topType);

    }

    private void addContainers() throws Exception {
        ContainerWrapper top = ContainerHelper.addContainer("01", TestCommon
            .getNewBarcode(r), null, site, containerTypeMap.get("TopCT"));
        containerMap.put("Top", top);

        ContainerWrapper childL1 = ContainerHelper.addContainer(null,
            TestCommon.getNewBarcode(r), top, site, containerTypeMap
                .get("ChildCtL1"), 0, 0);
        containerMap.put("ChildL1", childL1);
    }

    @Test
    public void testGettersAndSetters() throws Exception {
        PatientWrapper patient = PatientHelper.addPatient(Utils
            .getRandomNumericString(20), study);
        testGettersAndSetters(patient);
    }

    @Test
    public void testCompareTo() throws Exception {
        // create patient1 and patient2 with patient 2 being the second when
        // sorted
        String pnumber = "12345";
        PatientWrapper patient1 = PatientHelper.addPatient(pnumber, study);
        pnumber = "12346";
        PatientWrapper patient2 = PatientHelper.addPatient(pnumber, study);

        Assert.assertEquals(-1, patient1.compareTo(patient2));

        // now set patient2's number to be first when sorted
        patient2.setNumber("12344");
        patient2.persist();

        Assert.assertEquals(1, patient1.compareTo(patient2));

        // compare patient1 to itself
        Assert.assertEquals(0, patient1.compareTo(patient1));
    }

    @Test
    public void testReset() throws Exception {
        PatientWrapper patient = PatientHelper.addPatient(Utils
            .getRandomNumericString(20), study);
        patient.reset();
    }

    @Test
    public void testReload() throws Exception {
        PatientWrapper patient = PatientHelper.addPatient(Utils
            .getRandomNumericString(20), study);
        patient.reload();
    }

    @Test
    public void testGetWrappedClass() throws Exception {
        PatientWrapper patient = PatientHelper.addPatient(Utils
            .getRandomNumericString(20), study);
        Assert.assertEquals(Patient.class, patient.getWrappedClass());
    }

    @Test
    public void testDelete() throws Exception {
        PatientWrapper patient = PatientHelper.addPatient(Utils
            .getRandomNumericString(20), study);
        patient.delete();
        study.reload();

        // create new patient with patient visits, should not be allowed to
        // delete
        patient = PatientHelper.addPatient(Utils.getRandomNumericString(20),
            study);
        addContainerTypes();
        addContainers();
        addClinic(patient);
        patient.persist();
        ShipmentWrapper shipment = ShipmentHelper.newShipment(clinic);
        shipment.setPatientCollection(Arrays.asList(patient));
        shipment.persist();
        patient.reload();

        shipment = patient.getShipmentCollection().get(0);
        Assert.assertNotNull(shipment);

        int count = r.nextInt(15) + 1;
        List<PatientVisitWrapper> visits = new ArrayList<PatientVisitWrapper>();
        for (int i = 0; i < count; i++) {
            visits.add(PatientVisitHelper.newPatientVisit(patient, shipment,
                Utils.getRandomDate()));
        }
        patient.setPatientVisitCollection(visits);
        patient.persist();
        patient.reload();

        visits = patient.getPatientVisitCollection();
        List<SampleTypeWrapper> allSampleTypes = SampleTypeWrapper
            .getGlobalSampleTypes(appService, true);
        SampleWrapper sample = SampleHelper.addSample(allSampleTypes.get(0),
            containerMap.get("ChildL1"), visits.get(0), 0, 0);
        patient.reload();

        try {
            patient.delete();
            Assert
                .fail("should not be allowed to delete patient with visits and samples");
        } catch (Exception e) {
            Assert.assertTrue(true);
        }

        // // delete sample and patient
        sample.delete();
        patient.delete();
    }

    @Test
    public void testGetStudy() throws Exception {
        PatientWrapper patient = PatientHelper.addPatient(Utils
            .getRandomNumericString(20), study);
        Assert.assertEquals(study, patient.getStudy());
    }

    @Test
    public void testCheckPatientNumberUnique() throws Exception {
        String pnumber = "12345";
        PatientHelper.addPatient(pnumber, study);
        PatientWrapper patient2 = PatientHelper.newPatient(pnumber, study);

        try {
            patient2.persist();
            Assert
                .fail("should not be allowed to add patient because of duplicate name");
        } catch (BiobankCheckException e) {
            Assert.assertTrue(true);
        }
    }

    @Test
    public void testGetPatientVisitCollection() throws Exception {
        PatientWrapper patient = PatientHelper.addPatient(Utils
            .getRandomNumericString(20), study);
        List<PatientVisitWrapper> list = patient.getPatientVisitCollection();
        Assert.assertEquals(null, list);

        ClinicWrapper clinic = ClinicHelper.addClinic(site,
            "Clinic - Patient Test " + Utils.getRandomString(10));
        ContactWrapper contact = ContactHelper.addContact(clinic,
            "Contact - Patient Test");
        study.setContactCollection(Arrays.asList(contact));
        study.persist();

        ShipmentWrapper shipment = ShipmentHelper.addShipment(clinic, patient);

        List<PatientVisitWrapper> visitsAdded = PatientVisitHelper
            .addPatientVisits(patient, shipment, 3);

        patient.reload();
        List<PatientVisitWrapper> visits = patient.getPatientVisitCollection();
        Assert.assertTrue(visits.containsAll(visitsAdded));

        // delete some random visits, ensure at least one left
        int numToDelete = r.nextInt(visitsAdded.size() - 1);
        for (int i = 0; i < numToDelete; ++i) {
            PatientVisitWrapper v = visitsAdded.get(r.nextInt(visitsAdded
                .size()));
            visitsAdded.remove(v);
            v.delete();
        }

        // make sure patient now only has the visits that were not deleted
        patient.reload();
        visits = patient.getPatientVisitCollection();
        Assert.assertTrue(visits.containsAll(visitsAdded));

        // now remove all patient visits
        while (visitsAdded.size() > 0) {
            PatientVisitWrapper v = visitsAdded.get(0);
            v.delete();
            visitsAdded.remove(0);
        }

        // make sure patient does not have any patient visits
        patient.reload();
        visits = patient.getPatientVisitCollection();
        Assert.assertEquals(0, visits.size());
    }

    @Test
    public void testGetPatientInSite() throws Exception {
        String pnumber = Utils.getRandomNumericString(20);
        PatientWrapper patient = PatientHelper.addPatient(pnumber, study);

        PatientWrapper patient2 = PatientWrapper.getPatientInSite(appService,
            pnumber, site);
        Assert.assertEquals(patient, patient2);
    }

    @Test
    public void testGetPatientShipmentCollection() throws Exception {
        PatientWrapper patient = PatientHelper.addPatient(Utils
            .getRandomNumericString(20), study);
        addClinic(patient);

        List<ShipmentWrapper> shipments = new ArrayList<ShipmentWrapper>();
        for (int i = 0, n = r.nextInt(10); i < n; ++i) {
            ShipmentWrapper ship = ShipmentHelper.newShipment(clinic);
            ship.setPatientCollection(Arrays.asList(patient));
            ship.persist();
            shipments.add(ship);
        }
        patient.reload();

        List<ShipmentWrapper> savedShipments = patient.getShipmentCollection();
        Assert.assertEquals(shipments.size(), savedShipments.size());
        for (ShipmentWrapper shipment : savedShipments) {
            Assert.assertTrue(shipments.contains(shipment));
        }
    }
}
