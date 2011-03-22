package edu.ualberta.med.biobank.exporters;

import org.eclipse.jface.viewers.ITableLabelProvider;

import edu.ualberta.med.biobank.exporters.ReportExporter.Data;

public class ReportExporterHelper {
    public static String[] getFormattedRow(Data data, Object row,
        ITableLabelProvider labelProvider) {
        int numHeaders = data.getHeaders().size();

        String[] formattedRow = new String[numHeaders];

        for (int i = 0; i < numHeaders; i++) {
            formattedRow[i] = labelProvider.getColumnText(row, i);
        }

        return formattedRow;
    }
}
