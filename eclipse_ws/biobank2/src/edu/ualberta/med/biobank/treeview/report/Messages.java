package edu.ualberta.med.biobank.treeview.report;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
    private static final String BUNDLE_NAME = "edu.ualberta.med.biobank.treeview.report.messages"; //$NON-NLS-1$
    public static String PrivateReportsGroup_myreport_node_label;
    public static String ReportAdapter_copy_label;
    public static String ReportAdapter_copy_naming;
    public static String ReportAdapter_delete_confirm_msg;
    public static String ReportAdapter_delete_confirm_title;
    public static String ReportAdapter_delete_label;
    public static String ReportAdapter_report_label;
    public static String ReportEntityGroup_new_label;
    public static String SharedReportsGroup_shared_node_label;
    static {
        // initialize resource bundle
        NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }

    private Messages() {
    }
}
