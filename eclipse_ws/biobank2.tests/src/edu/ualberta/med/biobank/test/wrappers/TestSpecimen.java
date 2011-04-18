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
import edu.ualberta.med.biobank.common.util.DispatchState;
import edu.ualberta.med.biobank.common.util.RowColPos;
import edu.ualberta.med.biobank.common.wrappers.ActivityStatusWrapper;
import edu.ualberta.med.biobank.common.wrappers.AliquotedSpecimenWrapper;
import edu.ualberta.med.biobank.common.wrappers.CollectionEventWrapper;
import edu.ualberta.med.biobank.common.wrappers.ContainerTypeWrapper;
import edu.ualberta.med.biobank.common.wrappers.ContainerWrapper;
import edu.ualberta.med.biobank.common.wrappers.DispatchWrapper;
import edu.ualberta.med.biobank.common.wrappers.OriginInfoWrapper;
import edu.ualberta.med.biobank.common.wrappers.ShippingMethodWrapper;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;
import edu.ualberta.med.biobank.common.wrappers.SpecimenTypeWrapper;
import edu.ualberta.med.biobank.common.wrappers.SpecimenWrapper;
import edu.ualberta.med.biobank.server.applicationservice.exceptions.ValueNotSetException;
import edu.ualberta.med.biobank.test.TestDatabase;
import edu.ualberta.med.biobank.test.Utils;
import edu.ualberta.med.biobank.test.internal.ContainerHelper;
import edu.ualberta.med.biobank.test.internal.ContainerTypeHelper;
import edu.ualberta.med.biobank.test.internal.DispatchHelper;
import edu.ualberta.med.biobank.test.internal.OriginInfoHelper;
import edu.ualberta.med.biobank.test.internal.SiteHelper;
import edu.ualberta.med.biobank.test.internal.SpecimenHelper;
import edu.ualberta.med.biobank.test.internal.SpecimenTypeHelper;

public class TestSpecimen extends TestDatabase {

    private SpecimenWrapper spc;

    private SiteWrapper site;

    private ContainerWrapper topContainer;

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();

