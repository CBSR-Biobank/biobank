package edu.ualberta.med.biobank.export;

import java.io.FileWriter;
import java.io.IOException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.supercsv.io.CsvListWriter;
import org.supercsv.prefs.CsvPreference;

import edu.ualberta.med.biobank.SessionManager;

public class CsvDataExporter extends GuiDataExporter {
    private static final String[] VALID_EXTS = { "*.csv" }; 

    public CsvDataExporter() {
        super("Export CSV");
    }

    @Override
    protected void export(Data data, ITableLabelProvider labelProvider,
        IProgressMonitor monitor) throws DataExportException {
        canExport(data);

        String path = getPath(data, VALID_EXTS);

        CsvListWriter writer = null;

        try {
            writer = new CsvListWriter(new FileWriter(path),
                CsvPreference.STANDARD_PREFERENCE);

            // can only write column names to a CSV, no comments, title, etc.
            // are allowed b/c cannot have comments in the file, only headers
            // (column names) and data
            writer.writeHeader(data.getColumnNames().toArray(new String[0]));

            int numHeaders = data.getColumnNames().size();

            Object[] labels = new Object[numHeaders];
            for (Object row : data.getRows()) {
                if (monitor.isCanceled()) {
                    return;
                }

                for (int i = 0; i < numHeaders; i++) {
                    labels[i] = labelProvider.getColumnText(row, i);
                }

                writer.write(labels);
            }

            writer.close();
            SessionManager.log("exportCSV",
                data.getTitle(), LOG_TYPE);
        } catch (IOException e) {
            throw new DataExportException(e.getMessage());
        } catch (Exception e) {
            return;
        }
    }
}
