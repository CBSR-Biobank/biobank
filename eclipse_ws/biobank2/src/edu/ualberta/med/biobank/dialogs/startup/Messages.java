package edu.ualberta.med.biobank.dialogs.startup;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
    private static final String BUNDLE_NAME = "edu.ualberta.med.biobank.dialogs.startup.messages"; //$NON-NLS-1$

    public static String LoginDialog_main_title;
    public static String LoginDialog_title;
    public static String LoginDialog_description;
    public static String LoginDialog_noWorkingCenter_admin_msg;

    public static String LoginDialog_noWorkingCenter_error_msg;

    public static String LoginDialog_openperspective_error_msg;

    public static String LoginDialog_password_label;

    public static String LoginDialog_password_validaton_msg;

    public static String LoginDialog_workingCenter_admin_title;

    public static String LoginDialog_workingCenter_error_title;

    public static String LoginDialog_workingCenterSelection_error_msg;

    public static String LoginDialog_workingCenterSelection_error_title;

    public static String LoginDialog_secure_connection_label;

    public static String LoginDialog_server_label;

    public static String LoginDialog_server_validation_msg;

    public static String LoginDialog_serverUrl_error_msg;

    public static String LoginDialog_serverUrl_error_title;

    public static String LoginDialog_superAdmin_error_msg;

    public static String LoginDialog_superAdmin_error_title;

    public static String LoginDialog_superAdmin_label;

    public static String LoginDialog_user_error_msg;

    public static String LoginDialog_user_error_title;

    public static String LoginDialog_user_label;

    public static String LoginDialog_user_validation_msg;

    public static String WorkingCenterSelectDialog_available_centers_label;

    public static String WorkingCenterSelectDialog_title;

    public static String WorkingCenterSelectDialog_description;

    static {
        // initialize resource bundle
        NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }

    private Messages() {
    }

}
