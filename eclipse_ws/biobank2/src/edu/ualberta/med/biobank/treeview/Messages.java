package edu.ualberta.med.biobank.treeview;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
    private static final String BUNDLE_NAME = "edu.ualberta.med.biobank.treeview.messages"; //$NON-NLS-1$
    public static String AbstractSearchedNode_clear;
    public static String AbstractSearchedNode_searched;
    public static String AbstractTodayNode_today;
    public static String AdapterBase_confirm_delete_title;
    public static String AdapterBase_delete_error_title;
    public static String AdapterBase_delete_label;
    public static String AdapterBase_edit_label;
    public static String AdapterBase_load_error_title;
    public static String AdapterBase_loading;
    public static String AdapterBase_new_label;
    public static String AdapterBase_unknow;
    public static String AdapterBase_view_label;
    public static String SpecimenAdapter_2;
    public static String SpecimenAdapter_specimen_label;
    static {
        // initialize resource bundle
        NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }

    private Messages() {
    }
}
