package edu.ualberta.med.biobank.test.wrappers;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import edu.ualberta.med.biobank.common.exception.BiobankCheckException;
import edu.ualberta.med.biobank.common.exception.DuplicateEntryException;
import edu.ualberta.med.biobank.common.wrappers.ClinicWrapper;
import edu.ualberta.med.biobank.common.wrappers.CollectionEventWrapper;
import edu.ualberta.med.biobank.common.wrappers.ContactWrapper;
import edu.ualberta.med.biobank.common.wrappers.ContainerTypeWrapper;
import edu.ualberta.med.biobank.common.wrappers.ContainerWrapper;
import edu.ualberta.med.biobank.common.wrappers.PatientWrapper;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;
import edu.ualberta.med.biobank.common.wrappers.SpecimenTypeWrapper;
import edu.ualberta.med.biobank.common.wrappers.StudyWrapper;
import edu.ualberta.med.biobank.model.Patient;
import edu.ualberta.med.biobank.test.TestDatabase;
import edu.ualberta.med.biobank.test.Utils;
import edu.ualberta.med.biobank.test.internal.ClinicHelper;
import edu.ualberta.med.biobank.test.internal.CollectionEventHelper;
import edu.ualberta.med.biobank.test.internal.ContactHelper;
import edu.ualberta.med.biobank.test.internal.ContainerHelper;
import edu.ualberta.med.biobank.test.internal.ContainerTypeHelper;
import edu.ualberta.med.biobank.test.internal.PatientHelper;
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
        clinic = ClinicHelper.addClinic("Clinic - Patient Test "
            + Utils.getRandomString(10));
        ContactWrapper contact = ContactHelper.addContact(clinic,
            "Contact - Patient Test");
        study.addToContactCollection(Arrays.asList(contact));
        study.persist();
    }

    private void addContainerTypes() throws Exception {
        // first add container types
        ContainerTypeWrapper topType, childType;

        List<SpecimenTypeWrapper> allSampleTypes = SpecimenTypeWrapper
            .getAllSpecimenTypes(appService, true);

        childType = ContainerTypeHelper.newContainerType(site,
            "Child L1 Container Type", "CCTL1", 3, 4, 5, false);
        childType.addToSpecimenTypeCollection(allSampleTypes);
        childType.persist();
        containerTypeMap.put("ChildCtL1", childType);

        topType = ContainerTypeHelper.newContainerType(site,
            "Top Container Type", "TCT", 2, 3, 10, true);
        topType.addToChildContainerTypeCollection(Arrays
            .asList(containerTypeMap.get("ChildCtL1")));
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

        // create new patient with patient pevents, should not be allowed to
        // delete
        patient = PatientHelper.addPatient(Utils.getRandomNumericString(20),
            study);
        addContainerTypes();
        addContainers();
        addClinic(patient);
        patient.persist();
        // FIXME
        // SourceVesselWrapper sv = SourceVesselHelper.newSourceVessel(patient,
        // Utils.getRandomDate(), 0.1);
        // CollectionEventWrapper cevent = CollectionEventHelper
        // .newCollectionEvent(site,
        // ShippingMethodWrapper.getShippingMethods(appService).get(0));
        // cevent.addToSourceVesselCollection(Arrays.asList(sv));
        // sv.setCollectionEvent(cevent);
        // cevent.persist();
        // patient.reload();
        //
        // cevent = patient.getCollectionEventCollection().get(0);
        // Assert.assertNotNull(cevent);
        //
        // int count = r.nextInt(15) + 1;
        // List<ProcessingEventWrapper> pevent = new
        // ArrayList<ProcessingEventWrapper>();
        // for (int i = 0; i < count; i++) {
        // ProcessingEventWrapper pe = ProcessingEventHelper
        // .newProcessingEvent(site, patient, Utils.getRandomDate(),
        // Utils.getRandomDate());
        // pe.setPatient(patient);
        // pe.persist();
        // pe.reload();
        // pevent.add(pe);
        // }
        // patient.addToProcessingEventCollection(pevent);
        // patient.persist();
        // patient.reload();
        //
        // pevent = patient.getProcessingEventCollection(false);
        // List<SpecimenTypeWrapper> allSampleTypes = SpecimenTypeWrapper
        // .getAllSpecimenTypes(appService, true);
        // SpecimenWrapper aliquot = SpecimenHelper.addAliquot(
        // allSampleTypes.get(0), containerMap.get("ChildL1"), pevent.get(0),
        // 0, 0);
        // patient.reload();
        //
        // try {
        // patient.delete();
        // Assert.fail("should not be allowed to delete patient with samples");
        // } catch (Exception e) {
        // Assert.assertTrue(true);
        // }
        //
        // // delete aliquot and patient
        // aliquot.delete();
        //
        // try {
        // patient.delete();
        // Assert
        // .fail("should not be allowed to delete patient with processing events");
        // } catch (Exception e) {
        // Assert.assertTrue(true);
        // }
        // for (ProcessingEventWrapper pe : patient
        // .getProcessingEventCollection(false)) {
        // pe.delete();
        // }
        //
        // try {
        // patient.delete();
        // Assert
        // .fail("should not be allowed to delete patient linked to source vessels");
        // } catch (Exception e) {
        // Assert.assertTrue(true);
        // }
        //
        // DbHelper.deleteFromList(cevent.getSourceVesselCollection(false));
        // cevent.delete();
        // patient.delete();
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
        } catch (DuplicateEntryException e) {
            Assert.assertTrue(true);
        }
    }

    @Test
    public void testGetProcessingEventCollection() throws Exception {
        // FIXME
        // PatientWrapper patient = PatientHelper.addPatient(
        // Utils.getRandomNumericString(20), study);
        // List<ProcessingEventWrapper> list = patient
        // .getProcessingEventCollection(false);
        // Assert.assertTrue(list.isEmpty());
        //
        // List<ProcessingEventWrapper> visitsAdded = ProcessingEventHelper
        // .addProcessingEvents(site, patient, 3, false);
        //
        // patient.reload();
        // List<ProcessingEventWrapper> pevents = patient
        // .getProcessingEventCollection(false);
        // Assert.assertTrue(pevents.containsAll(visitsAdded));
        //
        // // delete some random pevents, ensure at least one left
        // int numToDelete = r.nextInt(visitsAdded.size() - 1);
        // for (int i = 0; i < numToDelete; ++i) {
        // ProcessingEventWrapper v = visitsAdded.get(r.nextInt(visitsAdded
        // .size()));
        // visitsAdded.remove(v);
        // v.delete();
        // }
        //
        // // make sure patient now only has the pevents that were not deleted
        // patient.reload();
        // pevents = patient.getProcessingEventCollection(false);
        // Assert.assertTrue(pevents.containsAll(visitsAdded));
        //
        // // now remove all patient pevents
        // while (visitsAdded.size() > 0) {
        // ProcessingEventWrapper v = visitsAdded.get(0);
        // v.delete();
        // visitsAdded.remove(0);
        // }
        //
        // // make sure patient does not have any patient pevents
        // patient.reload();
        // pevents = patient.getProcessingEventCollection(false);
        // Assert.assertEquals(0, pevents.size());
    }

    @Test
    public void testAddProcessingEvents() throws Exception {
        String name = "testAddProcessingEvents" + r.nextInt();
        PatientWrapper patient = PatientHelper.addPatient(name, study);
        patient.reload();

        // FIXME
        // ProcessingEventWrapper pevent = ProcessingEventHelper
        // .addProcessingEvent(site, patient, Utils.getRandomDate(),
        // Utils.getRandomDate());
        // patient.addToProcessingEventCollection(Arrays.asList(pevent));
        // patient.persist();
        // patient.reload();
        // Assert.assertEquals(1, patient.getProcessingEventCollection(false)
        // .size());
        //
        // pevent = ProcessingEventHelper.addProcessingEvent(site, patient,
        // Utils.getRandomDate(), Utils.getRandomDate());
        // patient.addToProcessingEventCollection(Arrays.asList(pevent));
        // patient.persist();
        // patient.reload();
        // Assert.assertEquals(2, patient.getProcessingEventCollection(false)
        // .size());
    }

    @Test
    public void testGetPatientCollectionEventCollection() throws Exception {
        PatientWrapper patient = PatientHelper.addPatient(
            Utils.getRandomNumericString(20), study);
        // FIXME
        // List<CollectionEventWrapper> cevents = new
        // ArrayList<CollectionEventWrapper>();
        // for (int i = 0, n = r.nextInt(10); i < n; ++i) {
        // CollectionEventWrapper ce = CollectionEventHelper
        // .addCollectionEvent(site, ShippingMethodWrapper
        // .getShippingMethods(appService).get(0), SourceVesselHelper
        // .newSourceVessel(patient, Utils.getRandomDate(), 0.1));
        // cevents.add(ce);
        // }
        // patient.reload();
        //
        // List<CollectionEventWrapper> savedCollectionEvents = patient
        // .getCollectionEventCollection();
        // Assert.assertEquals(cevents.size(), savedCollectionEvents.size());
        // for (CollectionEventWrapper cevent : savedCollectionEvents) {
        // Assert.assertTrue(cevents.contains(cevent));
        // }
    }

    @Test
    public void testGetVisits() throws Exception {
        String name = "testGetVisits" + r.nextInt();
        PatientWrapper patient1 = PatientHelper.addPatient(name + "_1", study);
        PatientWrapper patient2 = PatientHelper.addPatient(name + "_2", study);

        Date dateProcessed1 = Utils.getRandomDate();
        Date dateDrawn1 = Utils.getRandomDate();
        Date dateDrawn1_1 = Utils.getRandomDate();
        Date dateProcessed2 = Utils.getRandomDate();
        Date dateDrawn2 = Utils.getRandomDate();
        Date dateProcessed3 = Utils.getRandomDate();
        Date dateDrawn3 = Utils.getRandomDate();

        // FIXME
        // ProcessingEventWrapper visit1 = ProcessingEventHelper
        // .addProcessingEvent(site, patient1, dateProcessed1, dateDrawn1);
        // ProcessingEventWrapper visit1_1 = ProcessingEventHelper
        // .addProcessingEvent(site, patient1, dateProcessed1, dateDrawn1_1);
        // ProcessingEventWrapper visit2 = ProcessingEventHelper
        // .addProcessingEvent(site, patient1, dateProcessed2, dateDrawn2);
        // ProcessingEventWrapper visit3 = ProcessingEventHelper
        // .addProcessingEvent(site, patient2, dateProcessed3, dateDrawn3);
        //
        // patient1.reload();
        // patient2.reload();
        //
        // List<ProcessingEventWrapper> visitsFound = patient1.getVisits(
        // dateProcessed1, dateDrawn1);
        // Assert.assertTrue(visitsFound.size() == 1);
        // Assert.assertTrue(visitsFound.contains(visit1));
        //
        // visitsFound = patient1.getVisits(dateProcessed1, dateDrawn1_1);
        // Assert.assertTrue(visitsFound.size() == 1);
        // Assert.assertTrue(visitsFound.contains(visit1_1));
        //
        // visitsFound = patient1.getVisits(dateProcessed2, dateDrawn2);
        // Assert.assertTrue(visitsFound.size() == 1);
        // Assert.assertEquals(visit2, visitsFound.get(0));
        //
        // visitsFound = patient1.getVisits(dateProcessed3, dateDrawn3);
        // Assert.assertEquals(0, visitsFound.size());
        //
        // visitsFound = patient2.getVisits(dateProcessed3, dateDrawn3);
        // Assert.assertTrue(visitsFound.size() == 1);
        // Assert.assertEquals(visit3, visitsFound.get(0));
    }

    @Test
    public void testGetSampleCount() throws Exception {
        String patientName = Utils.getRandomNumericString(20);
        PatientWrapper patient1 = PatientHelper.addPatient(patientName, study);
        PatientWrapper patient2 = PatientHelper.addPatient(patientName + "_2",
            study);

        addContainerTypes();
        addContainers();
        patient1.persist();
        // FIXME
        // CollectionEventWrapper cevent = CollectionEventHelper
        // .addCollectionEvent(
        // site,
        // ShippingMethodWrapper.getShippingMethods(appService).get(0),
        // SourceVesselHelper.newSourceVessel(patient1,
        // Utils.getRandomDate(), 0.1));
        // cevent.persist();
        // patient1.reload();
        // patient2.reload();
        //
        // cevent = patient1.getCollectionEventCollection().get(0);
        // Assert.assertNotNull(cevent);
        //
        // ContainerWrapper childL1 = containerMap.get("ChildL1");
        // int maxCols = childL1.getColCapacity();
        // int count = 5;
        // for (PatientWrapper patient : Arrays.asList(patient1, patient2)) {
        // List<ProcessingEventWrapper> pevents = new
        // ArrayList<ProcessingEventWrapper>();
        // for (int i = 0; i < count; i++) {
        // pevents.add(ProcessingEventHelper.newProcessingEvent(site,
        // patient, Utils.getRandomDate(), Utils.getRandomDate()));
        // }
        // patient.addToProcessingEventCollection(pevents);
        // patient.persist();
        // patient.reload();
        // }
        //
        // List<SpecimenTypeWrapper> allSampleTypes = SpecimenTypeWrapper
        // .getAllSpecimenTypes(appService, true);
        //
        // int sampleTypeCount = allSampleTypes.size();
        // List<SpecimenWrapper> samples = new ArrayList<SpecimenWrapper>();
        // Map<PatientWrapper, Integer> patientSampleCount = new
        // HashMap<PatientWrapper, Integer>();
        // for (PatientWrapper patient : Arrays.asList(patient1, patient2)) {
        // patientSampleCount.put(patient, 0);
        // }
        //
        // // 2 samples per pevent
        // int sampleCount = 0;
        // for (PatientWrapper patient : Arrays.asList(patient1, patient2)) {
        // for (ProcessingEventWrapper pevent : patient
        // .getProcessingEventCollection(false)) {
        // for (int i = 0; i < 2; ++i) {
        // samples.add(SpecimenHelper.addAliquot(
        // allSampleTypes.get(r.nextInt(sampleTypeCount)),
        // childL1, pevent, sampleCount / maxCols, sampleCount
        // % maxCols));
        // patient.reload();
        // patientSampleCount.put(patient,
        // patientSampleCount.get(patient) + 1);
        // ++sampleCount;
        // Assert.assertEquals(patientSampleCount.get(patient)
        // .intValue(), patient.getSpecimensCount(true));
        // Assert.assertEquals(patientSampleCount.get(patient)
        // .intValue(), patient.getSpecimensCount(false));
        // }
        // }
        // }
        //
        // Assert
        // .assertEquals(1, patient1.getSourceVesselCollection(false).size());
        //
        // // now delete samples
        // for (PatientWrapper patient : Arrays.asList(patient1, patient2)) {
        // for (ProcessingEventWrapper pevent : patient
        // .getProcessingEventCollection(false)) {
        // samples = pevent.getSpecimenCollection(false);
        // while (samples.size() > 0) {
        // SpecimenWrapper aliquot = samples.get(0);
        // aliquot.delete();
        // pevent.reload();
        // patient.reload();
        // samples = pevent.getSpecimenCollection(false);
        // patientSampleCount.put(patient,
        // patientSampleCount.get(patient) - 1);
        // Assert.assertEquals(patientSampleCount.get(patient1)
        // .intValue(), patient1.getSpecimensCount(true));
        // Assert.assertEquals(patientSampleCount.get(patient1)
        // .intValue(), patient1.getSpecimensCount(false));
        // Assert.assertEquals(patientSampleCount.get(patient2)
        // .intValue(), patient2.getSpecimensCount(true));
        // Assert.assertEquals(patientSampleCount.get(patient2)
        // .intValue(), patient2.getSpecimensCount(false));
        // }
        // }
        // }
    }

    @Test
    public void testPatientMerge() throws Exception {
        String name = "testMerge" + r.nextInt();
        PatientWrapper patient = PatientHelper.addPatient(name + "_1", study);
        PatientWrapper patient2 = PatientHelper.addPatient(name + "_2", study);

        ClinicWrapper clinic = ClinicHelper.addClinic(name);
        ContactWrapper contact = ContactHelper.addContact(clinic, name);
        study.addToContactCollection(Arrays.asList(contact));
        study.persist();

        // FIXME
        // CollectionEventWrapper cevent = CollectionEventHelper
        // .addCollectionEvent(
        // site,
        // ShippingMethodWrapper.getShippingMethods(appService).get(0),
        // SourceVesselHelper.newSourceVessel(patient,
        // Utils.getRandomDate(), 0.1));
        // CollectionEventWrapper shipment2 = CollectionEventHelper
        // .addCollectionEvent(
        // site,
        // ShippingMethodWrapper.getShippingMethods(appService).get(0),
        // SourceVesselHelper.newSourceVessel(patient2,
        // Utils.getRandomDate(), 0.1));
        //
        // ProcessingEventWrapper visit1 = ProcessingEventHelper
        // .addProcessingEvent(site, patient, Utils.getRandomDate(),
        // Utils.getRandomDate());
        //
        // ProcessingEventWrapper visit2 = ProcessingEventHelper
        // .addProcessingEvent(site, patient2, Utils.getRandomDate(),
        // Utils.getRandomDate());
        //
        // Assert.assertEquals(patient, visit1.getPatient());
        // Assert.assertEquals(patient2, visit2.getPatient());
        //
        // patient.merge(patient2);
        //
        // patient.reload();
        // patient2.reload();
        // visit1.reload();
        // visit2.reload();
        // cevent.reload();
        // shipment2.reload();
        //
        // Assert.assertEquals(patient, visit1.getPatient());
        // Assert.assertEquals(patient, visit2.getPatient());
        //
        // Assert.assertTrue(cevent.getPatientCollection().contains(patient));
    }

    @Test
    public void testMergeFail() throws Exception {
        String name = "testMergeFail" + r.nextInt();
        ClinicWrapper clinic = ClinicHelper.addClinic(name);
        ContactWrapper contact = ContactHelper.addContact(clinic, name);
        study.addToContactCollection(Arrays.asList(contact));
        study.persist();

        StudyWrapper study2 = StudyHelper.addStudy(name + "_2");
        study2.addToContactCollection(Arrays.asList(ContactHelper.addContact(
            clinic, name + "_2")));
        study2.persist();

        PatientWrapper patient = PatientHelper.addPatient(name + "_1", study);
        PatientWrapper patient2 = PatientHelper.addPatient(name + "_2", study2);

        CollectionEventWrapper visit1 = CollectionEventHelper
            .addCollectionEvent(site, patient, 1);

        CollectionEventWrapper visit2 = CollectionEventHelper
            .addCollectionEvent(site, patient2, 1);

        Assert.assertEquals(patient, visit1.getPatient());
        Assert.assertEquals(patient2, visit2.getPatient());

        try {
            patient.merge(patient2);
            Assert
                .fail("Should not be able to merge patients that are not in the same study");
        } catch (BiobankCheckException bce) {
            Assert.assertTrue(true);
        }
    }

    @Test
    public void testGetPatient() throws Exception {
        PatientHelper.addPatient("testp", StudyHelper.addStudy("testst"));
        Assert.assertEquals(PatientWrapper.getPatient(appService, "testp")
            .getPnumber(), "testp");
    }
}
