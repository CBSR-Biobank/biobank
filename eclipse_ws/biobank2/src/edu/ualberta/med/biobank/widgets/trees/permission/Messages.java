package edu.ualberta.med.biobank.widgets.trees.permission;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
    private static final String BUNDLE_NAME = "edu.ualberta.med.biobank.widgets.trees.permission.messages"; //$NON-NLS-1$
    public static String PermissionCheckTree_title;
    public static String PermissionRootNode_text;
    static {
        // initialize resource bundle
        NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }

    private Messages() {
    }
}
