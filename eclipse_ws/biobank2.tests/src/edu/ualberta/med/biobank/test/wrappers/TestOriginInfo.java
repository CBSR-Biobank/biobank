package edu.ualberta.med.biobank.test.wrappers;

import java.util.Date;
import java.util.List;

import junit.framework.Assert;

import org.junit.Test;

import edu.ualberta.med.biobank.common.wrappers.ClinicWrapper;
import edu.ualberta.med.biobank.common.wrappers.OriginInfoWrapper;
import edu.ualberta.med.biobank.common.wrappers.ShipmentInfoWrapper;
import edu.ualberta.med.biobank.common.wrappers.ShippingMethodWrapper;
import edu.ualberta.med.biobank.test.TestDatabase;
import edu.ualberta.med.biobank.test.Utils;
import edu.ualberta.med.biobank.test.internal.ClinicHelper;
import edu.ualberta.med.biobank.test.internal.OriginInfoHelper;
import edu.ualberta.med.biobank.test.internal.ShipmentInfoHelper;

public class TestOriginInfo extends TestDatabase {

    @Test
    public void testGetShipmentsByDateReceived() throws Exception {
        String name = "testGetShipmentsByDateReceived" + r.nextInt();
        ClinicWrapper clinic = ClinicHelper.addClinic("clinic" + name);

        OriginInfoWrapper oi = OriginInfoHelper.newOriginInfo(clinic);
        Date dateReceived = Utils.getRandomDate();
        ShipmentInfoWrapper shipInfo = ShipmentInfoHelper.newShipmentInfo(
            clinic,
            ShippingMethodWrapper.getShippingMethods(appService).get(0),
            Utils.getRandomString(20), dateReceived);
        oi.setShipmentInfo(shipInfo);
        oi.persist();

        List<OriginInfoWrapper> res = OriginInfoWrapper
            .getShipmentsByDateReceived(appService, dateReceived, clinic);
        Assert.assertEquals(1, res.size());
        Assert.assertEquals(oi, res.get(0));

        res = OriginInfoWrapper.getShipmentsByDateReceived(appService,
            Utils.getRandomDate(), clinic);
        Assert.assertEquals(0, res.size());
    }

    @Test
    public void testGetTodayShipments() throws Exception {
        String name = "testGetTodayShipments" + r.nextInt();
        ClinicWrapper clinic = ClinicHelper.addClinic("clinic" + name);

        OriginInfoWrapper oi = OriginInfoHelper.newOriginInfo(clinic);
        ShipmentInfoWrapper shipInfo = ShipmentInfoHelper.newShipmentInfo(
            clinic,
            ShippingMethodWrapper.getShippingMethods(appService).get(0),
            Utils.getRandomString(20), new Date());
        oi.setShipmentInfo(shipInfo);
        oi.persist();

        List<OriginInfoWrapper> res = OriginInfoWrapper
            .getShipmentsByDateReceived(appService, new Date(), clinic);
        Assert.assertEquals(1, res.size());
        Assert.assertEquals(oi, res.get(0));

        res = OriginInfoWrapper.getShipmentsByDateReceived(appService,
            Utils.getRandomDate(), clinic);
        Assert.assertEquals(0, res.size());
    }
}
