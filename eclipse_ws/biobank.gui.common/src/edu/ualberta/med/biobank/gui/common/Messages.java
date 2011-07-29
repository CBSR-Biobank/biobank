package edu.ualberta.med.biobank.gui.common;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
    private static final String BUNDLE_NAME = "edu.ualberta.med.biobank.gui.common.messages"; //$NON-NLS-1$
    public static String BgcPlugin_access_denied_error_msg;
    public static String BgcPlugin_access_denied_error_title;
    public static String BgcPlugin_connection_error_msg;
    public static String BgcPlugin_connection_error_title;
    public static String BgcPlugin_database_error_msg;
    static {
        // initialize resource bundle
        NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }

    private Messages() {
    }
}
