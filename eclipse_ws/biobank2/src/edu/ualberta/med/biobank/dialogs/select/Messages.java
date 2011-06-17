package edu.ualberta.med.biobank.dialogs.select;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
    private static final String BUNDLE_NAME = "edu.ualberta.med.biobank.dialogs.select.messages"; //$NON-NLS-1$
    public static String ContactAddDialog_description_add;
    public static String ContactAddDialog_description_edit;
    public static String ContactAddDialog_email_label;
    public static String ContactAddDialog_fax_label;
    public static String ContactAddDialog_mobile_label;
    public static String ContactAddDialog_name_label;
    public static String ContactAddDialog_office_label;
    public static String ContactAddDialog_pager_label;
    public static String ContactAddDialog_title_add;
    public static String ContactAddDialog_title_edit;
    public static String ContactAddDialog_title_label;
    public static String SelectClinicContactDialog_description;
    public static String SelectClinicContactDialog_dialog_title;
    public static String SelectClinicContactDialog_main_title;
    public static String SelectParentContainerDialog_description;
    public static String SelectParentContainerDialog_dialog_title;
    public static String SelectParentContainerDialog_main_title;
    public static String SelectParentContainerDialog_parents_list_label;
    public static String SelectParentContainerDialog_select_label;
    public static String SelectParentContainerDialog_select_validation_error_msg;
    public static String SelectStudyDialog_dialog_title;
    public static String SelectStudyDialog_main_message;
    public static String SelectStudyDialog_main_title;
    static {
        // initialize resource bundle
        NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }

    private Messages() {
    }
}
