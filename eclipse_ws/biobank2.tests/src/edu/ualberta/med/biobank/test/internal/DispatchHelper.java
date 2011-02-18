package edu.ualberta.med.biobank.test.internal;

import java.util.Arrays;
import java.util.Date;

import edu.ualberta.med.biobank.common.wrappers.ActivityStatusWrapper;
import edu.ualberta.med.biobank.common.wrappers.AliquotWrapper;
import edu.ualberta.med.biobank.common.wrappers.CenterWrapper;
import edu.ualberta.med.biobank.common.wrappers.DispatchWrapper;
import edu.ualberta.med.biobank.common.wrappers.ShippingMethodWrapper;
import edu.ualberta.med.biobank.test.Utils;
import edu.ualberta.med.biobank.test.wrappers.TestCommon;

public class DispatchHelper extends DbHelper {

    public static DispatchWrapper newDispatch(CenterWrapper<?> sender,
        CenterWrapper<?> receiver, ShippingMethodWrapper method,
        String waybill, Date dateReceived, AliquotWrapper... aliquots)
        throws Exception {
        DispatchWrapper dispatch = new DispatchWrapper(appService);
        dispatch.setSender(sender);
        dispatch.setReceiver(receiver);
        dispatch.setShippingMethod(method);
        dispatch.setWaybill(waybill);
        dispatch.setActivityStatus(ActivityStatusWrapper.getActivityStatus(
            appService, ActivityStatusWrapper.ACTIVE_STATUS_STRING));
        if (dateReceived != null) {
            dispatch.setDateReceived(dateReceived);
        }

        dispatch.setDeparted(Utils.getRandomDate());

        if (aliquots != null) {
            dispatch.addAliquots(Arrays.asList(aliquots));
        }

        return dispatch;
    }

    public static DispatchWrapper newDispatch(CenterWrapper<?> sender,
        CenterWrapper<?> receiver, ShippingMethodWrapper method,
        String waybill, Date dateReceived) throws Exception {
        return newDispatch(sender, receiver, method, waybill, dateReceived,
            (AliquotWrapper[]) null);
    }

    public static DispatchWrapper newDispatch(CenterWrapper<?> sender,
        CenterWrapper<?> receiver, ShippingMethodWrapper method)
        throws Exception {
        return newDispatch(sender, receiver, method,
            TestCommon.getNewWaybill(r), Utils.getRandomDate());
    }

    public static DispatchWrapper addDispatch(CenterWrapper<?> sender,
        CenterWrapper<?> receiver, ShippingMethodWrapper method,
        String waybill, Date dateReceived, AliquotWrapper... containers)
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
