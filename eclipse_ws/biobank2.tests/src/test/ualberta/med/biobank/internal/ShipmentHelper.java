package test.ualberta.med.biobank.internal;

import test.ualberta.med.biobank.Utils;
import edu.ualberta.med.biobank.common.wrappers.ClinicWrapper;
import edu.ualberta.med.biobank.common.wrappers.ShipmentWrapper;

public class ShipmentHelper extends DbHelper {

    public static ShipmentWrapper newShipment(ClinicWrapper clinic,
        String waybill) throws Exception {
        ShipmentWrapper shipment = new ShipmentWrapper(appService);
        shipment.setClinic(clinic);
        shipment.setWaybill(waybill);

        return shipment;
    }

    public static ShipmentWrapper addShipment(ClinicWrapper clinic)
        throws Exception {
        ShipmentWrapper shipment = newShipment(clinic, Utils.getRandomString(5));
        shipment.persist();
        return shipment;
    }

    public static ShipmentWrapper addShipment(ClinicWrapper clinic,
        String waybill) throws Exception {
        ShipmentWrapper shipment = newShipment(clinic, waybill);
        shipment.persist();
        return shipment;
    }

}
