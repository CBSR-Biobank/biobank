package test.ualberta.med.biobank.internal;

import java.util.Date;

import edu.ualberta.med.biobank.common.wrappers.ClinicWrapper;
import edu.ualberta.med.biobank.common.wrappers.ShipmentWrapper;

public class ShipmentHelper extends DbHelper {

    public static ShipmentWrapper newShipment(ClinicWrapper clinic,
        Date dateDrawn) throws Exception {
        ShipmentWrapper shipment = new ShipmentWrapper(appService);
        shipment.setClinic(clinic);
        if (dateDrawn != null) {
            shipment.setDateDrawn(dateDrawn);
        }

        return shipment;
    }

    public static ShipmentWrapper addShipment(ClinicWrapper clinic)
        throws Exception {
        ShipmentWrapper shipment = newShipment(clinic, null);
        shipment.persist();
        return shipment;
    }

    public static ShipmentWrapper addShipment(ClinicWrapper clinic,
        Date dateDrawn) throws Exception {
        ShipmentWrapper shipment = newShipment(clinic, dateDrawn);
        shipment.persist();
        return shipment;
    }

}
