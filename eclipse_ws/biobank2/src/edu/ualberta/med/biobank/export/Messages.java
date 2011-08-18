package edu.ualberta.med.biobank.export;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
    private static final String BUNDLE_NAME = "edu.ualberta.med.biobank.export.messages"; //$NON-NLS-1$
    public static String CsvDataExporter_cancel_msg;
    public static String CsvDataExporter_name;
    public static String GuiDataExporter_exportAs;
    public static String GuiDataExporter_exporting;
    public static String GuiDataExporter_exporting_error_msg;
    public static String GuiDataExporter_noresult_msg;
    public static String PdfDataExporter_cancel_msg;
    public static String PdfDataExporter_log_export;
    public static String PdfDataExporter_logging_error_msg;
    public static String PdfDataExporter_name;
    public static String PdfDataExporter_saving_error_msg;
    public static String PdfDataExporter_toomanyrows_error_msg;
    public static String PrintPdfDataExporter_error_msg;
    public static String PrintPdfDataExporter_log_msg;
    public static String PrintPdfDataExporter_logging_error_msg;
    public static String PrintPdfDataExporter_name;
    static {
        // initialize resource bundle
        NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }

    private Messages() {
    }
}
