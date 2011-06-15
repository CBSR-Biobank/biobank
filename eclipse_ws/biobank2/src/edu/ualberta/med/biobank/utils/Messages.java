package edu.ualberta.med.biobank.utils;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
    private static final String BUNDLE_NAME = "edu.ualberta.med.biobank.utils.messages"; //$NON-NLS-1$
    public static String FilePromptUtil_create_path_error_title;
    public static String FilePromptUtil_create_path_msg;
    public static String FilePromptUtil_create_path_title;
    public static String FilePromptUtil_create_pathe_error_msg;
    public static String FilePromptUtil_path_directory_error_msg;
    public static String FilePromptUtil_path_error_title;
    public static String FilePromptUtil_path_write_error_msg;
    public static String SearchType_barcode_cont_label;
    public static String SearchType_form_open_error_msg;
    public static String SearchType_inventoryid_label;
    public static String SearchType_label_cont_label;
    public static String SearchType_nonactive_spec_label;
    public static String SearchType_pEvent_list_title;
    public static String SearchType_position_spec_label;
    public static String SearchType_question_msg;
    public static String SearchType_question_title;
    public static String SearchType_specimens_list_label;
    public static String SearchType_worksheet_label;
    static {
        // initialize resource bundle
        NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }

    private Messages() {
    }
}
