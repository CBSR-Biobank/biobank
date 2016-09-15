package edu.ualberta.med.biobank.preferences;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

import edu.ualberta.med.biobank.BiobankPlugin;
import edu.ualberta.med.biobank.common.util.StringUtil;

/**
 * Class used to initialise default preference values.
 */
public class PreferenceInitializer extends AbstractPreferenceInitializer {

    @Override
    public void initializeDefaultPreferences() {
        IPreferenceStore store = BiobankPlugin.getDefault().getPreferenceStore();
        store.setDefault(PreferenceConstants.GENERAL_CONFIRM, "CONFIRM"); //$NON-NLS-1$
        store.setDefault(PreferenceConstants.GENERAL_CANCEL, "CANCEL"); //$NON-NLS-1$
        store.setDefault(PreferenceConstants.LINK_ASSIGN_ACTIVITY_LOG_INTO_FILE, true);

        store.setDefault(PreferenceConstants.LINK_ASSIGN_ACTIVITY_LOG_PATH, StringUtil.EMPTY_STRING);
        store.setDefault(PreferenceConstants.LINK_ASSIGN_ACTIVITY_LOG_ASK_PRINT, true);
        store.setDefault(PreferenceConstants.SCANNER_DPI, 300);

        store.setDefault(PreferenceConstants.SERVER_LIST, "biobank.cbsr.ualberta.ca\nccnabiobank.cbsr.ualberta.ca\ncntrpbiobank.cbsr.ualberta.ca\ncbsr-training.med.ualberta.ca\naicml-med.cs.ualberta.ca"); //$NON-NLS-1$
    }
}
