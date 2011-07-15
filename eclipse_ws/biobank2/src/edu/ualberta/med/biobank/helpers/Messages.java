package edu.ualberta.med.biobank.helpers;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
    private static final String BUNDLE_NAME = "edu.ualberta.med.biobank.helpers.messages"; //$NON-NLS-1$
    public static String SessionHelper_client_error_title;
    public static String SessionHelper_client_invalid_error_msg;
    public static String SessionHelper_clientVersion_debug_msg;
    public static String SessionHelper_login_error_msg;
    public static String SessionHelper_login_error_title;
    public static String SessionHelper_login_server_error_msg;
    public static String SessionHelper_server_newversion_error_msg;
    public static String SessionHelper_server_noversion_error_msg;
    public static String SessionHelper_server_oldversion_error_msg;
    public static String SessionHelper_server_oldversion_log_msg;
    public static String SessionHelper_server_version_error_title;
    static {
        // initialize resource bundle
        NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }

    private Messages() {
    }
}
