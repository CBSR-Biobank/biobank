package edu.ualberta.med.biobank.test.internal;

import java.util.Arrays;
import java.util.Date;

import edu.ualberta.med.biobank.common.wrappers.AliquotWrapper;
import edu.ualberta.med.biobank.common.wrappers.DispatchWrapper;
import edu.ualberta.med.biobank.common.wrappers.ShippingMethodWrapper;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;
import edu.ualberta.med.biobank.common.wrappers.StudyWrapper;
import edu.ualberta.med.biobank.test.Utils;
import edu.ualberta.med.biobank.test.wrappers.TestCommon;

public class DispatchHelper extends DbHelper {

    public static DispatchWrapper newShipment(SiteWrapper sender,
        SiteWrapper receiver, StudyWrapper study, ShippingMethodWrapper method,
        String waybill, Date dateReceived, AliquotWrapper... aliquots)
        throws Exception {
        DispatchWrapper shipment =
            new DispatchWrapper(appService);
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
            shipment.addNewAliquots(Arrays.asList(aliquots));
        }

        return shipment;
    }

    public static DispatchWrapper newShipment(SiteWrapper sender,
        SiteWrapper receiver, StudyWrapper study, ShippingMethodWrapper method,
        String waybill, Date dateReceived) throws Exception {
        return newShipment(sender, receiver, study, method, waybill,
            dateReceived, (AliquotWrapper[]) null);
    }

    public static DispatchWrapper newShipment(SiteWrapper sender,
        SiteWrapper receiver, StudyWrapper study, ShippingMethodWrapper method)
        throws Exception {
        return newShipment(sender, receiver, study, method,
            TestCommon.getNewWaybill(r), Utils.getRandomDate());
    }

    public static DispatchWrapper addShipment(SiteWrapper sender,
        SiteWrapper receiver, StudyWrapper study, ShippingMethodWrapper method,
        String waybill, Date dateReceived, AliquotWrapper... containers)
        throws Exception {
        DispatchWrapper shipment =
            newShipment(sender, receiver, study, method, waybill, dateReceived,
                containers);
        shipment.persist();
        return shipment;
    }

    public static DispatchWrapper addShipment(SiteWrapper sender,
        SiteWrapper receiver, StudyWrapper study, ShippingMethodWrapper method,
        String waybill, Date dateReceived) throws Exception {
        DispatchWrapper shipment =
            newShipment(sender, receiver, study, method, waybill, dateReceived);
        shipment.persist();
        return shipment;
    }

    public static DispatchWrapper addShipment(SiteWrapper sender,
        SiteWrapper receiver, StudyWrapper study, ShippingMethodWrapper method)
        throws Exception {
        DispatchWrapper shipment =
            newShipment(sender, receiver, study, method);
        shipment.persist();
        return shipment;
    }

}
