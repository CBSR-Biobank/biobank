package edu.ualberta.med.biobank.widgets.report;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
    private static final String BUNDLE_NAME = "edu.ualberta.med.biobank.widgets.report.messages"; //$NON-NLS-1$
    public static String ColumnSelectWidget_available_label;
    public static String ColumnSelectWidget_displayed_label;
    public static String ComboFilterValueWidget_select_enter_text;
    public static String FilterRow_nosuggest_error_msg;
    public static String FilterRow_nosuggest_error_title;
    public static String FilterRow_possible_value_tooltip;
    public static String FilterRow_problem_suggest_error_msg;
    public static String FilterRow_toolong_suggest_error_msg;
    public static String FilterRow_toomany_suggest_error_msg;
    public static String SetFilterValueWidget_add_label;
    public static String SetFilterValueWidget_collapse_label;
    public static String SetFilterValueWidget_expand_label;
    public static String SetFilterValueWidget_novalue_label;
    public static String SetFilterValueWidget_remove_label;
    public static String TextFilterValueWidget_wildcard_info;
    static {
        // initialize resource bundle
        NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }

    private Messages() {
    }
}
