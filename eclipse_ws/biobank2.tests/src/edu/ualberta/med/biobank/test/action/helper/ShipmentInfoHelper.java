package edu.ualberta.med.biobank.test.action.helper;

import edu.ualberta.med.biobank.common.action.info.ShipmentInfoSaveInfo;
import edu.ualberta.med.biobank.common.action.info.ShippingMethodInfo;
import edu.ualberta.med.biobank.common.action.shipment.ShippingMethodSaveAction;
import edu.ualberta.med.biobank.test.Utils;
import edu.ualberta.med.biobank.test.action.IActionExecutor;

public class ShipmentInfoHelper extends Helper {

    public static ShipmentInfoSaveInfo createRandomShipmentInfo(
        IActionExecutor actionExecutor) throws Exception {
        return new ShipmentInfoSaveInfo(null, Utils.getRandomString(5),
            Utils.getRandomDate(), Utils.getRandomDate(),
            Utils.getRandomString(10), createNewShippingMethod(actionExecutor));
    }

    public static ShippingMethodInfo createNewShippingMethod(
        IActionExecutor actionExecutor) throws Exception {
        ShippingMethodSaveAction action =
            new ShippingMethodSaveAction(null, Utils.getRandomString(5));
        Integer id = actionExecutor.exec(action).getId();
        return new ShippingMethodInfo(id);
    }
}
