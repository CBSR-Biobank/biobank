package edu.ualberta.med.biobank.forms.utils;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
    private static final String BUNDLE_NAME =
        "edu.ualberta.med.biobank.forms.utils.messages"; //$NON-NLS-1$
    public static String RequestTableGroup_0;
    public static String RequestTableGroup_1;
    public static String RequestTableGroup_2;
    static {
        // initialize resource bundle
        NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }

    private Messages() {
    }
}
