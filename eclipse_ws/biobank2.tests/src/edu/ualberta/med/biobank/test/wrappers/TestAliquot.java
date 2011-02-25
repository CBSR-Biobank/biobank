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
import edu.ualberta.med.biobank.common.util.RowColPos;
import edu.ualberta.med.biobank.common.wrappers.ActivityStatusWrapper;
import edu.ualberta.med.biobank.common.wrappers.SpecimenWrapper;
import edu.ualberta.med.biobank.common.wrappers.ClinicWrapper;
import edu.ualberta.med.biobank.common.wrappers.ContactWrapper;
import edu.ualberta.med.biobank.common.wrappers.ContainerTypeWrapper;
import edu.ualberta.med.biobank.common.wrappers.ContainerWrapper;
import edu.ualberta.med.biobank.common.wrappers.PatientWrapper;
import edu.ualberta.med.biobank.common.wrappers.ProcessingEventWrapper;
import edu.ualberta.med.biobank.common.wrappers.AliquotedSpecimenWrapper;
import edu.ualberta.med.biobank.common.wrappers.SpecimenTypeWrapper;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;
import edu.ualberta.med.biobank.common.wrappers.StudyWrapper;
import edu.ualberta.med.biobank.model.ProcessingEvent;
import edu.ualberta.med.biobank.server.applicationservice.exceptions.ValueNotSetException;
import edu.ualberta.med.biobank.test.TestDatabase;
import edu.ualberta.med.biobank.test.Utils;
import edu.ualberta.med.biobank.test.internal.AliquotHelper;
import edu.ualberta.med.biobank.test.internal.ClinicHelper;
import edu.ualberta.med.biobank.test.internal.ContactHelper;
import edu.ualberta.med.biobank.test.internal.ContainerHelper;
import edu.ualberta.med.biobank.test.internal.ContainerTypeHelper;
import edu.ualberta.med.biobank.test.internal.PatientHelper;
import edu.ualberta.med.biobank.test.internal.ProcessingEventHelper;
import edu.ualberta.med.biobank.test.internal.SpecimenTypeHelper;
import edu.ualberta.med.biobank.test.internal.SiteHelper;
import edu.ualberta.med.biobank.test.internal.StudyHelper;

public class TestAliquot extends TestDatabase {

    private SpecimenWrapper aliquot;

    private SiteWrapper site;

    private ContainerWrapper topContainer;

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();

        site = SiteHelper.addSite("sitename" + r.nextInt());
        SpecimenTypeWrapper sampleType = SpecimenTypeHelper
            .addSampleType("sampletype" + r.nextInt());

        ContainerTypeWrapper typeChild = ContainerTypeHelper.addContainerType(
            site, "ctTypeChild" + r.nextInt(), "ctChild", 1, 4, 5, false);
        typeChild.addToSampleTypeCollection(Arrays.asList(sampleType));
        typeChild.persist();

        ContainerTypeWrapper topType = ContainerTypeHelper.addContainerType(
            site, "topType" + r.nextInt(), "ct", 1, 4, 5, true);
        topType.addToChildContainerTypeCollection(Arrays.asList(typeChild));
        topType.persist();

        topContainer = ContainerHelper.addContainer("top" + r.nextInt(), "cc",
            null, site, topType);

        ContainerWrapper container = ContainerHelper.addContainer(null, "2nd",
            topContainer, site, typeChild, 0, 0);

        StudyWrapper study = StudyHelper.addStudy("studyname" + r.nextInt());
        PatientWrapper patient = PatientHelper.addPatient(
            Utils.getRandomString(4), study);
        ClinicWrapper clinic = ClinicHelper.addClinic("clinicname"
            + r.nextInt());
        ContactWrapper contact = ContactHelper.addContact(clinic,
            "ContactClinic");
        study.addToContactCollection(Arrays.asList(contact));
        study.persist();

        site.addToStudyCollection(Arrays.asList(study));
        site.persist();
        site.reload();

