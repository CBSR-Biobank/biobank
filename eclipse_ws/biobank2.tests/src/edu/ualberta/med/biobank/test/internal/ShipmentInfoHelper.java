package edu.ualberta.med.biobank.test.internal;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import edu.ualberta.med.biobank.common.wrappers.CenterWrapper;
import edu.ualberta.med.biobank.common.wrappers.ShipmentInfoWrapper;
import edu.ualberta.med.biobank.common.wrappers.ShippingMethodWrapper;
import edu.ualberta.med.biobank.common.wrappers.SpecimenWrapper;
import edu.ualberta.med.biobank.test.Utils;
import edu.ualberta.med.biobank.test.wrappers.TestCommon;

public class ShipmentInfoHelper extends DbHelper {

    private static List<ShipmentInfoWrapper> createdShipInfos = new ArrayList<ShipmentInfoWrapper>();

    public static ShipmentInfoWrapper newShipmentInfo(CenterWrapper<?> center,
        ShippingMethodWrapper method, String waybill, Date dateReceived,
        SpecimenWrapper... spcs) throws Exception {
        ShipmentInfoWrapper shipInfo = new ShipmentInfoWrapper(appService);

        for (SpecimenWrapper spc : spcs) {
            spc.getOriginInfo().setShipmentInfo(shipInfo);
            if (center != null) {
                spc.setCurrentCenter(center);
                spc.getOriginInfo().setCenter(center);
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
        return newShipmentInfo(center, method, TestCommon.getNewWaybill(r),
            Utils.getRandomDate());
    }

    public static ShipmentInfoWrapper addShipmentInfo(CenterWrapper<?> center,
        ShippingMethodWrapper method, String waybill, Date dateReceived,
        SpecimenWrapper... spcs) throws Exception {
        ShipmentInfoWrapper shipInfo = newShipmentInfo(center, method, waybill,
            dateReceived, spcs);
        shipInfo.persist();
        createdShipInfos.add(shipInfo);
        return shipInfo;
    }

    public static ShipmentInfoWrapper addShipmentInfo(CenterWrapper<?> center,
        ShippingMethodWrapper method, String waybill, SpecimenWrapper... spcs)
        throws Exception {
        return addShipmentInfo(center, method, waybill, Utils.getRandomDate(), spcs);
    }

    public static ShipmentInfoWrapper addShipmentInfo(CenterWrapper<?> center,
        ShippingMethodWrapper method, SpecimenWrapper... spcs) throws Exception {
        return addShipmentInfo(center, method, TestCommon.getNewWaybill(r),
            Utils.getRandomDate(), spcs);
    }

    public static void deleteCreatedShipInfos() throws Exception {
        DbHelper.deleteFromList(createdShipInfos);
        createdShipInfos.clear();
    }

}
