package edu.ualberta.med.biobank.treeview.patient;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
    private static final String BUNDLE_NAME = "edu.ualberta.med.biobank.treeview.patient.messages"; //$NON-NLS-1$

    public static String CollectionEventAdapter_cevent_label;

    public static String CollectionEventAdapter_create_error_msg;

    public static String CollectionEventAdapter_delete_confirm_msg;

    public static String CollectionEventAdapter_error_title;

    public static String CollectionEventAdapter_nospecimens_label;

    public static String CollectionEventEntryForm_title_new;
    public static String CollectionEventEntryForm_title_edit;

    public static String PatientAdapter_add_cevent_label;

    public static String PatientAdapter_delete_confirm_msg;

    public static String PatientAdapter_error;

    public static String PatientAdapter_message;

    public static String PatientAdapter_patient_label;

    static {
        // initialize resource bundle
        NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }

    private Messages() {
    }
}