        ProcessingEventWrapper pv = ProcessingEventHelper.addProcessingEvent(
            site, patient, Utils.getRandomDate(), Utils.getRandomDate());
        aliquot = AliquotHelper.newAliquot(sampleType, container, pv, 0, 0);
        container.reload();
    }

    @Test
    public void testGettersAndSetters() throws Exception {
        aliquot.persist();
        testGettersAndSetters(aliquot);
    }

    @Test
    public void testPersistFailActivityStatusNull() throws Exception {
        SpecimenWrapper pAliquot = AliquotHelper.newAliquot(
            aliquot.getSpecimenType(), null);
        pAliquot.setProcessingEvent(aliquot.getProcessingEvent());

        try {
            pAliquot.persist();
            Assert.fail("Should not insert the aliquot : no activity status");
        } catch (ValueNotSetException vnse) {
            Assert.assertTrue(true);
        }
        pAliquot.setActivityStatus(ActivityStatusWrapper
            .getActiveActivityStatus(appService));
        pAliquot.persist();
    }

    @Test
    public void testCheckInventoryIdUnique() throws BiobankCheckException,
        Exception {
        aliquot.persist();
        SpecimenWrapper duplicate = AliquotHelper.newAliquot(
            aliquot.getSpecimenType(), aliquot.getParent(),
            aliquot.getProcessingEvent(), 2, 2);

        duplicate.setInventoryId(aliquot.getInventoryId());
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
        aliquot.setInventoryId("toto" + i);
        aliquot.persist();
        SpecimenWrapper duplicate = AliquotHelper.newAliquot(
            aliquot.getSpecimenType(), aliquot.getParent(),
            aliquot.getProcessingEvent(), 2, 2);

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
        aliquot.persist();

        SpecimenWrapper duplicate = AliquotHelper.newAliquot(
            aliquot.getSpecimenType(), aliquot.getParent(),
            aliquot.getProcessingEvent(), 2, 2);
        duplicate.setInventoryId(aliquot.getInventoryId());

        try {
            duplicate.persist();
            Assert.fail("same inventory id !");
        } catch (DuplicateEntryException dee) {
            Assert.assertTrue(true);
        }

        duplicate.setInventoryId("qqqq" + r.nextInt());
        duplicate.persist();

        duplicate.setInventoryId(aliquot.getInventoryId());
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
        aliquot.setInventoryId("toto" + i);
        aliquot.persist();

        SpecimenWrapper duplicate = AliquotHelper.newAliquot(
            aliquot.getSpecimenType(), aliquot.getParent(),
            aliquot.getProcessingEvent(), 2, 2);
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
        aliquot.persist();
        RowColPos pos = aliquot.getPosition();

        SpecimenWrapper duplicate = AliquotHelper.newAliquot(
            aliquot.getSpecimenType(), aliquot.getParent(),
            aliquot.getProcessingEvent(), pos.row, pos.col);

        try {
            duplicate.persist();
            Assert
                .fail("should not be allowed to add an aliquot in a position that is not empty");
        } catch (BiobankCheckException bce) {
            Assert.assertTrue(true);
        }

        duplicate.setPosition(new RowColPos(2, 3));
        duplicate.persist();

        duplicate.setInventoryId(Utils.getRandomString(5));
        duplicate.persist();
    }

    @Test
    public void testPersistCheckParentAcceptSampleType()
        throws BiobankCheckException, Exception {
        SpecimenTypeWrapper oldSampleType = aliquot.getSpecimenType();

        SpecimenTypeWrapper type2 = SpecimenTypeHelper
            .addSampleType("sampletype_2");
        aliquot.setSpecimenType(type2);
        try {
            aliquot.persist();
            Assert.fail("Container can't hold this type !");
        } catch (BiobankCheckException bce) {
            Assert.assertTrue(true);
        }

        aliquot.setSpecimenType(oldSampleType);
        aliquot.persist();

        ContainerWrapper container = new ContainerWrapper(appService);
        SpecimenWrapper aliquot = new SpecimenWrapper(appService);
        aliquot.setParent(container);
        try {
            aliquot.persist();
            Assert.fail("container has no container type");
        } catch (BiobankCheckException bce) {
            Assert.assertTrue(true);
        }
    }

    @Test
    public void testCheckProcessingEventNotNull() throws BiobankCheckException,
        Exception {
        aliquot.setProcessingEvent(null);
        try {
            aliquot.persist();
            Assert.fail("Patient visit should be set!");
        } catch (ValueNotSetException vnse) {
            Assert.assertTrue(true);
        }
    }

    @Test
    public void testDelete() throws Exception {
        aliquot.persist();
        SpecimenTypeWrapper type1 = aliquot.getSpecimenType();
        SpecimenTypeWrapper type2 = SpecimenTypeHelper
            .addSampleType("sampletype_2");
        SpecimenTypeHelper.removeFromCreated(type2);
        type2.delete();

        try {
            type1.delete();
            Assert.fail("cannot delete a type use by a sample");
        } catch (BiobankCheckException bce) {
            Assert.assertTrue(true);
        }

        aliquot.delete();
        SpecimenTypeHelper.removeFromCreated(type1);
        type1.delete();
    }

    @Test
    public void testGetSetProcessingEvent() {
        ProcessingEventWrapper pvw = new ProcessingEventWrapper(appService,
            new ProcessingEvent());
        aliquot.setProcessingEvent(pvw);
        Assert.assertTrue(aliquot.getProcessingEvent().getId() == pvw.getId());
    }

    @Test
    public void testSetSpecimenPositionFromString() throws Exception {
        aliquot.setSpecimenPositionFromString("A1", aliquot.getParent());
        aliquot.persist();
        Assert.assertTrue(aliquot.getPositionString(false, false).equals("A1"));
        RowColPos pos = aliquot.getPosition();
        Assert.assertTrue((pos.col == 0) && (pos.row == 0));

        aliquot.setSpecimenPositionFromString("C2", aliquot.getParent());
        aliquot.persist();
        Assert.assertTrue(aliquot.getPositionString(false, false).equals("C2"));
        pos = aliquot.getPosition();
        Assert.assertTrue((pos.col == 1) && (pos.row == 2));

        try {
            aliquot.setSpecimenPositionFromString("79", aliquot.getParent());
            Assert.fail("invalid position");
        } catch (Exception bce) {
            Assert.assertTrue(true);
        }

        SpecimenWrapper aliquot = new SpecimenWrapper(appService);
        Assert.assertNull(aliquot.getPositionString());
    }

    @Test
    public void testGetPositionString() throws Exception {
        aliquot.setSpecimenPositionFromString("A1", aliquot.getParent());
        Assert.assertTrue(aliquot.getPositionString(false, false).equals("A1"));
        String parentLabel = aliquot.getParent().getLabel();
        Assert.assertTrue(aliquot.getPositionString(true, false).equals(
            parentLabel + "A1"));
        Assert.assertTrue(aliquot.getPositionString().equals(
            parentLabel + "A1 ("
                + topContainer.getContainerType().getNameShort() + ")"));
    }

    @Test
    public void testGetSetPosition() throws Exception {
        RowColPos position = new RowColPos();
        position.row = 1;
        position.col = 3;
        aliquot.setPosition(position);
        RowColPos newPosition = aliquot.getPosition();
        Assert.assertEquals(position.row, newPosition.row);
        Assert.assertEquals(position.col, newPosition.col);

        // ensure position remains after persist
        aliquot.persist();
        aliquot.reload();
        newPosition = aliquot.getPosition();
        Assert.assertEquals(position.row, newPosition.row);
        Assert.assertEquals(position.col, newPosition.col);

        // test setting position to null
        aliquot.setPosition(null);
        aliquot.persist();
        aliquot.reload();
        Assert.assertEquals(null, aliquot.getPosition());
        Assert.assertEquals(null, aliquot.getParent());
    }

    @Test
    public void testGetSetParent() throws Exception {
        Assert.assertTrue(aliquot.hasParent());
        ContainerWrapper oldParent = aliquot.getParent();
        ContainerTypeWrapper type = ContainerTypeHelper.addContainerType(site,
            "newCtType", "ctNew", 1, 4, 5, true);
        type.addToSampleTypeCollection(Arrays.asList(aliquot.getSpecimenType()));
        type.persist();
        ContainerWrapper parent = ContainerHelper.addContainer(
            "newcontainerParent", "ccNew", null, site, type);

        aliquot.setParent(parent);
        aliquot.persist();
        // check to make sure gone from old parent
        oldParent.reload();
        Assert.assertTrue(oldParent.getSpecimens().size() == 0);
        // check to make sure added to new parent
        parent.reload();
        Assert.assertTrue(aliquot.getParent() != null);
        Collection<SpecimenWrapper> sampleWrappers = parent.getSpecimens()
            .values();
        boolean found = false;
        for (SpecimenWrapper sampleWrapper : sampleWrappers) {
            if (sampleWrapper.getId().equals(aliquot.getId()))
                found = true;
        }
        Assert.assertTrue(found);

        // test for no parent
        SpecimenWrapper aliquot2 = new SpecimenWrapper(appService);
        Assert.assertFalse(aliquot2.hasParent());
    }

    @Test
    public void testGetSetSpecimenType() throws BiobankCheckException, Exception {
        SpecimenTypeWrapper stw = aliquot.getSpecimenType();
        SpecimenTypeWrapper newType = SpecimenTypeHelper.addSampleType("newStw");
        stw.persist();
        Assert.assertTrue(stw.getId() != newType.getId());
        aliquot.setSpecimenType(newType);
        Assert.assertTrue(newType.getId() == aliquot.getSpecimenType().getId());

        SpecimenWrapper sample1 = new SpecimenWrapper(appService);
        sample1.setSpecimenType(null);
        Assert.assertNull(sample1.getSpecimenType());
    }

    @Test
    public void testGetSetQuantityFromType() throws Exception {
        Double quantity = aliquot.getQuantity();
        aliquot.setQuantityFromType();
        // no sample storages defined yet, should be null
        Assert.assertTrue(quantity == null);

        ActivityStatusWrapper activeStatus = ActivityStatusWrapper
            .getActiveActivityStatus(appService);

        AliquotedSpecimenWrapper ss1 = new AliquotedSpecimenWrapper(appService);
        ss1.setSpecimenType(SpecimenTypeHelper.addSampleType("ss1"));
        ss1.setVolume(1.0);
        ss1.setStudy(aliquot.getProcessingEvent().getPatient().getStudy());
        ss1.setActivityStatus(activeStatus);
        ss1.persist();
        AliquotedSpecimenWrapper ss2 = new AliquotedSpecimenWrapper(appService);
        ss2.setSpecimenType(SpecimenTypeHelper.addSampleType("ss2"));
        ss2.setVolume(2.0);
        ss2.setStudy(aliquot.getProcessingEvent().getPatient().getStudy());
        ss2.setActivityStatus(activeStatus);
        ss2.persist();
        AliquotedSpecimenWrapper ss3 = new AliquotedSpecimenWrapper(appService);
        ss3.setSpecimenType(aliquot.getSpecimenType());
        ss3.setVolume(3.0);
        ss3.setStudy(aliquot.getProcessingEvent().getPatient().getStudy());
        ss3.setActivityStatus(activeStatus);
        ss3.persist();
        aliquot.getProcessingEvent().getPatient().getStudy()
            .addToSampleStorageCollection(Arrays.asList(ss1, ss2, ss3));
        // should be 3
        aliquot.setQuantityFromType();
        Assert.assertTrue(aliquot.getQuantity().equals(3.0));
    }

    @Test
    public void testGetFormattedLinkDate() throws Exception {
        Date date = Utils.getRandomDate();
        aliquot.setLinkDate(date);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Assert.assertTrue(sdf.format(date).equals(
            aliquot.getFormattedLinkDate()));
    }

    @Test
    public void testCompareTo() throws BiobankCheckException, Exception {
        aliquot.setInventoryId("defgh");
        aliquot.persist();
        SpecimenWrapper sample2 = AliquotHelper.newAliquot(
            aliquot.getSpecimenType(), aliquot.getParent(),
            aliquot.getProcessingEvent(), 2, 3);
        sample2.setInventoryId("awert");
        sample2.persist();
        Assert.assertTrue(aliquot.compareTo(sample2) > 0);

        sample2.setInventoryId("qwerty");
        sample2.persist();
        Assert.assertTrue(aliquot.compareTo(sample2) < 0);
    }

    @Test
    public void testGetSpecimen() throws Exception {
        ContainerWrapper container = aliquot.getParent();
        ContainerTypeWrapper containerType = container.getContainerType();
        ProcessingEventWrapper pv = aliquot.getProcessingEvent();
        SpecimenTypeWrapper sampleType = containerType.getSpecimenTypeCollection(
            false).get(0);
        Assert.assertNotNull(sampleType);
        aliquot.setInventoryId(Utils.getRandomString(5));
        aliquot.persist();
        AliquotHelper.addAliquot(sampleType, container, pv, 3, 3);

        SpecimenWrapper foundAliquot = SpecimenWrapper.getSpecimen(appService,
            aliquot.getInventoryId(), null);
        Assert.assertNotNull(foundAliquot);
        Assert.assertEquals(foundAliquot, aliquot);
    }

    @Test
    public void testGetSpecimensNonActive() throws Exception {
        ContainerWrapper container = aliquot.getParent();
        ContainerTypeWrapper containerType = container.getContainerType();
        ProcessingEventWrapper pv = aliquot.getProcessingEvent();
        SpecimenTypeWrapper sampleType = containerType.getSpecimenTypeCollection(
            false).get(0);
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

        List<SpecimenWrapper> activeAliquots = new ArrayList<SpecimenWrapper>();
        List<SpecimenWrapper> nonActiveAliquots = new ArrayList<SpecimenWrapper>();

        activeAliquots.add(aliquot);
        for (int i = 1, n = container.getColCapacity(); i < n; ++i) {
            activeAliquots.add(AliquotHelper.addAliquot(sampleType, container,
                pv, 0, i));

            SpecimenWrapper a = AliquotHelper.newAliquot(sampleType, container,
                pv, 1, i);
            a.setActivityStatus(activityStatusNonActive);
            a.persist();
            a.reload();
            nonActiveAliquots.add(a);
        }

        List<SpecimenWrapper> aliquots = SpecimenWrapper
            .getSpecimensNonActiveInSite(appService, site);
        Assert.assertEquals(nonActiveAliquots.size(), aliquots.size());
        Assert.assertTrue(aliquots.containsAll(nonActiveAliquots));
        Assert.assertFalse(aliquots.containsAll(activeAliquots));
    }

    @Test
    public void testGetSpecimensInSiteWithPositionLabel() throws Exception {
        ContainerWrapper container = aliquot.getParent();
        ContainerTypeWrapper containerType = container.getContainerType();
        ProcessingEventWrapper pv = aliquot.getProcessingEvent();
        SpecimenTypeWrapper sampleType = containerType.getSpecimenTypeCollection(
            false).get(0);
        Assert.assertNotNull(sampleType);
        aliquot.setInventoryId(Utils.getRandomString(5));
        aliquot.persist();

        AliquotHelper.addAliquot(sampleType, container, pv, 0, 1);
        AliquotHelper.addAliquot(sampleType, container, pv, 1, 0);
        aliquot = AliquotHelper.newAliquot(sampleType, container, pv, 0, 2);
        aliquot.setInventoryId(Utils.getRandomString(5));
        aliquot.persist();

        List<SpecimenWrapper> aliquots = SpecimenWrapper
            .getSpecimensInSiteWithPositionLabel(appService, site,
                aliquot.getPositionString(true, false));
        Assert.assertEquals(1, aliquots.size());
        Assert.assertEquals(aliquots.get(0), aliquot);
    }

    @Test
    public void testResetAlreadyInDatabase() throws Exception {
        aliquot.persist();
        String old = aliquot.getInventoryId();
        aliquot.setInventoryId("toto");
        aliquot.reset();
        Assert.assertEquals(old, aliquot.getInventoryId());
    }

    @Test
    public void testResetNew() throws Exception {
        aliquot.setInventoryId("toto");
        aliquot.reset();
        Assert.assertEquals(null, aliquot.getInventoryId());
    }

    @Test
    public void testCheckPosition() throws BiobankCheckException, Exception {
        aliquot.persist();
        ContainerWrapper container = aliquot.getParent();

        SpecimenWrapper aliquot2 = new SpecimenWrapper(appService);
        aliquot2.setPosition(aliquot.getPosition());

        Assert.assertFalse(aliquot2.isPositionFree(container));

        aliquot2.setPosition(new RowColPos(2, 3));
        Assert.assertTrue(aliquot2.isPositionFree(container));
    }

    @Test
    public void testDebugRandomMethods() throws Exception {
        ContainerWrapper container = aliquot.getParent();
        ContainerTypeWrapper containerType = container.getContainerType();
        ProcessingEventWrapper pv = aliquot.getProcessingEvent();
        SpecimenTypeWrapper sampleType = containerType.getSpecimenTypeCollection(
            false).get(0);
        Assert.assertNotNull(sampleType);

        AliquotHelper.addAliquot(sampleType, container, pv, 0, 0);
        SpecimenWrapper aliquot = AliquotHelper.newAliquot(sampleType,
            container, pv, 2, 3);
        aliquot.setInventoryId(Utils.getRandomString(5));
        aliquot.persist();
        AliquotHelper.addAliquot(sampleType, null, pv, null, null);

        DebugUtil.getRandomLinkedAliquots(appService, site.getId());
        DebugUtil.getRandomAssignedAliquots(appService, site.getId());
        DebugUtil.getRandomNonAssignedNonDispatchedAliquots(appService,
            site.getId());
        DebugUtil.getRandomDispatchedAliquots(appService, site.getId());

        Assert.fail("not real tests here");
    }

    @Test
    public void testGetDispatches() throws Exception {
        Assert.fail("test need to be rewritten");
        // String name = "testGetDispatchs" + r.nextInt();
        // SiteWrapper destSite = SiteHelper.addSite(name);
        // StudyWrapper study = aliquot.getProcessingEvent().getPatient()
        // .getStudy();
        // destSite.addToStudyCollection(Arrays.asList(study));
        // destSite.persist();
        // destSite.reload();
        // site.persist();
        // site.reload();
        // ShippingMethodWrapper method = ShippingMethodWrapper
        // .getShippingMethods(appService).get(0);
        // DispatchWrapper dCollectionEvent = DispatchHelper.newDispatch(site,
        // destSite, method);
        //
        // // add an aliquot that has not been persisted
        // try {
        // dCollectionEvent.addAliquots(Arrays.asList(aliquot));
        // Assert.fail("Should not be allowed to add aliquots not yet in DB");
        // } catch (BiobankCheckException bce) {
        // Assert.assertTrue(true);
        // }
        //
        // aliquot.persist();
        // aliquot.reload();
        //
        // dCollectionEvent = DispatchHelper.newDispatch(site, destSite,
        // method);
        // dCollectionEvent.addAliquots(Arrays.asList(aliquot));
        // dCollectionEvent.persist();
        // aliquot.reload();
        //
        // List<DispatchWrapper> aliquotDispatchs = aliquot.getDispatchs();
        // Assert.assertEquals(1, aliquotDispatchs.size());
        // Assert.assertTrue(aliquotDispatchs.contains(dCollectionEvent));
        //
        // Assert.assertTrue(dCollectionEvent.isInCreationState());
        //
        // // site send aliquots
        // dCollectionEvent.setState(DispatchState.IN_TRANSIT);
        // dCollectionEvent.persist();
        // Assert.assertTrue(dCollectionEvent.isInTransitState());
        //
        // // dest site receive aliquot
        // dCollectionEvent.setState(DispatchState.RECEIVED);
        // dCollectionEvent.receiveAliquots(Arrays.asList(aliquot));
        // dCollectionEvent.persist();
        // Assert.assertTrue(dCollectionEvent.isInReceivedState());
        //
        // // dispatch aliquot to second site
        // SiteWrapper destSite2 = SiteHelper.addSite(name + "_2");
        // destSite2.addToStudyCollection(Arrays.asList(study));
        // destSite2.persist();
        // destSite2.reload();
        // destSite.persist();
        //
        // destSite.reload();
        // DispatchWrapper dCollectionEvent2 = DispatchHelper.newDispatch(
        // destSite, destSite2, method);
        // try {
        // dCollectionEvent2.addAliquots(Arrays.asList(aliquot));
        // Assert
        // .fail("Cannot reuse a aliquot if it has not been received (ie: need a 'Active' status)");
        // } catch (BiobankCheckException bce) {
        // Assert.assertTrue(true);
        // }
        //
        // aliquot.reload();
        // // assign a position to this aliquot
        // ContainerTypeWrapper topType = ContainerTypeHelper.addContainerType(
        // destSite, "ct11", "ct11", 1, 5, 6, true);
        // ContainerWrapper topCont = ContainerHelper.addContainer("11", "11",
        // null, destSite, topType);
        // ContainerTypeWrapper childType =
        // ContainerTypeHelper.addContainerType(
        // destSite, "ct22", "ct22", 2, 4, 7, false);
        // topType.addToChildContainerTypeCollection(Arrays.asList(childType));
        // topType.persist();
        // ContainerWrapper cont = ContainerHelper.addContainer("22", "22",
        // topCont, destSite, childType, 4, 5);
        // childType.addToSampleTypeCollection(Arrays.asList(aliquot
        // .getSpecimenType()));
        // childType.persist();
        // cont.reload();
        // cont.addAliquot(2, 3, aliquot);
        // aliquot.persist();
        //
        // // add to new shipment
        // dCollectionEvent2.addAliquots(Arrays.asList(aliquot));
        // dCollectionEvent2.persist();
        //
        // aliquot.reload();
        // aliquotDispatchs = aliquot.getDispatchs();
        // Assert.assertEquals(2, aliquotDispatchs.size());
        // Assert.assertTrue(aliquotDispatchs.contains(dCollectionEvent));
        // Assert.assertTrue(aliquotDispatchs.contains(dCollectionEvent2));
    }
}
