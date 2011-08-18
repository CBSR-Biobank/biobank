package edu.ualberta.med.biobank.gui.common.forms;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
    private static final String BUNDLE_NAME = "edu.ualberta.med.biobank.gui.common.forms.messages"; //$NON-NLS-1$
    public static String BgcEntryFormActions_cancel_label;
    public static String BgcEntryFormActions_confirm_tooltip;
    public static String BgcEntryFormActions_print_error_msg;
    public static String BgcEntryFormActions_print_label;
    public static String BgcEntryFormActions_reset_label;
    static {
        // initialize resource bundle
        NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }

    private Messages() {
    }
}
