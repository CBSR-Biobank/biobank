package edu.ualberta.med.biobank.test.wrappers;

import java.text.SimpleDateFormat;
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

import edu.ualberta.med.biobank.common.exception.BiobankCheckException;
import edu.ualberta.med.biobank.common.formatters.DateFormatter;
import edu.ualberta.med.biobank.common.util.RowColPos;
import edu.ualberta.med.biobank.common.wrappers.ActivityStatusWrapper;
import edu.ualberta.med.biobank.common.wrappers.AliquotWrapper;
import edu.ualberta.med.biobank.common.wrappers.ClinicWrapper;
import edu.ualberta.med.biobank.common.wrappers.ContactWrapper;
import edu.ualberta.med.biobank.common.wrappers.ContainerTypeWrapper;
import edu.ualberta.med.biobank.common.wrappers.ContainerWrapper;
import edu.ualberta.med.biobank.common.wrappers.PatientVisitWrapper;
import edu.ualberta.med.biobank.common.wrappers.PatientWrapper;
import edu.ualberta.med.biobank.common.wrappers.PvSourceVesselWrapper;
import edu.ualberta.med.biobank.common.wrappers.SampleStorageWrapper;
import edu.ualberta.med.biobank.common.wrappers.SampleTypeWrapper;
import edu.ualberta.med.biobank.common.wrappers.ShipmentWrapper;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;
import edu.ualberta.med.biobank.common.wrappers.SourceVesselWrapper;
import edu.ualberta.med.biobank.common.wrappers.StudyWrapper;
import edu.ualberta.med.biobank.model.Aliquot;
import edu.ualberta.med.biobank.model.PatientVisit;
import edu.ualberta.med.biobank.model.PvAttr;
import edu.ualberta.med.biobank.test.TestDatabase;
import edu.ualberta.med.biobank.test.Utils;
import edu.ualberta.med.biobank.test.internal.AliquotHelper;
import edu.ualberta.med.biobank.test.internal.ClinicHelper;
import edu.ualberta.med.biobank.test.internal.ContactHelper;
import edu.ualberta.med.biobank.test.internal.ContainerHelper;
import edu.ualberta.med.biobank.test.internal.ContainerTypeHelper;
import edu.ualberta.med.biobank.test.internal.DbHelper;
import edu.ualberta.med.biobank.test.internal.PatientHelper;
import edu.ualberta.med.biobank.test.internal.PatientVisitHelper;
import edu.ualberta.med.biobank.test.internal.PvSourceVesselHelper;
import edu.ualberta.med.biobank.test.internal.SampleStorageHelper;
import edu.ualberta.med.biobank.test.internal.ShipmentHelper;
import edu.ualberta.med.biobank.test.internal.SiteHelper;
import edu.ualberta.med.biobank.test.internal.StudyHelper;
import gov.nih.nci.system.query.hibernate.HQLCriteria;

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
        .asList(new String[] { "getPvAttrValue", "getPvAttrTypeName" });

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
        study.addContacts(Arrays.asList(contact));
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

    private void addPvAttrs() throws Exception {
        // add PvAtt to study
        List<String> types = SiteWrapper.getPvAttrTypeNames(appService);
        Assert
            .assertTrue("PvAttrTypes not initialized", types.contains("text"));
        study.setStudyPvAttr("PMBC Count", "number");
        study.setStudyPvAttr("Worksheet", "text");
        study.setStudyPvAttr("Date", "date_time");
        study.setStudyPvAttr("Consent", "select_multiple", new String[] { "c1",
            "c2", "c3" });
        study.setStudyPvAttr("Visit", "select_single", new String[] { "v1",
            "v2", "v3", "v4" });
        study.persist();
        study.reload();
    }

    @Test
    public void testGettersAndSetters() throws Exception {
        PatientVisitWrapper visit = PatientVisitHelper.addPatientVisit(patient,
            shipment, Utils.getRandomDate(), Utils.getRandomDate());
        testGettersAndSetters(visit, GETTER_SKIP_METHODS);

        visit = new PatientVisitWrapper(appService);
        Assert.assertEquals(null, visit.getPatient());
    }

    @Test
    public void testCompareTo() throws Exception {
        // visit2's date processed is 1 day after visit1's
        Date date = Utils.getRandomDate();
        PatientVisitWrapper visit1 = PatientVisitHelper.addPatientVisit(
            patient, shipment, date, date);

        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DATE, 1);
        ShipmentWrapper shipment2 = ShipmentHelper.addShipment(clinic, patient);

        PatientVisitWrapper visit2 = PatientVisitHelper.addPatientVisit(
            patient, shipment2, cal.getTime(), date);

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
            shipment, Utils.getRandomDate(), Utils.getRandomDate());
        visit.reset();
    }

    @Test
    public void testReload() throws Exception {
        PatientVisitWrapper visit = PatientVisitHelper.addPatientVisit(patient,
            shipment, Utils.getRandomDate(), Utils.getRandomDate());
        visit.reload();
    }

    @Test
    public void testDelete() throws Exception {
        PatientVisitWrapper visit = PatientVisitHelper.addPatientVisit(patient,
            shipment, Utils.getRandomDate(), Utils.getRandomDate());
        visit.delete();

        // make sure visit cannot be deleted if it has samples
        visit = PatientVisitHelper.addPatientVisit(patient, shipment,
            Utils.getRandomDate(), Utils.getRandomDate());
        addContainerTypes();
        addContainers();
        List<SampleTypeWrapper> allSampleTypes = SampleTypeWrapper
            .getGlobalSampleTypes(appService, true);
        AliquotWrapper aliquot = AliquotHelper.addAliquot(
            allSampleTypes.get(0), containerMap.get("ChildL1"), visit, 0, 0);
        visit.reload();

        try {
            visit.delete();
            Assert.fail("should not be allowed to delete patient visit");
        } catch (Exception e) {
            Assert.assertTrue(true);
        }

        // delete aliquot and visit
        aliquot.delete();
        visit.reload();
        visit.delete();
    }

    @Test
    public void testGetWrappedClass() throws Exception {
        PatientVisitWrapper visit = PatientVisitHelper.addPatientVisit(patient,
            shipment, Utils.getRandomDate(), Utils.getRandomDate());
        Assert.assertEquals(PatientVisit.class, visit.getWrappedClass());
    }

    @Test
    public void testGetSampleCollection() throws Exception {
        PatientVisitWrapper visit = PatientVisitHelper.addPatientVisit(patient,
            shipment, Utils.getRandomDate(), Utils.getRandomDate());
        addContainerTypes();
        addContainers();
        List<SampleTypeWrapper> allSampleTypes = SampleTypeWrapper
            .getGlobalSampleTypes(appService, true);
        int allSampleTypesCount = allSampleTypes.size();
        ContainerWrapper container = containerMap.get("ChildL1");

        // fill container with random samples
        Map<Integer, AliquotWrapper> sampleMap = new HashMap<Integer, AliquotWrapper>();
        int rows = container.getRowCapacity().intValue();
        int cols = container.getColCapacity().intValue();
        for (int row = 0; row < rows; ++row) {
            for (int col = 0; col < cols; ++col) {
                if (r.nextGaussian() > 0.0)
                    continue;
                // System.out.println("setting aliquot at: " + row + ", " +
                // col);
                sampleMap.put(row + col * rows, AliquotHelper.addAliquot(
                    allSampleTypes.get(r.nextInt(allSampleTypesCount)),
                    container, visit, row, col));
            }
        }
        visit.reload();

        // verify that all samples are there
        Collection<AliquotWrapper> visitSamples = visit.getAliquotCollection();
        Assert.assertEquals(sampleMap.size(), visitSamples.size());

        for (AliquotWrapper aliquot : visitSamples) {
            RowColPos pos = aliquot.getPosition();
            // System.out.println("getting aliquot from: " + pos.row + ", "
            // + pos.col);
            Assert.assertNotNull(pos);
            Assert.assertNotNull(pos.row);
            Assert.assertNotNull(pos.col);
            Assert.assertNotNull(sampleMap.get(pos.row + pos.col * rows));
            Assert.assertEquals(aliquot,
                sampleMap.get(pos.row + pos.col * rows));
        }

        // delete all samples now
        for (AliquotWrapper aliquot : visitSamples) {
            aliquot.delete();
        }
        visit.reload();
        visitSamples = visit.getAliquotCollection();
        Assert.assertEquals(0, visitSamples.size());
    }

    @Test
    public void testGetSetPvAttrLabels() throws Exception {
        addPvAttrs();
        List<String> labels = Arrays.asList(study.getStudyPvAttrLabels());
        Assert.assertEquals(5, labels.size());

        PatientVisitWrapper visit = PatientVisitHelper.addPatientVisit(patient,
            shipment, TestCommon.getUniqueDate(r), Utils.getRandomDate());
        visit.reload();

        Assert.assertEquals(0, visit.getPvAttrLabels().length);

        visit.setPvAttrValue("PMBC Count", "-0.543");
        visit.setPvAttrValue("Worksheet", "abcdefghi");
        visit.setPvAttrValue("Date", "1999-12-31 23:59");
        visit.setPvAttrValue("Consent", "c1;c2;c3");
        visit.setPvAttrValue("Visit", "v1");
        visit.persist();

        labels = Arrays.asList(visit.getPvAttrLabels());
        Assert.assertEquals(5, labels.size());
        Assert.assertTrue(labels.containsAll(Arrays.asList("PMBC Count",
            "Worksheet", "Date", "Consent", "Visit")));

        // set an invalid label
        try {
            visit.setPvAttrValue("xyz", "abcdef");
            Assert.fail("should not be allowed to assign invalid value");
        } catch (Exception e) {
            Assert.assertTrue(true);
        }
    }

    @Test
    public void testEmptyGetPvAttr() throws Exception {
        PatientVisitWrapper visit = PatientVisitHelper.addPatientVisit(patient,
            shipment, TestCommon.getUniqueDate(r), Utils.getRandomDate());
        visit.reload();
        List<String> pvAttr = Arrays.asList(visit.getPvAttrLabels());
        Assert.assertEquals(0, pvAttr.size());
    }

    @Test
    public void testGetPvAttr() throws Exception {
        addPvAttrs();

        PatientVisitWrapper visit = PatientVisitHelper.addPatientVisit(patient,
            shipment, TestCommon.getUniqueDate(r), Utils.getRandomDate());
        visit.reload();

        // no values have been set yet, they should return null
        Assert.assertEquals(null, visit.getPvAttrValue("PMBC Count"));
        Assert.assertEquals(null, visit.getPvAttrValue("Worksheet"));
        Assert.assertEquals(null, visit.getPvAttrValue("Date"));
        Assert.assertEquals(null, visit.getPvAttrValue("Consent"));
        Assert.assertEquals(null, visit.getPvAttrValue("Visit"));

        // select an invalid PvAttr label
        try {
            visit.getPvAttrValue("abcdef");
            Assert.fail("should not be query an invalid label");
        } catch (Exception e) {
            Assert.assertTrue(true);
        }

        // assign PvAttrs correctly
        String worksheetValue = Utils.getRandomString(10, 20);
        visit.setPvAttrValue("PMBC Count", "-0.543");
        visit.setPvAttrValue("Worksheet", worksheetValue);
        visit.setPvAttrValue("Date", "1999-12-31 23:59");
        visit.setPvAttrValue("Consent", "c1;c2;c3");
        visit.setPvAttrValue("Visit", "v1");
        visit.persist();

        visit.reload();
        List<String> pvAttr = Arrays.asList(visit.getPvAttrLabels());
        Assert.assertEquals(5, pvAttr.size());
        Assert.assertEquals("-0.543", visit.getPvAttrValue("PMBC Count"));
        Assert.assertEquals(worksheetValue, visit.getPvAttrValue("Worksheet"));
        Assert.assertEquals("1999-12-31 23:59", visit.getPvAttrValue("Date"));
        Assert.assertEquals("c1;c2;c3", visit.getPvAttrValue("Consent"));
        Assert.assertEquals("v1", visit.getPvAttrValue("Visit"));

        // select an invalid value for a number PvAttr
        try {
            visit.setPvAttrValue("PMBC Count", "abcdef");
            Assert.fail("should not be allowed to assign invalid value");
        } catch (Exception e) {
            Assert.assertTrue(true);
        }

        // select an invalid value for a date_time PvAttr
        try {
            visit.setPvAttrValue("PMBC Count", "1999-12-31 2300:59");
            Assert.fail("should not be allowed to assign invalid value");
        } catch (Exception e) {
            Assert.assertTrue(true);
        }

        // select an invalid value for a select_multiple PvAttr
        try {
            visit.setPvAttrValue("Consent", "c2;c99");
            Assert.fail("should not be allowed to assign invalid value");
        } catch (Exception e) {
            Assert.assertTrue(true);
        }

        // select an invalid value for a select_single PvAttr
        try {
            visit.setPvAttrValue("Visit", "abcdef");
            Assert.fail("should not be allowed to assign invalid value");
        } catch (Exception e) {
            Assert.assertTrue(true);
        }
    }

    @Test
    public void testGetPvAttrTypeName() throws Exception {
        addPvAttrs();
        List<String> labels = Arrays.asList(study.getStudyPvAttrLabels());
        Assert.assertEquals(5, labels.size());

        PatientVisitWrapper visit = PatientVisitHelper.addPatientVisit(patient,
            shipment, TestCommon.getUniqueDate(r), Utils.getRandomDate());
        visit.reload();

        // get types before they are assigned on patient visit
        Assert.assertEquals("number", visit.getPvAttrTypeName("PMBC Count"));
        Assert.assertEquals("text", visit.getPvAttrTypeName("Worksheet"));
        Assert.assertEquals("date_time", visit.getPvAttrTypeName("Date"));
        Assert.assertEquals("select_multiple",
            visit.getPvAttrTypeName("Consent"));
        Assert.assertEquals("select_single", visit.getPvAttrTypeName("Visit"));

        // select an invalid label
        try {
            visit.getPvAttrTypeName("xyz");
            Assert.fail("should not be allowed get type for invalid label");
        } catch (Exception e) {
            Assert.assertTrue(true);
        }

        visit.setPvAttrValue("PMBC Count", "-0.543");
        visit.setPvAttrValue("Worksheet", "abcdefghi");
        visit.setPvAttrValue("Date", "1999-12-31 23:59");
        visit.setPvAttrValue("Consent", "c1;c2;c3");
        visit.setPvAttrValue("Visit", "v1");
        visit.persist();

        // set value to null
        visit.setPvAttrValue("PMBC Count", null);
        visit.persist();

        // get types after they are assigned on patient visit
        Assert.assertEquals("number", visit.getPvAttrTypeName("PMBC Count"));
        Assert.assertEquals("text", visit.getPvAttrTypeName("Worksheet"));
        Assert.assertEquals("date_time", visit.getPvAttrTypeName("Date"));
        Assert.assertEquals("select_multiple",
            visit.getPvAttrTypeName("Consent"));
        Assert.assertEquals("select_single", visit.getPvAttrTypeName("Visit"));
    }

    @Test
    public void testPvAttrPermissible() throws Exception {
        addPvAttrs();
        List<String> labels = Arrays.asList(study.getStudyPvAttrLabels());
        Assert.assertEquals(5, labels.size());

        PatientVisitWrapper visit = PatientVisitHelper.addPatientVisit(patient,
            shipment, TestCommon.getUniqueDate(r), Utils.getRandomDate());
        visit.reload();

        visit.setPvAttrValue("PMBC Count", "-0.543");
        visit.setPvAttrValue("Worksheet", "abcdefghi");
        visit.setPvAttrValue("Date", "1999-12-31 23:59");
        visit.setPvAttrValue("Consent", "c1;c2;c3");
        visit.setPvAttrValue("Visit", "v1");
        visit.persist();
        visit.reload();

        Assert.assertEquals(null, visit.getPvAttrPermissible("PMBC Count"));
        Assert.assertEquals(null, visit.getPvAttrPermissible("Worksheet"));
        Assert.assertEquals(null, visit.getPvAttrPermissible("Date"));

        List<String> permissibles = Arrays.asList(visit
            .getPvAttrPermissible("Consent"));
        Assert.assertEquals(3, permissibles.size());
        Assert.assertTrue(permissibles.containsAll(Arrays.asList("c1", "c2",
            "c3")));

        permissibles = Arrays.asList(visit.getPvAttrPermissible("Visit"));
        Assert.assertEquals(4, permissibles.size());
        Assert.assertTrue(permissibles.containsAll(Arrays.asList("v1", "v2",
            "v3", "v4")));

        // select an invalid label
        try {
            visit.getPvAttrPermissible("xyz");
            Assert
                .fail("should not be allowed get permissible for invalid label");
        } catch (Exception e) {
            Assert.assertTrue(true);
        }
    }

    @Test
    public void testGetSetPvAttrActivityStatus() throws Exception {
        addPvAttrs();
        List<String> labels = Arrays.asList(study.getStudyPvAttrLabels());
        Assert.assertEquals(5, labels.size());

        PatientVisitWrapper visit = PatientVisitHelper.addPatientVisit(patient,
            shipment, TestCommon.getUniqueDate(r), Utils.getRandomDate());
        visit.reload();

        // lock an attribute
        study.setStudyPvAttrActivityStatus("Worksheet",
            ActivityStatusWrapper.getActivityStatus(appService, "Disabled"));
        study.persist();
        visit.reload();
        try {
            visit.setPvAttrValue("Worksheet", "xyz");
            Assert.fail("should not be allowed set value for locked label");
        } catch (Exception e) {
            Assert.assertTrue(true);
        }

        // unlock the attribute
        study.setStudyPvAttrActivityStatus("Worksheet",
            ActivityStatusWrapper.getActivityStatus(appService, "Active"));
        study.persist();
        visit.reload();
        visit.setPvAttrValue("Worksheet", "xyz");
        visit.persist();
    }

    @Test
    public void testDuplicatePvAttr() throws Exception {
        addPvAttrs();
        List<String> labels = Arrays.asList(study.getStudyPvAttrLabels());
        Assert.assertEquals(5, labels.size());

        PatientVisitWrapper visit = PatientVisitHelper.addPatientVisit(patient,
            shipment, TestCommon.getUniqueDate(r), Utils.getRandomDate());
        visit.reload();

        visit.setPvAttrValue("Worksheet", "abcdefghi");
        visit.persist();

        // change the worksheet value
        visit.setPvAttrValue("Worksheet", "jklmnopqr");
        visit.persist();
        visit.reload();

        // make sure only one value in database
        HQLCriteria c = new HQLCriteria(
            "select pvattr from "
                + PatientVisit.class.getName()
                + " as pv "
                + "join pv.pvAttrCollection as pvattr "
                + "join pvattr.studyPvAttr as spvattr where pv.id = ? and spvattr.label= ?",
            Arrays.asList(new Object[] { visit.getId(), "Worksheet" }));
        List<PvAttr> results = appService.query(c);
        Assert.assertEquals(1, results.size());
    }

    @Test
    public void testGetFormattedDateProcessed() throws Exception {
        Date date = Utils.getRandomDate();
        PatientVisitWrapper visit = PatientVisitHelper.addPatientVisit(patient,
            shipment, date, date);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Assert.assertTrue(sdf.format(date).equals(
            visit.getFormattedDateProcessed()));
    }

    @Test
    public void testGetPatient() throws Exception {
        PatientVisitWrapper visit = PatientVisitHelper.addPatientVisit(patient,
            shipment, TestCommon.getUniqueDate(r), Utils.getRandomDate());
        Assert.assertEquals(patient, visit.getPatient());
    }

    @Test
    public void testGetShipment() throws Exception {
        PatientVisitWrapper visit = PatientVisitHelper.addPatientVisit(patient,
            shipment, TestCommon.getUniqueDate(r), Utils.getRandomDate());
        Assert.assertEquals(shipment, visit.getShipment());

        // check for no shipment
        visit = new PatientVisitWrapper(appService);
        visit.setPatient(patient);
        visit.setDateProcessed(TestCommon.getUniqueDate(r));
        Assert.assertEquals(null, visit.getShipment());
    }

    @Test
    public void testCheckHasShipment() {
        // check valid case
        PatientVisitWrapper visit = PatientVisitHelper.newPatientVisit(patient,
            shipment, TestCommon.getUniqueDate(r), Utils.getRandomDate());

        // check invalid case
        visit = new PatientVisitWrapper(appService);
        visit.setPatient(patient);
        visit.setDateProcessed(TestCommon.getUniqueDate(r));

        try {
            visit.persist();
            Assert
                .fail("should not be allowed to add a visits with no shipment");
        } catch (Exception e) {
            Assert.assertTrue(true);
        }
    }

    @Test
    public void testCheckPatientInShipment() throws Exception {
        // check valid case
        PatientVisitWrapper visit = PatientVisitHelper.newPatientVisit(patient,
            shipment, TestCommon.getUniqueDate(r), Utils.getRandomDate());

        // check invalid case
        PatientWrapper patient2 = PatientHelper.addPatient(
            Utils.getRandomNumericString(20), study);
        visit = PatientVisitHelper.newPatientVisit(patient2, shipment,
            TestCommon.getUniqueDate(r), Utils.getRandomDate());

        try {
            visit.persist();
            Assert
                .fail("should not be allowed to add a visits with patient not in shipment");
        } catch (Exception e) {
            Assert.assertTrue(true);
        }
    }

    @Test
    public void testPersist() throws Exception {
        PatientVisitWrapper pv = PatientVisitHelper.newPatientVisit(patient,
            shipment, DateFormatter.dateFormatter.parse("2009-12-25 00:00"),
            Utils.getRandomDate());
        pv.persist();
    }

    @Test
    public void testAddPvSourceVessels() throws Exception {
        PatientVisitWrapper visit = PatientVisitHelper.addPatientVisit(patient,
            shipment, Utils.getRandomDate(), Utils.getRandomDate());

        PvSourceVesselWrapper ss1, ss2, ss3;

        ss1 = PvSourceVesselHelper.newPvSourceVessel(SourceVesselWrapper
            .getAllSourceVessels(appService).get(0), visit);
        ss2 = PvSourceVesselHelper.newPvSourceVessel(SourceVesselWrapper
            .getAllSourceVessels(appService).get(1), visit);

        visit.addPvSourceVessels(Arrays.asList(ss1, ss2));
        visit.persist();

        visit.reload();
        // get the sorted list
        List<PvSourceVesselWrapper> list = visit
            .getPvSourceVesselCollection(true);
        Assert.assertEquals(2, list.size());
        Assert.assertTrue(list.get(0).compareTo(list.get(1)) < 0);

        ss3 = PvSourceVesselHelper.newPvSourceVessel(SourceVesselWrapper
            .getAllSourceVessels(appService).get(1), visit);

        PvSourceVesselWrapper pvss = list.get(0);
        visit.removePvSourceVessels(Arrays.asList(pvss));
        visit.addPvSourceVessels(Arrays.asList(ss3));

        visit.persist();

        list = visit.getPvSourceVesselCollection();
        Assert.assertEquals(2, list.size());
    }

    @Test
    public void testAddNewSample() throws BiobankCheckException, Exception {
        PatientVisitWrapper visit = PatientVisitHelper.addPatientVisit(patient,
            shipment, Utils.getRandomDate(), Utils.getRandomDate());

        List<SampleTypeWrapper> types = SampleTypeWrapper.getGlobalSampleTypes(
            appService, false);
        SampleStorageWrapper ss1 = SampleStorageHelper.addSampleStorage(study,
            DbHelper.chooseRandomlyInList(types));
        SampleStorageWrapper ss2 = SampleStorageHelper.addSampleStorage(study,
            DbHelper.chooseRandomlyInList(types));
        SampleTypeWrapper sampleType = DbHelper.chooseRandomlyInList(types);
        SampleStorageWrapper ss3 = SampleStorageHelper.newSampleStorage(study,
            sampleType);
        ss3.setVolume(3.0);
        ss3.persist();
        AliquotWrapper newSample = visit.addNewAliquot("newid", sampleType,
            Arrays.asList(ss1, ss2, ss3),
            ActivityStatusWrapper.getActivityStatus(appService, "Active"));
        Aliquot dbSample = ModelUtils.getObjectWithId(appService,
            Aliquot.class, newSample.getId());
        Assert.assertEquals(dbSample.getSampleType().getId(), newSample
            .getSampleType().getId());
        Assert.assertTrue(dbSample.getQuantity().equals(3.0));
    }
}
