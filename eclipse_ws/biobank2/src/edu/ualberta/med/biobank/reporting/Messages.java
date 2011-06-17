package edu.ualberta.med.biobank.reporting;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
    private static final String BUNDLE_NAME = "edu.ualberta.med.biobank.reporting.messages"; //$NON-NLS-1$
    public static String ReportingUtils_cancel_error_msg;
    public static String ReportingUtils_extension_error_msg;
    public static String ReportingUtils_file_type_error_msg;
    public static String ReportingUtils_footer_print_msg;
    public static String ReportingUtils_jasper_printing_error_msg;
    public static String ReportingUtils_jasperfile_error_msg;
    public static String ReportingUtils_pdf_file_msg;
    public static String ReportingUtils_printer_error_msg;
    public static String ReportingUtils_printing_error_msg;
    public static String ReportingUtils_printing_error_title;
    static {
        // initialize resource bundle
        NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }

    private Messages() {
    }
}
