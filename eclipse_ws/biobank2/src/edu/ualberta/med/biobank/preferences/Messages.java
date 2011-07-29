package edu.ualberta.med.biobank.preferences;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
    private static final String BUNDLE_NAME = "edu.ualberta.med.biobank.preferences.messages"; //$NON-NLS-1$
    public static String DefaultTopPreferencePage_description;

    public static String GeneralPreferencePage_version_label;
    public static String IssueTrackerPreferencePage_email_label;

    public static String IssueTrackerPreferencePage_password_label;
    public static String IssueTrackerPreferencePage_port_label;
    public static String IssueTrackerPreferencePage_server_label;
    public static String IssueTrackerPreferencePage_username_label;

    public static String LinkAssignPreferencePage_ask_print_label;
    public static String LinkAssignPreferencePage_cancel_barcode_label;
    public static String LinkAssignPreferencePage_confirm_barcode_label;
    public static String LinkAssignPreferencePage_logs_path_label;
    public static String LinkAssignPreferencePage_save_logs_label;

    public static String ServerPreferencePage_edit_dialog_msg;
    public static String ServerPreferencePage_edit_button_label;
    public static String ServerPreferencePage_edit_dialog_title;
    public static String ServerPreferencePage_servers_label;

    static {
        // initialize resource bundle
        NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }

    private Messages() {
    }
}
