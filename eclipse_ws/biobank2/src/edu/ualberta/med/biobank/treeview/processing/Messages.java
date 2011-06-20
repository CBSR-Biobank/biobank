package edu.ualberta.med.biobank.treeview.processing;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
    private static final String BUNDLE_NAME = "edu.ualberta.med.biobank.treeview.processing.messages"; //$NON-NLS-1$

    public static String ProvessingEventAdapter_tooltiptext;
    public static String ProvessingEventAdapter_tooltiptext_withdate;
    public static String ProcessingEventAdapter_deleteMsg;

    public static String ProcessingEventAdapter_pevent_label;

    public static String ProcessingEventGroup_pevent_add_label;

    public static String SpecimenAssignAdapter_assign_label;

    public static String SpecimenLinkAdapter_link_label;

    static {
        // initialize resource bundle
        NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }

    private Messages() {
    }
}
