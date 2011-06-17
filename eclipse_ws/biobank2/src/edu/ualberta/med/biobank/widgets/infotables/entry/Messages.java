package edu.ualberta.med.biobank.widgets.infotables.entry;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
    private static final String BUNDLE_NAME = "edu.ualberta.med.biobank.widgets.infotables.entry.messages"; //$NON-NLS-1$

    public static String SourceSpecimenEntryInfoTable_delete_title;
    public static String SourceSpecimenEntryInfoTable_delete_question;

    public static String AliquotedSpecimenEntryInfoTable_delete_title;
    public static String AliquotedSpecimenEntryInfoTable_delete_question;

    public static String SpecimenEntryInfoTable_delete_title;
    public static String SpecimenEntryInfoTable_delete_question;

    static {
        // initialize resource bundle
        NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }

    private Messages() {
    }
}
