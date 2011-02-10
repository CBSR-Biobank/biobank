package edu.ualberta.med.biobank.test.wrappers;

import org.junit.Assert;
import org.junit.Test;

import edu.ualberta.med.biobank.common.exception.BiobankCheckException;
import edu.ualberta.med.biobank.common.formatters.DateFormatter;
import edu.ualberta.med.biobank.common.wrappers.DispatchWrapper;
import edu.ualberta.med.biobank.common.wrappers.ShippingMethodWrapper;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;
import edu.ualberta.med.biobank.model.Dispatch;
import edu.ualberta.med.biobank.test.TestDatabase;
import edu.ualberta.med.biobank.test.Utils;
import edu.ualberta.med.biobank.test.internal.DispatchHelper;
import edu.ualberta.med.biobank.test.internal.SiteHelper;

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
