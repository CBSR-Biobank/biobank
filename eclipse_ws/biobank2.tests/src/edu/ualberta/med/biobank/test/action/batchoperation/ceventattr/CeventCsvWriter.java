package edu.ualberta.med.biobank.test.action.batchoperation.ceventattr;

import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Set;

import org.supercsv.cellprocessor.ConvertNullTo;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.io.CsvMapWriter;
import org.supercsv.io.ICsvMapWriter;
import org.supercsv.prefs.CsvPreference;

import edu.ualberta.med.biobank.common.action.batchoperation.ceventattr.CeventAttrBatchOpInputPojo;

/**
 * Used for testing the Collection Event Attribute CSV Import feature.
 * 
 * @author Nelson Loyola
 * 
 */
public class CeventCsvWriter {

    /**
     * Generates a Collection Event Attribute CSV file with patient numbers.
     */
    static void write(String filename, Set<CeventAttrBatchOpInputPojo> pojos)
        throws IOException {

        final String[] header = new String[] {
            "Patient Number", "Visit Number", "Attribute Name", "Attribute Value"
        };

        ICsvMapWriter writer = new CsvMapWriter(new FileWriter(filename),
            CsvPreference.EXCEL_PREFERENCE);

        final CellProcessor[] processing = new CellProcessor[] {
            new ConvertNullTo(""),
            new ConvertNullTo(""),
            new ConvertNullTo(""),
            new ConvertNullTo("")
        };

        try {
            writer.writeHeader(header);

            for (CeventAttrBatchOpInputPojo pojo : pojos) {
                final HashMap<String, ? super Object> data = new HashMap<String, Object>();
                data.put(header[0], pojo.getPatientNumber());
                data.put(header[1], pojo.getVisitNumber());
                data.put(header[2], pojo.getAttrName());
                data.put(header[3], pojo.getAttrValue());
                writer.write(data, header, processing);
            }
        } finally {
            writer.close();
        }
    }

}
