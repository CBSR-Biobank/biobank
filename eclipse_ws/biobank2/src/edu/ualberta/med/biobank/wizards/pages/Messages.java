package edu.ualberta.med.biobank.wizards.pages;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
    private static final String BUNDLE_NAME = "edu.ualberta.med.biobank.wizards.pages.messages"; //$NON-NLS-1$
    public static String EnterPnumberPage_pnber_description;
    public static String EnterPnumberPage_pnber_label;
    public static String EnterPnumberPage_pnber_required_msg;
    public static String SelectPatientVisitPage_visit_description;
    public static String SelectPatientVisitPage_visit_required_msg;
    static {
        // initialize resource bundle
        NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }

    private Messages() {
    }
}
