package edu.ualberta.med.biobank.logs;

import java.util.HashMap;
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
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.JRPrintServiceExporter;
import net.sf.jasperreports.engine.export.JRPrintServiceExporterParameter;

import org.eclipse.swt.SWT;
import org.eclipse.swt.printing.PrintDialog;
import org.eclipse.swt.printing.PrinterData;
import org.eclipse.ui.PlatformUI;

public class LogInfo {

    private static JasperReport logPrintReport;

    private String info;

    public LogInfo(String info) {
        this.info = info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getInfo() {
        return info;
    }

    public static void printLogReport(String username, List<LogInfo> logsList)
        throws Exception {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("username", username);
        JasperPrint print = JasperFillManager.fillReport(LogInfo.class
            .getResourceAsStream("ScanReportPrintForm.jasper"), map,
            new JRBeanCollectionDataSource(logsList));

        PrintDialog dialog = new PrintDialog(PlatformUI.getWorkbench()
            .getActiveWorkbenchWindow().getShell(), SWT.NONE);
        PrinterData data = dialog.open();

        if (data.printToFile == true) {
            String fileName = data.fileName;
            if (fileName.endsWith(".pdf")) {
                String prefix = "file://";
                if (fileName.startsWith(prefix)) {
                    fileName = fileName.substring(prefix.length());
                }
                JasperExportManager.exportReportToPdfFile(print, fileName);
            } else {
                throw new Exception("can't save to file type " + data.fileName);
            }
        } else {

            PrintService[] services = PrintServiceLookup.lookupPrintServices(
                DocFlavor.SERVICE_FORMATTED.PRINTABLE, null);
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
            exporter.setParameter(JRExporterParameter.JASPER_PRINT, print);
            exporter.setParameter(
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
