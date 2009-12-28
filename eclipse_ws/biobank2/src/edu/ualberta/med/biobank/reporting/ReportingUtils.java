package edu.ualberta.med.biobank.reporting;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

import javax.print.DocFlavor;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;

import net.sf.jasperreports.engine.JRExporter;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.JRPrintServiceExporter;
import net.sf.jasperreports.engine.export.JRPrintServiceExporterParameter;

import org.eclipse.swt.SWT;
import org.eclipse.swt.printing.PrintDialog;
import org.eclipse.swt.printing.PrinterData;
import org.eclipse.ui.PlatformUI;

public class ReportingUtils {

    public static JasperPrint createReport(String reportName,
        Map<String, Object> parameters, List<?> list) throws Exception {

        InputStream reportStream = ReportingUtils.class
            .getResourceAsStream(reportName + ".jasper");
        if (reportStream == null) {
            throw new Exception("No report available with name " + reportName);
        }
        return JasperFillManager.fillReport(reportStream, parameters,
            new JRBeanCollectionDataSource(list));
    }

    public static void printReport(String reportName,
        Map<String, Object> parameters, List<?> list) throws Exception {

        JasperPrint jasperPrint = createReport(reportName, parameters, list);

        PrintDialog dialog = new PrintDialog(PlatformUI.getWorkbench()
            .getActiveWorkbenchWindow().getShell(), SWT.NONE);
        PrinterData data = dialog.open();
        if (data != null) {
            if (data.printToFile == true) {
                String fileName = data.fileName;
                if (fileName.endsWith(".pdf")) {
                    String prefix = "file://";
                    if (fileName.startsWith(prefix)) {
                        fileName = fileName.substring(prefix.length());
                    }
                    JasperExportManager.exportReportToPdfFile(jasperPrint,
                        fileName);
                } else {
                    throw new Exception("Can't save to file type "
                        + data.fileName);
                }
            } else {
                PrintService[] services = PrintServiceLookup
                    .lookupPrintServices(DocFlavor.SERVICE_FORMATTED.PRINTABLE,
                        null);
                PrintService service = null;
                for (PrintService ps : services) {
                    if (ps.getName().equals(data.name)) {
                        service = ps;
                    }
                }
                if (service == null) {
                    throw new Exception("Error with printer");
                }
                JRExporter exporter = new JRPrintServiceExporter();
                exporter.setParameter(JRExporterParameter.JASPER_PRINT,
                    jasperPrint);
                exporter
                    .setParameter(
                        JRPrintServiceExporterParameter.PRINT_SERVICE_ATTRIBUTE_SET,
                        service.getAttributes());
                exporter.setParameter(
                    JRPrintServiceExporterParameter.DISPLAY_PAGE_DIALOG,
                    Boolean.FALSE);
                exporter.setParameter(
                    JRPrintServiceExporterParameter.DISPLAY_PRINT_DIALOG,
                    Boolean.FALSE);

                exporter.exportReport();
            }
        }
    }

    public static void saveReport(String reportName,
        Map<String, Object> parameters, List<?> list, String fileName)
        throws Exception {

        JasperPrint jasperPrint = createReport(reportName, parameters, list);
        JasperExportManager.exportReportToPdfFile(jasperPrint, fileName);
    }
}
