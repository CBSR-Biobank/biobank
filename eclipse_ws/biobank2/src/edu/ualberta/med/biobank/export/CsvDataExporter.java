package edu.ualberta.med.biobank.export;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.supercsv.io.CsvListWriter;
import org.supercsv.prefs.CsvPreference;

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
                CsvPreference.EXCEL_PREFERENCE);

            List<String> headers = new ArrayList<String>();
            headers.add("title: " + data.getTitle());
            headers.add("columns: "
                + StringUtils.join(data.getColumnNames(), ","));

            if (data.getDescription() != null) {
                headers.add("description:");
                headers.addAll(data.getDescription());
            }

            writer.writeHeader(headers.toArray(new String[0]));

            int numHeaders = data.getColumnNames().size();

            Object[] labels = new Object[numHeaders];
            for (Object row : data.getRows()) {
                if (monitor.isCanceled()) {
                    throw new DataExportException("Exporting canceled.");
                }

                for (int i = 0; i < numHeaders; i++) {
                    labels[i] = labelProvider.getColumnText(row, i);
                }

                writer.write(labels);
            }

            writer.close();
        } catch (IOException e) {
            throw new DataExportException(e.getMessage());
        }
    }

}
