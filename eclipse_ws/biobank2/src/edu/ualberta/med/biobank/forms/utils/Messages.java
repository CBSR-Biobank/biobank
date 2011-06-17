package edu.ualberta.med.biobank.forms.utils;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
    private static final String BUNDLE_NAME = "edu.ualberta.med.biobank.forms.utils.messages"; //$NON-NLS-1$

    public static String PalletScanManagement_dialog_scanError_title;

    static {
        // initialize resource bundle
        NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }

    private Messages() {
    }

    public static String format(String key, Object... params) {
        return NLS.bind(key, params);
    }
}
