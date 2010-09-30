package edu.ualberta.med.biobank.test.internal;

import java.util.Arrays;
import java.util.Date;

import edu.ualberta.med.biobank.common.wrappers.AliquotWrapper;
import edu.ualberta.med.biobank.common.wrappers.DispatchShipmentAliquotWrapper.STATE;
import edu.ualberta.med.biobank.common.wrappers.DispatchShipmentWrapper;
import edu.ualberta.med.biobank.common.wrappers.ShippingMethodWrapper;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;
import edu.ualberta.med.biobank.common.wrappers.StudyWrapper;
import edu.ualberta.med.biobank.test.Utils;
import edu.ualberta.med.biobank.test.wrappers.TestCommon;

public class DispatchShipmentHelper extends DbHelper {

    public static DispatchShipmentWrapper newShipment(SiteWrapper sender,
        SiteWrapper receiver, StudyWrapper study, ShippingMethodWrapper method,
        String waybill, Date dateReceived, AliquotWrapper... aliquots)
        throws Exception {
        DispatchShipmentWrapper shipment =
            new DispatchShipmentWrapper(appService);
        shipment.setSender(sender);
        shipment.setReceiver(receiver);
        shipment.setStudy(study);
        shipment.setShippingMethod(method);
        shipment.setWaybill(waybill);
        if (dateReceived != null) {
            shipment.setDateReceived(dateReceived);
        }

        shipment.setDateShipped(Utils.getRandomDate());

        if (aliquots != null) {
            shipment.addAliquots(Arrays.asList(aliquots), STATE.NONE_STATE);
        }

        return shipment;
    }

    public static DispatchShipmentWrapper newShipment(SiteWrapper sender,
        SiteWrapper receiver, StudyWrapper study, ShippingMethodWrapper method,
        String waybill, Date dateReceived) throws Exception {
        return newShipment(sender, receiver, study, method, waybill,
            dateReceived, (AliquotWrapper[]) null);
    }

    public static DispatchShipmentWrapper newShipment(SiteWrapper sender,
        SiteWrapper receiver, StudyWrapper study, ShippingMethodWrapper method)
        throws Exception {
        return newShipment(sender, receiver, study, method,
            TestCommon.getNewWaybill(r), Utils.getRandomDate());
    }

    public static DispatchShipmentWrapper addShipment(SiteWrapper sender,
        SiteWrapper receiver, StudyWrapper study, ShippingMethodWrapper method,
        String waybill, Date dateReceived, AliquotWrapper... containers)
        throws Exception {
        DispatchShipmentWrapper shipment =
            newShipment(sender, receiver, study, method, waybill, dateReceived,
                containers);
        shipment.persist();
        return shipment;
    }

    public static DispatchShipmentWrapper addShipment(SiteWrapper sender,
        SiteWrapper receiver, StudyWrapper study, ShippingMethodWrapper method,
        String waybill, Date dateReceived) throws Exception {
        DispatchShipmentWrapper shipment =
            newShipment(sender, receiver, study, method, waybill, dateReceived);
        shipment.persist();
        return shipment;
    }

    public static DispatchShipmentWrapper addShipment(SiteWrapper sender,
        SiteWrapper receiver, StudyWrapper study, ShippingMethodWrapper method)
        throws Exception {
        DispatchShipmentWrapper shipment =
            newShipment(sender, receiver, study, method);
        shipment.persist();
        return shipment;
    }

}
