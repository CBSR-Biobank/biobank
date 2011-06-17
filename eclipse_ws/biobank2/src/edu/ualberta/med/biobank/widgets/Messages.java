package edu.ualberta.med.biobank.widgets;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
    private static final String BUNDLE_NAME = "edu.ualberta.med.biobank.widgets.infotables.messages"; //$NON-NLS-1$

    public static String AliquotedSpecimenSelectionWidget_selections_validation_msg;
    public static String AliquotedSpecimenSelectionWidget_selections_status_msg;

    static {
        // initialize resource bundle
        NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }

    private Messages() {
    }
}
