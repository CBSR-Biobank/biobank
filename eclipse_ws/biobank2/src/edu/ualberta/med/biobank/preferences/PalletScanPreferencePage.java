package edu.ualberta.med.biobank.preferences;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import edu.ualberta.med.biobank.BioBankPlugin;

public class PalletScanPreferencePage extends FieldEditorPreferencePage
    implements IWorkbenchPreferencePage {

    public PalletScanPreferencePage() {
        super(GRID);
        setPreferenceStore(BioBankPlugin.getDefault().getPreferenceStore());
    }

    /**
     * Creates the field editors. Field editors are abstractions of the common
     * GUI blocks needed to manipulate various types of preferences. Each field
     * editor knows how to save and restore itself.
     */
    @Override
    public void createFieldEditors() {
        addField(new BooleanFieldEditor(
            PreferenceConstants.SCAN_LINK_ROW_SELECT_ONLY,
            "Select sample type for whole row only", getFieldEditorParent()));
    }

    @Override
    public void init(IWorkbench workbench) {
    }
}