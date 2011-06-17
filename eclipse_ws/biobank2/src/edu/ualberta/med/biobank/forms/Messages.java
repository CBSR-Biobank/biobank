package edu.ualberta.med.biobank.forms;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
    private static final String BUNDLE_NAME = "edu.ualberta.med.biobank.forms.messages"; //$NON-NLS-1$

    public static String label_name;
    public static String label_nameShort;
    public static String label_activity;
    public static String label_comments;

    public static String SiteViewForm_field_studyCount_label;
    public static String site_field_type_label;
    public static String SiteViewForm_field_topLevelCount_label;
    public static String SiteViewForm_field_patientCount_label;
    public static String SiteViewForm_field_pvCount_label;
    public static String SiteViewForm_field_totalSpecimen;
    public static String SiteViewForm_types_title;
    public static String SiteViewForm_type_add;
    public static String SiteViewForm_topContainers_title;
    public static String SiteViewForm_topContainers_add;
    public static String SiteViewForm_title;
    public static String SiteViewForm_studies_title;
    public static String SiteViewForm_reload_error_msg;
    public static String SiteEntryForm_creation_msg;
    public static String SiteEntryForm_edition_msg;
    public static String SiteEntryForm_title_new;
    public static String SiteEntryForm_title_edit;
    public static String SiteEntryForm_main_title;
    public static String SiteEntryForm_main_description;
    public static String SiteEntryForm_field_name_validation_msg;
    public static String SiteEntryForm_field_nameShort_validation_msg;
    public static String SiteEntryForm_field_activity_validation_msg;
    public static String SiteEntryForm_studies_title;
    public static String SiteEntryForm_studies_add;

    public static String study_visit_info_dateProcessed;
    public static String StudyViewForm_title;
    public static String StudyViewForm_field_label_total_patients;
    public static String StudyViewForm_field_label_total_patientVisits;
    public static String StudyViewForm_clinic_title;
    public static String StudyViewForm_source_specimen_title;
    public static String StudyViewForm_aliquoted_specimen_title;
    public static String StudyViewForm_visit_info_attributes_title;
    public static String StudyViewForm_visit_info_msg;
    public static String StudyEntryForm_creation_msg;
    public static String StudyEntryForm_edition_msg;
    public static String StudyEntryForm_title_new;
    public static String StudyEntryForm_title_edit;
    public static String StudyEntryForm_main_title;
    public static String StudyEntryForm_contacts_title;
    public static String StudyEntryForm_activity_validator_msg;
    public static String StudyEntryForm_aliquoted_specimens_button_add;
    public static String StudyEntryForm_aliquoted_specimens_title;
    public static String StudyEntryForm_contacts_button_add;
    public static String StudyEntryForm_name_validator_msg;
    public static String StudyEntryForm_nameShort_validator_msg;
    public static String StudyEntryForm_source_specimens_button_add;
    public static String StudyEntryForm_source_specimens_title;
    public static String StudyEntryForm_visit_info_title;

    public static String clinic_field_label_sendsShipments;
    public static String clinic_contact_title;
    public static String ClinicViewForm_title;
    public static String ClinicViewForm_field_label_totalPatients;
    public static String ClinicViewForm_field_label_totalCollectionEvents;
    public static String ClinicViewForm_studies_title;
    public static String ClinicEntryForm_creation_msg;
    public static String ClinicEntryForm_msg_ok;
    public static String ClinicEntryForm_msg_noClinicName;
    public static String ClinicEntryForm_title_new;
    public static String ClinicEntryForm_title_edit;
    public static String ClinicEntryForm_main_title;
    public static String ClinicEntryForm_main_description;
    public static String ClinicEntryForm_activity_validator_msg;
    public static String ClinicEntryForm_contact_button_add;

    public static String container_field_label_site;
    public static String container_field_label_label;
    public static String container_field_label_barcode;
    public static String container_field_label_type;
    public static String container_field_label_temperature;
    public static String ContainerViewForm_specimens_title;
    public static String ContainerViewForm_title;
    public static String ContainerViewForm_visual_title;
    public static String ContainerViewForm_visualization_delete_error_msg;
    public static String ContainerViewForm_visualization_delete_monitor_msg;
    public static String ContainerViewForm_visualization_delete_select_label;
    public static String ContainerViewForm_visualization_delete_button_text;
    public static String ContainerViewForm_visualization_error_msg;
    public static String ContainerViewForm_visualization_all_label;
    public static String ContainerViewForm_visualization_init_button_text;
    public static String ContainerViewForm_visualization_init_error_msg;
    public static String ContainerViewForm_visualization_init_selection_label;
    public static String ContainerViewForm_vizualisation_delete_confirm_msg;
    public static String ContainerViewForm_vizualisation_delete_confirm_title;
    public static String ContainerViewForm_vizualisation_init_monitor_msg;
    public static String ContainerViewForm_initCell_error_title;
    public static String ContainerViewForm_initCell_error_msg;
    public static String ContainerViewForm_refresh_error_msg;

    public static String containerType_field_label_site;
    public static String containerType_field_label_topLevel;
    public static String containerType_field_label_rows;
    public static String containerType_field_label_cols;
    public static String containerType_field_label_temperature;
    public static String containerType_field_label_scheme;
    public static String ContainerTypeViewForm_title;
    public static String ContainerTypeViewForm_specimens_title;
    public static String ContainerTypeViewForm_specimens_label;
    public static String ContainerTypeViewForm_types_title;
    public static String ContainerTypeViewForm_types_label;
    public static String ContainerTypeViewForm_visual;
    public static String ContainerTypeEntryForm_creation_msg;
    public static String ContainerTypeEntryForm_edition_msg;
    public static String ContainerTypeEntryForm_name_validation_msg;
    public static String ContainerTypeEntryForm_nameShort_validation_msg;
    public static String ContainerTypeEntryForm_scheme_validation_msg;
    public static String ContainerTypeEntryForm_scheme_error_msg;
    public static String ContainerTypeEntryForm_rows_validation_msg;
    public static String ContainerTypeEntryForm_cols_validation_msg;
    public static String ContainerTypeEntryForm_temperature_validation_msg;
    public static String ContainerTypeEntryForm_activity_validation_msg;
    public static String ContainerTypeEntryForm_new_title;
    public static String ContainerTypeEntryForm_edit_title;
    public static String ContainerTypeEntryForm_main_title;
    public static String ContainerTypeEntryForm_contents_title;
    public static String ContainerTypeEntryForm_contents_button_container;
    public static String ContainerTypeEntryForm_contents_button_specimen;
    public static String ContainerTypeEntryForm_contents_specimen_selected;
    public static String ContainerTypeEntryForm_contents_specimen_available;
    public static String ContainerTypeEntryForm_contents_subcontainer_selected;
    public static String ContainerTypeEntryForm_contents_subcontainer_available;
    public static String ContainerTypeEntryForm_save_error_msg_specimen_added;
    public static String ContainerTypeEntryForm_save_error_msg_specimen_removed;
    public static String ContainerTypeEntryForm_save_error_msg_subcontainer_added;
    public static String ContainerTypeEntryForm_save_error_msg_subcontainer_removed;

    public static String PatientViewForm_title;
    public static String patient_field_label_study;
    public static String PatientViewForm_label_createdAt;
    public static String PatientViewForm_label_totalVisits;
    public static String PatientViewForm_label_totalSourceSpecimens;
    public static String PatientViewForm_label_totalAliquotedSpecimens;
    public static String PatientViewForm_visits_title;
    public static String PatientEntryForm_creation_msg;
    public static String PatientEntryForm_edition_msg;
    public static String PatientEntryForm_patientNumber_validation_msg;
    public static String PatientEntryForm_new_title;
    public static String PatientEntryForm_edit_title;
    public static String PatientEntryForm_main_title;
    public static String PatientEntryForm_field_study_label;
    public static String PatientEntryForm_field_study_validation_msg;
    public static String PatientEntryForm_field_pNumber_label;
    public static String PatientEntryForm_retrieve_error_msg;

    public static String CollectionEventEntryForm_creation_msg;
    public static String CollectionEventEntryForm_edition_msg;
    public static String CollectionEventEntryForm_main_title;
    public static String CollectionEventEntryForm_title_new;
    public static String CollectionEventEntryForm_title_edit;
    public static String CollectionEventEntryForm_field_study_label;
    public static String CollectionEventEntryForm_field_patient_label;
    public static String CollectionEventEntryForm_field_visitNumber_label;
    public static String CollectionEventEntryForm_field_visitNumber_validation_msg;
    public static String CollectionEventEntryForm_specimens_title;
    public static String CollectionEventEntryForm_specimens_add_title;
    public static String CollectionEventEntryForm_specimenstypes_error_msg;
    public static String CollectionEventViewForm_title;
    public static String CollectionEventViewForm_main_title;
    public static String CollectionEventViewForm_sourcespecimens_title;
    public static String CollectionEventViewForm_aliquotedspecimens_title;
    public static String CollectionEventEntryForm_timeDrawn_label;
    public static String CollectionEventEntryForm_field_activity_validation_msg;

    public static String ProcessingEventViewForm_title;
    public static String ProcessingEventViewForm_specimens_title;
    public static String ProcessingEventEntryForm_title_new;
    public static String ProcessingEventEntryForm_creation_msg;
    public static String ProcessingEventEntryForm_edition_msg;
    public static String ProcessingEventEntryForm_title_edit_worksheet;
    public static String ProcessingEventEntryForm_title_edit_noworksheet;
    public static String ProcessingEventEntryForm_main_title;
    public static String ProcessingEvent_field_center_label;
    public static String ProcessingEvent_field_date_label;
    public static String ProcessingEventEntryForm_field_date_validation_msg;
    public static String ProcessingEvent_field_worksheet_label;
    public static String ProcessingEventEntryForm_field_activity_validation_msg;
    public static String ProcessingEventEntryForm_specimens_title;

    public static String DecodePlate_tabTitle;
    public static String DecodePlate_dialog_scanError_title;
    public static String ScanPlate_tabTitle;
    public static String ScanPlate_dialog_scanError_title;

    static {
        // initialize resource bundle
        NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }

    private Messages() {
    }

    public static String format(String formattedString, Object... params) {
        return NLS.bind(formattedString, params);
    }
}
