package edu.ualberta.med.biobank.gui.common.validators;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
    private static final String BUNDLE_NAME = "edu.ualberta.med.biobank.gui.common.validators.messages"; //$NON-NLS-1$
    public static String NonEmptyStringValidator_non_string_error;
    static {
        // initialize resource bundle
        NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }

    private Messages() {
    }
}
