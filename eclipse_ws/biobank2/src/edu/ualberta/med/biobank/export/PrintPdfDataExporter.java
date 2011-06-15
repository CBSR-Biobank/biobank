package edu.ualberta.med.biobank.export;

import java.util.List;
import java.util.Map;

import net.sf.jasperreports.engine.JasperPrint;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.viewers.ITableLabelProvider;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.gui.common.BgcPlugin;
import edu.ualberta.med.biobank.reporting.ReportingUtils;

public class PrintPdfDataExporter extends PdfDataExporter {
    public PrintPdfDataExporter() {
        super(Messages.PrintPdfDataExporter_name);
    }

    @Override
    protected void export(Data data, ITableLabelProvider labelProvider,
        IProgressMonitor monitor) throws DataExportException {
        canExport(data);

        List<Map<String, String>> maps = getPropertyMaps(data, labelProvider,
            monitor);

        try {
            JasperPrint jasperPrint = ReportingUtils.createDynamicReport(
                data.getTitle(), data.getDescription(), data.getColumnNames(),
                maps);
            ReportingUtils.printReport(jasperPrint);
        } catch (Exception e) {
            BgcPlugin
                .openAsyncError(Messages.PrintPdfDataExporter_error_msg, e);
            return;
        }
        try {
            SessionManager.log(Messages.PrintPdfDataExporter_log_msg,
                data.getTitle(), LOG_TYPE);
        } catch (Exception e) {
            BgcPlugin.openAsyncError(
                Messages.PrintPdfDataExporter_logging_error_msg, e);
        }
    }
}
