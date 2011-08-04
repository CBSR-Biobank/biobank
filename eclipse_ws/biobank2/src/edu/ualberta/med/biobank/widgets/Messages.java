package edu.ualberta.med.biobank.widgets;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
    private static final String BUNDLE_NAME = "edu.ualberta.med.biobank.widgets.messages"; //$NON-NLS-1$

    public static String AliquotedSpecimenSelectionWidget_aliquoted_spec_title;

    public static String AliquotedSpecimenSelectionWidget_selections_validation_msg;
    public static String AliquotedSpecimenSelectionWidget_selections_status_msg;

    public static String AliquotedSpecimenSelectionWidget_sources_spec_title;

    public static String BiobankLabelProvider_count_error_title;

    public static String BiobankLabelProvider_loading;

    public static String BiobankLabelProvider_none_string;

    public static String CancelConfirmWidget_cancel_label;

    public static String CancelConfirmWidget_cancelconfirm_label;

    public static String CancelConfirmWidget_confirm_label;

    public static String ComboAndQuantityWidget_qunatity_label;

    public static String FileBrowser_browse_label;

    public static String FileBrowser_open_label;

    public static String PlateSelectionWidget_plate_label;

    public static String PlateSelectionWidget_select_label;

    public static String PvInfoWidget_add_label;

    public static String PvInfoWidget_consent_help;

    public static String PvInfoWidget_consent_label;

    public static String PvInfoWidget_consent_prompt;

    public static String PvInfoWidget_consent_title;

    public static String PvInfoWidget_move_bottom_label;

    public static String PvInfoWidget_move_down_label;

    public static String PvInfoWidget_move_top_label;

    public static String PvInfoWidget_move_up_label;

    public static String PvInfoWidget_patient_type_help;

    public static String PvInfoWidget_patient_type_label;

    public static String PvInfoWidget_patient_type_prompt;

    public static String PvInfoWidget_patient_type_title;

    public static String PvInfoWidget_remove_label;

    public static String PvInfoWidget_visit_type_help;

    public static String PvInfoWidget_visit_type_label;

    public static String PvInfoWidget_visit_type_prompt;

    public static String PvInfoWidget_visit_type_title;

    public static String SpecimenEntryWidget_already_added_error_msg;

    public static String SpecimenEntryWidget_delete_question_msg;

    public static String SpecimenEntryWidget_delete_question_title;

    public static String SpecimenEntryWidget_error_title;

    public static String SpecimenEntryWidget_inventoryid_label;

    public static String SpecimenEntryWidget_retrieve_error_title;

    public static String TopContainerListWidget_all_label;

    public static String TopContainerListWidget_load_error_title;

    public static String TopContainerListWidget_retrieve_error_title;

    public static String TopContainerListWidget_site_label;

    public static String TopContainerListWidget_topContainers_label;

    static {
        // initialize resource bundle
        NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }

    private Messages() {
    }
}
