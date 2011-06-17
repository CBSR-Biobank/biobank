package edu.ualberta.med.biobank.rcp;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
    private static final String BUNDLE_NAME = "edu.ualberta.med.biobank.rcp.messages"; //$NON-NLS-1$
    public static String ApplicationActionBarAdvisor_applicationlogs_menu_label;
    public static String ApplicationActionBarAdvisor_command_error_title;
    public static String ApplicationActionBarAdvisor_errormail_menu_description;
    public static String ApplicationActionBarAdvisor_errormail_menu_name;
    public static String ApplicationActionBarAdvisor_exportlogs_menu_description;
    public static String ApplicationActionBarAdvisor_exportlogs_menu_label;
    public static String ApplicationActionBarAdvisor_help_menu_name;
    public static String ApplicationActionBarAdvisor_shortcuts_menu_description;
    public static String ApplicationActionBarAdvisor_shortcuts_menu_name;
    public static String ApplicationWorkbenchAdvisor_close_error_msg;
    public static String ApplicationWorkbenchAdvisor_close_error_title;
    public static String ApplicationWorkbenchWindowAdvisor_center_text;
    public static String ApplicationWorkbenchWindowAdvisor_ready_msg;
    static {
        // initialize resource bundle
        NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }

    private Messages() {
    }
}
