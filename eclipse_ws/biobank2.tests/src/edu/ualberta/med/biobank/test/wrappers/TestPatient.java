package edu.ualberta.med.biobank.test.wrappers;

import java.util.ArrayList;
import java.util.Arrays;
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
import edu.ualberta.med.biobank.common.wrappers.ProcessingEventWrapper;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;
import edu.ualberta.med.biobank.common.wrappers.SpecimenTypeWrapper;
import edu.ualberta.med.biobank.common.wrappers.SpecimenWrapper;
import edu.ualberta.med.biobank.common.wrappers.StudyWrapper;
import edu.ualberta.med.biobank.model.Patient;
import edu.ualberta.med.biobank.test.TestDatabase;
import edu.ualberta.med.biobank.test.Utils;
import edu.ualberta.med.biobank.test.internal.ClinicHelper;
import edu.ualberta.med.biobank.test.internal.CollectionEventHelper;
import edu.ualberta.med.biobank.test.internal.ContactHelper;
import edu.ualberta.med.biobank.test.internal.ContainerHelper;
import edu.ualberta.med.biobank.test.internal.ContainerTypeHelper;
import edu.ualberta.med.biobank.test.internal.DbHelper;
import edu.ualberta.med.biobank.test.internal.PatientHelper;
import edu.ualberta.med.biobank.test.internal.ProcessingEventHelper;
import edu.ualberta.med.biobank.test.internal.SiteHelper;
import edu.ualberta.med.biobank.test.internal.SpecimenHelper;
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

    private void addClinic() throws Exception {
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
        String name = "testDelete" + r.nextInt();
        PatientWrapper patient = PatientHelper.addPatient(name, study);
        patient.delete();
        study.reload();

        // create new patient with processing events, should not be allowed to
        // delete
        patient = PatientHelper.addPatient(name, study);
        addContainerTypes();
        addContainers();
        addClinic();
        patient.persist();

        SpecimenWrapper parentSpc = SpecimenHelper.addParentSpecimen(clinic,
            study, patient);
        patient.reload();
        CollectionEventWrapper cevent = patient.getCollectionEventCollection(
            false).get(0);
        Assert.assertNotNull(cevent);

        List<SpecimenTypeWrapper> contSampleTypes = containerMap.get("ChildL1")
            .getContainerType().getSpecimenTypeCollection();

        int count = containerMap.get("ChildL1").getColCapacity();
        List<ProcessingEventWrapper> pevents = new ArrayList<ProcessingEventWrapper>();
        for (int i = 0; i < count; i++) {
            ProcessingEventWrapper pe = ProcessingEventHelper
                .addProcessingEvent(site, patient, Utils.getRandomDate());
            SpecimenHelper.addSpecimen(parentSpc,
                DbHelper.chooseRandomlyInList(contSampleTypes), cevent, pe,
                containerMap.get("ChildL1"), 0, i);
            pevents.add(pe);
        }
        patient.persist();
        patient.reload();

        pevents = patient.getProcessingEventCollection(false);
        SpecimenWrapper spc = SpecimenHelper.addSpecimen(parentSpc,
            DbHelper.chooseRandomlyInList(contSampleTypes), cevent,
            pevents.get(0), containerMap.get("ChildL1"), 1, 0);
        patient.reload();

        try {
            patient.delete();
            Assert.fail("should not be allowed to delete patient with samples");
        } catch (Exception e) {
            Assert.assertTrue(true);
        }

        // delete specimen and patient
        spc.delete();

        try {
            patient.delete();
            Assert
                .fail("should not be allowed to delete patient with processing events");
        } catch (Exception e) {
            Assert.assertTrue(true);
        }

        for (ProcessingEventWrapper pe : patient
            .getProcessingEventCollection(false)) {
            DbHelper.deleteFromList(pe.getSpecimenCollection(false));
            pe.reload();
            pe.delete();
        }

        try {
            patient.delete();
            Assert
                .fail("should not be allowed to delete patient linked to collection event");
        } catch (Exception e) {
            Assert.assertTrue(true);
        }

        DbHelper.deleteCollectionEvents(patient
            .getCollectionEventCollection(false));

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
        } catch (DuplicateEntryException e) {
            Assert.assertTrue(true);
        }
    }

    @Test
    public void testGetProcessingEventCollection() throws Exception {
        String name = "testGetProcessingEventCollection" + r.nextInt();
        PatientWrapper patient = PatientHelper.addPatient(name, study);
        List<ProcessingEventWrapper> list = patient
            .getProcessingEventCollection(false);
        Assert.assertTrue(list.isEmpty());

        List<SpecimenTypeWrapper> allSampleTypes = SpecimenTypeWrapper
            .getAllSpecimenTypes(appService, true);

        addClinic();
        SpecimenWrapper parentSpc = SpecimenHelper.addParentSpecimen(clinic,
            study, patient);

        List<ProcessingEventWrapper> origPevents = ProcessingEventHelper
            .addProcessingEvents(site, patient, Utils.getRandomDate(),
                parentSpc, allSampleTypes, r.nextInt(15) + 5, 1);

        patient.reload();
        List<ProcessingEventWrapper> pevents = patient
            .getProcessingEventCollection(false);
        Assert.assertTrue(pevents.containsAll(origPevents));

        // delete random pevents, ensure at least one left
        int numToDelete = r.nextInt(pevents.size() - 1);
        List<ProcessingEventWrapper> deletePevents = new ArrayList<ProcessingEventWrapper>();
        for (int i = 0; i < numToDelete; ++i) {
            ProcessingEventWrapper pevent = DbHelper
                .chooseRandomlyInList(origPevents);
            deletePevents.add(pevent);
            origPevents.remove(pevent);
        }
        DbHelper.deleteProcessingEvents(deletePevents);

        // make sure patient now only has the pevents that were not deleted
        patient.reload();
        pevents = patient.getProcessingEventCollection(false);
        Assert.assertTrue(pevents.containsAll(pevents));

        // now remove all patient pevents
        DbHelper.deleteProcessingEvents(origPevents);

        // make sure patient does not have any patient pevents
        patient.reload();
        Assert.assertEquals(0, patient.getProcessingEventCollection(false)
            .size());
    }

    @Test
    public void testAddProcessingEvents() throws Exception {
        String name = "testAddProcessingEvents" + r.nextInt();
        PatientWrapper patient = PatientHelper.addPatient(name, study);
        patient.reload();
        addClinic();
        SpecimenWrapper parentSpc = SpecimenHelper.addParentSpecimen(clinic,
            study, patient);

        List<SpecimenTypeWrapper> allSampleTypes = SpecimenTypeWrapper
            .getAllSpecimenTypes(appService, true);

        List<ProcessingEventWrapper> peventSet1 = ProcessingEventHelper
            .addProcessingEvents(site, patient, Utils.getRandomDate(),
                parentSpc, allSampleTypes, r.nextInt(15) + 5, 1);
        patient.reload();
        Assert.assertEquals(peventSet1.size(), patient
            .getProcessingEventCollection(false).size());

        List<ProcessingEventWrapper> peventSet2 = ProcessingEventHelper
            .addProcessingEvents(site, patient, Utils.getRandomDate(),
                parentSpc, allSampleTypes, r.nextInt(10) + 5, 1);
        patient.reload();
        Assert.assertEquals(peventSet1.size() + peventSet2.size(), patient
            .getProcessingEventCollection(false).size());
    }

    @Test
    public void testGetPatientCollectionEventCollection() throws Exception {
        String name = "testGetPatientCollectionEventCollection" + r.nextInt();
        PatientWrapper patient = PatientHelper.addPatient(name, study);
        addClinic();
        List<CollectionEventWrapper> cevents = CollectionEventHelper
            .addCollectionEvents(clinic, patient, name);

        patient.reload();
        List<CollectionEventWrapper> savedCollectionEvents = patient
            .getCollectionEventCollection(false, false);
        Assert.assertEquals(cevents.size(), savedCollectionEvents.size());
        Assert.assertTrue(savedCollectionEvents.containsAll(cevents));
    }

    @Test
    public void testGetSpecimenCount() throws Exception {
        String name = "testGetPatientCollectionEventCollection" + r.nextInt();
        PatientWrapper patient1 = PatientHelper.addPatient(name + "_1", study);
        PatientWrapper patient2 = PatientHelper.addPatient(name + "_2", study);

        addContainerTypes();
        addContainers();
        addClinic();

        SpecimenWrapper[] parentSpcs = new SpecimenWrapper[] {
            SpecimenHelper.addParentSpecimen(clinic, study, patient1),
            SpecimenHelper.addParentSpecimen(clinic, study, patient2) };

        for (SpecimenWrapper parentSpc : parentSpcs) {
            Assert.assertNotNull(parentSpc.getCollectionEvent());
        }

        List<ProcessingEventWrapper> pevents = new ArrayList<ProcessingEventWrapper>();
        int count = 5;
        for (PatientWrapper patient : Arrays.asList(patient1, patient2)) {
            for (int i = 0; i < count; i++) {
                pevents.add(ProcessingEventHelper.addProcessingEvent(site,
                    patient, Utils.getRandomDate()));
            }
        }

        ContainerWrapper childL1 = containerMap.get("ChildL1");
        int maxCols = childL1.getColCapacity();
        List<SpecimenTypeWrapper> spcTypes = childL1.getContainerType()
            .getSpecimenTypeCollection();

        List<SpecimenWrapper> samples = new ArrayList<SpecimenWrapper>();
        Map<PatientWrapper, Integer> patientSampleCount = new HashMap<PatientWrapper, Integer>();
        for (PatientWrapper patient : Arrays.asList(patient1, patient2)) {
            patientSampleCount.put(patient, 0);
        }

        // 2 specimens per pevent
        int sampleCount = 0;
        for (SpecimenWrapper parentSpc : parentSpcs) {
            CollectionEventWrapper cevent = parentSpc.getCollectionEvent();
            PatientWrapper patient = cevent.getPatient();
            for (ProcessingEventWrapper pevent : patient
                .getProcessingEventCollection(false)) {
                for (int i = 0; i < 2; ++i) {
                    samples.add(SpecimenHelper.addSpecimen(parentSpc,
                        DbHelper.chooseRandomlyInList(spcTypes), cevent,
                        pevent, childL1, sampleCount / maxCols, sampleCount
                            % maxCols));
                    patient.reload();
                    patientSampleCount.put(patient,
                        patientSampleCount.get(patient) + 1);
                    ++sampleCount;
                    Assert.assertEquals(patientSampleCount.get(patient)
                        .longValue(), patient.getAliquotedSpecimenCount(true));
                    Assert.assertEquals(patientSampleCount.get(patient)
                        .intValue(), patient.getAliquotedSpecimenCount(false));
                }
            }
            patient.reload();
            Assert.assertEquals(1, patient.getSourceSpecimenCount(true));
            Assert.assertEquals(1, patient.getSourceSpecimenCount(false));
        }

        // now delete specimens
        for (PatientWrapper patient : Arrays.asList(patient1, patient2)) {
            for (ProcessingEventWrapper pevent : patient
                .getProcessingEventCollection(false)) {
                samples = pevent.getSpecimenCollection(false);
                while (samples.size() > 0) {
                    SpecimenWrapper aliquot = samples.get(0);
                    aliquot.delete();
                    pevent.reload();
                    patient.reload();
                    samples = pevent.getSpecimenCollection(false);
                    patientSampleCount.put(patient,
                        patientSampleCount.get(patient) - 1);
                    Assert.assertEquals(patientSampleCount.get(patient1)
                        .intValue(), patient1.getAliquotedSpecimenCount(true));
                    Assert.assertEquals(patientSampleCount.get(patient1)
                        .intValue(), patient1.getAliquotedSpecimenCount(false));
                    Assert.assertEquals(patientSampleCount.get(patient2)
                        .intValue(), patient2.getAliquotedSpecimenCount(true));
                    Assert.assertEquals(patientSampleCount.get(patient2)
                        .intValue(), patient2.getAliquotedSpecimenCount(false));
                }
            }
        }
    }

    @Test
    public void testPatientMerge() throws Exception {
        String name = "testMerge" + r.nextInt();
        PatientWrapper patient1 = PatientHelper.addPatient(name + "_1", study);
        PatientWrapper patient2 = PatientHelper.addPatient(name + "_2", study);

        addContainerTypes();
        addContainers();
        addClinic();

        SpecimenWrapper[] parentSpcs = new SpecimenWrapper[] {
            SpecimenHelper.addParentSpecimen(clinic, study, patient1),
            SpecimenHelper.addParentSpecimen(clinic, study, patient2) };

        CollectionEventWrapper cevent2 = parentSpcs[1].getCollectionEvent();
        cevent2.setVisitNumber(2);
        cevent2.persist();

        ContainerWrapper childL1 = containerMap.get("ChildL1");
        List<SpecimenTypeWrapper> contSampleTypes = childL1.getContainerType()
            .getSpecimenTypeCollection();
        int colCapacity = childL1.getColCapacity();
        int storedSpecimenCount = 0;

        List<ProcessingEventWrapper> pevents = new ArrayList<ProcessingEventWrapper>();
        for (SpecimenWrapper parentSpc : parentSpcs) {
            PatientWrapper patient = parentSpc.getCollectionEvent()
                .getPatient();
            List<ProcessingEventWrapper> patientPevents = ProcessingEventHelper
                .addProcessingEvents(site, patient, Utils.getRandomDate(),
                    parentSpc, contSampleTypes, 5, 2);

            // store the first specimen from each pevent in a childL1
            for (ProcessingEventWrapper pevent : patientPevents) {
                pevent.reload();
                childL1.addSpecimen(storedSpecimenCount / colCapacity,
                    storedSpecimenCount % colCapacity, pevent
                        .getSpecimenCollection(false).get(0));
                storedSpecimenCount++;
            }

            pevents.addAll(patientPevents);

        }

        patient1.reload();
        patient2.reload();

        patient1.merge(patient2);

        patient1.reload();
        patient2.reload();

        for (ProcessingEventWrapper pevent : pevents) {
            pevent.reload();
            Assert.assertEquals(patient1, pevent.getSpecimenCollection(false)
                .get(0).getCollectionEvent().getPatient());
        }

        for (SpecimenWrapper parentSpc : parentSpcs) {
            Assert.assertEquals(patient1, parentSpc.getCollectionEvent()
                .getPatient());
            Assert.assertEquals(patient1, parentSpc
                .getOriginalCollectionEvent().getPatient());
        }
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
