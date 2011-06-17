package edu.ualberta.med.biobank.widgets.infotables;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
    private static final String BUNDLE_NAME = "edu.ualberta.med.biobank.widgets.infotables.messages"; //$NON-NLS-1$

    public static String CollectionEventInfoTable_header_visitNumber;
    public static String CollectionEventInfoTable_header_numSourceSpecimens;
    public static String CollectionEventInfoTable_header_numAliquotedSpecimens;
    public static String CollectionEventInfoTable_header_comment;

    public static String infotable_loading_msg;
    public static String label_activity;

    public static String SourceSpecimen_field_type_label;
    public static String SourceSpecimen_field_type_validation_msg;
    public static String SourceSpecimen_field_timeDrawn_label;
    public static String SourceSpecimen_field_originalVolume_label;

    public static String AliquotedSpecimen_field_type_label;
    public static String AliquotedSpecimen_field_type_validation_msg;
    public static String AliquotedSpecimen_field_volume_label;
    public static String AliquotedSpecimen_field_validation_msg;
    public static String AliquotedSpecimen_field_quantity_label;
    public static String AliquotedSpecimen_field_quantity_validation_msg;

    static {
        // initialize resource bundle
        NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }

    private Messages() {
    }
}
