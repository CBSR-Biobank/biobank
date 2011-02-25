package edu.ualberta.med.biobank.test.wrappers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import edu.ualberta.med.biobank.common.exception.BiobankCheckException;
import edu.ualberta.med.biobank.common.formatters.DateFormatter;
import edu.ualberta.med.biobank.common.wrappers.ActivityStatusWrapper;
import edu.ualberta.med.biobank.common.wrappers.SpecimenWrapper;
import edu.ualberta.med.biobank.common.wrappers.ClinicWrapper;
import edu.ualberta.med.biobank.common.wrappers.ContactWrapper;
import edu.ualberta.med.biobank.common.wrappers.ContainerTypeWrapper;
import edu.ualberta.med.biobank.common.wrappers.ContainerWrapper;
import edu.ualberta.med.biobank.common.wrappers.DispatchWrapper;
import edu.ualberta.med.biobank.common.wrappers.PatientWrapper;
import edu.ualberta.med.biobank.common.wrappers.ProcessingEventWrapper;
import edu.ualberta.med.biobank.common.wrappers.SpecimenTypeWrapper;
import edu.ualberta.med.biobank.common.wrappers.ShippingMethodWrapper;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;
import edu.ualberta.med.biobank.common.wrappers.StudyWrapper;
import edu.ualberta.med.biobank.model.Dispatch;
import edu.ualberta.med.biobank.test.TestDatabase;
import edu.ualberta.med.biobank.test.Utils;
import edu.ualberta.med.biobank.test.internal.AliquotHelper;
import edu.ualberta.med.biobank.test.internal.ClinicHelper;
import edu.ualberta.med.biobank.test.internal.ContactHelper;
import edu.ualberta.med.biobank.test.internal.ContainerHelper;
import edu.ualberta.med.biobank.test.internal.ContainerTypeHelper;
import edu.ualberta.med.biobank.test.internal.DispatchHelper;
import edu.ualberta.med.biobank.test.internal.PatientHelper;
import edu.ualberta.med.biobank.test.internal.ProcessingEventHelper;
import edu.ualberta.med.biobank.test.internal.SiteHelper;
import edu.ualberta.med.biobank.test.internal.StudyHelper;

public class TestDispatch extends TestDatabase {

    @Test
    public void testGettersAndSetters() throws Exception {
        String name = "testGettersAndSetters" + r.nextInt();
        SiteWrapper senderSite = SiteHelper.addSite(name + "_sender");
        SiteWrapper receiverSite = SiteHelper.addSite(name + "_receiver");

        SiteWrapper[] allSites = new SiteWrapper[] { senderSite, receiverSite };

        for (SiteWrapper site : allSites) {
            site.persist();
            site.reload();
        }

        DispatchWrapper dispatch = DispatchHelper.addDispatch(senderSite,
            receiverSite, ShippingMethodWrapper.getShippingMethods(appService)
                .get(0));
        testGettersAndSetters(dispatch);
    }

    @Test
    public void testConstructor() throws Exception {
        Dispatch rawDispatch = new Dispatch();
        DispatchWrapper dispatch = new DispatchWrapper(appService, rawDispatch);
        Assert.assertNotNull(dispatch);
    }

    @Test
    public void testGetSetSender() throws Exception {
        String name = "testGetSetSender" + r.nextInt();
        SiteWrapper senderSite = SiteHelper.addSite(name + "_sender");
        SiteWrapper receiverSite = SiteHelper.addSite(name + "_receiver");

        SiteWrapper[] allSites = new SiteWrapper[] { senderSite, receiverSite };

        for (SiteWrapper site : allSites) {
            site.persist();
            site.reload();
        }

        DispatchWrapper dispatch = DispatchHelper.newDispatch(null,
            receiverSite, ShippingMethodWrapper.getShippingMethods(appService)
                .get(0));
        Assert.assertNull(dispatch.getSender());

        dispatch.setSender(senderSite);
        dispatch.persist();

        Assert.assertEquals(senderSite, dispatch.getSender());

        DispatchWrapper dispatch2 = new DispatchWrapper(appService,
            dispatch.getWrappedObject());

        Assert.assertEquals(senderSite, dispatch2.getSender());
    }

