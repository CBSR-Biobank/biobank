package test.ualberta.med.biobank.internal;

import edu.ualberta.med.biobank.common.wrappers.ClinicWrapper;
import edu.ualberta.med.biobank.common.wrappers.ShipmentWrapper;

public class ShipmentHelper extends DbHelper {

    public static ShipmentWrapper newShipment(ClinicWrapper clinic)
        throws Exception {
        ShipmentWrapper shipment = new ShipmentWrapper(appService);
        shipment.setClinic(clinic);

        return shipment;
    }

    public static ShipmentWrapper addShipment(ClinicWrapper clinic)
        throws Exception {
        ShipmentWrapper shipment = newShipment(clinic);
        shipment.persist();
        return shipment;
    }

}
