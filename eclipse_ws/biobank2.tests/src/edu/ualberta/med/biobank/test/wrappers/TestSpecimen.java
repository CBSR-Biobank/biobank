package edu.ualberta.med.biobank.test.wrappers;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import edu.ualberta.med.biobank.common.debug.DebugUtil;
import edu.ualberta.med.biobank.common.exception.BiobankCheckException;
import edu.ualberta.med.biobank.common.exception.DuplicateEntryException;
import edu.ualberta.med.biobank.common.util.DispatchSpecimenState;
import edu.ualberta.med.biobank.common.util.DispatchState;
import edu.ualberta.med.biobank.common.util.RowColPos;
import edu.ualberta.med.biobank.common.wrappers.ActivityStatusWrapper;
import edu.ualberta.med.biobank.common.wrappers.AliquotedSpecimenWrapper;
import edu.ualberta.med.biobank.common.wrappers.ClinicWrapper;
import edu.ualberta.med.biobank.common.wrappers.CollectionEventWrapper;
import edu.ualberta.med.biobank.common.wrappers.ContainerTypeWrapper;
import edu.ualberta.med.biobank.common.wrappers.ContainerWrapper;
import edu.ualberta.med.biobank.common.wrappers.DispatchWrapper;
import edu.ualberta.med.biobank.common.wrappers.OriginInfoWrapper;
import edu.ualberta.med.biobank.common.wrappers.PatientWrapper;
import edu.ualberta.med.biobank.common.wrappers.ProcessingEventWrapper;
import edu.ualberta.med.biobank.common.wrappers.ShippingMethodWrapper;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;
import edu.ualberta.med.biobank.common.wrappers.SpecimenTypeWrapper;
import edu.ualberta.med.biobank.common.wrappers.SpecimenWrapper;
import edu.ualberta.med.biobank.common.wrappers.StudyWrapper;
import edu.ualberta.med.biobank.server.applicationservice.exceptions.ValueNotSetException;
import edu.ualberta.med.biobank.test.TestDatabase;
import edu.ualberta.med.biobank.test.Utils;
import edu.ualberta.med.biobank.test.internal.CollectionEventHelper;
import edu.ualberta.med.biobank.test.internal.ContainerHelper;
import edu.ualberta.med.biobank.test.internal.ContainerTypeHelper;
import edu.ualberta.med.biobank.test.internal.DbHelper;
import edu.ualberta.med.biobank.test.internal.DispatchHelper;
import edu.ualberta.med.biobank.test.internal.PatientHelper;
import edu.ualberta.med.biobank.test.internal.ProcessingEventHelper;
import edu.ualberta.med.biobank.test.internal.SiteHelper;
import edu.ualberta.med.biobank.test.internal.SpecimenHelper;
import edu.ualberta.med.biobank.test.internal.SpecimenTypeHelper;
import edu.ualberta.med.biobank.test.internal.StudyHelper;

public class TestSpecimen extends TestDatabase {

    private SpecimenWrapper parentSpc;

    private SpecimenWrapper childSpc;

    private SiteWrapper site;

    private ContainerWrapper topContainer;

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();

        parentSpc = SpecimenHelper.addParentSpecimen();
        site = (SiteWrapper) parentSpc.getOriginInfo().getCenter();
        ContainerTypeWrapper typeChild = ContainerTypeHelper.addContainerType(
            site, "ctTypeChild" + r.nextInt(), "ctChild", 1, 4, 5, false);
        typeChild.addToSpecimenTypeCollection(Arrays.asList(parentSpc
            .getSpecimenType()));
        typeChild.persist();

        ContainerTypeWrapper topType = ContainerTypeHelper.addContainerType(
            site, "topType" + r.nextInt(), "ct", 1, 4, 5, true);
        topType.addToChildContainerTypeCollection(Arrays.asList(typeChild));
        topType.persist();

        topContainer = ContainerHelper.addContainer("top" + r.nextInt(), "cc",
            null, site, topType);

