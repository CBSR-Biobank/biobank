package edu.ualberta.med.biobank.test.internal;

import java.util.Arrays;
import java.util.Date;

import edu.ualberta.med.biobank.common.wrappers.CenterWrapper;
import edu.ualberta.med.biobank.common.wrappers.DispatchWrapper;
import edu.ualberta.med.biobank.common.wrappers.ShipmentInfoWrapper;
import edu.ualberta.med.biobank.common.wrappers.ShippingMethodWrapper;
import edu.ualberta.med.biobank.common.wrappers.SpecimenWrapper;
import edu.ualberta.med.biobank.test.Utils;
import edu.ualberta.med.biobank.test.wrappers.TestCommon;

public class DispatchHelper extends DbHelper {

    public static DispatchWrapper newDispatch(CenterWrapper<?> sender,
        CenterWrapper<?> receiver, ShippingMethodWrapper method,
        String waybill, Date dateReceived, SpecimenWrapper... aliquots)
        throws Exception {
        DispatchWrapper dispatch = new DispatchWrapper(appService);
        dispatch.setSenderCenter(sender);
        dispatch.setReceiverCenter(receiver);

        ShipmentInfoWrapper shipInfo = new ShipmentInfoWrapper(appService);
        dispatch.setShipmentInfo(shipInfo);

        shipInfo.setShippingMethod(method);
        shipInfo.setWaybill(waybill);

        if (dateReceived != null) {
            shipInfo.setReceivedAt(dateReceived);
        }

        shipInfo.setPackedAt(Utils.getRandomDate());

        if (aliquots != null) {
            dispatch.addSpecimens(Arrays.asList(aliquots));
        }

        return dispatch;
    }

    public static DispatchWrapper newDispatch(CenterWrapper<?> sender,
        CenterWrapper<?> receiver, ShippingMethodWrapper method,
        String waybill, Date dateReceived) throws Exception {
        return newDispatch(sender, receiver, method, waybill, dateReceived,
            (SpecimenWrapper[]) null);
    }

    public static DispatchWrapper newDispatch(CenterWrapper<?> sender,
        CenterWrapper<?> receiver, ShippingMethodWrapper method)
        throws Exception {
        return newDispatch(sender, receiver, method,
            TestCommon.getNewWaybill(r), Utils.getRandomDate());
    }

    public static DispatchWrapper addDispatch(CenterWrapper<?> sender,
        CenterWrapper<?> receiver, ShippingMethodWrapper method,
        String waybill, Date dateReceived, SpecimenWrapper... containers)
        throws Exception {
        DispatchWrapper dispatch = newDispatch(sender, receiver, method,
            waybill, dateReceived, containers);
        dispatch.persist();
        return dispatch;
    }

    public static DispatchWrapper addDispatch(CenterWrapper<?> sender,
        CenterWrapper<?> receiver, ShippingMethodWrapper method,
        String waybill, Date dateReceived) throws Exception {
        DispatchWrapper dispatch = newDispatch(sender, receiver, method,
            waybill, dateReceived);
        dispatch.persist();
        return dispatch;
    }

    public static DispatchWrapper addDispatch(CenterWrapper<?> sender,
        CenterWrapper<?> receiver, ShippingMethodWrapper method)
        throws Exception {
        DispatchWrapper dispatch = newDispatch(sender, receiver, method);
        dispatch.persist();
        return dispatch;
    }
}
