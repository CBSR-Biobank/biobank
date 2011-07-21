package edu.ualberta.med.biobank.widgets.trees;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
    private static final String BUNDLE_NAME = "edu.ualberta.med.biobank.widgets.trees.messages"; //$NON-NLS-1$
    public static String DispatchSpecimensTreeTable_edit_label;
    public static String DispatchSpecimensTreeTable_comment_label;
    public static String DispatchSpecimensTreeTable_inventoryid_label;
    public static String DispatchSpecimensTreeTable_modidy_comment_label;
    public static String DispatchSpecimensTreeTable_pnumber_label;
    public static String DispatchSpecimensTreeTable_set_missing_label;
    public static String DispatchSpecimensTreeTable_status_label;
    public static String DispatchSpecimensTreeTable_type_label;
    public static String ReportTreeWidget_custom_label;
    public static String ReportTreeWidget_delete_label;
    public static String ReportTreeWidget_error_msg;
    public static String ReportTreeWidget_error_title;
    static {
        // initialize resource bundle
        NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }

    private Messages() {
    }
}
