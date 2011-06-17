package edu.ualberta.med.biobank.treeview.patient;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
    private static final String BUNDLE_NAME = "edu.ualberta.med.biobank.treeview.processing.messages"; //$NON-NLS-1$

    public static String CollectionEventEntryForm_title_new;
    public static String CollectionEventEntryForm_title_edit;

    static {
        // initialize resource bundle
        NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }

    private Messages() {
    }
}
