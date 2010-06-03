package edu.ualberta.med.biobank.preferences;

import java.io.File;

import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

import edu.ualberta.med.biobank.BioBankPlugin;

/**
 * Class used to initialise default preference values.
 */
public class PreferenceInitializer extends AbstractPreferenceInitializer {

    @Override
    public void initializeDefaultPreferences() {
        IPreferenceStore store = BioBankPlugin.getDefault()
            .getPreferenceStore();
        store.setDefault(PreferenceConstants.GENERAL_CONFIRM, "CONFIRM");
        store.setDefault(PreferenceConstants.GENERAL_CANCEL, "CANCEL");
        store.setDefault(
            PreferenceConstants.LINK_ASSIGN_ACTIVITY_LOG_INTO_FILE, true);

        String path = Platform.getInstallLocation().getURL().getPath()
            + File.separator + "activityLogs";
        File file = new File(path);
        if (!file.exists()) {
            file.mkdir();
        }
        store.setDefault(PreferenceConstants.LINK_ASSIGN_ACTIVITY_LOG_PATH,
            path);
        store.setDefault(
            PreferenceConstants.LINK_ASSIGN_ACTIVITY_LOG_ASK_PRINT, true);
        store.setDefault(PreferenceConstants.SCANNER_DPI, 300);
        store.setDefault(PreferenceConstants.SCAN_LINK_ROW_SELECT_ONLY, true);
        store.setDefault(
            PreferenceConstants.PALLET_SCAN_CONTAINER_NAME_CONTAINS, "pallet");
        store.setDefault(PreferenceConstants.CABINET_CONTAINER_NAME_CONTAINS,
            "cabinet");

        for (int i = 0; i < PreferenceConstants.SCANNER_PLATE_BARCODES.length; i++) {
            store.setDefault(PreferenceConstants.SCANNER_PLATE_BARCODES[i],
                "PLATE" + (i + 1));
        }

        store.setDefault(PreferenceConstants.ISSUE_TRACKER_EMAIL,
            "biobank2@gmail.com");
        store.setDefault(PreferenceConstants.ISSUE_TRACKER_SMTP_SERVER,
            "smtp.gmail.com");
        store.setDefault(PreferenceConstants.ISSUE_TRACKER_SMTP_SERVER_PORT,
            465);
        store.setDefault(PreferenceConstants.ISSUE_TRACKER_SMTP_SERVER_USER,
            "biobank2@gmail.com");
        store.setDefault(
            PreferenceConstants.ISSUE_TRACKER_SMTP_SERVER_PASSWORD, "catissue");

        store
            .setDefault(PreferenceConstants.SERVER_LIST,
                "cbsr.med.ualberta.ca:8443\n10.8.31.50:8443\naicml-med.cs.ualberta.ca:8443");
    }
}
