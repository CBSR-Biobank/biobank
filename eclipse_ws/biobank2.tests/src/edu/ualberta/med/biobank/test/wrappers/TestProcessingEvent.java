package edu.ualberta.med.biobank.test.wrappers;

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
import edu.ualberta.med.biobank.common.wrappers.ClinicWrapper;
import edu.ualberta.med.biobank.common.wrappers.ContactWrapper;
import edu.ualberta.med.biobank.common.wrappers.ContainerTypeWrapper;
import edu.ualberta.med.biobank.common.wrappers.ContainerWrapper;
import edu.ualberta.med.biobank.common.wrappers.PatientWrapper;
import edu.ualberta.med.biobank.common.wrappers.ProcessingEventWrapper;
import edu.ualberta.med.biobank.common.wrappers.ShippingMethodWrapper;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;
import edu.ualberta.med.biobank.common.wrappers.SpecimenTypeWrapper;
import edu.ualberta.med.biobank.common.wrappers.SpecimenWrapper;
import edu.ualberta.med.biobank.common.wrappers.StudyWrapper;
import edu.ualberta.med.biobank.test.TestDatabase;
import edu.ualberta.med.biobank.test.Utils;
import edu.ualberta.med.biobank.test.internal.ClinicHelper;
import edu.ualberta.med.biobank.test.internal.ContactHelper;
import edu.ualberta.med.biobank.test.internal.ContainerHelper;
import edu.ualberta.med.biobank.test.internal.ContainerTypeHelper;
import edu.ualberta.med.biobank.test.internal.PatientHelper;
import edu.ualberta.med.biobank.test.internal.ProcessingEventHelper;
import edu.ualberta.med.biobank.test.internal.SiteHelper;
import edu.ualberta.med.biobank.test.internal.SpecimenHelper;
import edu.ualberta.med.biobank.test.internal.StudyHelper;

public class TestProcessingEvent extends TestDatabase {

    private Map<String, ContainerWrapper> containerMap;

    private Map<String, ContainerTypeWrapper> containerTypeMap;

    private SiteWrapper site;

    private StudyWrapper study;

    private ClinicWrapper clinic;

