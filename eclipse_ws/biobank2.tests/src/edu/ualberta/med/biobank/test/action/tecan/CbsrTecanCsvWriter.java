package edu.ualberta.med.biobank.test.action.tecan;

import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Set;

import org.supercsv.cellprocessor.ConvertNullTo;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.io.CsvMapWriter;
import org.supercsv.io.ICsvMapWriter;
import org.supercsv.prefs.CsvPreference;

import edu.ualberta.med.biobank.common.action.tecan.CbsrTecanCsvRow;

/**
 * Used for testing the Tecan CSV file.
 *
 * @author loyola
 *
 */
public class CbsrTecanCsvWriter {

    /**
     * Generates a Tecan CSV file.
     */
    @SuppressWarnings("nls")
    static void write(String filename, Set<CbsrTecanCsvRow> csvRows)
        throws IOException {
        final String[] header = new String[] {
            "rackId",
            "cavityId",
            "position",
            "sourceId",
            "concentration",
            "concentrationUnit",
            "volume",
            "userDefined1",
            "userDefined2",
            "userDefined3",
            "userDefined4",
            "userDefined5",
            "plateErrors",
            "samplEerrors",
            "sampleInstanceId",
            "sampleId",
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

            for (CbsrTecanCsvRow csvRow : csvRows) {
                final HashMap<String, ? super Object> data =
                    new HashMap<String, Object>();
                data.put(header[0], csvRow.getRackId());
                data.put(header[1], csvRow.getCavityId());
                data.put(header[2], csvRow.getPosition());
                data.put(header[3], csvRow.getSourceId());
                data.put(header[4], csvRow.getConcentration());
                data.put(header[5], csvRow.getConcentrationUnit());
                data.put(header[6], csvRow.getVolume());
                data.put(header[7], csvRow.getUserDefined1());
                data.put(header[8], csvRow.getUserDefined2());
                data.put(header[9], csvRow.getUserDefined3());
                data.put(header[10], csvRow.getUserDefined4());
                data.put(header[11], csvRow.getUserDefined5());
                data.put(header[12], csvRow.getPlateErrors());
                data.put(header[13], csvRow.getSamplEerrors());
                data.put(header[14], csvRow.getSampleInstanceId());
                data.put(header[15], csvRow.getSampleId());

                writer.write(data, header, processing);
            }
        } finally {
            writer.close();
        }
    }

}
