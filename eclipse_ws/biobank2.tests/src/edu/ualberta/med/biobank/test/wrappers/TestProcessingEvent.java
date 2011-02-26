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
import edu.ualberta.med.biobank.common.wrappers.AliquotedSpecimenWrapper;
import edu.ualberta.med.biobank.common.wrappers.ClinicWrapper;
import edu.ualberta.med.biobank.common.wrappers.CollectionEventWrapper;
import edu.ualberta.med.biobank.common.wrappers.ContactWrapper;
import edu.ualberta.med.biobank.common.wrappers.ContainerTypeWrapper;
import edu.ualberta.med.biobank.common.wrappers.ContainerWrapper;
import edu.ualberta.med.biobank.common.wrappers.PatientWrapper;
import edu.ualberta.med.biobank.common.wrappers.ProcessingEventWrapper;
import edu.ualberta.med.biobank.common.wrappers.ShippingMethodWrapper;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;
import edu.ualberta.med.biobank.common.wrappers.SourceVesselWrapper;
import edu.ualberta.med.biobank.common.wrappers.SpecimenTypeWrapper;
import edu.ualberta.med.biobank.common.wrappers.SpecimenWrapper;
import edu.ualberta.med.biobank.common.wrappers.StudyWrapper;
import edu.ualberta.med.biobank.common.wrappers.internal.EventAttrTypeWrapper;
import edu.ualberta.med.biobank.model.CollectionEvent;
import edu.ualberta.med.biobank.model.ProcessingEvent;
import edu.ualberta.med.biobank.test.TestDatabase;
import edu.ualberta.med.biobank.test.Utils;
import edu.ualberta.med.biobank.test.internal.AliquotHelper;
import edu.ualberta.med.biobank.test.internal.ClinicHelper;
import edu.ualberta.med.biobank.test.internal.CollectionEventHelper;
import edu.ualberta.med.biobank.test.internal.ContactHelper;
import edu.ualberta.med.biobank.test.internal.ContainerHelper;
import edu.ualberta.med.biobank.test.internal.ContainerTypeHelper;
import edu.ualberta.med.biobank.test.internal.DbHelper;
import edu.ualberta.med.biobank.test.internal.PatientHelper;
import edu.ualberta.med.biobank.test.internal.ProcessingEventHelper;
import edu.ualberta.med.biobank.test.internal.SampleStorageHelper;
import edu.ualberta.med.biobank.test.internal.SiteHelper;
import edu.ualberta.med.biobank.test.internal.SourceVesselHelper;
import edu.ualberta.med.biobank.test.internal.StudyHelper;
import gov.nih.nci.system.query.hibernate.HQLCriteria;

public class TestProcessingEvent extends TestDatabase {

    private Map<String, ContainerWrapper> containerMap;

    private Map<String, ContainerTypeWrapper> containerTypeMap;

    private SiteWrapper site;

    private StudyWrapper study;

    private ClinicWrapper clinic;

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
        childType.addToSampleTypeCollection(allSampleTypes);
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

    private void addPvAttrs() throws Exception {
        // add PvAtt to study
        Collection<String> types = EventAttrTypeWrapper
            .getAllEventAttrTypesMap(appService).keySet();
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
        ProcessingEventWrapper pevent = ProcessingEventHelper
            .addProcessingEvent(site, patient, Utils.getRandomDate(),
                Utils.getRandomDate());
        testGettersAndSetters(pevent, GETTER_SKIP_METHODS);
    }

    @Test
    public void testCompareTo() throws Exception {
        // visit2's date processed is 1 day after visit1's
        Date date = Utils.getRandomDate();
        ProcessingEventWrapper visit1 = ProcessingEventHelper
            .addProcessingEvent(site, patient, date, date);

        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DATE, 1);

