package edu.ualberta.med.biobank.test.internal;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import edu.ualberta.med.biobank.common.util.DispatchSpecimenState;
import edu.ualberta.med.biobank.common.wrappers.CenterWrapper;
import edu.ualberta.med.biobank.common.wrappers.DispatchWrapper;
import edu.ualberta.med.biobank.common.wrappers.ShipmentInfoWrapper;
import edu.ualberta.med.biobank.common.wrappers.ShippingMethodWrapper;
import edu.ualberta.med.biobank.common.wrappers.SpecimenWrapper;
import edu.ualberta.med.biobank.test.Utils;
import edu.ualberta.med.biobank.test.wrappers.TestCommon;

public class DispatchHelper extends DbHelper {

    public static List<DispatchWrapper> createdDispatches = new ArrayList<DispatchWrapper>();

    public static DispatchWrapper newDispatch(CenterWrapper<?> sender,
        CenterWrapper<?> receiver, ShippingMethodWrapper method,
        String waybill, Date dateReceived, SpecimenWrapper... specimens)
        throws Exception {
        DispatchWrapper dispatch = new DispatchWrapper(appService);
        dispatch.setSenderCenter(sender);
        dispatch.setReceiverCenter(receiver);

        ShipmentInfoWrapper shipInfo = new ShipmentInfoWrapper(appService);
        dispatch.setShipmentInfo(shipInfo);

        shipInfo.setShippingMethod(method);

        if (waybill != null) {
            shipInfo.setWaybill(waybill);
        }

        if (dateReceived != null) {
            shipInfo.setReceivedAt(dateReceived);
        }

        shipInfo.setPackedAt(Utils.getRandomDate());

        if (specimens != null) {
            dispatch.addSpecimens(Arrays.asList(specimens),
                DispatchSpecimenState.NONE);
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
        String waybill, Date dateReceived, boolean addToCreatedList,
        SpecimenWrapper... containers) throws Exception {
        DispatchWrapper dispatch = newDispatch(sender, receiver, method,
            waybill, dateReceived, containers);
        dispatch.persist();
        dispatch.reload();
        if (addToCreatedList) {
            createdDispatches.add(dispatch);
        }
        return dispatch;

    }

    public static DispatchWrapper addDispatch(CenterWrapper<?> sender,
        CenterWrapper<?> receiver, ShippingMethodWrapper method,
        String waybill, Date dateReceived, SpecimenWrapper... containers)
        throws Exception {
        return addDispatch(sender, receiver, method, waybill, dateReceived,
            true, containers);
    }

    public static DispatchWrapper addDispatch(CenterWrapper<?> sender,
        CenterWrapper<?> receiver, ShippingMethodWrapper method,
        String waybill, Date dateReceived) throws Exception {
        return addDispatch(sender, receiver, method, waybill, dateReceived,
            true);
    }

    public static DispatchWrapper addDispatch(CenterWrapper<?> sender,
        CenterWrapper<?> receiver, ShippingMethodWrapper method)
        throws Exception {
        return addDispatch(sender, receiver, method, null, null, true);
    }

    public static void deleteCreatedDispatches() throws Exception {
        DbHelper.deleteDispatches(createdDispatches);
        createdDispatches.clear();
    }
}
