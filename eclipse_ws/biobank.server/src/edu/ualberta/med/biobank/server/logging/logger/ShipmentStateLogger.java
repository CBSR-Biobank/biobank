package edu.ualberta.med.biobank.server.logging.logger;

import java.util.Date;

import edu.ualberta.med.biobank.model.Log;
import edu.ualberta.med.biobank.model.Shipment;

public class ShipmentStateLogger extends BiobankObjectStateLogger {

    protected ShipmentStateLogger() {
    }

    @Override
    protected Log getLogObject(Object obj) {
        if (obj instanceof Shipment) {
            Shipment ship = (Shipment) obj;
            Log log = new Log();
            String details = "";
            Date dateReceived = ship.getDateReceived();
            if (dateReceived != null) {
                details = "Received:" + dateTimeFormatter.format(dateReceived);
            }
            String waybill = ship.getWaybill();
            if (waybill != null) {
                details += " - Waybill:" + waybill;
            }
            log.setDetails(details);
            log.setType("Shipment");
            return log;
        }
        return null;
    }

}
