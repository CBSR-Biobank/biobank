package edu.ualberta.med.biobank.preferences;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

import edu.ualberta.med.biobank.BioBankPlugin;

/**
 * Class used to initialize default preference values.
 */
public class PreferenceInitializer extends AbstractPreferenceInitializer {

    /*
     * (non-Javadoc)
     * 
     * @seeorg.eclipse.core.runtime.preferences.AbstractPreferenceInitializer#
     * initializeDefaultPreferences()
     */
    @Override
    public void initializeDefaultPreferences() {
        IPreferenceStore store = BioBankPlugin.getDefault()
            .getPreferenceStore();
        store.setDefault(PreferenceConstants.GENERAL_CONFIRM, "CONFIRM");
        store.setDefault(PreferenceConstants.GENERAL_CANCEL, "CANCEL");
        store.setDefault(PreferenceConstants.GENERAL_ASK_PRINT, true);
        store.setDefault(PreferenceConstants.GENERAL_TIME_OUT, 10);
        store.setDefault(PreferenceConstants.SCANNER_DPI, 300);
        store.setDefault(PreferenceConstants.SCAN_LINK_ROW_SELECT_ONLY, true);

        for (int i = 0; i < PreferenceConstants.SCANNER_PLATE_BARCODES.length; i++) {
            store.setDefault(PreferenceConstants.SCANNER_PLATE_BARCODES[i],
                "PLATE" + (i + 1));
        }
    }
}
