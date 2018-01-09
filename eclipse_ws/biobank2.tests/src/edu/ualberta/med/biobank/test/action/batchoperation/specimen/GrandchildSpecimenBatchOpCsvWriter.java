package edu.ualberta.med.biobank.test.action.batchoperation.specimen;

import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Set;

import org.supercsv.cellprocessor.ConvertNullTo;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.io.CsvMapWriter;
import org.supercsv.io.ICsvMapWriter;
import org.supercsv.prefs.CsvPreference;

import edu.ualberta.med.biobank.common.action.batchoperation.specimen.GrandchildSpecimenBatchOpInputPojo;
import edu.ualberta.med.biobank.common.formatters.DateFormatter;

public class GrandchildSpecimenBatchOpCsvWriter {

    /**
     * Generates a Grandchild Specimens CSV file with random patient numbers.
     *
     * @param filename The name of the CSV file to be saved.
     * @param pojos The information to write to the CSV file.
     *
     * @throws IOException If the file could not be saved.
     */
    @SuppressWarnings("nls")
    static void write(String filename,
                      Set<GrandchildSpecimenBatchOpInputPojo> pojos)
                          throws IOException {
        final String[] header = new String[] {
            "inventoryId",
            "parentInventoryID",
            "volume",
            "specimenType",
            "createdAt",
            "patientNumber",
            "originCenter",
            "currentCenter",
            "palletProductBarcode",
            "rootContainerType",
            "palletLabel",
            "palletPosition",
            "comment"
        };

        ICsvMapWriter writer =
            new CsvMapWriter(new FileWriter(filename), CsvPreference.EXCEL_PREFERENCE);

        final CellProcessor[] processing = new CellProcessor[] {
            new ConvertNullTo(""),
            new ConvertNullTo(""),
            new ConvertNullTo(""),
            new ConvertNullTo(""),
            new ConvertNullTo(""),
            new ConvertNullTo(""),
            new ConvertNullTo(""),
            new ConvertNullTo(""),
            new ConvertNullTo(""),
            new ConvertNullTo(""),
            new ConvertNullTo(""),
            new ConvertNullTo(""),
            new ConvertNullTo("")
        };

        try {
            writer.writeHeader(header);

            for (GrandchildSpecimenBatchOpInputPojo pojo : pojos) {
                final HashMap<String, ? super Object> data = new HashMap<String, Object>();
                data.put(header[0], pojo.getInventoryId());
                data.put(header[1], pojo.getParentInventoryId());
                data.put(header[2], pojo.getVolume());
                data.put(header[3], pojo.getSpecimenType());
                data.put(header[4], DateFormatter.formatAsDateTime(pojo.getCreatedAt()));
                data.put(header[5], pojo.getPatientNumber());
                data.put(header[6], pojo.getOriginCenter());
                data.put(header[7], pojo.getCurrentCenter());
                data.put(header[8], pojo.getPalletProductBarcode());
                data.put(header[9], pojo.getRootContainerType());
                data.put(header[10], pojo.getPalletLabel());
                data.put(header[11], pojo.getPalletPosition());
                data.put(header[12], pojo.getComment());
                writer.write(data, header, processing);
            }
        } finally {
            writer.close();
        }
    }

}
