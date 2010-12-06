package edu.ualberta.med.biobank.preferences;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import edu.ualberta.med.biobank.BioBankPlugin;

public class GeneralPreferencePage extends FieldEditorPreferencePage implements
    IWorkbenchPreferencePage {

    private BooleanFieldEditor enabledFieldEditor;

    public GeneralPreferencePage() {
        super(GRID);
        setPreferenceStore(BioBankPlugin.getDefault().getPreferenceStore());
    }

    @Override
    public void init(IWorkbench workbench) {
    }

    @Override
    protected void createFieldEditors() {

        enabledFieldEditor = new BooleanFieldEditor(
            PreferenceConstants.GENERAL_SHOW_VERSION,
            "Show Version in Main Window Title", getFieldEditorParent());
        addField(enabledFieldEditor);
    }

}
