package edu.ualberta.med.biobank.test.internal;

import java.util.Date;

import edu.ualberta.med.biobank.common.wrappers.CenterWrapper;
import edu.ualberta.med.biobank.common.wrappers.ShipmentInfoWrapper;
import edu.ualberta.med.biobank.common.wrappers.ShippingMethodWrapper;
import edu.ualberta.med.biobank.common.wrappers.SpecimenWrapper;
import edu.ualberta.med.biobank.test.Utils;
import edu.ualberta.med.biobank.test.wrappers.TestCommon;

public class ShipmentInfoHelper extends DbHelper {

    public static ShipmentInfoWrapper newShipment(CenterWrapper<?> center,
        ShippingMethodWrapper method, String waybill, Date dateReceived,
        SpecimenWrapper... spcs) throws Exception {
        ShipmentInfoWrapper shipInfo = new ShipmentInfoWrapper(appService);

        for (SpecimenWrapper spc : spcs) {
            spc.getOriginInfo().setShipmentInfo(shipInfo);
            if (center != null) {
                spc.setCurrentCenter(center);
            }
        }

        shipInfo.setShippingMethod(method);
        shipInfo.setWaybill(waybill);
        if (dateReceived != null) {
            shipInfo.setReceivedAt(dateReceived);
        }

        shipInfo.setPackedAt(Utils.getRandomDate());

        return shipInfo;
    }

    public static ShipmentInfoWrapper newShipment(CenterWrapper<?> center,
        ShippingMethodWrapper method) throws Exception {
        return newShipment(center, method, TestCommon.getNewWaybill(r),
            Utils.getRandomDate());
    }

    public static ShipmentInfoWrapper addShipment(CenterWrapper<?> center,
        ShippingMethodWrapper method, String waybill, Date dateReceived,
        SpecimenWrapper... spcs) throws Exception {
        ShipmentInfoWrapper shipInfo = newShipment(center, method, waybill,
            dateReceived, spcs);
        shipInfo.persist();
        return shipInfo;
    }

    public static ShipmentInfoWrapper addShipment(CenterWrapper<?> center,
        ShippingMethodWrapper method, String waybill, SpecimenWrapper... spcs)
        throws Exception {
        return addShipment(center, method, waybill, Utils.getRandomDate(), spcs);
    }

    public static ShipmentInfoWrapper addShipment(CenterWrapper<?> center,
        ShippingMethodWrapper method, SpecimenWrapper... spcs) throws Exception {
        return addShipment(center, method, TestCommon.getNewWaybill(r),
            Utils.getRandomDate(), spcs);
    }

}
