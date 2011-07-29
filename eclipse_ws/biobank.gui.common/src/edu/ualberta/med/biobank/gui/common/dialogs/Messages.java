package edu.ualberta.med.biobank.gui.common.dialogs;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
    private static final String BUNDLE_NAME = "edu.ualberta.med.biobank.gui.common.dialogs.messages"; //$NON-NLS-1$
    public static String BgcBaseDialog_save_error_title;
    static {
        // initialize resource bundle
        NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }

    private Messages() {
    }
}
