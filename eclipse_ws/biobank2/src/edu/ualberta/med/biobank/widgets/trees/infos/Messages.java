package edu.ualberta.med.biobank.widgets.trees.infos;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
    private static final String BUNDLE_NAME =
        "edu.ualberta.med.biobank.widgets.trees.infos.messages"; //$NON-NLS-1$
    public static String AbstractInfoTreeWidget_first_label;
    public static String AbstractInfoTreeWidget_last_label;
    public static String AbstractInfoTreeWidget_load_error_title;
    public static String AbstractInfoTreeWidget_next_label;
    public static String AbstractInfoTreeWidget_previous_label;
    public static String InfoTreeWidget_add_label;
    public static String InfoTreeWidget_delete_label;
    public static String InfoTreeWidget_edit_label;
    public static String InfoTreeWidget_pages_text;
    public static String SpecimenTypeEntryInfoTree_name_already_added_error_msg;
    public static String SpecimenTypeEntryInfoTree_name_short_already_added_error_msg;
    public static String SpecimenTypeEntryInfoTree_check_error_title;
    public static String SpecimenTypeEntryInfoTree_delete_error_msg;
    public static String SpecimenTypeEntryInfoTree_delete_error_title;
    public static String SpecimenTypeEntryInfoTree_delete_question_msg;
    public static String SpecimenTypeEntryInfoTree_delete_question_title;
    public static String SpecimenTypeEntryInfoTree_delete_type_error_msg;
    public static String SpecimenTypeEntryInfoTree_refresh_error_title;
    public static String SpecimenTypeEntryInfoTree_save_error_title;
    public static String SpecimenTypeEntryInfoTree_unaivalable_error_title;
    public static String SpecimenTypeInfoTree_loading;
    public static String SpecimenTypeInfoTree_name_label;
    public static String SpecimenTypeInfoTree_nameShort_label;
    static {
        // initialize resource bundle
        NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }

    private Messages() {
    }
}
