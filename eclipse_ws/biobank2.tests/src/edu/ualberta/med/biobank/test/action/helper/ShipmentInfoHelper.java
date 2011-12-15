package edu.ualberta.med.biobank.test.action.helper;

import edu.ualberta.med.biobank.common.action.info.ShipmentInfoSaveInfo;
import edu.ualberta.med.biobank.common.action.info.ShippingMethodInfo;
import edu.ualberta.med.biobank.common.action.shipment.ShippingMethodSaveAction;
import edu.ualberta.med.biobank.server.applicationservice.BiobankApplicationService;
import edu.ualberta.med.biobank.test.Utils;

public class ShipmentInfoHelper extends Helper {

    public static ShipmentInfoSaveInfo createRandomShipmentInfo(
        BiobankApplicationService appService) throws Exception {
        return new ShipmentInfoSaveInfo(null, Utils.getRandomString(5),
            Utils.getRandomDate(), Utils.getRandomDate(),
            Utils.getRandomString(10), createNewShippingMethod(appService));
    }

    public static ShippingMethodInfo createNewShippingMethod(
        BiobankApplicationService appService) throws Exception {
        ShippingMethodSaveAction action =
            new ShippingMethodSaveAction(null, Utils.getRandomString(5));
        Integer id = appService.doAction(action).getId();
        return new ShippingMethodInfo(id);
    }
}
