package edu.ualberta.med.biobank.preferences;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

import edu.ualberta.med.biobank.BiobankPlugin;

/**
 * Class used to initialise default preference values.
 */
public class PreferenceInitializer extends AbstractPreferenceInitializer {

    @Override
    public void initializeDefaultPreferences() {
        IPreferenceStore store = BiobankPlugin.getDefault()
            .getPreferenceStore();
        store.setDefault(PreferenceConstants.GENERAL_CONFIRM, "CONFIRM");
        store.setDefault(PreferenceConstants.GENERAL_CANCEL, "CANCEL");
        store.setDefault(
            PreferenceConstants.LINK_ASSIGN_ACTIVITY_LOG_INTO_FILE, true);

        store.setDefault(PreferenceConstants.LINK_ASSIGN_ACTIVITY_LOG_PATH, "");
        store.setDefault(
            PreferenceConstants.LINK_ASSIGN_ACTIVITY_LOG_ASK_PRINT, true);
        store.setDefault(PreferenceConstants.SCANNER_DPI, 300);

        store.setDefault(PreferenceConstants.ISSUE_TRACKER_EMAIL,
            "biobank@cs.ualberta.ca");
        store.setDefault(PreferenceConstants.ISSUE_TRACKER_SMTP_SERVER,
            "smtp.gmail.com");
        store.setDefault(PreferenceConstants.ISSUE_TRACKER_SMTP_SERVER_PORT,
            465);
        store.setDefault(PreferenceConstants.ISSUE_TRACKER_SMTP_SERVER_USER,
            "biobank2@gmail.com");
        store.setDefault(
            PreferenceConstants.ISSUE_TRACKER_SMTP_SERVER_PASSWORD, "catissue");

        store
            .setDefault(
                PreferenceConstants.SERVER_LIST,
                "cbsr.med.ualberta.ca\n10.8.31.50\ncbsr-training.med.ualberta.ca\n10.8.31.51\naicml-med.cs.ualberta.ca");
    }
}
