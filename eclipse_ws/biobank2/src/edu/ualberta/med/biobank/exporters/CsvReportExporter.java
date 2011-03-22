package edu.ualberta.med.biobank.exporters;

import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.supercsv.io.CsvListWriter;
import org.supercsv.prefs.CsvPreference;

public class CsvReportExporter implements ReportExporter {
    private static final String[] PATH_FILTER_EXTS = new String[] { "*.csv" };

    @Override
    public String getName() {
        return "Export CSV";
    }

    @Override
    public String[] getPathFilterExtensions() {
        return PATH_FILTER_EXTS;
    }

    @Override
    public void canExport(Data data) throws Exception {
        if (data.getResults() == null || data.getResults().isEmpty()) {
            throw new Exception("No results to export.");
        }
    }

    @Override
    public void export(Data data, String path,
        ITableLabelProvider labelProvider, IProgressMonitor monitor)
        throws Exception {

        CsvListWriter writer = new CsvListWriter(new FileWriter(path),
            CsvPreference.EXCEL_PREFERENCE);

        List<String> headers = new ArrayList<String>();
        headers.add("title: " + data.getTitle());
        headers.add("columns: " + StringUtils.join(data.getHeaders(), ","));

        if (data.getComments() != null) {
            headers.add("comments: ");
            headers.addAll(data.getComments());
        }

        writer.writeHeader(headers.toArray(new String[0]));

        int numHeaders = data.getHeaders().size();

        Object[] rowData = new Object[numHeaders];
        for (Object resultRow : data.getResults()) {
            if (monitor.isCanceled()) {
                throw new Exception("Exporting canceled.");
            }

            rowData = ReportExporterHelper.getFormattedRow(data, resultRow,
                labelProvider);

            writer.write(rowData);
        }

        writer.close();
    }
}
