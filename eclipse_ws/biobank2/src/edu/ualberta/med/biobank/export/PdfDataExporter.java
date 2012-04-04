package edu.ualberta.med.biobank.export;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.jasperreports.engine.JasperPrint;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.viewers.ITableLabelProvider;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.util.AbstractBiobankListProxy;
import edu.ualberta.med.biobank.gui.common.BgcPlugin;
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
            if (data.getRows().size() < 0 || data.getRows().size() >= 1000) {
                throw new DataExportException(
                    "Results exceed 1000 rows and cannot be exported. Please export to CSV or refine your search.");
            }
        }
    }

    @Override
    protected void export(Data data, ITableLabelProvider labelProvider,
        IProgressMonitor monitor) throws DataExportException {
        canExport(data);

        List<Map<String, String>> maps;
        String path = getPath(data, VALID_EXTS);
        try {
            maps = getPropertyMaps(data, labelProvider,
                monitor, true);
        } catch (Exception e) {
            // canceled
            return;
        }
        try {
            JasperPrint jasperPrint = ReportingUtils.createDynamicReport(
                data.getTitle(), data.getDescription(), data.getColumnNames(),
                maps, true);
            ReportingUtils.saveReport(jasperPrint, path);
        } catch (Exception e) {
            BgcPlugin.openAsyncError("Error saving to PDF",
                e);
            return;
        }
        try {
            SessionManager.log("exportPDF",
                data.getTitle(), LOG_TYPE);
        } catch (Exception e) {
            BgcPlugin.openAsyncError(
                "Error Logging Export", e);
        }
    }

    /**
     * if useIntergerProperties is true then the map will contain [{0=value},
     * {1=value}...] instead of [{name=value}...] (see issue #1312)
     */
    protected static List<Map<String, String>> getPropertyMaps(Data data,
        ITableLabelProvider labelProvider, IProgressMonitor monitor,
        boolean useIntegerProperties) throws Exception {
        List<Map<String, String>> maps = new ArrayList<Map<String, String>>();

        for (Object row : data.getRows()) {
            if (monitor.isCanceled()) {
                throw new Exception();
            }

            Map<String, String> map = getPropertyMap(data, row, labelProvider,
                useIntegerProperties);
            maps.add(map);
        }

        return maps;
    }

    /**
     * if useIntergerProperties is true then the map will contain [{0=value},
     * {1=value}...] instead of [{name=value}...] (see issue #1312)
     */
    protected static Map<String, String> getPropertyMap(Data data, Object row,
        ITableLabelProvider labelProvider, boolean useIntegerProperties) {
        Map<String, String> map = new HashMap<String, String>();

        for (int i = 0, n = data.getColumnNames().size(); i < n; i++) {
            String property;
            if (useIntegerProperties)
                property = String.valueOf(i);
            else
                property = data.getColumnNames().get(i);
            String value = labelProvider.getColumnText(row, i);
            map.put(property, value);
        }

        return map;
    }
}
