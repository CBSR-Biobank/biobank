package edu.ualberta.med.biobank.test.action.csvimport.patient;

import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Set;

import org.supercsv.io.CsvMapWriter;
import org.supercsv.io.ICsvMapWriter;
import org.supercsv.prefs.CsvPreference;

import edu.ualberta.med.biobank.common.action.batchoperation.patient.PatientBatchOpInputRow;
import edu.ualberta.med.biobank.common.formatters.DateFormatter;

/**
 * Used for testing the Patient CSV file Legacy Import feature.
 * 
 * @author Nelson Loyola
 * 
 */
@SuppressWarnings("nls")
class PatientCsvWriter {

    /**
     * Generates a Patient CSV file with random patient numbers.
     * 
     * @param filename The name of the CSV file to be saved.
     * @param patientInfos set of beans containing the information for each row.
     * @throws IOException If the file could not be saved.
     */
    static void write(String filename, Set<PatientBatchOpInputRow> patientInfos)
        throws IOException {
        final String[] header = new String[] {
            "Study", "Patient Number", "Created At"
        };

        ICsvMapWriter writer =
            new CsvMapWriter(new FileWriter(filename),
                CsvPreference.EXCEL_PREFERENCE);

        try {
            writer.writeHeader(header);

            for (PatientBatchOpInputRow info : patientInfos) {
                final HashMap<String, ? super Object> data =
                    new HashMap<String, Object>();
                data.put(header[0], info.getStudyName());
                data.put(header[1], info.getPatientNumber());
                data.put(header[2],
                    DateFormatter.formatAsDateTime(info.getCreatedAt()));
                writer.write(data, header);
            }
        } finally {
            writer.close();
        }
    }

}
