package edu.ualberta.med.biobank;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
    private static final String BUNDLE_NAME = "edu.ualberta.med.biobank.messages"; //$NON-NLS-1$

    public static String SessionManager_actions_error_title;

    public static String SessionManager_error_message;
    public static String SessionManager_noconnection_error_msg;
    static {
        // initialize resource bundle
        NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }

    private Messages() {
    }
}