    @Test
    public void testGetSetReceiver() throws Exception {
        String name = "testGetSetReceiver" + r.nextInt();
        SiteWrapper senderSite = SiteHelper.addSite(name + "_sender");
        SiteWrapper receiverSite = SiteHelper.addSite(name + "_receiver");

        SiteWrapper[] allSites = new SiteWrapper[] { senderSite, receiverSite };

        for (SiteWrapper site : allSites) {
            site.persist();
            site.reload();
        }

        DispatchWrapper dispatch = DispatchHelper.newDispatch(senderSite, null,
            ShippingMethodWrapper.getShippingMethods(appService).get(0));
        Assert.assertNull(dispatch.getReceiver());

        dispatch.setReceiver(receiverSite);
        dispatch.persist();

        Assert.assertEquals(receiverSite, dispatch.getReceiver());

        DispatchWrapper dispatch2 = new DispatchWrapper(appService,
            dispatch.getWrappedObject());

        Assert.assertEquals(receiverSite, dispatch2.getReceiver());
    }

    @Test
    public void testPersist() throws Exception {
        String name = "testPersist" + r.nextInt();
        SiteWrapper senderSite = SiteHelper.addSite(name + "_sender");
        SiteWrapper senderSite2 = SiteHelper.addSite(name + "_sender2");
        SiteWrapper receiverSite = SiteHelper.addSite(name + "_receiver");
        SiteWrapper receiverSite2 = SiteHelper.addSite(name + "_receiver2");

        SiteWrapper[] allSites = new SiteWrapper[] { senderSite, senderSite2,
            receiverSite, receiverSite2 };

        for (SiteWrapper site : allSites) {
            site.persist();
            site.reload();
        }

        ShippingMethodWrapper method = ShippingMethodWrapper
            .getShippingMethods(appService).get(0);
        DispatchHelper.addDispatch(senderSite, receiverSite, method, name,
            Utils.getRandomDate());

        // test no sender
        DispatchWrapper dispatch = DispatchHelper.newDispatch(null,
            receiverSite, method, TestCommon.getNewWaybill(r),
            Utils.getRandomDate());
        try {
            dispatch.persist();
            Assert
                .fail("should not be allowed to persist a dispatch shipment without a sender");
        } catch (BiobankCheckException e) {
            Assert.assertTrue(true);
        }

        // test no receiver
        dispatch = DispatchHelper.newDispatch(senderSite, null, method,
            TestCommon.getNewWaybill(r), Utils.getRandomDate());
        try {
            dispatch.persist();
            Assert
                .fail("should not be allowed to persist a dispatch shipment without a receiver");
        } catch (BiobankCheckException e) {
            Assert.assertTrue(true);
        }
    }

    @Test
    public void testCompatreTo() throws Exception {
        String name = "testCompareTo" + r.nextInt();
        SiteWrapper senderSite = SiteHelper.addSite(name + "_sender");
        SiteWrapper receiverSite = SiteHelper.addSite(name + "_receiver");

        SiteWrapper[] allSites = new SiteWrapper[] { senderSite, receiverSite };

        for (SiteWrapper site : allSites) {
            site.persist();
            site.reload();
        }

        ShippingMethodWrapper method = ShippingMethodWrapper
            .getShippingMethods(appService).get(0);

        DispatchWrapper dispatch1 = DispatchHelper.addDispatch(senderSite,
            receiverSite, method);
        dispatch1.setDateReceived(DateFormatter.dateFormatter
            .parse("2010-02-01 23:00"));

        DispatchWrapper dispatch2 = DispatchHelper.addDispatch(senderSite,
            receiverSite, method);
        dispatch2.setDateReceived(DateFormatter.dateFormatter
            .parse("2009-12-01 23:00"));

        Assert.assertTrue(dispatch1.compareTo(dispatch2) > 0);
        Assert.assertTrue(dispatch2.compareTo(dispatch1) < 0);

        Assert.assertTrue(dispatch1.compareTo(null) == 0);
        Assert.assertTrue(dispatch2.compareTo(null) == 0);
    }