        ProcessingEventWrapper visit2 = ProcessingEventHelper
            .addProcessingEvent(site, patient, cal.getTime(), date);

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
        ProcessingEventWrapper pevent = ProcessingEventHelper
            .addProcessingEvent(site, patient, Utils.getRandomDate(),
                Utils.getRandomDate());
        pevent.reset();
    }

    @Test
    public void testReload() throws Exception {
        ProcessingEventWrapper pevent = ProcessingEventHelper
            .addProcessingEvent(site, patient, Utils.getRandomDate(),
                Utils.getRandomDate());
        pevent.reload();
    }

    @Test
    public void testDelete() throws Exception {
        ProcessingEventWrapper pevent = ProcessingEventHelper
            .addProcessingEvent(site, patient, Utils.getRandomDate(),
                Utils.getRandomDate());
        pevent.delete();

        // make sure pevent cannot be deleted if it has samples
        pevent = ProcessingEventHelper.addProcessingEvent(site, patient,
            Utils.getRandomDate(), Utils.getRandomDate());
        addContainerTypes();
        addContainers();
        List<SpecimenTypeWrapper> allSampleTypes = SpecimenTypeWrapper
            .getAllSpecimenTypes(appService, true);
        SpecimenWrapper aliquot = AliquotHelper.addAliquot(
            allSampleTypes.get(0), containerMap.get("ChildL1"), pevent, 0, 0);
        pevent.reload();

        try {
            pevent.delete();
            Assert.fail("should not be allowed to delete Processing Event");
        } catch (Exception e) {
            Assert.assertTrue(true);
        }

        // delete aliquot and pevent
        aliquot.delete();
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
            Assert.fail("one pevent still there");
        } catch (BiobankCheckException bce) {
            Assert.assertTrue(true);
        }

        CollectionEventHelper.addCollectionEvent(site, ShippingMethodWrapper
            .getShippingMethods(appService).get(0), SourceVesselHelper
            .newSourceVessel(patient1, Utils.getRandomDate(), 0.1));

        int countBefore = appService.search(CollectionEvent.class,
            new CollectionEvent()).size();
        ceventTest.reload();
        DbHelper.deleteFromList(ceventTest.getSourceVesselCollection(false));
        ceventTest.delete();
        int countAfter = appService.search(CollectionEvent.class,
            new CollectionEvent()).size();
        Assert.assertEquals(countBefore - 1, countAfter);
    }

    @Test
    public void testGetWrappedClass() throws Exception {
        ProcessingEventWrapper pevent = ProcessingEventHelper
            .addProcessingEvent(site, patient, Utils.getRandomDate(),
                Utils.getRandomDate());
        Assert.assertEquals(ProcessingEvent.class, pevent.getWrappedClass());
    }

    @Test
    public void testGetSampleCollection() throws Exception {
        ProcessingEventWrapper pevent = ProcessingEventHelper
            .addProcessingEvent(site, patient, Utils.getRandomDate(),
                Utils.getRandomDate());
        addContainerTypes();
        addContainers();
        List<SpecimenTypeWrapper> allSampleTypes = SpecimenTypeWrapper
            .getAllSpecimenTypes(appService, true);
        int allSampleTypesCount = allSampleTypes.size();
        ContainerWrapper container = containerMap.get("ChildL1");

        // fill container with random samples
        Map<Integer, SpecimenWrapper> sampleMap = new HashMap<Integer, SpecimenWrapper>();
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
                    container, pevent, row, col));
            }
        }
        pevent.reload();

        // verify that all samples are there
        Collection<SpecimenWrapper> visitSamples = pevent
            .getSpecimenCollection(false);
        Assert.assertEquals(sampleMap.size(), visitSamples.size());

        for (SpecimenWrapper aliquot : visitSamples) {
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
        for (SpecimenWrapper aliquot : visitSamples) {
            aliquot.delete();
        }
        pevent.reload();
        visitSamples = pevent.getSpecimenCollection(false);
        Assert.assertEquals(0, visitSamples.size());
    }

    @Test
    public void testGetSetPvAttrLabels() throws Exception {
        addPvAttrs();
        List<String> labels = Arrays.asList(study.getStudyPvAttrLabels());
        Assert.assertEquals(5, labels.size());

        ProcessingEventWrapper pevent = ProcessingEventHelper
            .addProcessingEvent(site, patient, TestCommon.getUniqueDate(r),
                Utils.getRandomDate());
        pevent.reload();

        Assert.assertEquals(0, pevent.getPvAttrLabels().length);

        pevent.setPvAttrValue("PMBC Count", "-0.543");
        pevent.setPvAttrValue("Worksheet", "abcdefghi");
        pevent.setPvAttrValue("Date", "1999-12-31 23:59");
        pevent.setPvAttrValue("Consent", "c1;c2;c3");
        pevent.setPvAttrValue("Visit", "v1");
        pevent.persist();

        labels = Arrays.asList(pevent.getPvAttrLabels());
        Assert.assertEquals(5, labels.size());
        Assert.assertTrue(labels.containsAll(Arrays.asList("PMBC Count",
            "Worksheet", "Date", "Consent", "Visit")));

        // set an invalid label
        try {
            pevent.setPvAttrValue("xyz", "abcdef");
            Assert.fail("should not be allowed to assign invalid value");
        } catch (Exception e) {
            Assert.assertTrue(true);
        }
    }

    @Test
    public void testEmptyGetPvAttr() throws Exception {
        ProcessingEventWrapper pevent = ProcessingEventHelper
            .addProcessingEvent(site, patient, TestCommon.getUniqueDate(r),
                Utils.getRandomDate());
        pevent.reload();
        List<String> pvAttr = Arrays.asList(pevent.getPvAttrLabels());
        Assert.assertEquals(0, pvAttr.size());
    }

    @Test
    public void testGetPvAttr() throws Exception {
        addPvAttrs();

        ProcessingEventWrapper pevent = ProcessingEventHelper
            .addProcessingEvent(site, patient, TestCommon.getUniqueDate(r),
                Utils.getRandomDate());
        pevent.reload();

        // no values have been set yet, they should return null
        Assert.assertEquals(null, pevent.getPvAttrValue("PMBC Count"));
        Assert.assertEquals(null, pevent.getPvAttrValue("Worksheet"));
        Assert.assertEquals(null, pevent.getPvAttrValue("Date"));
        Assert.assertEquals(null, pevent.getPvAttrValue("Consent"));
        Assert.assertEquals(null, pevent.getPvAttrValue("Visit"));

        // select an invalid PvAttr label
        try {
            pevent.getPvAttrValue("abcdef");
            Assert.fail("should not be query an invalid label");
        } catch (Exception e) {
            Assert.assertTrue(true);
        }

        // assign PvAttrs correctly
        String worksheetValue = Utils.getRandomString(10, 20);
        pevent.setPvAttrValue("PMBC Count", "-0.543");
        pevent.setPvAttrValue("Worksheet", worksheetValue);
        pevent.setPvAttrValue("Date", "1999-12-31 23:59");
        pevent.setPvAttrValue("Consent", "c1;c2;c3");
        pevent.setPvAttrValue("Visit", "v1");
        pevent.persist();

        pevent.reload();
        List<String> pvAttr = Arrays.asList(pevent.getPvAttrLabels());
        Assert.assertEquals(5, pvAttr.size());
        Assert.assertEquals("-0.543", pevent.getPvAttrValue("PMBC Count"));
        Assert.assertEquals(worksheetValue, pevent.getPvAttrValue("Worksheet"));
        Assert.assertEquals("1999-12-31 23:59", pevent.getPvAttrValue("Date"));
        Assert.assertEquals("c1;c2;c3", pevent.getPvAttrValue("Consent"));
        Assert.assertEquals("v1", pevent.getPvAttrValue("Visit"));

        // select an invalid value for a number PvAttr
        try {
            pevent.setPvAttrValue("PMBC Count", "abcdef");
            Assert.fail("should not be allowed to assign invalid value");
        } catch (Exception e) {
            Assert.assertTrue(true);
        }

        // select an invalid value for a date_time PvAttr
        try {
            pevent.setPvAttrValue("PMBC Count", "1999-12-31 2300:59");
            Assert.fail("should not be allowed to assign invalid value");
        } catch (Exception e) {
            Assert.assertTrue(true);
        }

        // select an invalid value for a select_multiple PvAttr
        try {
            pevent.setPvAttrValue("Consent", "c2;c99");
            Assert.fail("should not be allowed to assign invalid value");
        } catch (Exception e) {
            Assert.assertTrue(true);
        }

        // select an invalid value for a select_single PvAttr
        try {
            pevent.setPvAttrValue("Visit", "abcdef");
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

        ProcessingEventWrapper pevent = ProcessingEventHelper
            .addProcessingEvent(site, patient, TestCommon.getUniqueDate(r),
                Utils.getRandomDate());
        pevent.reload();

        // get types before they are assigned on Processing Event
        Assert.assertEquals("number", pevent.getPvAttrTypeName("PMBC Count"));
        Assert.assertEquals("text", pevent.getPvAttrTypeName("Worksheet"));
        Assert.assertEquals("date_time", pevent.getPvAttrTypeName("Date"));
        Assert.assertEquals("select_multiple",
            pevent.getPvAttrTypeName("Consent"));
        Assert.assertEquals("select_single", pevent.getPvAttrTypeName("Visit"));

        // select an invalid label
        try {
            pevent.getPvAttrTypeName("xyz");
            Assert.fail("should not be allowed get type for invalid label");
        } catch (Exception e) {
            Assert.assertTrue(true);
        }

        pevent.setPvAttrValue("PMBC Count", "-0.543");
        pevent.setPvAttrValue("Worksheet", "abcdefghi");
        pevent.setPvAttrValue("Date", "1999-12-31 23:59");
        pevent.setPvAttrValue("Consent", "c1;c2;c3");
        pevent.setPvAttrValue("Visit", "v1");
        pevent.persist();

        // set value to null
        pevent.setPvAttrValue("PMBC Count", null);
        pevent.persist();

        // get types after they are assigned on Processing Event
        Assert.assertEquals("number", pevent.getPvAttrTypeName("PMBC Count"));
        Assert.assertEquals("text", pevent.getPvAttrTypeName("Worksheet"));
        Assert.assertEquals("date_time", pevent.getPvAttrTypeName("Date"));
        Assert.assertEquals("select_multiple",
            pevent.getPvAttrTypeName("Consent"));
        Assert.assertEquals("select_single", pevent.getPvAttrTypeName("Visit"));
    }

    @Test
    public void testPvAttrPermissible() throws Exception {
        addPvAttrs();
        List<String> labels = Arrays.asList(study.getStudyPvAttrLabels());
        Assert.assertEquals(5, labels.size());

        ProcessingEventWrapper pevent = ProcessingEventHelper
            .addProcessingEvent(site, patient, TestCommon.getUniqueDate(r),
                Utils.getRandomDate());
        pevent.reload();

        pevent.setPvAttrValue("PMBC Count", "-0.543");
        pevent.setPvAttrValue("Worksheet", "abcdefghi");
        pevent.setPvAttrValue("Date", "1999-12-31 23:59");
        pevent.setPvAttrValue("Consent", "c1;c2;c3");
        pevent.setPvAttrValue("Visit", "v1");
        pevent.persist();
        pevent.reload();

        Assert.assertEquals(null, pevent.getPvAttrPermissible("PMBC Count"));
        Assert.assertEquals(null, pevent.getPvAttrPermissible("Worksheet"));
        Assert.assertEquals(null, pevent.getPvAttrPermissible("Date"));

        List<String> permissibles = Arrays.asList(pevent
            .getPvAttrPermissible("Consent"));
        Assert.assertEquals(3, permissibles.size());
        Assert.assertTrue(permissibles.containsAll(Arrays.asList("c1", "c2",
            "c3")));

        permissibles = Arrays.asList(pevent.getPvAttrPermissible("Visit"));
        Assert.assertEquals(4, permissibles.size());
        Assert.assertTrue(permissibles.containsAll(Arrays.asList("v1", "v2",
            "v3", "v4")));

        // select an invalid label
        try {
            pevent.getPvAttrPermissible("xyz");
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

        ProcessingEventWrapper pevent = ProcessingEventHelper
            .addProcessingEvent(site, patient, TestCommon.getUniqueDate(r),
                Utils.getRandomDate());
        pevent.reload();

        // lock an attribute
        study.setStudyPvAttrActivityStatus("Worksheet", ActivityStatusWrapper
            .getActivityStatus(appService,
                ActivityStatusWrapper.CLOSED_STATUS_STRING));
        study.persist();
        pevent.reload();
        try {
            pevent.setPvAttrValue("Worksheet", "xyz");
            Assert.fail("should not be allowed set value for locked label");
        } catch (Exception e) {
            Assert.assertTrue(true);
        }

        // unlock the attribute
        study.setStudyPvAttrActivityStatus("Worksheet",
            ActivityStatusWrapper.getActiveActivityStatus(appService));
        study.persist();
        pevent.reload();
        pevent.setPvAttrValue("Worksheet", "xyz");
        pevent.persist();
    }

    @Test
    public void testDuplicatePvAttr() throws Exception {
        addPvAttrs();
        List<String> labels = Arrays.asList(study.getStudyPvAttrLabels());
        Assert.assertEquals(5, labels.size());

        ProcessingEventWrapper pevent = ProcessingEventHelper
            .addProcessingEvent(site, patient, TestCommon.getUniqueDate(r),
                Utils.getRandomDate());
        pevent.reload();

        pevent.setPvAttrValue("Worksheet", "abcdefghi");
        pevent.persist();

        // change the worksheet value
        pevent.setPvAttrValue("Worksheet", "jklmnopqr");
        pevent.persist();
        pevent.reload();

        // make sure only one value in database
        HQLCriteria c = new HQLCriteria(
            "select pvattr from "
                + ProcessingEvent.class.getName()
                + " as pv "
                + "join pv.pvAttrCollection as pvattr "
                + "join pvattr.studyPvAttr as spvattr where pv.id = ? and spvattr.label= ?",
            Arrays.asList(new Object[] { pevent.getId(), "Worksheet" }));
        List<PvAttr> results = appService.query(c);
        Assert.assertEquals(1, results.size());
    }

    @Test
    public void testGetFormattedDateProcessed() throws Exception {
        Date date = Utils.getRandomDate();
        ProcessingEventWrapper pevent = ProcessingEventHelper
            .addProcessingEvent(site, patient, date, date);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Assert.assertTrue(sdf.format(date).equals(
            pevent.getFormattedDateProcessed()));
    }

    @Test
    public void testGetPatient() throws Exception {
        ProcessingEventWrapper pevent = ProcessingEventHelper
            .addProcessingEvent(site, patient, TestCommon.getUniqueDate(r),
                Utils.getRandomDate());
        Assert.assertEquals(patient, pevent.getPatient());
    }

    @Test
    public void testCheckHasCollectionEvent() throws Exception {
        // check valid case
        ProcessingEventWrapper pevent = ProcessingEventHelper
            .newProcessingEvent(site, patient, TestCommon.getUniqueDate(r),
                Utils.getRandomDate());

        // check invalid case
        pevent = new ProcessingEventWrapper(appService);
        pevent.setPatient(patient);
        pevent.setDateProcessed(TestCommon.getUniqueDate(r));

        try {
            pevent.persist();
            Assert
                .fail("should not be allowed to add a visits with no shipment");
        } catch (Exception e) {
            Assert.assertTrue(true);
        }
    }

    @Test
    public void testPersist() throws Exception {
        ProcessingEventWrapper pv = ProcessingEventHelper.newProcessingEvent(
            site, patient,
            DateFormatter.dateFormatter.parse("2009-12-25 00:00"),
            Utils.getRandomDate());
        pv.persist();
    }

    @Test
    public void testAddSourceVessels() throws Exception {
        ProcessingEventWrapper pevent = ProcessingEventHelper
            .addProcessingEvent(site, patient, Utils.getRandomDate(),
                Utils.getRandomDate());

        SourceVesselWrapper sv1, sv2, sv3;

        sv1 = SourceVesselHelper.newSourceVessel(pevent.getPatient(),
            Utils.getRandomDate(), 0.01);
        sv2 = SourceVesselHelper.newSourceVessel(pevent.getPatient(),
            Utils.getRandomDate(), 0.01);

        CollectionEventWrapper cevent = CollectionEventHelper
            .addCollectionEvent(site,
                ShippingMethodWrapper.getShippingMethods(appService).get(0),
                sv1, sv2);

        pevent.addToSourceVesselCollection(Arrays.asList(sv1, sv2));
        pevent.persist();

        for (SourceVesselWrapper sv : Arrays.asList(sv1, sv2)) {
            sv.setCollectionEvent(cevent);
            sv.setProcessingEvent(pevent);
            sv.persist();
        }

        pevent.reload();
        // get the sorted list
        List<SourceVesselWrapper> list = pevent.getSourceVesselCollection(true);
        Assert.assertEquals(2, list.size());
        Assert.assertTrue(list.get(0).compareTo(list.get(1)) < 0);

        sv3 = SourceVesselHelper.newSourceVessel(pevent.getPatient(),
            Utils.getRandomDate(), 0.01);

        SourceVesselWrapper pvss = list.get(0);
        pevent.removeFromSourceVesselCollection(Arrays.asList(pvss));
        pevent.addToSourceVesselCollection(Arrays.asList(sv3));

        pevent.persist();

        list = pevent.getSourceVesselCollection(false);
        Assert.assertEquals(2, list.size());
    }

    @Test
    public void testAddAliquot() throws BiobankCheckException, Exception {
        ProcessingEventWrapper pevent = ProcessingEventHelper
            .addProcessingEvent(site, patient, Utils.getRandomDate(),
                Utils.getRandomDate());

        List<SpecimenTypeWrapper> types = SpecimenTypeWrapper
            .getAllSpecimenTypes(appService, false);
        SampleStorageHelper.addSampleStorage(study,
            DbHelper.chooseRandomlyInList(types));
        SampleStorageHelper.addSampleStorage(study,
            DbHelper.chooseRandomlyInList(types));
        SpecimenTypeWrapper sampleType = DbHelper.chooseRandomlyInList(types);
        AliquotedSpecimenWrapper ss3 = SampleStorageHelper.newSampleStorage(
            study, sampleType);
        ss3.setVolume(3.0);
        ss3.persist();
        pevent.reload();

        String inventoryId = "newid";
        SpecimenWrapper newAliquot = new SpecimenWrapper(appService);
        newAliquot.setInventoryId(inventoryId);
        newAliquot.setLinkDate(new Date());
        newAliquot.setSpecimenType(sampleType);
        newAliquot.setActivityStatus(ActivityStatusWrapper
            .getActiveActivityStatus(appService));
        pevent.addChildSpecimens(Arrays.asList(newAliquot));
        pevent.persist();

        SpecimenWrapper aliquot = SpecimenWrapper.getSpecimen(appService,
            inventoryId, null);
        Assert.assertNotNull(aliquot);
        Assert.assertEquals(aliquot.getSpecimenType().getId(), newAliquot
            .getSpecimenType().getId());
        Assert.assertTrue(aliquot.getQuantity().equals(3.0));
        Assert.assertEquals(aliquot.getProcessingEvent().getId(),
            pevent.getId());
    }
}
