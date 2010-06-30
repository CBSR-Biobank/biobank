package edu.ualberta.med.biobank.preferences;

import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import edu.ualberta.med.biobank.BioBankPlugin;

public class CabinetPreferencePage extends FieldEditorPreferencePage implements
    IWorkbenchPreferencePage {

    public CabinetPreferencePage() {
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
        addField(new StringFieldEditor(
            PreferenceConstants.CABINET_CONTAINER_NAME_CONTAINS,
            "Top cabinet container types name contains:",
            getFieldEditorParent()));
    }

    @Override
    public void init(IWorkbench workbench) {
    }
}