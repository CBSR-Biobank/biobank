package edu.ualberta.med.biobank.widgets.multiselect;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
    private static final String BUNDLE_NAME = "edu.ualberta.med.biobank.widgets.multiselect.messages"; //$NON-NLS-1$
    public static String MultiSelectWidget_move_tooltip;
    public static String MultiSelectWidget_remove_tooltip;
    static {
        // initialize resource bundle
        NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }

    private Messages() {
    }
}