    @Test
    public void testReset() throws Exception {
        String name = "testReset" + r.nextInt();
        SiteWrapper senderSite = SiteHelper.addSite(name + "_sender");
        SiteWrapper receiverSite = SiteHelper.addSite(name + "_receiver");

        SiteWrapper[] allSites = new SiteWrapper[] { senderSite, receiverSite };

        for (SiteWrapper site : allSites) {
            site.persist();
            site.reload();
        }

        ShippingMethodWrapper method = ShippingMethodWrapper
            .getShippingMethods(appService).get(0);

        // test reset for a new object
        DispatchWrapper dispatch = DispatchHelper.newDispatch(senderSite,
            receiverSite, method, name, Utils.getRandomDate());

        dispatch.reset();
        Assert.assertEquals(null, dispatch.getWaybill());

        // test reset for an object already in database
        dispatch = DispatchHelper.addDispatch(senderSite, receiverSite, method,
            name, Utils.getRandomDate());
        dispatch.setWaybill("QQQQ");
        dispatch.reset();
        Assert.assertEquals(name, dispatch.getWaybill());
    }

    @Test
    public void testReload() throws Exception {
        String name = "testReload" + r.nextInt();
        SiteWrapper senderSite = SiteHelper.addSite(name + "_sender");
        SiteWrapper receiverSite = SiteHelper.addSite(name + "_receiver");

        SiteWrapper[] allSites = new SiteWrapper[] { senderSite, receiverSite };

        for (SiteWrapper site : allSites) {
            site.persist();
            site.reload();
        }

        DispatchWrapper shipment = DispatchHelper.addDispatch(senderSite,
            receiverSite, ShippingMethodWrapper.getShippingMethods(appService)
                .get(0), name, Utils.getRandomDate());

        try {
            shipment.reload();
            Assert.assertTrue(true);
        } catch (Exception e) {
            Assert.fail("cannot reload shipment");
        }
    }

    @Test
    public void testGetWrappedClass() throws Exception {
        DispatchWrapper dispatch = DispatchHelper.newDispatch(null, null,
            ShippingMethodWrapper.getShippingMethods(appService).get(0));
        Assert.assertEquals(Dispatch.class, dispatch.getWrappedClass());
    }

    @Test
    public void testDelete() throws Exception {
        String name = "testDelete" + r.nextInt();
        SiteWrapper senderSite = SiteHelper.addSite(name + "_sender");
        SiteWrapper receiverSite = SiteHelper.addSite(name + "_receiver");

        SiteWrapper[] allSites = new SiteWrapper[] { senderSite, receiverSite };

        for (SiteWrapper site : allSites) {
            site.persist();
            site.reload();
        }

        DispatchWrapper dispatch = DispatchHelper.addDispatch(senderSite,
            receiverSite, ShippingMethodWrapper.getShippingMethods(appService)
                .get(0), name, Utils.getRandomDate());

        int countBefore = appService.search(Dispatch.class, new Dispatch())
            .size();

        dispatch.delete();

        int countAfter = appService.search(Dispatch.class, new Dispatch())
            .size();

        Assert.assertEquals(countBefore - 1, countAfter);
    }

    private List<SpecimenWrapper> addAliquotsToContainerRow(
        ProcessingEventWrapper visit, ContainerWrapper container, int row,
        List<SpecimenTypeWrapper> sampleTypes) throws Exception {
        int numSampletypes = sampleTypes.size();
        int colCapacity = container.getColCapacity();
        List<SpecimenWrapper> aliquots = new ArrayList<SpecimenWrapper>();
        for (int i = 0; i < colCapacity; ++i) {
            aliquots.add(AliquotHelper.addAliquot(
                sampleTypes.get(r.nextInt(numSampletypes)),
                ActivityStatusWrapper.ACTIVE_STATUS_STRING, container, visit,
                row, i));
        }
        container.reload();
        visit.reload();
        return aliquots;
    }

