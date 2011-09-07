package edu.ualberta.med.biobank.test.wrappers;

import java.util.ArrayList;
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
import edu.ualberta.med.biobank.common.wrappers.ClinicWrapper;
import edu.ualberta.med.biobank.common.wrappers.ContactWrapper;
import edu.ualberta.med.biobank.common.wrappers.ContainerTypeWrapper;
import edu.ualberta.med.biobank.common.wrappers.ContainerWrapper;
import edu.ualberta.med.biobank.common.wrappers.PatientWrapper;
import edu.ualberta.med.biobank.common.wrappers.ProcessingEventWrapper;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;
import edu.ualberta.med.biobank.common.wrappers.SpecimenTypeWrapper;
import edu.ualberta.med.biobank.common.wrappers.SpecimenWrapper;
import edu.ualberta.med.biobank.common.wrappers.StudyWrapper;
import edu.ualberta.med.biobank.model.ProcessingEvent;
import edu.ualberta.med.biobank.server.applicationservice.exceptions.ModelIsUsedException;
import edu.ualberta.med.biobank.test.TestDatabase;
import edu.ualberta.med.biobank.test.Utils;
import edu.ualberta.med.biobank.test.internal.ClinicHelper;
import edu.ualberta.med.biobank.test.internal.ContactHelper;
import edu.ualberta.med.biobank.test.internal.ContainerHelper;
import edu.ualberta.med.biobank.test.internal.ContainerTypeHelper;
import edu.ualberta.med.biobank.test.internal.DbHelper;
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
            TestCommon.getNewBarcode(r), site, containerTypeMap.get("TopCT"));
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

        List<SpecimenTypeWrapper> allSpcTypes = SpecimenTypeWrapper
            .getAllSpecimenTypes(appService, true);

        // make sure pevent cannot be deleted if it has samples
        SpecimenWrapper parentSpc = SpecimenHelper.addParentSpecimen();
        SpecimenWrapper childSpc = SpecimenHelper.addSpecimens(site, parentSpc,
            1, allSpcTypes).get(0);
        parentSpc.reload();
        pevent = parentSpc.getProcessingEvent();

        try {
            pevent.delete();
            Assert
                .fail("should not be allowed to delete Processing Event since it is associated with specimens");
        } catch (Exception e) {
            Assert.assertTrue(true);
        }

        // delete aliquot and pevent
        childSpc.reload();
        childSpc.delete();

        // must delete the parent specimen too as it is associated with the
        // processing event
        parentSpc.delete();

        pevent.reload();
        pevent.delete();
    }

    @Test
    public void testDeleteNoMoreSpecimens() throws Exception {
        String name = "testDeleteNoMoreSpecimens" + r.nextInt();
        SiteWrapper site = SiteHelper.addSite("site" + name);

        List<SpecimenTypeWrapper> allSpcTypes = SpecimenTypeWrapper
            .getAllSpecimenTypes(appService, true);
        SpecimenWrapper parentSpc = SpecimenHelper.addParentSpecimen();
        SpecimenWrapper childSpc = SpecimenHelper.addSpecimens(site, parentSpc,
            1, allSpcTypes).get(0);
        parentSpc.reload();
        ProcessingEventWrapper pevent = parentSpc.getProcessingEvent();

        try {
            pevent.delete();
            Assert
                .fail("should not be able to delete pevent with one or more specimens");
        } catch (ModelIsUsedException e) {
            Assert.assertTrue(true);
        }

        pevent.removeFromSpecimenCollection(Arrays.asList(childSpc));
        childSpc.delete();
        pevent.reload();
        pevent.persist();

        // must delete the parent specimen too as it is associated with the
        // processing event
        parentSpc.delete();

        // should be allowed to delete processing event
        pevent.delete();
    }

    @Test
    public void testGetWrappedClass() throws Exception {
        ProcessingEventWrapper pevent = ProcessingEventHelper
            .addProcessingEvent(site, patient, Utils.getRandomDate());
        Assert.assertEquals(ProcessingEvent.class, pevent.getWrappedClass());
    }

    @Test
    public void testGetSampleCollection() throws Exception {
        SpecimenWrapper parentSpc = SpecimenHelper.addParentSpecimen();

        ProcessingEventWrapper pevent = ProcessingEventHelper
            .addProcessingEvent(site, patient, Utils.getRandomDate());
        pevent.addToSpecimenCollection(Arrays.asList(parentSpc));
        pevent.persist();

        addContainerTypes();
        addContainers();
        List<SpecimenTypeWrapper> allSpcTypes = SpecimenTypeWrapper
            .getAllSpecimenTypes(appService, true);
        ContainerWrapper container = containerMap.get("ChildL1");

        // fill container with random samples
        Map<Integer, SpecimenWrapper> spcMap = new HashMap<Integer, SpecimenWrapper>();
        int rows = container.getRowCapacity().intValue();
        int cols = container.getColCapacity().intValue();
        for (int row = 0; row < rows; ++row) {
            for (int col = 0; col < cols; ++col) {
                if (r.nextGaussian() > 0.0)
                    continue;
                // System.out.println("setting aliquot at: " + row + ", " +
                // col);
                spcMap.put(row + (col * rows), SpecimenHelper.addSpecimen(
                    parentSpc, DbHelper.chooseRandomlyInList(allSpcTypes),
                    pevent, container, row, col));
            }
        }
        pevent.reload();

        // verify that all specimens are there
        Collection<SpecimenWrapper> peventSpcs = pevent
            .getSpecimenCollection(false);
        // add one because the map doesn't hold the parent specimen
        Assert.assertEquals(spcMap.size() + 1, peventSpcs.size());

        for (SpecimenWrapper spc : spcMap.values()) {
            RowColPos pos = spc.getPosition();
            // System.out.println("getting aliquot from: " + pos.row + ", "
            // + pos.col);
            Assert.assertNotNull(pos);
            Assert.assertNotNull(pos.getCol());
            Assert.assertNotNull(pos.getRow());
            Assert.assertEquals(spc,
                spcMap.get(pos.getRow() + (pos.getCol() * rows)));
        }

        // delete all samples now (children before parents)
        for (SpecimenWrapper aliquot : spcMap.values()) {
            aliquot.delete();
        }
        parentSpc.delete();

        pevent.reload();
        peventSpcs = pevent.getSpecimenCollection(false);
        Assert.assertEquals(0, peventSpcs.size());
    }

    @Test
    public void testPersist() throws Exception {
        ProcessingEventWrapper pv = ProcessingEventHelper.newProcessingEvent(
            site, patient,
            DateFormatter.dateFormatter.parse("2009-12-25 00:00"));
        pv.persist();
    }

    @Test
    public void testAddSpecimens() throws BiobankCheckException, Exception {
        SpecimenWrapper parentSpc = SpecimenHelper.addParentSpecimen();

        ProcessingEventWrapper pevent = ProcessingEventHelper
            .addProcessingEvent(site, patient, Utils.getRandomDate());
        pevent.addToSpecimenCollection(Arrays.asList(parentSpc));
        pevent.persist();

        addContainerTypes();
        addContainers();
        List<SpecimenTypeWrapper> allSpcTypes = SpecimenTypeWrapper
            .getAllSpecimenTypes(appService, true);
        ContainerWrapper container = containerMap.get("ChildL1");

        try {
            // fill container with random samples
            Map<Integer, SpecimenWrapper> spcMap = new HashMap<Integer, SpecimenWrapper>();
            int rows = container.getRowCapacity().intValue();
            int cols = container.getColCapacity().intValue();
            for (int row = 0; row < rows; ++row) {
                for (int col = 0; col < cols; ++col) {
                    // TODO: uncomment following
                    // if (r.nextGaussian() > 0.0)
                    // continue;
                    // System.out.println("setting aliquot at: " + row + ", " +
                    // col);
                    spcMap.put(row + (col * rows), SpecimenHelper.addSpecimen(
                        parentSpc, DbHelper.chooseRandomlyInList(allSpcTypes),
                        pevent, container, row, col));
                }
            }
            pevent.reload();

            for (SpecimenWrapper spc : spcMap.values()) {
                Assert.assertEquals(spc.getProcessingEvent().getId(),
                    pevent.getId());
            }

            // delete all samples now (children before parents)
            for (SpecimenWrapper aliquot : spcMap.values()) {
                aliquot.delete();
            }
            parentSpc.delete();
        } catch (Exception e) {
            System.out.println("oops!");
            System.out.println(e.getStackTrace());
        }
    }

    @Test
    public void testSpecimenCounts() throws Exception {
        List<SpecimenWrapper> parentSpcs = new ArrayList<SpecimenWrapper>();
        List<ProcessingEventWrapper> pevents = new ArrayList<ProcessingEventWrapper>();
        List<SpecimenTypeWrapper> allSpcTypes = SpecimenTypeWrapper
            .getAllSpecimenTypes(appService, true);

        final int NUM_PARENTS = 3;
        final int NUM_CHILDREN = 4;

        // create parents
        for (int i = 0; i < NUM_PARENTS; i++) {
            SpecimenWrapper parentSpc = SpecimenHelper.addParentSpecimen();
            parentSpcs.add(parentSpc);

            ProcessingEventWrapper pevent = ProcessingEventHelper
                .addProcessingEvent(site, patient, Utils.getRandomDate());
            pevents.add(pevent);

            // create children
            for (int j = 0; j < NUM_CHILDREN; j++) {
                SpecimenHelper.addSpecimen(parentSpc,
                    allSpcTypes.get(j % allSpcTypes.size()), pevent);
            }
        }

        for (ProcessingEventWrapper pevent : pevents) {
            long count = pevent.getSpecimenCount(true);
            Assert.assertTrue(count == NUM_CHILDREN);

            count = pevent.getSpecimenCount(false);
            Assert.assertTrue(count == NUM_CHILDREN);
        }
    }
}
