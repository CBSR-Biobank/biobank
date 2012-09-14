package edu.ualberta.med.biobank.test.action.csvimport.shipment;

import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Set;

import org.supercsv.io.CsvMapWriter;
import org.supercsv.io.ICsvMapWriter;
import org.supercsv.prefs.CsvPreference;

import edu.ualberta.med.biobank.common.action.batchoperation.shipment.ShipmentBatchOpInputRow;
import edu.ualberta.med.biobank.common.formatters.DateFormatter;

/**
 * Used for testing the Shipment CSV file Legacy Import feature.
 * 
 * @author loyola
 * 
 */
@SuppressWarnings("nls")
class ShipmentCsvWriter {

    public static void write(String filename, Set<ShipmentBatchOpInputRow> shipmentInfos)
        throws IOException {
        final String[] header = new String[] {
            "Received date time", "Sending center", "Receiving center",
            "Shipping method", "Waybill", "Comment"
        };

        ICsvMapWriter writer =
            new CsvMapWriter(new FileWriter(filename),
                CsvPreference.EXCEL_PREFERENCE);

        try {
            writer.writeHeader(header);

            for (ShipmentBatchOpInputRow info : shipmentInfos) {
                final HashMap<String, ? super Object> data =
                    new HashMap<String, Object>();
                data.put(header[0],
                    DateFormatter.formatAsDateTime(info.getDateReceived()));
                data.put(header[1], info.getSendingCenter());
                data.put(header[2], info.getReceivingCenter());
                data.put(header[3], info.getShippingMethod());
                data.put(header[4], info.getWaybill());
                data.put(header[5], info.getComment());
                writer.write(data, header);
            }
        } finally {
            writer.close();
        }

    }

}
