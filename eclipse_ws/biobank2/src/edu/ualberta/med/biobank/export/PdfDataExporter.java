package edu.ualberta.med.biobank.export;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.jasperreports.engine.JasperPrint;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.viewers.ITableLabelProvider;

import edu.ualberta.med.biobank.gui.common.BiobankGuiCommonPlugin;
import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.util.AbstractBiobankListProxy;
import edu.ualberta.med.biobank.reporting.ReportingUtils;

public class PdfDataExporter extends GuiDataExporter {
    private static final String[] VALID_EXTS = { "*.pdf" };

    public PdfDataExporter() {
        super("Export PDF");
    }

    protected PdfDataExporter(String name) {
        super(name);
    }

    @Override
    public void canExport(Data data) throws DataExportException {
        super.canExport(data);

        if (data.getRows() instanceof AbstractBiobankListProxy) {
            AbstractBiobankListProxy<?> proxy = (AbstractBiobankListProxy<?>) data
                .getRows();
            if (proxy.getRealSize() == -1) {
                throw new DataExportException("too many rows to export");
            }
        }
    }

    @Override
    protected void export(Data data, ITableLabelProvider labelProvider,
        IProgressMonitor monitor) throws DataExportException {
        canExport(data);

        String path = getPath(data, VALID_EXTS);
        List<Map<String, String>> maps = getPropertyMaps(data, labelProvider,
            monitor);

        try {
            JasperPrint jasperPrint = ReportingUtils.createDynamicReport(
                data.getTitle(), data.getDescription(), data.getColumnNames(),
                maps);
            ReportingUtils.saveReport(jasperPrint, path);
        } catch (Exception e) {
            BiobankGuiCommonPlugin.openAsyncError("Error saving to PDF", e);
            return;
        }
        try {
            SessionManager.log("exportPDF", data.getTitle(), "data");
        } catch (Exception e) {
            BiobankGuiCommonPlugin.openAsyncError("Error Logging Export", e);
        }
    }

    protected static List<Map<String, String>> getPropertyMaps(Data data,
        ITableLabelProvider labelProvider, IProgressMonitor monitor)
        throws DataExportException {
        List<Map<String, String>> maps = new ArrayList<Map<String, String>>();

        for (Object row : data.getRows()) {
            if (monitor.isCanceled()) {
                throw new DataExportException("exporting canceled");
            }

            Map<String, String> map = getPropertyMap(data, row, labelProvider);
            maps.add(map);
        }

        return maps;
    }

    protected static Map<String, String> getPropertyMap(Data data, Object row,
        ITableLabelProvider labelProvider) {
        Map<String, String> map = new HashMap<String, String>();

        for (int i = 0, n = data.getColumnNames().size(); i < n; i++) {
            String property = data.getColumnNames().get(i);
            String value = labelProvider.getColumnText(row, i);
            map.put(property, value);
        }

        return map;
    }
}
