package edu.ualberta.med.biobank.forms.listener;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
    private static final String BUNDLE_NAME = "edu.ualberta.med.biobank.forms.listener.messages"; //$NON-NLS-1$
    public static String ProgressMonitorDialogBusyListener_loading_error;
    static {
        // initialize resource bundle
        NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }

    private Messages() {
    }
}
