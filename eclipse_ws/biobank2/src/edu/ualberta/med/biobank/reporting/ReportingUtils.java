package edu.ualberta.med.biobank.reporting;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.print.DocFlavor;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRExporter;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.JRPrintServiceExporter;
import net.sf.jasperreports.engine.export.JRPrintServiceExporterParameter;

import org.eclipse.swt.SWT;
import org.eclipse.swt.printing.PrintDialog;
import org.eclipse.swt.printing.PrinterData;
import org.eclipse.ui.PlatformUI;

import ar.com.fdvs.dj.core.DynamicJasperHelper;
import ar.com.fdvs.dj.core.layout.ClassicLayoutManager;
import ar.com.fdvs.dj.domain.builders.FastReportBuilder;

public class ReportingUtils {

    public static JasperPrint createReport(String reportName,
        ArrayList<Object[]> params, ArrayList<Object> columnInfo, List<?> list)
        throws Exception {

        FastReportBuilder drb = new FastReportBuilder();
        for (int i = 0; i < columnInfo.size(); i++) {
            drb.addColumn((String) columnInfo.get(i),
                (String) columnInfo.get(i), String.class, 40, false)
                .setPrintBackgroundOnOddRows(true).setUseFullPageWidth(true);
        }

        String paramString = "";

        Map<String, Object> fields = new HashMap<String, Object>();

        for (int i = 0; i < params.size(); i++) {
            paramString += params.get(i)[0] + " : " + params.get(i)[1] + "\n";
        }
        fields.put("title", reportName);
        fields.put("infos", paramString);
        drb.setTemplateFile(ReportingUtils.class.getResource(
            "BasicReport.jrxml").getFile());

        JRDataSource ds = new JRBeanCollectionDataSource(list);
        JasperPrint jp = DynamicJasperHelper.generateJasperPrint(drb.build(),
            new ClassicLayoutManager(), ds, fields);
        return jp;

    }

    public static void printReport(String reportName,
        ArrayList<Object[]> params, ArrayList<Object> columnInfo, List<?> list)
        throws Exception {

        JasperPrint jasperPrint = createReport(reportName, params, columnInfo,
            list);

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
        ArrayList<Object[]> params, ArrayList<Object> columnInfo, List<?> list,
        String fileName) throws Exception {

        JasperPrint jasperPrint = createReport(reportName, params, columnInfo,
            list);
        JasperExportManager.exportReportToPdfFile(jasperPrint, fileName);
    }
}
