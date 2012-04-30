package edu.ualberta.med.biobank.handlers;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
    private static final String BUNDLE_NAME =
        "edu.ualberta.med.biobank.handlers.messages"; //$NON-NLS-1$
    public static String CbsrHelp_browser_error_msg;
    public static String CbsrHelp_url_error_msg;
    public static String DecodePlateHandler_decode_label;
    public static String DeleteSelectionHandler_delete_error_msg;
    public static String DeleteSelectionHandler_delete_error_title;
    public static String EditActivityStatusHandler_handler_error_msg;
    public static String EditShippingMethodsHandler_handler_error_msg;
    public static String EditSpecimenTypesHandler_handler_error_msg;
    public static String HandlerPermission_error;
    public static String HandlerPermission_message;
    public static String LinkAssignCommonHandler_link_assign_open_error_msg;
    public static String LoggingHandler_view_open_error;
    public static String MainAdministrationHandler_main_persp_error;
    public static String PatientAddHandler_patient_open_error;
    public static String PrintHandler_print_dialog_question;
    public static String PrintHandler_print_dialog_title;
    public static String ProcessingAdministrationHandler_perspective_error;
    public static String ReloadViewFormHandler_reload_error_msg;
    public static String ReloadViewFormHandler_reload_error_title;
    public static String ReportsHandler_init_error;
    public static String ResetHandler_reset_error;
    public static String ScanPlateHandler_scan_label;
    public static String SearchHandler_view_open_error;
    public static String SpecimenAssignHandler_error_message;
    public static String SpecimenAssignHandler_log_location;
    public static String SpecimenAssignHandler_specimenAssign_label;
    public static String SpecimenLinkHandler_error_message;
    public static String SpecimenLinkHandler_log_location;
    public static String SpecimenLinkHandler_specimen_link_label;
    static {
        // initialize resource bundle
        NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }

    private Messages() {
    }
}
