package edu.ualberta.med.biobank.export;

import java.util.List;
import java.util.Map;

import net.sf.jasperreports.engine.JasperPrint;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.gui.common.BgcPlugin;
import edu.ualberta.med.biobank.reporting.ReportingUtils;

public class PrintPdfDataExporter extends PdfDataExporter {
    private static final I18n i18n = I18nFactory
        .getI18n(PrintPdfDataExporter.class);

    @SuppressWarnings("nls")
    public PrintPdfDataExporter() {
        super(i18n.trc("Exporter Type Name", "Print"));
    }

    @SuppressWarnings("nls")
    @Override
    protected void export(Data data, ITableLabelProvider labelProvider,
        IProgressMonitor monitor) throws DataExportException {
        canExport(data);

        List<Map<String, String>> maps;
        try {
            maps = getPropertyMaps(data, labelProvider,
                monitor, true);
        } catch (Exception e1) {
            // cancelled
            return;
        }

        try {
            JasperPrint jasperPrint = ReportingUtils.createDynamicReport(
                data.getTitle(), data.getDescription(), data.getColumnNames(),
                maps, true);
            ReportingUtils.printReport(jasperPrint);
        } catch (Exception e) {
            BgcPlugin
                .openAsyncError(
                    // TR: error title dialog
                    i18n.tr("Error printing PDF"), e);
            return;
        }
        try {
            SessionManager.log("print",
                data.getTitle(), LOG_TYPE);
        } catch (Exception e) {
            BgcPlugin.openAsyncError(
                // TR: error title dialog
                i18n.tr("Error Logging Print"), e);
        }
    }
}
