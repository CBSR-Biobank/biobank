package edu.ualberta.med.biobank.test.wrappers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import edu.ualberta.med.biobank.common.exception.BiobankCheckException;
import edu.ualberta.med.biobank.common.wrappers.AliquotWrapper;
import edu.ualberta.med.biobank.common.wrappers.ClinicWrapper;
import edu.ualberta.med.biobank.common.wrappers.ContactWrapper;
import edu.ualberta.med.biobank.common.wrappers.ContainerTypeWrapper;
import edu.ualberta.med.biobank.common.wrappers.ContainerWrapper;
import edu.ualberta.med.biobank.common.wrappers.PatientVisitWrapper;
import edu.ualberta.med.biobank.common.wrappers.PatientWrapper;
import edu.ualberta.med.biobank.common.wrappers.SampleTypeWrapper;
import edu.ualberta.med.biobank.common.wrappers.ShipmentWrapper;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;
import edu.ualberta.med.biobank.common.wrappers.StudyWrapper;
import edu.ualberta.med.biobank.model.Patient;
import edu.ualberta.med.biobank.test.TestDatabase;
import edu.ualberta.med.biobank.test.Utils;
import edu.ualberta.med.biobank.test.internal.AliquotHelper;
import edu.ualberta.med.biobank.test.internal.ClinicHelper;
import edu.ualberta.med.biobank.test.internal.ContactHelper;
import edu.ualberta.med.biobank.test.internal.ContainerHelper;
import edu.ualberta.med.biobank.test.internal.ContainerTypeHelper;
import edu.ualberta.med.biobank.test.internal.PatientHelper;
import edu.ualberta.med.biobank.test.internal.PatientVisitHelper;
import edu.ualberta.med.biobank.test.internal.ShipmentHelper;
import edu.ualberta.med.biobank.test.internal.SiteHelper;
import edu.ualberta.med.biobank.test.internal.StudyHelper;

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
        study = StudyHelper.addStudy("Study - Patient Test "
            + Utils.getRandomString(10));
        containerMap = new HashMap<String, ContainerWrapper>();
        containerTypeMap = new HashMap<String, ContainerTypeWrapper>();
    }

    private void addClinic(PatientWrapper patient) throws Exception {
        clinic = ClinicHelper.addClinic(site,
            "Clinic - Patient Test " + Utils.getRandomString(10));
        ContactWrapper contact = ContactHelper.addContact(clinic,
            "Contact - Patient Test");
        study.addContacts(Arrays.asList(contact));
        study.persist();
    }

    private void addContainerTypes() throws Exception {
        // first add container types
        ContainerTypeWrapper topType, childType;

        List<SampleTypeWrapper> allSampleTypes = SampleTypeWrapper
            .getGlobalSampleTypes(appService, true);

        childType = ContainerTypeHelper.newContainerType(site,
            "Child L1 Container Type", "CCTL1", 3, 4, 5, false);
        childType.addSampleTypes(allSampleTypes);
        childType.persist();
        containerTypeMap.put("ChildCtL1", childType);

        topType = ContainerTypeHelper.newContainerType(site,
            "Top Container Type", "TCT", 2, 3, 10, true);
        topType.addChildContainerTypes(Arrays.asList(containerTypeMap
            .get("ChildCtL1")));
        topType.persist();
        containerTypeMap.put("TopCT", topType);

    }

    private void addContainers() throws Exception {
        ContainerWrapper top = ContainerHelper.addContainer("01",
            TestCommon.getNewBarcode(r), null, site,
            containerTypeMap.get("TopCT"));
        containerMap.put("Top", top);

        ContainerWrapper childL1 = ContainerHelper.addContainer(null,
            TestCommon.getNewBarcode(r), top, site,
            containerTypeMap.get("ChildCtL1"), 0, 0);
        containerMap.put("ChildL1", childL1);
    }

    @Test
    public void testGettersAndSetters() throws Exception {
        PatientWrapper patient = PatientHelper.addPatient(
            Utils.getRandomNumericString(20), study);
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
        patient2.setPnumber("12344");
        patient2.persist();

        Assert.assertEquals(1, patient1.compareTo(patient2));

        // compare patient1 to itself
        Assert.assertEquals(0, patient1.compareTo(patient1));
    }

    @Test
    public void testReset() throws Exception {
        PatientWrapper patient = PatientHelper.addPatient(
            Utils.getRandomNumericString(20), study);
        patient.reset();
    }

    @Test
    public void testReload() throws Exception {
        PatientWrapper patient = PatientHelper.addPatient(
            Utils.getRandomNumericString(20), study);
        patient.reload();
    }

    @Test
    public void testGetWrappedClass() throws Exception {
        PatientWrapper patient = PatientHelper.addPatient(
            Utils.getRandomNumericString(20), study);
        Assert.assertEquals(Patient.class, patient.getWrappedClass());
    }

    @Test
    public void testDelete() throws Exception {
        PatientWrapper patient = PatientHelper.addPatient(
            Utils.getRandomNumericString(20), study);
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
        shipment.addPatients(Arrays.asList(patient));
        shipment.persist();
        patient.reload();

        shipment = patient.getShipmentCollection().get(0);
        Assert.assertNotNull(shipment);

        int count = r.nextInt(15) + 1;
        List<PatientVisitWrapper> visits = new ArrayList<PatientVisitWrapper>();
        for (int i = 0; i < count; i++) {
            visits.add(PatientVisitHelper.newPatientVisit(patient, shipment,
                Utils.getRandomDate(), Utils.getRandomDate()));
        }
        patient.addPatientVisits(visits);
        patient.persist();
        patient.reload();

        visits = patient.getPatientVisitCollection();
        List<SampleTypeWrapper> allSampleTypes = SampleTypeWrapper
            .getGlobalSampleTypes(appService, true);
        AliquotWrapper aliquot = AliquotHelper.addAliquot(
            allSampleTypes.get(0), containerMap.get("ChildL1"), visits.get(0),
            0, 0);
        patient.reload();

        try {
            patient.delete();
            Assert.fail("should not be allowed to delete patient with samples");
        } catch (Exception e) {
            Assert.assertTrue(true);
        }

        // delete aliquot and patient
        aliquot.delete();

        try {
            patient.delete();
            Assert.fail("should not be allowed to delete patient with visits");
        } catch (Exception e) {
            Assert.assertTrue(true);
        }
        for (PatientVisitWrapper visit : patient.getPatientVisitCollection()) {
            visit.delete();
        }

        try {
            patient.delete();
            Assert
                .fail("should not be allowed to delete patient linked to shipments");
        } catch (Exception e) {
            Assert.assertTrue(true);
        }
        shipment.delete();

        patient.delete();
    }

    @Test
    public void testGetStudy() throws Exception {
        PatientWrapper patient = new PatientWrapper(appService);
        Assert.assertNull(patient.getStudy());
        patient = PatientHelper.addPatient(Utils.getRandomNumericString(20),
            study);
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
        PatientWrapper patient = PatientHelper.addPatient(
            Utils.getRandomNumericString(20), study);
        List<PatientVisitWrapper> list = patient.getPatientVisitCollection();
        Assert.assertEquals(null, list);

        ClinicWrapper clinic = ClinicHelper.addClinic(site,
            "Clinic - Patient Test " + Utils.getRandomString(10));
        ContactWrapper contact = ContactHelper.addContact(clinic,
            "Contact - Patient Test");
        study.addContacts(Arrays.asList(contact));
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
    public void testAddPatientVisits() throws Exception {
        String name = "testAddPatientVisits" + r.nextInt();
        PatientWrapper patient = PatientHelper.addPatient(name, study);
        ClinicWrapper clinic = ClinicHelper.addClinic(site,
            name + Utils.getRandomString(10));
        ContactWrapper contact = ContactHelper.addContact(clinic, name);
        study.addContacts(Arrays.asList(contact));
        study.persist();
        ShipmentWrapper shipment = ShipmentHelper.addShipment(clinic, patient);

        PatientVisitWrapper visit = PatientVisitHelper.newPatientVisit(patient,
            shipment, Utils.getRandomDate(), Utils.getRandomDate());
        patient.addPatientVisits(Arrays.asList(visit));
        patient.persist();
        patient.reload();
        Assert.assertEquals(1, patient.getPatientVisitCollection().size());

        visit = PatientVisitHelper.newPatientVisit(patient, shipment,
            Utils.getRandomDate(), Utils.getRandomDate());
        patient.addPatientVisits(Arrays.asList(visit));
        patient.persist();
        patient.reload();
        Assert.assertEquals(2, patient.getPatientVisitCollection().size());
    }

    @Test
    public void testGetPatientInSite() throws Exception {
        String pnumber = Utils.getRandomNumericString(20);
        PatientWrapper patient = PatientHelper.addPatient(pnumber, study);

        PatientWrapper patient2 = PatientWrapper.getPatientInSite(appService,
            pnumber, site);
        Assert.assertEquals(patient, patient2);

        PatientWrapper patient3 = PatientWrapper.getPatientInSite(appService,
            Utils.getRandomNumericString(20), site);
        Assert.assertNull(patient3);
    }

    @Test
    public void testGetPatientShipmentCollection() throws Exception {
        PatientWrapper patient = PatientHelper.addPatient(
            Utils.getRandomNumericString(20), study);
        addClinic(patient);

        List<ShipmentWrapper> shipments = new ArrayList<ShipmentWrapper>();
        for (int i = 0, n = r.nextInt(10); i < n; ++i) {
            ShipmentWrapper ship = ShipmentHelper.newShipment(clinic);
            ship.addPatients(Arrays.asList(patient));
            ship.persist();
            shipments.add(ship);
        }
        patient.reload();

        List<ShipmentWrapper> savedShipments = patient
            .getShipmentCollection(true);
        Assert.assertEquals(shipments.size(), savedShipments.size());
        for (ShipmentWrapper shipment : savedShipments) {
            Assert.assertTrue(shipments.contains(shipment));
        }
    }

    @Test
    public void testGetVisits() throws Exception {
        String name = "testGetVisits" + r.nextInt();
        PatientWrapper patient1 = PatientHelper.addPatient(name + "_1", study);
        PatientWrapper patient2 = PatientHelper.addPatient(name + "_2", study);

        ClinicWrapper clinic = ClinicHelper.addClinic(site, name);
        ContactWrapper contact = ContactHelper.addContact(clinic, name);
        study.addContacts(Arrays.asList(contact));
        study.persist();

        ShipmentWrapper shipment = ShipmentHelper.addShipment(clinic, patient1,
            patient2);

        Date dateProcessed1 = Utils.getRandomDate();
        Date dateDrawn1 = Utils.getRandomDate();
        Date dateDrawn1_1 = Utils.getRandomDate();
        Date dateProcessed2 = Utils.getRandomDate();
        Date dateDrawn2 = Utils.getRandomDate();
        Date dateProcessed3 = Utils.getRandomDate();
        Date dateDrawn3 = Utils.getRandomDate();

        PatientVisitWrapper visit1 = PatientVisitHelper.addPatientVisit(
            patient1, shipment, dateProcessed1, dateDrawn1);
        PatientVisitWrapper visit1_1 = PatientVisitHelper.addPatientVisit(
            patient1, shipment, dateProcessed1, dateDrawn1_1);
        PatientVisitWrapper visit2 = PatientVisitHelper.addPatientVisit(
            patient1, shipment, dateProcessed2, dateDrawn2);
        PatientVisitWrapper visit3 = PatientVisitHelper.addPatientVisit(
            patient2, shipment, dateProcessed3, dateDrawn3);

        patient1.reload();
        patient2.reload();

        List<PatientVisitWrapper> visitsFound = patient1.getVisits(
            dateProcessed1, dateDrawn1);
        Assert.assertTrue(visitsFound.size() == 1);
        Assert.assertTrue(visitsFound.contains(visit1));

        visitsFound = patient1.getVisits(dateProcessed1, dateDrawn1_1);
        Assert.assertTrue(visitsFound.size() == 1);
        Assert.assertTrue(visitsFound.contains(visit1_1));

        visitsFound = patient1.getVisits(dateProcessed2, dateDrawn2);
        Assert.assertTrue(visitsFound.size() == 1);
        Assert.assertEquals(visit2, visitsFound.get(0));

        visitsFound = patient1.getVisits(dateProcessed3, dateDrawn3);
        Assert.assertEquals(0, visitsFound.size());

        visitsFound = patient2.getVisits(dateProcessed3, dateDrawn3);
        Assert.assertTrue(visitsFound.size() == 1);
        Assert.assertEquals(visit3, visitsFound.get(0));
    }

    @Test
    public void testGetSampleCount() throws Exception {
        String patientName = Utils.getRandomNumericString(20);
        PatientWrapper patient1 = PatientHelper.addPatient(patientName, study);
        PatientWrapper patient2 = PatientHelper.addPatient(patientName + "_2",
            study);
        patient1.delete();
        study.reload();

        // create new patient with patient visits
        patient1 = PatientHelper.addPatient(Utils.getRandomNumericString(20),
            study);
        addContainerTypes();
        addContainers();
        addClinic(patient1);
        patient1.persist();
        ShipmentWrapper shipment = ShipmentHelper.newShipment(clinic);
        shipment.addPatients(Arrays.asList(patient1));
        shipment.persist();
        patient1.reload();

        shipment = patient1.getShipmentCollection().get(0);
        Assert.assertNotNull(shipment);

        ContainerWrapper childL1 = containerMap.get("ChildL1");
        int maxCols = childL1.getColCapacity();
        int count = r.nextInt(5) + 1;
        for (PatientWrapper patient : Arrays.asList(patient1, patient2)) {
            List<PatientVisitWrapper> visits = new ArrayList<PatientVisitWrapper>();
            for (int i = 0; i < count; i++) {
                visits.add(PatientVisitHelper.newPatientVisit(patient,
                    shipment, Utils.getRandomDate(), Utils.getRandomDate()));
            }
            patient.addPatientVisits(visits);
            patient.persist();
            patient.reload();
        }

        List<SampleTypeWrapper> allSampleTypes = SampleTypeWrapper
            .getGlobalSampleTypes(appService, true);

        int sampleTypeCount = allSampleTypes.size();
        List<AliquotWrapper> samples = new ArrayList<AliquotWrapper>();
        Map<PatientWrapper, Integer> patientSampleCount = new HashMap<PatientWrapper, Integer>();
        for (PatientWrapper patient : Arrays.asList(patient1, patient2)) {
            patientSampleCount.put(patient, 0);
        }

        // 2 samples per visit
        int sampleCount = 0;
        for (PatientWrapper patient : Arrays.asList(patient1, patient2)) {
            for (PatientVisitWrapper visit : patient
                .getPatientVisitCollection()) {
                for (int i = 0; i < 2; ++i) {
                    samples.add(AliquotHelper.addAliquot(
                        allSampleTypes.get(r.nextInt(sampleTypeCount)),
                        childL1, visit, sampleCount / maxCols, sampleCount
                            % maxCols));
                    patient.reload();
                    patientSampleCount.put(patient,
                        patientSampleCount.get(patient) + 1);
                    ++sampleCount;
                    Assert.assertEquals(patientSampleCount.get(patient)
                        .intValue(), patient.getAliquotsCount());
                }
            }
        }

        // now delete samples
        for (PatientWrapper patient : Arrays.asList(patient1, patient2)) {
            for (PatientVisitWrapper visit : patient
                .getPatientVisitCollection()) {
                samples = visit.getAliquotCollection();
                while (samples.size() > 0) {
                    AliquotWrapper aliquot = samples.get(0);
                    aliquot.delete();
                    visit.reload();
                    patient.reload();
                    samples = visit.getAliquotCollection();
                    patientSampleCount.put(patient,
                        patientSampleCount.get(patient) - 1);
                    Assert.assertEquals(patientSampleCount.get(patient1)
                        .intValue(), patient1.getAliquotsCount());
                    Assert.assertEquals(patientSampleCount.get(patient2)
                        .intValue(), patient2.getAliquotsCount());
                }
            }
        }
    }

    @Test
    public void testGetPatientsInTodayShipments() throws Exception {
        String name = "testTodayShipments_" + r.nextInt();
        PatientWrapper patient1 = PatientHelper.addPatient(name + "_1", study);
        addClinic(patient1);
        ShipmentWrapper ship = ShipmentHelper.newShipment(clinic);
        ship.addPatients(Arrays.asList(patient1));
        ship.persist();

        PatientWrapper patient2 = PatientHelper.addPatient(name + "_2", study);
        addClinic(patient2);
        PatientWrapper patient3 = PatientHelper.addPatient(name + "_3", study);
        addClinic(patient3);
        ShipmentWrapper ship2 = ShipmentHelper.newShipment(clinic);
        ship2.setDateReceived(new Date()); // today
        ship2.addPatients(Arrays.asList(patient2, patient3));
        ship2.persist();

        List<PatientWrapper> todayPatients = PatientWrapper
            .getPatientsInTodayShipments(appService, site);
        Assert.assertEquals(2, todayPatients.size());
        Assert.assertTrue(todayPatients.contains(patient2));
        Assert.assertTrue(todayPatients.contains(patient3));
    }

    @Test
    public void testGetLastWeekPatientVisits() throws Exception {
        String name = "testGetLastWeekPatientVisits" + r.nextInt();
        PatientWrapper patient = PatientHelper.addPatient(name, study);

        ClinicWrapper clinic = ClinicHelper.addClinic(site, name);
        ContactWrapper contact = ContactHelper.addContact(clinic, name);
        study.addContacts(Arrays.asList(contact));
        study.persist();

        ShipmentWrapper shipment = ShipmentHelper.addShipment(clinic, patient);

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, -10); // 10 days ago
        PatientVisitHelper.addPatientVisit(patient, shipment,
            calendar.getTime(), Utils.getRandomDate());
        calendar.add(Calendar.DATE, 3); // 7 days ago
        PatientVisitWrapper visit2 = PatientVisitHelper.addPatientVisit(
            patient, shipment, calendar.getTime(), Utils.getRandomDate());
        calendar.add(Calendar.DATE, 5); // 2 days ago
        PatientVisitWrapper visit3 = PatientVisitHelper.addPatientVisit(
            patient, shipment, calendar.getTime(), Utils.getRandomDate());
        patient.reload();

        List<PatientVisitWrapper> visits = patient.getLast7DaysPatientVisits();
        Assert.assertEquals(2, visits.size());
        Assert.assertTrue(visits.contains(visit2));
        Assert.assertTrue(visits.contains(visit3));

    }
}
