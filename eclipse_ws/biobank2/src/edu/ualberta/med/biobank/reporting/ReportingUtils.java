package edu.ualberta.med.biobank.reporting;

import java.io.File;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import javax.print.DocFlavor;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExporter;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.export.JRCsvExporter;
import net.sf.jasperreports.engine.export.JRPrintServiceExporter;
import net.sf.jasperreports.engine.export.JRPrintServiceExporterParameter;
import net.sf.jasperreports.engine.xml.JRXmlLoader;

import org.eclipse.swt.SWT;
import org.eclipse.swt.printing.PrintDialog;
import org.eclipse.swt.printing.PrinterData;
import org.eclipse.ui.PlatformUI;

import ar.com.fdvs.dj.domain.constants.Font;

public class ReportingUtils {

    public static Font sansSerif = new Font(Font.MEDIUM, "SansSerif", false);

    public static Font sansSerifBold = new Font(Font.MEDIUM, "SansSerif", true);

    public static JasperPrint createStandardReport(String reportName,
        Map<String, Object> parameters, List<?> list) throws Exception {
        InputStream reportStream = ReportingUtils.class
            .getResourceAsStream(reportName + ".jrxml");
        if (reportStream == null) {
            throw new Exception("No report available with name " + reportName);
        }
        JasperDesign jdesign = JRXmlLoader.load(reportStream);
        JasperReport report = JasperCompileManager.compileReport(jdesign);
        return JasperFillManager.fillReport(report, parameters,
            new JRBeanCollectionDataSource(list));
    }

    public static void saveReport(JasperPrint jasperPrint, String path)
        throws Exception {
        if (path == null)
            throw new Exception("Printing Canceled.");

        String prefix = "file://";
        if (path.startsWith(prefix)) {
            path = path.substring(prefix.length());
        }
        if (path.endsWith(".pdf")) {
            JasperExportManager.exportReportToPdfFile(jasperPrint, path);
        } else if (path.endsWith(".csv")) {
            JRExporter csvExporter = new JRCsvExporter();
            csvExporter.setParameter(JRExporterParameter.JASPER_PRINT,
                jasperPrint);
            csvExporter.setParameter(JRExporterParameter.OUTPUT_FILE, new File(
                path));
            csvExporter.exportReport();
        } else {
            throw new Exception(
                "Not a valid extension. Please use 'pdf' or 'csv'.");
        }
    }

    public static void printReport(JasperPrint jasperPrint) throws Exception {
        // Use SWT PrintDialog instead of the JasperReport method that use java
        // swing gui.
        PrintDialog dialog = new PrintDialog(PlatformUI.getWorkbench()
            .getActiveWorkbenchWindow().getShell(), SWT.NONE);
        PrinterData data = dialog.open();
        if (data != null) {
            // use the standard java method to retrieve print services
            PrintService[] services = PrintServiceLookup.lookupPrintServices(
                DocFlavor.SERVICE_FORMATTED.PRINTABLE, null);
            PrintService service = null;
            // try to find the correct PrintService using the Swt PrinterData
            // information
            for (PrintService ps : services) {
                if (ps.getName().equals(data.name)) {
                    service = ps;
                }
            }
            if (service == null) {
                // corresponding PrintService not found
                if (data.printToFile == true) {
                    // if printToFile asked, can print to a pdf file only
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
                    throw new Exception(
                        "Error with printer - No Print Service found with name "
                            + data.name);
                }
            } else {
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

                try {
                    exporter.exportReport();
                } catch (JRException e) {
                    throw new Exception(
                        "Printing Canceled. Check your printer settings and try again.");
                }
            }
        }

    }
}