        ContainerWrapper container = ContainerHelper.addContainer(null, "2nd",
            topContainer, site, typeChild, 3, 3);

        childSpc = SpecimenHelper.addSpecimens(
            parentSpc.getCollectionEvent().getPatient(),
            (ClinicWrapper) parentSpc.getOriginInfo().getCenter(), container,
            0, 0, 1).get(0);
    }

    @Test
    public void testGettersAndSetters() throws Exception {
        testGettersAndSetters(parentSpc);
    }

    @Test
    public void testPersistFailActivityStatusNull() throws Exception {
        parentSpc.setActivityStatus(null);
        try {
            parentSpc.persist();
            Assert.fail("Should not insert the specimen : no activity status");
        } catch (ValueNotSetException vnse) {
            Assert.assertTrue(true);
        }
        parentSpc.setActivityStatus(ActivityStatusWrapper
            .getActiveActivityStatus(appService));
        parentSpc.persist();
    }

    @Test
    public void testCheckInventoryIdUnique() throws BiobankCheckException,
        Exception {
        SpecimenWrapper duplicate = SpecimenHelper.newSpecimen(parentSpc,
            childSpc.getSpecimenType(),
            ActivityStatusWrapper.ACTIVE_STATUS_STRING,
            parentSpc.getCollectionEvent(), childSpc.getProcessingEvent(),
            childSpc.getParentContainer(), 2, 2);

        duplicate.setInventoryId(parentSpc.getInventoryId());
        try {
            duplicate.checkInventoryIdUnique();
            Assert.fail("The check should detect that this is the same");
        } catch (DuplicateEntryException e) {
            Assert.assertTrue(true);
        }
    }

    @Test
    public void testCheckInventoryIdUniqueCaseSensitive()
        throws BiobankCheckException, Exception {
        int i = r.nextInt();
        parentSpc.setInventoryId("toto" + i);
        parentSpc.persist();
        SpecimenWrapper duplicate = SpecimenHelper.newSpecimen(parentSpc,
            childSpc.getSpecimenType(),
            ActivityStatusWrapper.ACTIVE_STATUS_STRING,
            parentSpc.getCollectionEvent(), childSpc.getProcessingEvent(),
            childSpc.getParentContainer(), 2, 2);

        duplicate.setInventoryId("TOTO" + i);
        try {
            duplicate.checkInventoryIdUnique();
            Assert.assertTrue(true);
        } catch (BiobankCheckException bce) {
            Assert.fail("InventoryId is case sensitive. Should not fail");
        }
    }

    @Test
    public void testPersistCheckInventoryIdUnique()
        throws BiobankCheckException, Exception {

        SpecimenWrapper duplicate = SpecimenHelper.newSpecimen(parentSpc,
            childSpc.getSpecimenType(),
            ActivityStatusWrapper.ACTIVE_STATUS_STRING,
            parentSpc.getCollectionEvent(), childSpc.getProcessingEvent(),
            childSpc.getParentContainer(), 2, 2);
        duplicate.setInventoryId(parentSpc.getInventoryId());

        try {
            duplicate.persist();
            Assert.fail("same inventory id !");
        } catch (DuplicateEntryException dee) {
            Assert.assertTrue(true);
        }
        duplicate.setInventoryId("qqqq" + r.nextInt());
        duplicate.persist();

        duplicate.setInventoryId(parentSpc.getInventoryId());
        try {
            duplicate.persist();
            Assert
                .fail("still can't save it with  the same inventoryId after a first add with anotehr inventoryId!");
        } catch (DuplicateEntryException dee) {
            Assert.assertTrue(true);
        }
    }

    @Test
    public void testPersistCheckInventoryIdUniqueCaseSensitive()
        throws BiobankCheckException, Exception {
        int i = r.nextInt();
        parentSpc.setInventoryId("toto" + i);
        parentSpc.persist();

        SpecimenWrapper duplicate = SpecimenHelper.newSpecimen(parentSpc,
            childSpc.getSpecimenType(),
            ActivityStatusWrapper.ACTIVE_STATUS_STRING,
            parentSpc.getCollectionEvent(), childSpc.getProcessingEvent(),
            childSpc.getParentContainer(), 2, 2);
        duplicate.setInventoryId("toto" + i);

        try {
            duplicate.persist();
            Assert.fail("same inventory id !");
        } catch (DuplicateEntryException dee) {
            Assert.assertTrue(true);
        }

        duplicate.setInventoryId("TOTO" + r.nextInt());
        duplicate.persist();
    }

    @Test
    public void testPersistPositionAlreadyUsed() throws BiobankCheckException,
        Exception {
        parentSpc.persist();
        RowColPos pos = parentSpc.getPosition();

        SpecimenWrapper duplicate = SpecimenHelper.newSpecimen(parentSpc,
            childSpc.getSpecimenType(),
            ActivityStatusWrapper.ACTIVE_STATUS_STRING,
            parentSpc.getCollectionEvent(), childSpc.getProcessingEvent(),
            childSpc.getParentContainer(), pos.row, pos.col);

        try {
            duplicate.persist();
            Assert
                .fail("should not be allowed to add an specimen in a position that is not empty");
        } catch (BiobankCheckException bce) {
            Assert.assertTrue(true);
        }

        duplicate.setPosition(new RowColPos(2, 3));
        duplicate.persist();

        duplicate.setInventoryId(Utils.getRandomString(5));
        duplicate.persist();
    }

    @Test
    public void testPersistCheckParentAcceptSpecimenType()
        throws BiobankCheckException, Exception {
        SpecimenTypeWrapper oldSpecimenType = parentSpc.getSpecimenType();

        SpecimenTypeWrapper type2 = SpecimenTypeHelper
            .addSpecimenType("sampletype_2");
        parentSpc.setSpecimenType(type2);
        try {
            parentSpc.persist();
            Assert.fail("Container can't hold this type !");
        } catch (BiobankCheckException bce) {
            Assert.assertTrue(true);
        }

        parentSpc.setSpecimenType(oldSpecimenType);
        parentSpc.persist();

        ContainerWrapper container = new ContainerWrapper(appService);
        SpecimenWrapper specimen = new SpecimenWrapper(appService);
        specimen.setParent(container);
        try {
            specimen.persist();
            Assert.fail("container has no container type");
        } catch (BiobankCheckException bce) {
            Assert.assertTrue(true);
        }
    }

    @Test
    public void testCheckProcessingEventNotNull() throws BiobankCheckException,
        Exception {
        parentSpc.setCollectionEvent(null);
        try {
            parentSpc.persist();
            Assert.fail("Patient visit should be set!");
        } catch (ValueNotSetException vnse) {
            Assert.assertTrue(true);
        }
    }

    @Test
    public void testDelete() throws Exception {
        SpecimenTypeWrapper type1 = parentSpc.getSpecimenType();
        SpecimenTypeWrapper type2 = SpecimenTypeHelper
            .addSpecimenType("sampletype_2");
        SpecimenTypeHelper.removeFromCreated(type2);
        type2.delete();

        try {
            type1.delete();
            Assert.fail("cannot delete a type use by a sample");
        } catch (BiobankCheckException bce) {
            Assert.assertTrue(true);
        }

        // before deleting the specimen type, the container type that uses it
        // needs to be deleted
        DbHelper.deleteContainers(site.getTopContainerCollection(false));
        DbHelper.deleteFromList(site.getContainerTypeCollection(false));

        parentSpc.delete();
        SpecimenTypeHelper.removeFromCreated(type1);
        type1.delete();
    }

    @Test
    public void testGetSetCollectionEvent() {
        CollectionEventWrapper cevent = new CollectionEventWrapper(appService);
        parentSpc.setCollectionEvent(cevent);
        Assert.assertTrue(parentSpc.getCollectionEvent().getId() == cevent
            .getId());
    }

    @Test
    public void testSetSpecimenPositionFromString() throws Exception {
        parentSpc.setSpecimenPositionFromString("A1",
            parentSpc.getParentContainer());
        parentSpc.persist();
        Assert.assertTrue(parentSpc.getPositionString(false, false)
            .equals("A1"));
        RowColPos pos = parentSpc.getPosition();
        Assert.assertTrue((pos.col == 0) && (pos.row == 0));

        parentSpc.setSpecimenPositionFromString("C2",
            parentSpc.getParentContainer());
        parentSpc.persist();
        Assert.assertTrue(parentSpc.getPositionString(false, false)
            .equals("C2"));
        pos = parentSpc.getPosition();
        Assert.assertTrue((pos.col == 1) && (pos.row == 2));

        try {
            parentSpc.setSpecimenPositionFromString("79",
                parentSpc.getParentContainer());
            Assert.fail("invalid position");
        } catch (Exception bce) {
            Assert.assertTrue(true);
        }

        SpecimenWrapper specimen = new SpecimenWrapper(appService);
        Assert.assertNull(specimen.getPositionString());
    }

    @Test
    public void testGetPositionString() throws Exception {
        parentSpc.setSpecimenPositionFromString("A1",
            parentSpc.getParentContainer());
        Assert.assertTrue(parentSpc.getPositionString(false, false)
            .equals("A1"));
        String parentLabel = parentSpc.getParentContainer().getLabel();
        Assert.assertTrue(parentSpc.getPositionString(true, false).equals(
            parentLabel + "A1"));
        Assert.assertTrue(parentSpc.getPositionString().equals(
            parentLabel + "A1 ("
                + topContainer.getContainerType().getNameShort() + ")"));
    }

    @Test
    public void testGetSetPosition() throws Exception {
        RowColPos position = new RowColPos();
        position.row = 1;
        position.col = 3;
        parentSpc.setPosition(position);
        RowColPos newPosition = parentSpc.getPosition();
        Assert.assertEquals(position.row, newPosition.row);
        Assert.assertEquals(position.col, newPosition.col);

        // ensure position remains after persist
        parentSpc.persist();
        parentSpc.reload();
        newPosition = parentSpc.getPosition();
        Assert.assertEquals(position.row, newPosition.row);
        Assert.assertEquals(position.col, newPosition.col);

        // test setting position to null
        parentSpc.setPosition(null);
        parentSpc.persist();
        parentSpc.reload();
        Assert.assertEquals(null, parentSpc.getPosition());
        Assert.assertEquals(null, parentSpc.getParentContainer());
    }

    @Test
    public void testGetSetParent() throws Exception {
        Assert.assertTrue(parentSpc.hasParent());
        ContainerWrapper oldParent = parentSpc.getParentContainer();
        ContainerTypeWrapper type = ContainerTypeHelper.addContainerType(site,
            "newCtType", "ctNew", 1, 4, 5, true);
        type.addToSpecimenTypeCollection(Arrays.asList(parentSpc
            .getSpecimenType()));
        type.persist();
        ContainerWrapper parent = ContainerHelper.addContainer(
            "newcontainerParent", "ccNew", null, site, type);

        parentSpc.setParent(parent);
        parentSpc.persist();
        // check to make sure gone from old parent
        oldParent.reload();
        Assert.assertTrue(oldParent.getSpecimens().size() == 0);
        // check to make sure added to new parent
        parent.reload();
        Assert.assertTrue(parentSpc.getParentContainer() != null);
        Collection<SpecimenWrapper> sampleWrappers = parent.getSpecimens()
            .values();
        boolean found = false;
        for (SpecimenWrapper sampleWrapper : sampleWrappers) {
            if (sampleWrapper.getId().equals(parentSpc.getId()))
                found = true;
        }
        Assert.assertTrue(found);

        // test for no parent
        SpecimenWrapper specimen2 = new SpecimenWrapper(appService);
        Assert.assertFalse(specimen2.hasParent());
    }

    @Test
    public void testGetSetSpecimenType() throws BiobankCheckException,
        Exception {
        SpecimenTypeWrapper stw = parentSpc.getSpecimenType();
        SpecimenTypeWrapper newType = SpecimenTypeHelper
            .addSpecimenType("newStw");
        stw.persist();
        Assert.assertTrue(stw.getId() != newType.getId());
        parentSpc.setSpecimenType(newType);
        Assert.assertTrue(newType.getId() == parentSpc.getSpecimenType()
            .getId());

        SpecimenWrapper sample1 = new SpecimenWrapper(appService);
        sample1.setSpecimenType(null);
        Assert.assertNull(sample1.getSpecimenType());
    }

    @Test
    public void testGetSetQuantityFromType() throws Exception {
        Double quantity = parentSpc.getQuantity();
        parentSpc.setQuantityFromType();
        // no sample storages defined yet, should be null
        Assert.assertTrue(quantity == null);

        ActivityStatusWrapper activeStatus = ActivityStatusWrapper
            .getActiveActivityStatus(appService);

        AliquotedSpecimenWrapper ss1 = new AliquotedSpecimenWrapper(appService);
        ss1.setSpecimenType(SpecimenTypeHelper.addSpecimenType("ss1"));
        ss1.setVolume(1.0);
        ss1.setStudy(parentSpc.getCollectionEvent().getPatient().getStudy());
        ss1.setActivityStatus(activeStatus);
        ss1.persist();
        AliquotedSpecimenWrapper ss2 = new AliquotedSpecimenWrapper(appService);
        ss2.setSpecimenType(SpecimenTypeHelper.addSpecimenType("ss2"));
        ss2.setVolume(2.0);
        ss2.setStudy(parentSpc.getCollectionEvent().getPatient().getStudy());
        ss2.setActivityStatus(activeStatus);
        ss2.persist();
        AliquotedSpecimenWrapper ss3 = new AliquotedSpecimenWrapper(appService);
        ss3.setSpecimenType(parentSpc.getSpecimenType());
        ss3.setVolume(3.0);
        ss3.setStudy(parentSpc.getCollectionEvent().getPatient().getStudy());
        ss3.setActivityStatus(activeStatus);
        ss3.persist();
        parentSpc.getCollectionEvent().getPatient().getStudy()
            .addToAliquotedSpecimenCollection(Arrays.asList(ss1, ss2, ss3));
        // should be 3
        parentSpc.setQuantityFromType();
        Assert.assertTrue(parentSpc.getQuantity().equals(3.0));
    }

    @Test
    public void testGetFormattedLinkDate() throws Exception {
        Date date = Utils.getRandomDate();
        parentSpc.setCreatedAt(date);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Assert.assertTrue(sdf.format(date).equals(
            parentSpc.getFormattedCreatedAt()));
    }

    @Test
    public void testCompareTo() throws BiobankCheckException, Exception {
        parentSpc.setInventoryId("defgh");
        parentSpc.persist();
        SpecimenWrapper sample2 = SpecimenHelper.newSpecimen(parentSpc,
            childSpc.getSpecimenType(),
            ActivityStatusWrapper.ACTIVE_STATUS_STRING,
            parentSpc.getCollectionEvent(), childSpc.getProcessingEvent(),
            childSpc.getParentContainer(), 2, 3);
        sample2.setInventoryId("awert");
        sample2.persist();
        Assert.assertTrue(parentSpc.compareTo(sample2) > 0);

        sample2.setInventoryId("qwerty");
        sample2.persist();
        Assert.assertTrue(parentSpc.compareTo(sample2) < 0);
    }

    @Test
    public void testGetSpecimen() throws Exception {
        ContainerWrapper container = parentSpc.getParentContainer();
        ContainerTypeWrapper containerType = container.getContainerType();
        SpecimenTypeWrapper sampleType = containerType
            .getSpecimenTypeCollection(false).get(0);
        Assert.assertNotNull(sampleType);
        parentSpc.setInventoryId(Utils.getRandomString(5));
        parentSpc.persist();

        SpecimenHelper.newSpecimen(parentSpc, childSpc.getSpecimenType(),
            ActivityStatusWrapper.ACTIVE_STATUS_STRING,
            parentSpc.getCollectionEvent(), childSpc.getProcessingEvent(),
            childSpc.getParentContainer(), 3, 3);

        SpecimenWrapper foundSpecimen = SpecimenWrapper.getSpecimen(appService,
            parentSpc.getInventoryId(), null);
        Assert.assertNotNull(foundSpecimen);
        Assert.assertEquals(foundSpecimen, parentSpc);
    }

    @Test
    public void testGetSpecimensNonActive() throws Exception {
        ContainerWrapper container = parentSpc.getParentContainer();
        ContainerTypeWrapper containerType = container.getContainerType();
        SpecimenTypeWrapper sampleType = containerType
            .getSpecimenTypeCollection(false).get(0);
        Assert.assertNotNull(sampleType);

        ActivityStatusWrapper activityStatusActive = ActivityStatusWrapper
            .getActiveActivityStatus(appService);
        ActivityStatusWrapper activityStatusNonActive = null;
        for (ActivityStatusWrapper a : ActivityStatusWrapper
            .getAllActivityStatuses(appService)) {
            if (!a.equals(activityStatusActive)) {
                activityStatusNonActive = a;
                break;
            }
        }

        List<SpecimenWrapper> activeSpecimens = new ArrayList<SpecimenWrapper>();
        List<SpecimenWrapper> nonActiveSpecimens = new ArrayList<SpecimenWrapper>();

        activeSpecimens.add(parentSpc);
        for (int i = 1, n = container.getColCapacity(); i < n; ++i) {
            activeSpecimens.add(SpecimenHelper.newSpecimen(parentSpc,
                childSpc.getSpecimenType(),
                ActivityStatusWrapper.ACTIVE_STATUS_STRING,
                parentSpc.getCollectionEvent(), childSpc.getProcessingEvent(),
                childSpc.getParentContainer(), 0, i));

            SpecimenWrapper a = SpecimenHelper.newSpecimen(parentSpc,
                childSpc.getSpecimenType(),
                ActivityStatusWrapper.ACTIVE_STATUS_STRING,
                parentSpc.getCollectionEvent(), childSpc.getProcessingEvent(),
                childSpc.getParentContainer(), 1, i);
            a.setActivityStatus(activityStatusNonActive);
            a.persist();
            nonActiveSpecimens.add(a);
        }

        List<SpecimenWrapper> specimens = SpecimenWrapper
            .getSpecimensNonActiveInCenter(appService, site);
        Assert.assertEquals(nonActiveSpecimens.size(), specimens.size());
        Assert.assertTrue(specimens.containsAll(nonActiveSpecimens));
        Assert.assertFalse(specimens.containsAll(activeSpecimens));
    }

    @Test
    public void testGetSpecimensInSiteWithPositionLabel() throws Exception {
        ContainerWrapper container = parentSpc.getParentContainer();
        ContainerTypeWrapper containerType = container.getContainerType();
        SpecimenTypeWrapper sampleType = containerType
            .getSpecimenTypeCollection(false).get(0);
        Assert.assertNotNull(sampleType);
        parentSpc.setInventoryId(Utils.getRandomString(5));
        parentSpc.persist();

        OriginInfoWrapper oi = new OriginInfoWrapper(appService);
        oi.setCenter(container.getSite());
        oi.persist();
        SpecimenHelper.newSpecimen(parentSpc, childSpc.getSpecimenType(),
            ActivityStatusWrapper.ACTIVE_STATUS_STRING,
            parentSpc.getCollectionEvent(), childSpc.getProcessingEvent(),
            childSpc.getParentContainer(), 0, 1);

        SpecimenHelper.newSpecimen(parentSpc, childSpc.getSpecimenType(),
            ActivityStatusWrapper.ACTIVE_STATUS_STRING,
            parentSpc.getCollectionEvent(), childSpc.getProcessingEvent(),
            childSpc.getParentContainer(), 1, 0);

        parentSpc = SpecimenHelper.newSpecimen(parentSpc,
            childSpc.getSpecimenType(),
            ActivityStatusWrapper.ACTIVE_STATUS_STRING,
            parentSpc.getCollectionEvent(), childSpc.getProcessingEvent(),
            childSpc.getParentContainer(), 0, 2);
        parentSpc.setInventoryId(Utils.getRandomString(5));
        parentSpc.persist();

        List<SpecimenWrapper> specimens = SpecimenWrapper
            .getSpecimensInSiteWithPositionLabel(appService, site,
                parentSpc.getPositionString(true, false));
        Assert.assertEquals(1, specimens.size());
        Assert.assertEquals(specimens.get(0), parentSpc);
    }

    @Test
    public void testResetAlreadyInDatabase() throws Exception {
        parentSpc.persist();
        String old = parentSpc.getInventoryId();
        parentSpc.setInventoryId("toto");
        parentSpc.reset();
        Assert.assertEquals(old, parentSpc.getInventoryId());
    }

    @Test
    public void testResetNew() throws Exception {
        SpecimenWrapper newSpec = SpecimenHelper.newSpecimen(parentSpc
            .getSpecimenType());
        newSpec.setInventoryId("toto");
        newSpec.reset();
        Assert.assertEquals(null, newSpec.getInventoryId());
    }

    @Test
    public void testCheckPosition() throws BiobankCheckException, Exception {
        parentSpc.persist();
        ContainerWrapper container = parentSpc.getParentContainer();

        SpecimenWrapper specimen2 = new SpecimenWrapper(appService);
        specimen2.setPosition(parentSpc.getPosition());

        Assert.assertFalse(specimen2.isPositionFree(container));

        specimen2.setPosition(new RowColPos(2, 3));
        Assert.assertTrue(specimen2.isPositionFree(container));
    }

    @Test
    public void testDebugRandomMethods() throws Exception {
        ContainerWrapper container = parentSpc.getParentContainer();
        ContainerTypeWrapper containerType = container.getContainerType();
        SpecimenTypeWrapper spcType = containerType.getSpecimenTypeCollection(
            false).get(0);
        Assert.assertNotNull(spcType);

        ProcessingEventWrapper pevent = ProcessingEventHelper
            .addProcessingEvent(parentSpc.getCurrentCenter(), parentSpc
                .getCollectionEvent().getPatient(), Utils.getRandomDate());

        // add aliquoted specimen
        SpecimenWrapper specimen = SpecimenHelper.newSpecimen(parentSpc,
            childSpc.getSpecimenType(),
            ActivityStatusWrapper.ACTIVE_STATUS_STRING,
            parentSpc.getCollectionEvent(), childSpc.getProcessingEvent(),
            childSpc.getParentContainer(), 2, 3);
        specimen.setInventoryId(Utils.getRandomString(5));
        specimen.persist();

        pevent.addToSpecimenCollection(Arrays.asList(specimen));
        pevent.persist();

        SpecimenHelper.newSpecimen(parentSpc, childSpc.getSpecimenType(),
            ActivityStatusWrapper.ACTIVE_STATUS_STRING,
            parentSpc.getCollectionEvent(), childSpc.getProcessingEvent(),
            childSpc.getParentContainer(), 2, 4);

        try {
            Assert.assertTrue(DebugUtil.getRandomLinkedAliquotedSpecimens(
                appService, site.getId()).size() > 0);
            Assert.assertTrue(DebugUtil.getRandomAssignedSpecimens(appService,
                site.getId()).size() > 0);
            Assert.assertTrue(DebugUtil
                .getRandomNonAssignedNonDispatchedSpecimens(appService,
                    site.getId(), 10).size() > 0);
        } catch (Exception e) {
            Assert.fail(e.getCause().getMessage());
        }

    }

    @Test
    public void testGetDispatches() throws Exception {
        String name = "testGetDispatches" + r.nextInt();
        SiteWrapper destSite = SiteHelper.addSite(name);
        StudyWrapper study = StudyHelper.addStudy(name);
        PatientWrapper patient = PatientHelper.addPatient(name, study);
        ShippingMethodWrapper method = ShippingMethodWrapper
            .getShippingMethods(appService).get(0);
        DispatchWrapper d = DispatchHelper.addDispatch(site, destSite, method);

        parentSpc = SpecimenHelper.newSpecimen(name);
        OriginInfoWrapper originInfo = new OriginInfoWrapper(appService);
        originInfo.setCenter(destSite);
        originInfo.persist();
        CollectionEventWrapper cevent = CollectionEventHelper
            .addCollectionEvent(site, patient, 1, originInfo, parentSpc);
        parentSpc = cevent.getAllSpecimenCollection(false).get(0);

        d.addSpecimens(Arrays.asList(parentSpc), DispatchSpecimenState.NONE);
        d.persist();
        parentSpc.reload();

        List<DispatchWrapper> specimenDispatches = parentSpc.getDispatches();
        Assert.assertEquals(1, specimenDispatches.size());
        Assert.assertTrue(specimenDispatches.contains(d));

        Assert.assertTrue(d.isInCreationState());

        // site send specimens
        d.setState(DispatchState.IN_TRANSIT);
        d.persist();
        Assert.assertTrue(d.isInTransitState());

        // dest site receive specimen
        d.setState(DispatchState.RECEIVED);
        d.receiveSpecimens(Arrays.asList(parentSpc));
        d.persist();
        Assert.assertTrue(d.isInReceivedState());

        // make sure spc now belongs to destSite
        destSite.reload();
        Assert.assertTrue(destSite.getSpecimenCollection(false).contains(
            parentSpc));

        // dispatch specimen to second site
        SiteWrapper destSite2 = SiteHelper.addSite(name + "_2");

        DispatchWrapper d2 = DispatchHelper.addDispatch(destSite, destSite2,
            method);
        d2.addSpecimens(Arrays.asList(parentSpc), DispatchSpecimenState.NONE);

        parentSpc.reload();
        // assign a position to this specimen
        ContainerTypeWrapper topType = ContainerTypeHelper.addContainerType(
            destSite, "ct11", "ct11", 1, 5, 6, true);
        ContainerWrapper topCont = ContainerHelper.addContainer("11", "11",
            null, destSite, topType);
        ContainerTypeWrapper childType = ContainerTypeHelper.addContainerType(
            destSite, "ct22", "ct22", 2, 4, 7, false);
        topType.addToChildContainerTypeCollection(Arrays.asList(childType));
        topType.persist();
        ContainerWrapper cont = ContainerHelper.addContainer("22", "22",
            topCont, destSite, childType, 4, 5);
        childType.addToSpecimenTypeCollection(Arrays.asList(parentSpc
            .getSpecimenType()));
        childType.persist();
        cont.reload();
        cont.addSpecimen(2, 3, parentSpc);
        parentSpc.persist();

        // add to new dispatch
        d2.addSpecimens(Arrays.asList(parentSpc), DispatchSpecimenState.NONE);
        d2.persist();

        // make sure spc still belongs to destSite
        destSite2.reload();
        Assert.assertTrue(destSite.getSpecimenCollection(false).contains(
            parentSpc));

        parentSpc.reload();
        specimenDispatches = parentSpc.getDispatches();
        Assert.assertEquals(2, specimenDispatches.size());
        Assert.assertTrue(specimenDispatches.contains(d));
        Assert.assertTrue(specimenDispatches.contains(d2));
    }
}
