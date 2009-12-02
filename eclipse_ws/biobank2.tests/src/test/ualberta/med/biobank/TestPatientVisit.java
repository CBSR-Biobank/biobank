package test.ualberta.med.biobank;

import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
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
import edu.ualberta.med.biobank.common.RowColPos;
import edu.ualberta.med.biobank.common.formatters.DateFormatter;
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
import edu.ualberta.med.biobank.model.PatientVisit;

public class TestPatientVisit extends TestDatabase {

    private Map<String, ContainerWrapper> containerMap;

    private Map<String, ContainerTypeWrapper> containerTypeMap;

    private SiteWrapper site;

    private StudyWrapper study;

    private ClinicWrapper clinic;

    private ShipmentWrapper shipment;

    private PatientWrapper patient;

    // the methods to skip in the getters and setters test
    private static final List<String> GETTER_SKIP_METHODS = Arrays
        .asList(new String[] { "getPvInfo", "getPvInfoType" });

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
        containerMap = new HashMap<String, ContainerWrapper>();
        containerTypeMap = new HashMap<String, ContainerTypeWrapper>();
        site = SiteHelper.addSite("Site - Patient Visit Test "
            + Utils.getRandomString(10));
        study = StudyHelper.addStudy(site, "Study - Patient Visit Test "
            + Utils.getRandomString(10));
        clinic = ClinicHelper.addClinic(site, "Clinic - Patient Visit Test "
            + Utils.getRandomString(10));
        ContactWrapper contact = ContactHelper.addContact(clinic,
            "Contact - Patient Visit Test");
        study.setContactCollection(Arrays
            .asList(new ContactWrapper[] { contact }));
        study.persist();
        patient = PatientHelper.addPatient(Utils.getRandomNumericString(20),
            study);
        shipment = ShipmentHelper.addShipment(clinic, patient);
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
        PatientVisitWrapper visit = PatientVisitHelper.addPatientVisit(patient,
            shipment, Utils.getRandomDate());
        testGettersAndSetters(visit, GETTER_SKIP_METHODS);
    }

    @Test
    public void testCompareTo() throws Exception {
        // visit2's date processed is 1 day after visit1's
        Date dateProcessed = Utils.getRandomDate();
        PatientVisitWrapper visit1 = PatientVisitHelper.addPatientVisit(
            patient, shipment, dateProcessed);

        Calendar cal = Calendar.getInstance();
        cal.setTime(dateProcessed);
        cal.add(Calendar.DATE, 1);
        ShipmentWrapper shipment2 = ShipmentHelper.addShipment(clinic, patient);

        PatientVisitWrapper visit2 = PatientVisitHelper.addPatientVisit(
            patient, shipment2, cal.getTime());

        Assert.assertEquals(-1, visit1.compareTo(visit2));

        // visit2's date processed is 1 day before visit1's
        cal.add(Calendar.DATE, -2);
        visit2.setDateProcessed(cal.getTime());
        visit2.persist();
        visit2.reload();
        Assert.assertEquals(1, visit1.compareTo(visit2));

        // check against itself
        Assert.assertEquals(0, visit1.compareTo(visit1));
    }

    @Test
    public void testReset() throws Exception {
        PatientVisitWrapper visit = PatientVisitHelper.addPatientVisit(patient,
            shipment, Utils.getRandomDate());
        visit.reset();
    }

    @Test
    public void testReload() throws Exception {
        PatientVisitWrapper visit = PatientVisitHelper.addPatientVisit(patient,
            shipment, Utils.getRandomDate());
        visit.reload();
    }

    @Test
    public void testDelete() throws Exception {
        PatientVisitWrapper visit = PatientVisitHelper.addPatientVisit(patient,
            shipment, Utils.getRandomDate());
        visit.delete();

        // make sure visit cannot be deleted if it has samples
        visit = PatientVisitHelper.addPatientVisit(patient, shipment, Utils
            .getRandomDate());
        addContainerTypes();
        addContainers();
        List<SampleTypeWrapper> allSampleTypes = SampleTypeWrapper
            .getGlobalSampleTypes(appService, true);
        SampleWrapper sample = SampleHelper.addSample(allSampleTypes.get(0),
            containerMap.get("ChildL1"), visit, 0, 0);
        visit.reload();

        try {
            visit.delete();
            Assert.fail("should not be allowed to delete patient visit");
        } catch (Exception e) {
            Assert.assertTrue(true);
        }

        // delete sample and visit
        sample.delete();
        visit.reload();
        visit.delete();
    }

    @Test
    public void testGetWrappedClass() throws Exception {
        PatientVisitWrapper visit = PatientVisitHelper.addPatientVisit(patient,
            shipment, Utils.getRandomDate());
        Assert.assertEquals(PatientVisit.class, visit.getWrappedClass());
    }

    @Test
    public void testGetSampleCollection() throws Exception {
        PatientVisitWrapper visit = PatientVisitHelper.addPatientVisit(patient,
            shipment, Utils.getRandomDate());
        addContainerTypes();
        addContainers();
        List<SampleTypeWrapper> allSampleTypes = SampleTypeWrapper
            .getGlobalSampleTypes(appService, true);
        int allSampleTypesCount = allSampleTypes.size();
        ContainerWrapper container = containerMap.get("ChildL1");

        // fill container with random samples
        Map<Integer, SampleWrapper> sampleMap = new HashMap<Integer, SampleWrapper>();
        int rows = container.getRowCapacity().intValue();
        int cols = container.getColCapacity().intValue();
        for (int row = 0; row < rows; ++row) {
            for (int col = 0; col < cols; ++col) {
                if (r.nextGaussian() > 0.0)
                    continue;
                sampleMap.put(row + col * rows, SampleHelper.addSample(
                    allSampleTypes.get(r.nextInt(allSampleTypesCount)),
                    container, visit, row, col));
            }
        }
        visit.reload();

        // verify that all samples are there
        Collection<SampleWrapper> visitSamples = visit.getSampleCollection();
        Assert.assertEquals(sampleMap.size(), visitSamples.size());

        for (SampleWrapper sample : visitSamples) {
            RowColPos pos = sample.getPosition();
            Assert
                .assertEquals(sample, sampleMap.get(pos.row + pos.col * rows));
        }

        // delete all samples now
        for (SampleWrapper sample : visitSamples) {
            sample.delete();
        }
        visit.reload();
        visitSamples = visit.getSampleCollection();
        Assert.assertEquals(0, visitSamples.size());
    }

    @Test
    public void testGetPvInfo() {
        fail("Not yet implemented");
    }

    @Test
    public void testGetPvInfoType() {
        fail("Not yet implemented");
    }

    @Test
    public void testPvInfoAllowedValues() {
        fail("Not yet implemented");
    }

    @Test
    public void testGetFormattedDateDrawn() {
        fail("Not yet implemented");
    }

    @Test
    public void testGetFormattedDateProcessed() {
        fail("Not yet implemented");
    }

    @Test
    public void testGetFormattedDateReceived() {
        fail("Not yet implemented");
    }

    @Test
    public void testGetPatient() {
        fail("Not yet implemented");
    }

    @Test
    public void testGetShipment() {
        fail("Not yet implemented");
    }

    @Test
    public void testGetPvInfoLabels() {
        fail("Not yet implemented");
    }

    @Test
    public void testRemoveDeletedPvSampleSources() {
        fail("Not yet implemented");
    }

    @Test
    public void testCheckVisitDateDrawnUnique() {
        fail("Not yet implemented");
    }

    @Test
    public void testPersist() throws Exception {
        PatientVisitWrapper pv = PatientVisitHelper.newPatientVisit(patient,
            shipment, DateFormatter.dateFormatter.parse("2009-12-25 00:00"));
        pv.persist();
    }

    public void testPersistFail() throws Exception {
        Assert.fail("check for checkVisitDateProcessedUnique");
        Assert.fail("check for checkPatientClinicInSameStudy");
    }

}