    private PatientWrapper patient;

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
        containerMap = new HashMap<String, ContainerWrapper>();
        containerTypeMap = new HashMap<String, ContainerTypeWrapper>();
        site = SiteHelper.addSite("Site - Processing Event Test "
            + Utils.getRandomString(10));
        study = StudyHelper.addStudy("Study - Processing Event Test "
            + Utils.getRandomString(10));
        clinic = ClinicHelper.addClinic("Clinic - Processing Event Test "
            + Utils.getRandomString(10));
        ContactWrapper contact = ContactHelper.addContact(clinic,
            "Contact - Processing Event Test");
        study.addToContactCollection(Arrays.asList(contact));
        study.persist();
        patient = PatientHelper.addPatient(Utils.getRandomNumericString(20),
            study);
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
        ProcessingEventWrapper pevent = ProcessingEventHelper
            .addProcessingEvent(site, patient, Utils.getRandomDate());
        testGettersAndSetters(pevent);
    }

    @Test
    public void testCompareTo() throws Exception {
        // visit2's date processed is 1 day after visit1's
        Date date = Utils.getRandomDate();
        ProcessingEventWrapper pevent1 = ProcessingEventHelper
            .addProcessingEvent(site, patient, date);

        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DATE, 1);

        ProcessingEventWrapper pevent2 = ProcessingEventHelper
            .addProcessingEvent(site, patient, cal.getTime());

        Assert.assertEquals(-1, pevent1.compareTo(pevent2));

        // visit2's date processed is 1 day before visit1's
        cal.add(Calendar.DATE, -2);
        pevent2.setCreatedAt(cal.getTime());
        pevent2.persist();
        pevent2.reload();
        Assert.assertEquals(1, pevent1.compareTo(pevent2));

        // check against itself
        Assert.assertEquals(0, pevent1.compareTo(pevent1));
    }

    @Test
    public void testReset() throws Exception {
        Date dateProcessed = Utils.getRandomDate();
        ProcessingEventWrapper pevent = ProcessingEventHelper
            .addProcessingEvent(site, patient, dateProcessed);
        Calendar cal = Calendar.getInstance();
        pevent.setCreatedAt(cal.getTime());
        pevent.reset();
        Assert.assertEquals(dateProcessed, pevent.getCreatedAt());
    }

    @Test
    public void testReload() throws Exception {
        Date dateProcessed = Utils.getRandomDate();
        ProcessingEventWrapper pevent = ProcessingEventHelper
            .addProcessingEvent(site, patient, dateProcessed);
        Calendar cal = Calendar.getInstance();
        pevent.setCreatedAt(cal.getTime());
        pevent.reload();
        Assert.assertEquals(dateProcessed, pevent.getCreatedAt());
    }

    @Test
    public void testDelete() throws Exception {
        ProcessingEventWrapper pevent = ProcessingEventHelper
            .addProcessingEvent(site, patient, Utils.getRandomDate());
        pevent.delete();

        // make sure pevent cannot be deleted if it has samples
        pevent = ProcessingEventHelper.addProcessingEvent(site, patient,
            Utils.getRandomDate());
        addContainerTypes();
        addContainers();

        SpecimenWrapper childSpc = SpecimenHelper.addSpecimens(patient, clinic,
            containerMap.get("ChildL1"), 0, 0, 1).get(0);
        pevent = childSpc.getProcessingEvent();

        try {
            pevent.delete();
            Assert
                .fail("should not be allowed to delete Processing Event since it is associated with specimens");
        } catch (Exception e) {
            Assert.assertTrue(true);
        }

        // delete aliquot and pevent
        childSpc.delete();
        pevent.reload();
        pevent.delete();
    }

    @Test
    public void testDeleteNoMoreVisits() throws Exception {
        String name = "testDeleteNoMoreVisits" + r.nextInt();
        SiteWrapper site = SiteHelper.addSite("site" + name);
        ClinicWrapper clinic = ClinicHelper.addClinic("clinic" + name);
        StudyWrapper study = StudyHelper.addStudy(name);
        ContactWrapper contact = ContactHelper.addContact(clinic, name);
        study.addToContactCollection(Arrays.asList(contact));
        study.persist();
        ShippingMethodWrapper method = ShippingMethodWrapper
            .getShippingMethods(appService).get(0);
        PatientWrapper patient1 = PatientHelper.addPatient(name, study);
        // FIXME
        // CollectionEventHelper.addCollectionEvent(site, method,
        // SourceVesselHelper.newSourceVessel(patient1, Utils.getRandomDate(),
        // 0.1));
        // CollectionEventWrapper ceventTest = CollectionEventHelper
        // .addCollectionEvent(
        // site,
        // method,
        // SourceVesselHelper.newSourceVessel(patient1,
        // Utils.getRandomDate(), 0.1));
        // CollectionEventHelper.addCollectionEvent(site, method,
        // SourceVesselHelper.newSourceVessel(patient1, Utils.getRandomDate(),
        // 0.1));
        //
        // ProcessingEventHelper.addProcessingEvent(site, patient1,
        // Utils.getRandomDate(), Utils.getRandomDate());
        // ceventTest.reload();
        //
        // try {
        // ceventTest.delete();
        // Assert.fail("one pevent still there");
        // } catch (BiobankCheckException bce) {
        // Assert.assertTrue(true);
        // }
        //
        // CollectionEventHelper.addCollectionEvent(site, ShippingMethodWrapper
        // .getShippingMethods(appService).get(0), SourceVesselHelper
        // .newSourceVessel(patient1, Utils.getRandomDate(), 0.1));
        //
        // int countBefore = appService.search(CollectionEvent.class,
        // new CollectionEvent()).size();
        // ceventTest.reload();
        // DbHelper.deleteFromList(ceventTest.getSourceVesselCollection(false));
        // ceventTest.delete();
        // int countAfter = appService.search(CollectionEvent.class,
        // new CollectionEvent()).size();
        // Assert.assertEquals(countBefore - 1, countAfter);
    }

    @Test
    public void testGetWrappedClass() throws Exception {
        // FIXME
        // ProcessingEventWrapper pevent = ProcessingEventHelper
        // .addProcessingEvent(site, patient, Utils.getRandomDate(),
        // Utils.getRandomDate());
        // Assert.assertEquals(ProcessingEvent.class, pevent.getWrappedClass());
    }

    @Test
    public void testGetSampleCollection() throws Exception {
        // FIXME
        // ProcessingEventWrapper pevent = ProcessingEventHelper
        // .addProcessingEvent(site, patient, Utils.getRandomDate(),
        // Utils.getRandomDate());
        // addContainerTypes();
        // addContainers();
        // List<SpecimenTypeWrapper> allSampleTypes = SpecimenTypeWrapper
        // .getAllSpecimenTypes(appService, true);
        // int allSampleTypesCount = allSampleTypes.size();
        // ContainerWrapper container = containerMap.get("ChildL1");
        //
        // // fill container with random samples
        // Map<Integer, SpecimenWrapper> sampleMap = new HashMap<Integer,
        // SpecimenWrapper>();
        // int rows = container.getRowCapacity().intValue();
        // int cols = container.getColCapacity().intValue();
        // for (int row = 0; row < rows; ++row) {
        // for (int col = 0; col < cols; ++col) {
        // if (r.nextGaussian() > 0.0)
        // continue;
        // // System.out.println("setting aliquot at: " + row + ", " +
        // // col);
        // sampleMap.put(row + col * rows, SpecimenHelper.addAliquot(
        // allSampleTypes.get(r.nextInt(allSampleTypesCount)),
        // container, pevent, row, col));
        // }
        // }
        // pevent.reload();
        //
        // // verify that all samples are there
        // Collection<SpecimenWrapper> visitSamples = pevent
        // .getSpecimenCollection(false);
        // Assert.assertEquals(sampleMap.size(), visitSamples.size());
        //
        // for (SpecimenWrapper aliquot : visitSamples) {
        // RowColPos pos = aliquot.getPosition();
        // // System.out.println("getting aliquot from: " + pos.row + ", "
        // // + pos.col);
        // Assert.assertNotNull(pos);
        // Assert.assertNotNull(pos.row);
        // Assert.assertNotNull(pos.col);
        // Assert.assertNotNull(sampleMap.get(pos.row + pos.col * rows));
        // Assert.assertEquals(aliquot,
        // sampleMap.get(pos.row + pos.col * rows));
        // }
        //
        // // delete all samples now
        // for (SpecimenWrapper aliquot : visitSamples) {
        // aliquot.delete();
        // }
        // pevent.reload();
        // visitSamples = pevent.getSpecimenCollection(false);
        // Assert.assertEquals(0, visitSamples.size());
    }

    @Test
    public void testGetFormattedDateProcessed() throws Exception {
        Date date = Utils.getRandomDate();
        // FIXME
        // ProcessingEventWrapper pevent = ProcessingEventHelper
        // .addProcessingEvent(site, patient, date, date);
        //
        // SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        // Assert.assertTrue(sdf.format(date).equals(
        // pevent.getFormattedDateProcessed()));
    }

    @Test
    public void testGetPatient() throws Exception {
        // FIXME
        // ProcessingEventWrapper pevent = ProcessingEventHelper
        // .addProcessingEvent(site, patient, TestCommon.getUniqueDate(r),
        // Utils.getRandomDate());
        // Assert.assertEquals(patient, pevent.getPatient());
    }

    @Test
    public void testCheckHasCollectionEvent() throws Exception {
        // FIXME
        // // check valid case
        // ProcessingEventWrapper pevent = ProcessingEventHelper
        // .newProcessingEvent(site, patient, TestCommon.getUniqueDate(r),
        // Utils.getRandomDate());
        //
        // // check invalid case
        // pevent = new ProcessingEventWrapper(appService);
        // pevent.setPatient(patient);
        // pevent.setDateProcessed(TestCommon.getUniqueDate(r));
        //
        // try {
        // pevent.persist();
        // Assert
        // .fail("should not be allowed to add a visits with no shipment");
        // } catch (Exception e) {
        // Assert.assertTrue(true);
        // }
    }

    @Test
    public void testPersist() throws Exception {
        // FIXME
        // ProcessingEventWrapper pv = ProcessingEventHelper.newProcessingEvent(
        // site, patient,
        // DateFormatter.dateFormatter.parse("2009-12-25 00:00"),
        // Utils.getRandomDate());
        // pv.persist();
    }

    @Test
    public void testAddSourceVessels() throws Exception {
        // FIXME
        // ProcessingEventWrapper pevent = ProcessingEventHelper
        // .addProcessingEvent(site, patient, Utils.getRandomDate(),
        // Utils.getRandomDate());
        //
        // SourceVesselWrapper sv1, sv2, sv3;
        //
        // sv1 = SourceVesselHelper.newSourceVessel(pevent.getPatient(),
        // Utils.getRandomDate(), 0.01);
        // sv2 = SourceVesselHelper.newSourceVessel(pevent.getPatient(),
        // Utils.getRandomDate(), 0.01);
        //
        // CollectionEventWrapper cevent = CollectionEventHelper
        // .addCollectionEvent(site,
        // ShippingMethodWrapper.getShippingMethods(appService).get(0),
        // sv1, sv2);
        //
        // pevent.addToSourceVesselCollection(Arrays.asList(sv1, sv2));
        // pevent.persist();
        //
        // for (SourceVesselWrapper sv : Arrays.asList(sv1, sv2)) {
        // sv.setCollectionEvent(cevent);
        // sv.setProcessingEvent(pevent);
        // sv.persist();
        // }
        //
        // pevent.reload();
        // // get the sorted list
        // List<SourceVesselWrapper> list =
        // pevent.getSourceVesselCollection(true);
        // Assert.assertEquals(2, list.size());
        // Assert.assertTrue(list.get(0).compareTo(list.get(1)) < 0);
        //
        // sv3 = SourceVesselHelper.newSourceVessel(pevent.getPatient(),
        // Utils.getRandomDate(), 0.01);
        //
        // SourceVesselWrapper pvss = list.get(0);
        // pevent.removeFromSourceVesselCollection(Arrays.asList(pvss));
        // pevent.addToSourceVesselCollection(Arrays.asList(sv3));
        //
        // pevent.persist();
        //
        // list = pevent.getSourceVesselCollection(false);
        // Assert.assertEquals(2, list.size());
    }

    @Test
    public void testAddAliquot() throws BiobankCheckException, Exception {
        // FIXME
        // ProcessingEventWrapper pevent = ProcessingEventHelper
        // .addProcessingEvent(site, patient, Utils.getRandomDate(),
        // Utils.getRandomDate());
        //
        // List<SpecimenTypeWrapper> types = SpecimenTypeWrapper
        // .getAllSpecimenTypes(appService, false);
        // SampleStorageHelper.addSampleStorage(study,
        // DbHelper.chooseRandomlyInList(types));
        // SampleStorageHelper.addSampleStorage(study,
        // DbHelper.chooseRandomlyInList(types));
        // SpecimenTypeWrapper sampleType =
        // DbHelper.chooseRandomlyInList(types);
        // AliquotedSpecimenWrapper ss3 = SampleStorageHelper.newSampleStorage(
        // study, sampleType);
        // ss3.setVolume(3.0);
        // ss3.persist();
        // pevent.reload();
        //
        // String inventoryId = "newid";
        // SpecimenWrapper newAliquot = new SpecimenWrapper(appService);
        // newAliquot.setInventoryId(inventoryId);
        // newAliquot.setLinkDate(new Date());
        // newAliquot.setSpecimenType(sampleType);
        // newAliquot.setActivityStatus(ActivityStatusWrapper
        // .getActiveActivityStatus(appService));
        // pevent.addChildSpecimens(Arrays.asList(newAliquot));
        // pevent.persist();
        //
        // SpecimenWrapper aliquot = SpecimenWrapper.getSpecimen(appService,
        // inventoryId, null);
        // Assert.assertNotNull(aliquot);
        // Assert.assertEquals(aliquot.getSpecimenType().getId(), newAliquot
        // .getSpecimenType().getId());
        // Assert.assertTrue(aliquot.getQuantity().equals(3.0));
        // Assert.assertEquals(aliquot.getProcessingEvent().getId(),
        // pevent.getId());
    }
}
