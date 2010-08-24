package edu.ualberta.med.biobank.test.wrappers;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import edu.ualberta.med.biobank.common.exception.BiobankCheckException;
import edu.ualberta.med.biobank.common.formatters.DateFormatter;
import edu.ualberta.med.biobank.common.wrappers.DispatchContainerWrapper;
import edu.ualberta.med.biobank.common.wrappers.DispatchShipmentWrapper;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;
import edu.ualberta.med.biobank.model.DispatchShipment;
import edu.ualberta.med.biobank.test.TestDatabase;
import edu.ualberta.med.biobank.test.Utils;
import edu.ualberta.med.biobank.test.internal.DispatchContainerHelper;
import edu.ualberta.med.biobank.test.internal.DispatchShipmentHelper;
import edu.ualberta.med.biobank.test.internal.SiteHelper;

public class TestDispatchShipment extends TestDatabase {

    @Test
    public void testGettersAndSetters() throws Exception {
        String name = "testGettersAndSetters" + r.nextInt();
        SiteWrapper senderSite = SiteHelper.addSite(name + "_sender");
        SiteWrapper receiverSite = SiteHelper.addSite(name + "_receiver");

        DispatchShipmentWrapper shipment = DispatchShipmentHelper.addShipment(
            senderSite, receiverSite);
        testGettersAndSetters(shipment);
    }

    @Test
    public void testConstructor() throws Exception {
        DispatchShipment shipmentRaw = new DispatchShipment();
        DispatchShipmentWrapper shipment = new DispatchShipmentWrapper(
            appService, shipmentRaw);
        Assert.assertNotNull(shipment);
    }

    @Test
    public void testGetSetSender() throws Exception {
        String name = "testGetSetSender" + r.nextInt();
        SiteWrapper senderSite = SiteHelper.addSite(name + "_sender");
        SiteWrapper receiverSite = SiteHelper.addSite(name + "_receiver");

        DispatchShipmentWrapper shipment = DispatchShipmentHelper.newShipment(
            null, receiverSite);
        Assert.assertNull(shipment.getSender());

        shipment.setSender(senderSite);
        shipment.persist();

        Assert.assertEquals(senderSite, shipment.getSender());

        DispatchShipmentWrapper shipment2 = new DispatchShipmentWrapper(
            appService, shipment.getWrappedObject());

        Assert.assertEquals(senderSite, shipment2.getSender());

    }

    @Test
    public void testGetSetReceiver() throws Exception {
        String name = "testGetSetReceiver" + r.nextInt();
        SiteWrapper senderSite = SiteHelper.addSite(name + "_sender");
        SiteWrapper receiverSite = SiteHelper.addSite(name + "_receiver");

        DispatchShipmentWrapper shipment = DispatchShipmentHelper.newShipment(
            senderSite, null);
        Assert.assertNull(shipment.getReceiver());

        shipment.setReceiver(receiverSite);
        shipment.persist();

        Assert.assertEquals(receiverSite, shipment.getReceiver());

        DispatchShipmentWrapper shipment2 = new DispatchShipmentWrapper(
            appService, shipment.getWrappedObject());

        Assert.assertEquals(receiverSite, shipment2.getReceiver());
    }

    @Test
    public void testPersist() throws Exception {
        String name = "testPersist" + r.nextInt();
        SiteWrapper senderSite = SiteHelper.addSite(name + "_sender");
        SiteWrapper senderSite2 = SiteHelper.addSite(name + "_sender2");
        SiteWrapper receiverSite = SiteHelper.addSite(name + "_receiver");
        SiteWrapper receiverSite2 = SiteHelper.addSite(name + "_receiver2");

        DispatchShipmentHelper.addShipment(senderSite, receiverSite, name,
            Utils.getRandomDate());

        // set waybill not unique for a shipment not yet database
        DispatchShipmentWrapper shipment2 = DispatchShipmentHelper.newShipment(
            senderSite, receiverSite2, name, Utils.getRandomDate());

        try {
            shipment2.persist();
            Assert
                .fail("should not be allowed to persist a dispatch shipment without a unique waybill: "
                    + shipment2.getWaybill());
        } catch (BiobankCheckException e) {
            Assert.assertTrue(true);
        }

        shipment2.setWaybill(name + "2");
        shipment2.persist();

        // set waybill not unique for a shipment retrieved from database
        shipment2.setWaybill(name);
        try {
            shipment2.persist();
            Assert
                .fail("should not be allowed to persist a dispatch shipment without a unique waybill: "
                    + shipment2.getWaybill());
        } catch (BiobankCheckException e) {
            Assert.assertTrue(true);
        }

        // set waybill to same for 2 different sending sites
        shipment2 = DispatchShipmentHelper.newShipment(senderSite2,
            receiverSite2, name, Utils.getRandomDate());
        try {
            shipment2.persist();
            Assert.assertTrue(true);
        } catch (BiobankCheckException e) {
            Assert
                .fail("should be allowed to persist a dispatch shipment with a unique waybill");
        }

        // test no sender
        DispatchShipmentWrapper shipment = DispatchShipmentHelper.newShipment(
            null, receiverSite, TestCommon.getNewWaybill(r),
            Utils.getRandomDate());
        try {
            shipment.persist();
            Assert
                .fail("should be allowed to persist a dispatch shipment without a sender");
        } catch (BiobankCheckException e) {
            Assert.assertTrue(true);
        }

        // test no receiver
        shipment = DispatchShipmentHelper.newShipment(senderSite, null,
            TestCommon.getNewWaybill(r), Utils.getRandomDate());
        try {
            shipment.persist();
            Assert
                .fail("should be allowed to persist a dispatch shipment without a receiver");
        } catch (BiobankCheckException e) {
            Assert.assertTrue(true);
        }
    }

