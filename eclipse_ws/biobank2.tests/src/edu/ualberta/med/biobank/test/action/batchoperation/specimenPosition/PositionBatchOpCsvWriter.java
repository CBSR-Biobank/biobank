package edu.ualberta.med.biobank.test.action.batchoperation.specimenPosition;

import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import org.supercsv.cellprocessor.ConvertNullTo;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.io.CsvMapWriter;
import org.supercsv.io.ICsvMapWriter;
import org.supercsv.prefs.CsvPreference;

import edu.ualberta.med.biobank.common.action.batchoperation.specimenPosition.PositionBatchOpPojo;

public class PositionBatchOpCsvWriter {

    public static void write(String filename, List<PositionBatchOpPojo> pojos) throws IOException {
        final String[] header = new String[] {
            "inventoryId",
            "currentPalletLabel",
            "palletProductBarcode",
            "rootContainerType",
            "palletLabel",
            "palletPosition",
            "comment"
        };

        ICsvMapWriter writer = new CsvMapWriter(new FileWriter(filename),
                                                CsvPreference.EXCEL_PREFERENCE);

        final CellProcessor[] processing = new CellProcessor[] {
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

            for (PositionBatchOpPojo info : pojos) {
                final HashMap<String, ? super Object> data = new HashMap<String, Object>();
                data.put(header[0], info.getInventoryId());
                data.put(header[1], info.getCurrentPalletLabel());
                data.put(header[2], info.getPalletProductBarcode());
                data.put(header[3], info.getRootContainerType());
                data.put(header[4], info.getPalletLabel());
                data.put(header[5], info.getPalletPosition());
                data.put(header[6], info.getComment());
                writer.write(data, header, processing);
            }
        } finally {
            writer.close();
        }

    }

}
