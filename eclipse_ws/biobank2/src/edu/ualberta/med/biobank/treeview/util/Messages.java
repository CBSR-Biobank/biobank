package edu.ualberta.med.biobank.treeview.util;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
    private static final String BUNDLE_NAME = "edu.ualberta.med.biobank.treeview.util.messages"; //$NON-NLS-1$
    public static String CallRunnablePersistOnAdapter_save_error_title;
    public static String CallRunnablePersistOnAdapter_saving;
    static {
        // initialize resource bundle
        NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }

    private Messages() {
    }
}
