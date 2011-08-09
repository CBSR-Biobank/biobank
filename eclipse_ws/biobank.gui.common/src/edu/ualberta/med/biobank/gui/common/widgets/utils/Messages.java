package edu.ualberta.med.biobank.gui.common.widgets.utils;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
    private static final String BUNDLE_NAME = "edu.ualberta.med.biobank.gui.common.widgets.utils.messages"; //$NON-NLS-1$
    public static String TableFilter_filterText_label;
    static {
        // initialize resource bundle
        NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }

    private Messages() {
    }
}