        spc = SpecimenHelper.addSpecimen();
        site = (SiteWrapper) spc.getCurrentCenter();
        ContainerTypeWrapper typeChild = ContainerTypeHelper.addContainerType(
            site, "ctTypeChild" + r.nextInt(), "ctChild", 1, 4, 5, false);
        typeChild.addToSpecimenTypeCollection(Arrays.asList(spc
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
        container.addSpecimen(0, 0, spc);
        spc.setParent(container);
        spc.persist();
    }

    @Test
    public void testGettersAndSetters() throws Exception {
        testGettersAndSetters(spc);
    }

    @Test
    public void testPersistFailActivityStatusNull() throws Exception {
        spc.setActivityStatus(null);
        try {
            spc.persist();
            Assert.fail("Should not insert the specimen : no activity status");
        } catch (ValueNotSetException vnse) {
            Assert.assertTrue(true);
        }
        spc.setActivityStatus(ActivityStatusWrapper
            .getActiveActivityStatus(appService));
        spc.persist();
    }

    @Test
    public void testCheckInventoryIdUnique() throws BiobankCheckException,
        Exception {
        SpecimenWrapper duplicate = SpecimenHelper.newSpecimen(
            spc.getSpecimenType(), spc.getParentContainer(),
            spc.getCollectionEvent(), 2, 2,
            OriginInfoHelper.addOriginInfo(site));

        duplicate.setInventoryId(spc.getInventoryId());
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
        spc.setInventoryId("toto" + i);
        spc.persist();
        SpecimenWrapper duplicate = SpecimenHelper.newSpecimen(
            spc.getSpecimenType(), spc.getParentContainer(),
            spc.getCollectionEvent(), 2, 2,
            OriginInfoHelper.addOriginInfo(site));

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

        SpecimenWrapper duplicate = SpecimenHelper.newSpecimen(
            spc.getSpecimenType(), spc.getParentContainer(),
            spc.getCollectionEvent(), 2, 2,
            OriginInfoHelper.addOriginInfo(spc.getCurrentCenter()));
        duplicate.setInventoryId(spc.getInventoryId());

        try {
            duplicate.persist();
            Assert.fail("same inventory id !");
        } catch (DuplicateEntryException dee) {
            Assert.assertTrue(true);
        }
        duplicate.setInventoryId("qqqq" + r.nextInt());
        duplicate.persist();

        duplicate.setInventoryId(spc.getInventoryId());
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
        spc.setInventoryId("toto" + i);
        spc.persist();

        SpecimenWrapper duplicate = SpecimenHelper.newSpecimen(
            spc.getSpecimenType(), spc.getParentContainer(),
            spc.getCollectionEvent(), 2, 2, spc.getOriginInfo());
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
        spc.persist();
        RowColPos pos = spc.getPosition();

        SpecimenWrapper duplicate = SpecimenHelper.newSpecimen(
            spc.getSpecimenType(), spc.getParentContainer(),
            spc.getCollectionEvent(), pos.row, pos.col, spc.getOriginInfo());

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
        SpecimenTypeWrapper oldSpecimenType = spc.getSpecimenType();

        SpecimenTypeWrapper type2 = SpecimenTypeHelper
            .addSpecimenType("sampletype_2");
        spc.setSpecimenType(type2);
        try {
            spc.persist();
            Assert.fail("Container can't hold this type !");
        } catch (BiobankCheckException bce) {
            Assert.assertTrue(true);
        }

        spc.setSpecimenType(oldSpecimenType);
        spc.persist();

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
        spc.setCollectionEvent(null);
        try {
            spc.persist();
            Assert.fail("Patient visit should be set!");
        } catch (ValueNotSetException vnse) {
            Assert.assertTrue(true);
        }
    }

    @Test
    public void testDelete() throws Exception {
        spc.persist();
        SpecimenTypeWrapper type1 = spc.getSpecimenType();
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

        spc.delete();
        SpecimenTypeHelper.removeFromCreated(type1);
        type1.delete();
    }

    @Test
    public void testGetSetCollectionEvent() {
        CollectionEventWrapper cevent = new CollectionEventWrapper(appService);
        spc.setCollectionEvent(cevent);
        Assert.assertTrue(spc.getCollectionEvent().getId() == cevent.getId());
    }

    @Test
    public void testSetSpecimenPositionFromString() throws Exception {
        spc.setSpecimenPositionFromString("A1", spc.getParentContainer());
        spc.persist();
        Assert.assertTrue(spc.getPositionString(false, false).equals("A1"));
        RowColPos pos = spc.getPosition();
        Assert.assertTrue((pos.col == 0) && (pos.row == 0));

        spc.setSpecimenPositionFromString("C2", spc.getParentContainer());
        spc.persist();
        Assert.assertTrue(spc.getPositionString(false, false).equals("C2"));
        pos = spc.getPosition();
        Assert.assertTrue((pos.col == 1) && (pos.row == 2));

        try {
            spc.setSpecimenPositionFromString("79", spc.getParentContainer());
            Assert.fail("invalid position");
        } catch (Exception bce) {
            Assert.assertTrue(true);
        }

        SpecimenWrapper specimen = new SpecimenWrapper(appService);
        Assert.assertNull(specimen.getPositionString());
    }

    @Test
    public void testGetPositionString() throws Exception {
        spc.setSpecimenPositionFromString("A1", spc.getParentContainer());
        Assert.assertTrue(spc.getPositionString(false, false).equals("A1"));
        String parentLabel = spc.getParentContainer().getLabel();
        Assert.assertTrue(spc.getPositionString(true, false).equals(
            parentLabel + "A1"));
        Assert.assertTrue(spc.getPositionString().equals(
            parentLabel + "A1 ("
                + topContainer.getContainerType().getNameShort() + ")"));
    }

    @Test
    public void testGetSetPosition() throws Exception {
        RowColPos position = new RowColPos();
        position.row = 1;
        position.col = 3;
        spc.setPosition(position);
        RowColPos newPosition = spc.getPosition();
        Assert.assertEquals(position.row, newPosition.row);
        Assert.assertEquals(position.col, newPosition.col);

        // ensure position remains after persist
        spc.persist();
        spc.reload();
        newPosition = spc.getPosition();
        Assert.assertEquals(position.row, newPosition.row);
        Assert.assertEquals(position.col, newPosition.col);

        // test setting position to null
        spc.setPosition(null);
        spc.persist();
        spc.reload();
        Assert.assertEquals(null, spc.getPosition());
        Assert.assertEquals(null, spc.getParentContainer());
    }

    @Test
    public void testGetSetParent() throws Exception {
        Assert.assertTrue(spc.hasParent());
        ContainerWrapper oldParent = spc.getParentContainer();
        ContainerTypeWrapper type = ContainerTypeHelper.addContainerType(site,
            "newCtType", "ctNew", 1, 4, 5, true);
        type.addToSpecimenTypeCollection(Arrays.asList(spc.getSpecimenType()));
        type.persist();
        ContainerWrapper parent = ContainerHelper.addContainer(
            "newcontainerParent", "ccNew", null, site, type);

        spc.setParent(parent);
        spc.persist();
        // check to make sure gone from old parent
        oldParent.reload();
        Assert.assertTrue(oldParent.getSpecimens().size() == 0);
        // check to make sure added to new parent
        parent.reload();
        Assert.assertTrue(spc.getParentContainer() != null);
        Collection<SpecimenWrapper> sampleWrappers = parent.getSpecimens()
            .values();
        boolean found = false;
        for (SpecimenWrapper sampleWrapper : sampleWrappers) {
            if (sampleWrapper.getId().equals(spc.getId()))
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
        SpecimenTypeWrapper stw = spc.getSpecimenType();
        SpecimenTypeWrapper newType = SpecimenTypeHelper
            .addSpecimenType("newStw");
        stw.persist();
        Assert.assertTrue(stw.getId() != newType.getId());
        spc.setSpecimenType(newType);
        Assert.assertTrue(newType.getId() == spc.getSpecimenType().getId());

        SpecimenWrapper sample1 = new SpecimenWrapper(appService);
        sample1.setSpecimenType(null);
        Assert.assertNull(sample1.getSpecimenType());
    }

    @Test
    public void testGetSetQuantityFromType() throws Exception {
        Double quantity = spc.getQuantity();
        spc.setQuantityFromType();
        // no sample storages defined yet, should be null
        Assert.assertTrue(quantity == null);

        ActivityStatusWrapper activeStatus = ActivityStatusWrapper
            .getActiveActivityStatus(appService);

        AliquotedSpecimenWrapper ss1 = new AliquotedSpecimenWrapper(appService);
        ss1.setSpecimenType(SpecimenTypeHelper.addSpecimenType("ss1"));
        ss1.setVolume(1.0);
        ss1.setStudy(spc.getCollectionEvent().getPatient().getStudy());
        ss1.setActivityStatus(activeStatus);
        ss1.persist();
        AliquotedSpecimenWrapper ss2 = new AliquotedSpecimenWrapper(appService);
        ss2.setSpecimenType(SpecimenTypeHelper.addSpecimenType("ss2"));
        ss2.setVolume(2.0);
        ss2.setStudy(spc.getCollectionEvent().getPatient().getStudy());
        ss2.setActivityStatus(activeStatus);
        ss2.persist();
        AliquotedSpecimenWrapper ss3 = new AliquotedSpecimenWrapper(appService);
        ss3.setSpecimenType(spc.getSpecimenType());
        ss3.setVolume(3.0);
        ss3.setStudy(spc.getCollectionEvent().getPatient().getStudy());
        ss3.setActivityStatus(activeStatus);
        ss3.persist();
        spc.getCollectionEvent().getPatient().getStudy()
            .addToAliquotedSpecimenCollection(Arrays.asList(ss1, ss2, ss3));
        // should be 3
        spc.setQuantityFromType();
        Assert.assertTrue(spc.getQuantity().equals(3.0));
    }

    @Test
    public void testGetFormattedLinkDate() throws Exception {
        Date date = Utils.getRandomDate();
        spc.setCreatedAt(date);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Assert.assertTrue(sdf.format(date).equals(spc.getFormattedCreatedAt()));
    }

    @Test
    public void testCompareTo() throws BiobankCheckException, Exception {
        spc.setInventoryId("defgh");
        spc.persist();
        SpecimenWrapper sample2 = SpecimenHelper.newSpecimen(
            spc.getSpecimenType(), spc.getParentContainer(),
            spc.getCollectionEvent(), 2, 3, spc.getOriginInfo());
        sample2.setInventoryId("awert");
        sample2.persist();
        Assert.assertTrue(spc.compareTo(sample2) > 0);

        sample2.setInventoryId("qwerty");
        sample2.persist();
        Assert.assertTrue(spc.compareTo(sample2) < 0);
    }

    @Test
    public void testGetSpecimen() throws Exception {
        ContainerWrapper container = spc.getParentContainer();
        ContainerTypeWrapper containerType = container.getContainerType();
        CollectionEventWrapper pv = spc.getCollectionEvent();
        SpecimenTypeWrapper sampleType = containerType
            .getSpecimenTypeCollection(false).get(0);
        Assert.assertNotNull(sampleType);
        spc.setInventoryId(Utils.getRandomString(5));
        spc.persist();

        SpecimenHelper.addSpecimen(sampleType, container, pv, 3, 3,
            container.getSite());

        SpecimenWrapper foundSpecimen = SpecimenWrapper.getSpecimen(appService,
            spc.getInventoryId(), null);
        Assert.assertNotNull(foundSpecimen);
        Assert.assertEquals(foundSpecimen, spc);
    }

    @Test
    public void testGetSpecimensNonActive() throws Exception {
        ContainerWrapper container = spc.getParentContainer();
        ContainerTypeWrapper containerType = container.getContainerType();
        CollectionEventWrapper pv = spc.getCollectionEvent();
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

        activeSpecimens.add(spc);
        for (int i = 1, n = container.getColCapacity(); i < n; ++i) {
            activeSpecimens.add(SpecimenHelper.addSpecimen(sampleType,
                container, pv, 0, i, spc.getOriginInfo()));

            SpecimenWrapper a = SpecimenHelper.newSpecimen(sampleType,
                container, pv, 1, i, spc.getOriginInfo());
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
        ContainerWrapper container = spc.getParentContainer();
        ContainerTypeWrapper containerType = container.getContainerType();
        CollectionEventWrapper pv = spc.getCollectionEvent();
        SpecimenTypeWrapper sampleType = containerType
            .getSpecimenTypeCollection(false).get(0);
        Assert.assertNotNull(sampleType);
        spc.setInventoryId(Utils.getRandomString(5));
        spc.persist();

        OriginInfoWrapper oi = new OriginInfoWrapper(appService);
        oi.setCenter(container.getSite());
        oi.persist();
        SpecimenHelper.addSpecimen(sampleType, container, pv, 0, 1, oi);
        SpecimenHelper.addSpecimen(sampleType, container, pv, 1, 0, oi);
        spc = SpecimenHelper.newSpecimen(sampleType, container, pv, 0, 2, oi);
        spc.setInventoryId(Utils.getRandomString(5));
        spc.persist();

        List<SpecimenWrapper> specimens = SpecimenWrapper
            .getSpecimensInSiteWithPositionLabel(appService, site,
                spc.getPositionString(true, false));
        Assert.assertEquals(1, specimens.size());
        Assert.assertEquals(specimens.get(0), spc);
    }

    @Test
    public void testResetAlreadyInDatabase() throws Exception {
        spc.persist();
        String old = spc.getInventoryId();
        spc.setInventoryId("toto");
        spc.reset();
        Assert.assertEquals(old, spc.getInventoryId());
    }

    @Test
    public void testResetNew() throws Exception {
        SpecimenWrapper newSpec = SpecimenHelper.newSpecimen(spc
            .getSpecimenType());
        newSpec.setInventoryId("toto");
        newSpec.reset();
        Assert.assertEquals(null, newSpec.getInventoryId());
    }

    @Test
    public void testCheckPosition() throws BiobankCheckException, Exception {
        spc.persist();
        ContainerWrapper container = spc.getParentContainer();

        SpecimenWrapper specimen2 = new SpecimenWrapper(appService);
        specimen2.setPosition(spc.getPosition());

        Assert.assertFalse(specimen2.isPositionFree(container));

        specimen2.setPosition(new RowColPos(2, 3));
        Assert.assertTrue(specimen2.isPositionFree(container));
    }

    @Test
    public void testDebugRandomMethods() throws Exception {
        ContainerWrapper container = spc.getParentContainer();
        ContainerTypeWrapper containerType = container.getContainerType();
        CollectionEventWrapper pv = spc.getCollectionEvent();
        SpecimenTypeWrapper sampleType = containerType
            .getSpecimenTypeCollection(false).get(0);
        Assert.assertNotNull(sampleType);

        SpecimenHelper.addSpecimen(sampleType, container, pv, 1, 1,
            spc.getOriginInfo());
        SpecimenWrapper specimen = SpecimenHelper.newSpecimen(sampleType,
            container, pv, 2, 3, spc.getOriginInfo());
        specimen.setInventoryId(Utils.getRandomString(5));
        specimen.persist();
        SpecimenHelper.addSpecimen(sampleType, null, pv, null, null,
            spc.getOriginInfo());

        try {
            Assert.assertTrue(DebugUtil.getRandomLinkedAliquotedSpecimens(
                appService, site.getId()).size() > 0);
            Assert.assertTrue(DebugUtil.getRandomAssignedSpecimens(appService,
                site.getId()).size() > 0);
            Assert.assertTrue(DebugUtil
                .getRandomNonAssignedNonDispatchedSpecimens(appService,
                    site.getId()).size() > 0);
        } catch (Exception e) {
            Assert.fail(e.getCause().getMessage());
        }

    }

    @Test
    public void testGetDispatches() throws Exception {
        String name = "testGetDispatchs" + r.nextInt();
        SiteWrapper destSite = SiteHelper.addSite(name);
        ShippingMethodWrapper method = ShippingMethodWrapper
            .getShippingMethods(appService).get(0);
        DispatchWrapper d = DispatchHelper.newDispatch(site, destSite, method);

        d.addSpecimens(Arrays.asList(spc));
        d.persist();
        spc.reload();

        List<DispatchWrapper> specimenDispatchs = spc.getDispatchs();
        Assert.assertEquals(1, specimenDispatchs.size());
        Assert.assertTrue(specimenDispatchs.contains(d));

        Assert.assertTrue(d.isInCreationState());

        // site send specimens
        d.setState(DispatchState.IN_TRANSIT);
        d.persist();
        Assert.assertTrue(d.isInTransitState());

        // dest site receive specimen
        d.setState(DispatchState.RECEIVED);
        d.receiveSpecimens(Arrays.asList(spc));
        d.persist();
        Assert.assertTrue(d.isInReceivedState());

        // dispatch specimen to second site
        SiteWrapper destSite2 = SiteHelper.addSite(name + "_2");

        DispatchWrapper d2 = DispatchHelper.newDispatch(destSite, destSite2,
            method);
        d2.addSpecimens(Arrays.asList(spc));

        spc.reload();
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
        childType.addToSpecimenTypeCollection(Arrays.asList(spc
            .getSpecimenType()));
        childType.persist();
        cont.reload();
        cont.addSpecimen(2, 3, spc);
        spc.persist();

        // add to new shipment
        d2.addSpecimens(Arrays.asList(spc));
        d2.persist();

        spc.reload();
        specimenDispatchs = spc.getDispatchs();
        Assert.assertEquals(2, specimenDispatchs.size());
        Assert.assertTrue(specimenDispatchs.contains(d));
        Assert.assertTrue(specimenDispatchs.contains(d2));
    }
}
