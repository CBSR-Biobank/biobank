package test.ualberta.med.biobank.internal;

import java.util.Date;

import test.ualberta.med.biobank.Utils;
import edu.ualberta.med.biobank.common.wrappers.ClinicWrapper;
import edu.ualberta.med.biobank.common.wrappers.ShipmentWrapper;

public class ShipmentHelper extends DbHelper {

    public static ShipmentWrapper newShipment(ClinicWrapper clinic,
        String waybill, Date dateReceived) throws Exception {
        ShipmentWrapper shipment = new ShipmentWrapper(appService);
        shipment.setClinic(clinic);
        shipment.setWaybill(waybill);
        if (dateReceived != null) {
            shipment.setDateReceived(dateReceived);
        }

        return shipment;
    }

    public static ShipmentWrapper addShipment(ClinicWrapper clinic)
        throws Exception {
        ShipmentWrapper shipment = newShipment(clinic,
            Utils.getRandomString(5), Utils.getRandomDate());
        shipment.persist();
        return shipment;
    }

    public static ShipmentWrapper addShipment(ClinicWrapper clinic,
        String waybill, Date dateReceived) throws Exception {
        ShipmentWrapper shipment = newShipment(clinic, waybill, dateReceived);
        shipment.persist();
        return shipment;
    }

}