    @Test
    public void testCompatreTo() throws Exception {
        String name = "testCompareTo" + r.nextInt();
        SiteWrapper senderSite = SiteHelper.addSite(name + "_sender");
        SiteWrapper receiverSite = SiteHelper.addSite(name + "_receiver");

        DispatchShipmentWrapper shipment1 = DispatchShipmentHelper.addShipment(
            senderSite, receiverSite);
        shipment1.setDateReceived(DateFormatter.dateFormatter
            .parse("2010-02-01 23:00"));

        DispatchShipmentWrapper shipment2 = DispatchShipmentHelper.addShipment(
            senderSite, receiverSite);
        shipment2.setDateReceived(DateFormatter.dateFormatter
            .parse("2009-12-01 23:00"));

        Assert.assertTrue(shipment1.compareTo(shipment2) > 0);
        Assert.assertTrue(shipment2.compareTo(shipment1) < 0);

        Assert.assertTrue(shipment1.compareTo(null) == 0);
        Assert.assertTrue(shipment2.compareTo(null) == 0);
    }

    @Test
    public void testReset() throws Exception {
        String name = "testReset" + r.nextInt();
        SiteWrapper senderSite = SiteHelper.addSite(name + "_sender");
        SiteWrapper receiverSite = SiteHelper.addSite(name + "_receiver");

        // test reset for a new object
        DispatchShipmentWrapper shipment = DispatchShipmentHelper.newShipment(
            senderSite, receiverSite, name, Utils.getRandomDate());

        shipment.reset();
        Assert.assertEquals(null, shipment.getWaybill());

        // test reset for an object already in database
        shipment = DispatchShipmentHelper.addShipment(senderSite, receiverSite,
            name, Utils.getRandomDate());
        shipment.setWaybill("QQQQ");
        shipment.reset();
        Assert.assertEquals(name, shipment.getWaybill());
    }

    @Test
    public void testReload() throws Exception {
        String name = "testReload" + r.nextInt();
        SiteWrapper senderSite = SiteHelper.addSite(name + "_sender");
        SiteWrapper receiverSite = SiteHelper.addSite(name + "_receiver");
        DispatchShipmentWrapper shipment = DispatchShipmentHelper.addShipment(
            senderSite, receiverSite, name, Utils.getRandomDate());

        try {
            shipment.reload();
            Assert.assertTrue(true);
        } catch (Exception e) {
            Assert.fail("cannot reload shipment");
        }
    }

    @Test
    public void testGetWrappedClass() throws Exception {
        DispatchShipmentWrapper shipment = DispatchShipmentHelper.newShipment(
            null, null);
        Assert.assertEquals(DispatchShipment.class, shipment.getWrappedClass());
    }

    @Test
    public void testDetlete() throws Exception {
        String name = "testDelete" + r.nextInt();
        SiteWrapper senderSite = SiteHelper.addSite(name + "_sender");
        SiteWrapper receiverSite = SiteHelper.addSite(name + "_receiver");
        DispatchShipmentWrapper shipment = DispatchShipmentHelper.addShipment(
            senderSite, receiverSite, name, Utils.getRandomDate());

        int countBefore = appService.search(DispatchShipment.class,
            new DispatchShipment()).size();

        shipment.delete();

        int countAfter = appService.search(DispatchShipment.class,
            new DispatchShipment()).size();

        Assert.assertEquals(countBefore - 1, countAfter);

    }

    @Test
    public void testGetSetContainerCollection() throws Exception {
        String name = "testGetSetContainerCollection" + r.nextInt();
        SiteWrapper senderSite = SiteHelper.addSite(name + "_sender");
        SiteWrapper receiverSite = SiteHelper.addSite(name + "_receiver");
        DispatchShipmentWrapper shipment = DispatchShipmentHelper.addShipment(
            senderSite, receiverSite);

        List<DispatchContainerWrapper> containers = new ArrayList<DispatchContainerWrapper>();
        for (int i = 0; i < r.nextInt(); ++i) {
            containers.add(DispatchContainerHelper.newContainerRandom(
                senderSite, name));
        }

        shipment.addSentContainers(containers);
        shipment.persist();
        shipment.reload();

        List<DispatchContainerWrapper> dispatchContainers = shipment
            .getSentContainerCollection();
        Assert.assertEquals(containers.size(), dispatchContainers.size());
        Assert.assertTrue(dispatchContainers.containsAll(containers));
    }

}
