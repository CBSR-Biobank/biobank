package edu.ualberta.med.biobank.test.internal;

import java.util.Arrays;
import java.util.Date;

import edu.ualberta.med.biobank.common.wrappers.DispatchContainerWrapper;
import edu.ualberta.med.biobank.common.wrappers.DispatchShipmentWrapper;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;
import edu.ualberta.med.biobank.test.Utils;
import edu.ualberta.med.biobank.test.wrappers.TestCommon;

public class DispatchShipmentHelper extends DbHelper {

    public static DispatchShipmentWrapper newShipment(SiteWrapper sender,
        SiteWrapper receiver, String waybill, Date dateReceived,
        DispatchContainerWrapper... containers) throws Exception {
        DispatchShipmentWrapper shipment = new DispatchShipmentWrapper(
            appService);
        if (sender != null) {
            shipment.setSender(sender);
        }
        if (receiver != null) {
            shipment.setReceiver(receiver);
        }
        shipment.setWaybill(waybill);
        if (dateReceived != null) {
            shipment.setDateReceived(dateReceived);
        }

        shipment.setDateShipped(Utils.getRandomDate());

        if (containers != null) {
            shipment.addSentContainers(Arrays.asList(containers));
        }

        return shipment;
    }

    public static DispatchShipmentWrapper newShipment(SiteWrapper sender,
        SiteWrapper receiver, String waybill, Date dateReceived)
        throws Exception {
        return newShipment(sender, receiver, waybill, dateReceived,
            (DispatchContainerWrapper[]) null);
    }

    public static DispatchShipmentWrapper newShipment(SiteWrapper sender,
        SiteWrapper receiver) throws Exception {
        return newShipment(sender, receiver, TestCommon.getNewWaybill(r),
            Utils.getRandomDate());
    }

    public static DispatchShipmentWrapper addShipment(SiteWrapper sender,
        SiteWrapper receiver, String waybill, Date dateReceived,
        DispatchContainerWrapper... containers) throws Exception {
        DispatchShipmentWrapper shipment = newShipment(sender, receiver,
            waybill, Utils.getRandomDate(), containers);
        shipment.persist();
        return shipment;
    }

    public static DispatchShipmentWrapper addShipment(SiteWrapper sender,
        SiteWrapper receiver, String waybill, Date dateReceived)
        throws Exception {
        DispatchShipmentWrapper shipment = newShipment(sender, receiver,
            waybill, Utils.getRandomDate());
        shipment.persist();
        return shipment;
    }

    public static DispatchShipmentWrapper addShipment(SiteWrapper sender,
        SiteWrapper receiver) throws Exception {
        DispatchShipmentWrapper shipment = newShipment(sender, receiver);
        shipment.persist();
        return shipment;
    }

    public static DispatchShipmentWrapper addShipmentRandomContainers(
        SiteWrapper sender, SiteWrapper receiver, String name, int numContainers)
        throws Exception {
        DispatchShipmentWrapper shipment = newShipment(sender, receiver);
        shipment.persist();

        DispatchContainerWrapper container;
        for (int i = 0; i < numContainers; ++i) {
            container = DispatchContainerHelper
                .addContainerRandom(sender, name);
        }

        return shipment;
    }

}
