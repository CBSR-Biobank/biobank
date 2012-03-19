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
    private static final String[] VALID_EXTS = { "*.pdf" }; //$NON-NLS-1$

    public PdfDataExporter() {
        super(Messages.PdfDataExporter_name);
    }

    protected PdfDataExporter(String name) {
        super(name);
    }

    @Override
    public void canExport(Data data) throws DataExportException {
        super.canExport(data);

        if (data.getRows() instanceof AbstractBiobankListProxy) {
            if (data.getRows().size() > 0 && data.getRows().size() <= 1000) {
                throw new DataExportException(
                    Messages.PdfDataExporter_toomanyrows_error_msg);
            }
        }
    }

    @Override
    protected void export(Data data, ITableLabelProvider labelProvider,
        IProgressMonitor monitor) throws DataExportException {
        canExport(data);

        String path = getPath(data, VALID_EXTS);
        List<Map<String, String>> maps = getPropertyMaps(data, labelProvider,
            monitor, true);

        try {
            JasperPrint jasperPrint = ReportingUtils.createDynamicReport(
                data.getTitle(), data.getDescription(), data.getColumnNames(),
                maps, true);
            ReportingUtils.saveReport(jasperPrint, path);
        } catch (Exception e) {
            BgcPlugin.openAsyncError(Messages.PdfDataExporter_saving_error_msg,
                e);
            return;
        }
        try {
            SessionManager.log(Messages.PdfDataExporter_log_export,
                data.getTitle(), LOG_TYPE);
        } catch (Exception e) {
            BgcPlugin.openAsyncError(
                Messages.PdfDataExporter_logging_error_msg, e);
        }
    }

    /**
     * if useIntergerProperties is true then the map will contain [{0=value},
     * {1=value}...] instead of [{name=value}...] (see issue #1312)
     */
    protected static List<Map<String, String>> getPropertyMaps(Data data,
        ITableLabelProvider labelProvider, IProgressMonitor monitor,
        boolean useIntegerProperties) throws DataExportException {
        List<Map<String, String>> maps = new ArrayList<Map<String, String>>();

        for (Object row : data.getRows()) {
            if (monitor.isCanceled()) {
                throw new DataExportException(
                    Messages.PdfDataExporter_cancel_msg);
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
