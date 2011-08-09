package edu.ualberta.med.biobank.gui.common.widgets;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
    private static final String BUNDLE_NAME = "edu.ualberta.med.biobank.gui.common.widgets.messages"; //$NON-NLS-1$
    public static String BgcFileBrowser_browse;
    public static String BgcFileBrowser_open;
    static {
        // initialize resource bundle
        NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }

    private Messages() {
    }
}
