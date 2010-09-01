package edu.ualberta.med.biobank.test.internal;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import edu.ualberta.med.biobank.common.wrappers.ActivityStatusWrapper;
import edu.ualberta.med.biobank.common.wrappers.AliquotWrapper;
import edu.ualberta.med.biobank.common.wrappers.DispatchShipmentWrapper;
import edu.ualberta.med.biobank.common.wrappers.ShippingMethodWrapper;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;
import edu.ualberta.med.biobank.test.Utils;
import edu.ualberta.med.biobank.test.wrappers.TestCommon;

public class DispatchShipmentHelper extends DbHelper {

    public static DispatchShipmentWrapper newShipment(SiteWrapper sender,
        SiteWrapper receiver, ShippingMethodWrapper method, String waybill,
        Date dateReceived, AliquotWrapper... aliquots) throws Exception {
        DispatchShipmentWrapper shipment = new DispatchShipmentWrapper(
            appService);
        if (sender != null) {
            shipment.setSender(sender);
        }
        if (receiver != null) {
            shipment.setReceiver(receiver);
        }
        shipment.setActivityStatus(ActivityStatusWrapper
            .getActiveActivityStatus(appService));
        shipment.setShippingMethod(method);
        shipment.setWaybill(waybill);
        if (dateReceived != null) {
            shipment.setDateReceived(dateReceived);
        }

        shipment.setDateShipped(Utils.getRandomDate());

        if (aliquots != null) {
            shipment.addAliquots(Arrays.asList(aliquots));
        }

        return shipment;
    }

    public static DispatchShipmentWrapper newShipment(SiteWrapper sender,
        SiteWrapper receiver, ShippingMethodWrapper method, String waybill,
        Date dateReceived) throws Exception {
        return newShipment(sender, receiver, method, waybill, dateReceived);
    }

    public static DispatchShipmentWrapper newShipment(SiteWrapper sender,
        SiteWrapper receiver, ShippingMethodWrapper method) throws Exception {
        return newShipment(sender, receiver, method,
            TestCommon.getNewWaybill(r), Utils.getRandomDate());
    }

    public static DispatchShipmentWrapper addShipment(SiteWrapper sender,
        SiteWrapper receiver, ShippingMethodWrapper method, String waybill,
        Date dateReceived, AliquotWrapper... containers) throws Exception {
        DispatchShipmentWrapper shipment = newShipment(sender, receiver,
            method, waybill, dateReceived, containers);
        shipment.persist();
        return shipment;
    }

    public static DispatchShipmentWrapper addShipment(SiteWrapper sender,
        SiteWrapper receiver, ShippingMethodWrapper method, String waybill,
        Date dateReceived) throws Exception {
        DispatchShipmentWrapper shipment = newShipment(sender, receiver,
            method, waybill, dateReceived);
        shipment.persist();
        return shipment;
    }

    public static DispatchShipmentWrapper addShipment(SiteWrapper sender,
        SiteWrapper receiver, ShippingMethodWrapper method) throws Exception {
        DispatchShipmentWrapper shipment = newShipment(sender, receiver, method);
        shipment.persist();
        return shipment;
    }

    public static DispatchShipmentWrapper addShipmentRandomAliquots(
        SiteWrapper sender, SiteWrapper receiver, ShippingMethodWrapper method,
        String waybill, Date dateReceived, String name, int numAliquots)
        throws Exception {
        DispatchShipmentWrapper shipment = newShipment(sender, receiver, method);
        shipment.persist();

        List<AliquotWrapper> aliquots = new ArrayList<AliquotWrapper>();
        for (int i = 0; i < numAliquots; ++i) {
            // aliquots.add(DispatchContainerHelper.addContainerRandom(sender,
            // shipment, name));
            // FIXME need to add aliquots with position
        }

        shipment.addAliquots(aliquots);
        return shipment;
    }

    public static DispatchShipmentWrapper addShipmentRandomContainers(
        SiteWrapper sender, SiteWrapper receiver, ShippingMethodWrapper method,
        String name, int numContainers) throws Exception {
        return addShipmentRandomAliquots(sender, receiver, method,
            TestCommon.getNewWaybill(r), Utils.getRandomDate(), name,
            numContainers);
    }

}