    @Test
    public void testGetSetSpecimenCollection() throws Exception {
        String name = "testGetSetSpecimenCollection" + r.nextInt();
        StudyWrapper study = StudyHelper.addStudy(name);
        SiteWrapper senderSite = SiteHelper.addSite(name + "_sender");
        senderSite.persist();
        SiteWrapper receiverSite = SiteHelper.addSite(name + "_receiver");
        receiverSite.persist();

        senderSite.persist();
        senderSite.reload();
        DispatchWrapper dispatch = DispatchHelper.addDispatch(senderSite,
            receiverSite, ShippingMethodWrapper.getShippingMethods(appService)
                .get(0));
        List<SpecimenTypeWrapper> sampleTypes = SpecimenTypeWrapper
            .getAllSampleTypes(appService, false);
        ContainerTypeWrapper containerType = ContainerTypeHelper
            .addContainerType(senderSite, name, name, 1, 8, 12, false);
        containerType.addToSampleTypeCollection(sampleTypes);
        containerType.persist();
        containerType.reload();
        ContainerTypeWrapper topContainerType = ContainerTypeHelper
            .addContainerTypeRandom(senderSite, name + "top", true);
        topContainerType.addToChildContainerTypeCollection(Arrays
            .asList(containerType));
        topContainerType.persist();
        topContainerType.reload();
        ContainerWrapper topContainer = ContainerHelper.addContainer(
            String.valueOf(r.nextInt()), name + "top", null, senderSite,
            topContainerType);
        ContainerWrapper container = ContainerHelper.addContainer(null, name,
            topContainer, senderSite, containerType, 0, 0);
        PatientWrapper patient = PatientHelper.addPatient(name, study);
        ClinicWrapper clinic = ClinicHelper.addClinic(name);
        ContactWrapper contact = ContactHelper.addContact(clinic, name);
        study.addToContactCollection(Arrays.asList(contact));
        study.persist();
        study.reload();
        ProcessingEventWrapper visit = ProcessingEventHelper
            .addProcessingEvent(clinic, patient, Utils.getRandomDate(),
                Utils.getRandomDate());

        List<SpecimenWrapper> aliquotSet1 = addAliquotsToContainerRow(visit,
            container, 0, sampleTypes);
        List<SpecimenWrapper> aliquotSet2 = addAliquotsToContainerRow(visit,
            container, 1, sampleTypes);

        dispatch.addSpecimens(aliquotSet1);
        dispatch.persist();
        dispatch.reload();

        List<SpecimenWrapper> shipmentAliquots = dispatch.getSpecimenCollection();
        Assert.assertEquals(aliquotSet1.size(), shipmentAliquots.size());

        // add more aliquots to row 2

        dispatch.addSpecimens(aliquotSet2);
        dispatch.persist();
        dispatch.reload();

        shipmentAliquots = dispatch.getSpecimenCollection();
        Assert.assertEquals(aliquotSet1.size() + aliquotSet2.size(),
            shipmentAliquots.size());

        dispatch.removeAliquots(aliquotSet1);
        dispatch.persist();
        dispatch.reload();

        shipmentAliquots = dispatch.getSpecimenCollection();
        Assert.assertEquals(aliquotSet2.size(), shipmentAliquots.size());
    }

    @Test
    public void testRemoveDispatchAliquots() {
        Assert.fail("testcase missing");
    }

    @Test
    public void testGetDispatchesInSite() {
        Assert.fail("testcase missing");
    }

    @Test
    public void testGetDispatchesInSiteByDateSent() {
        Assert.fail("testcase missing");
    }

    @Test
    public void testGetDispatchesInSiteByDateReceived() {
        Assert.fail("testcase missing");
    }

}
