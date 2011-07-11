package edu.ualberta.med.biobank.reporting;

import java.awt.Color;
import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.print.DocFlavor;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;

import net.sf.jasperreports.engine.JRDataSource;
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

import org.apache.commons.lang.StringUtils;
import org.eclipse.swt.SWT;
import org.eclipse.swt.printing.PrintDialog;
import org.eclipse.swt.printing.PrinterData;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

import ar.com.fdvs.dj.core.layout.ClassicLayoutManager;
import ar.com.fdvs.dj.domain.AutoText;
import ar.com.fdvs.dj.domain.Style;
import ar.com.fdvs.dj.domain.builders.FastReportBuilder;
import ar.com.fdvs.dj.domain.constants.Border;
import ar.com.fdvs.dj.domain.constants.Font;
import ar.com.fdvs.dj.domain.constants.Transparency;
import ar.com.fdvs.dj.domain.constants.VerticalAlign;
import edu.ualberta.med.biobank.common.formatters.DateFormatter;
import edu.ualberta.med.biobank.common.util.Holder;
import edu.ualberta.med.biobank.gui.common.BgcPlugin;

public class ReportingUtils {

    public static Font sansSerif = new Font(Font.MEDIUM, "SansSerif", false);

    public static Font sansSerifBold = new Font(Font.MEDIUM, "SansSerif", true);

    public static PrinterData data;

    /**
     * if userIntegerProperties is set to true, then the map contained inside
     * 'list' should be contain [{0=value}, {1=value}...] instead of
     * [{name=value}...] (see issue #1312)
     */
    public static JasperPrint createDynamicReport(String reportName,
        List<String> description, List<String> columnInfo, List<?> list,
        boolean useIntegerProperties) throws Exception {

        FastReportBuilder drb = new FastReportBuilder();
        for (int i = 0; i < columnInfo.size(); i++) {
            String title = columnInfo.get(i);
            String property = title;
            if (useIntegerProperties)
                property = String.valueOf(i);
            drb.addColumn(title, property, String.class, 40, false)
                .setPrintBackgroundOnOddRows(true).setUseFullPageWidth(true);
        }

        String infos = StringUtils.join(description,
            System.getProperty("line.separator"));

        Map<String, Object> fields = new HashMap<String, Object>();
        fields.put("title", reportName);
        fields.put("infos", infos);
        URL reportURL = ReportingUtils.class.getResource("BasicReport.jrxml");
        if (reportURL == null) {
            throw new Exception("No report available with name BasicReport");
        }
        drb.setTemplateFile(reportURL.getFile());
        drb.addAutoText(AutoText.AUTOTEXT_PAGE_X_OF_Y,
            AutoText.POSITION_FOOTER, AutoText.ALIGNMENT_RIGHT, 200, 40);
        drb.addAutoText(
            "Printed on " + DateFormatter.formatAsDateTime(new Date()),
            AutoText.POSITION_FOOTER, AutoText.ALIGNMENT_LEFT, 200);

        Style headerStyle = new Style();
        headerStyle.setFont(ReportingUtils.sansSerifBold);
        // headerStyle.setHorizontalAlign(HorizontalAlign.CENTER);
        headerStyle.setBorderBottom(Border.THIN);
        headerStyle.setVerticalAlign(VerticalAlign.MIDDLE);
        headerStyle.setBackgroundColor(Color.LIGHT_GRAY);
        headerStyle.setTransparency(Transparency.OPAQUE);
        Style detailStyle = new Style();
        detailStyle.setFont(ReportingUtils.sansSerif);
        drb.setDefaultStyles(null, null, headerStyle, detailStyle);

        JRDataSource ds = new JRBeanCollectionDataSource(list);
        JasperPrint jp = DynamicJasperHelper.generateJasperPrint(drb.build(),
            new ClassicLayoutManager(), ds, fields);
        return jp;
    }

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

    private static PrintService getPrinterService(PrinterData data) {
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
        return service;
    }

    private static void printViaPrinter(PrinterData data,
        JasperPrint jasperPrint) throws Exception {
        PrintService service = getPrinterService(data);
        if (service != null) {
            JRExporter exporter = new JRPrintServiceExporter();
            exporter
                .setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
            exporter.setParameter(
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
        } else {
            throw new Exception(
                "Error with printer - No Print Service found with name "
                    + data.name);
        }
    }

    private static void printViaFile(PrinterData data, JasperPrint jasperPrint)
        throws Exception {
        String fileName = null;

        // data.fileName is typically "FILE:" instead of null.

        if (data.fileName != null && data.fileName.endsWith(".pdf")) {
            fileName = data.fileName;
        } else {
            // on windows a custom dialog is required to print to file.
            FileDialog fd = new FileDialog(PlatformUI.getWorkbench()
                .getActiveWorkbenchWindow().getShell(), SWT.SAVE);
            fd.setOverwrite(true);
            fd.setText("Print document to Pdf file.");
            String[] filterExt = { "*.pdf" };
            fd.setFilterExtensions(filterExt);
            fd.setFileName(DateFormatter.formatAsDateTime(new Date()));
            final String path = fd.open();
            fileName = path;
        }

        if (fileName == null) {
            return;
        }

        if (fileName.endsWith(".pdf")) {
            String prefix = "file://";
            if (fileName.startsWith(prefix)) {
                fileName = fileName.substring(prefix.length());
            }
            JasperExportManager.exportReportToPdfFile(jasperPrint, fileName);
        } else {
            throw new Exception("Can't save to file type " + fileName);
        }
    }

    public static void printReport(final JasperPrint jasperPrint)
        throws Exception {
        // Use SWT PrintDialog instead of the JasperReport method that use java
        // swing gui.
        final Display display = Display.getDefault();
        final Holder<Exception> exception = new Holder<Exception>(null);

        display.syncExec(new Runnable() {
            @Override
            public void run() {
                Shell shell = display.getActiveShell();
                PrintDialog dialog = new PrintDialog(shell, SWT.NONE);
                PrinterData data = dialog.open();

                // if data is null : user cancled print.

                if (data != null) {
                    try {
                        if (data.printToFile == true) {
                            printViaFile(data, jasperPrint);
                        } else {
                            try {
                                printViaPrinter(data, jasperPrint);
                            } catch (Exception e) {
                                BgcPlugin.openAsyncError(
                                    "Printing Error",
                                    "Error: "
                                        + e.toString()
                                        + "\n\n"
                                        + "Select a file location to export the printed page.");
                                printViaFile(data, jasperPrint);
                            }
                        }
                    } catch (Exception e) {
                        exception.setValue(e);
                    }
                }
            }
        });

        if (exception.getValue() != null) {
            throw exception.getValue();
        }
    }
}
