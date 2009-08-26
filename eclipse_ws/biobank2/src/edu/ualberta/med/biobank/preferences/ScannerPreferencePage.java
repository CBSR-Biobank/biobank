package edu.ualberta.med.biobank.preferences;

import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import edu.ualberta.med.biobank.BioBankPlugin;

public class ScannerPreferencePage extends FieldEditorPreferencePage implements
    IWorkbenchPreferencePage {

    public ScannerPreferencePage() {
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
        for (int i = 1; i <= PreferenceConstants.SCANNER_PLATE_NUMBER; i++) {
            addField(new StringFieldEditor(PreferenceConstants.SCANNER_PLATE
                + i, "Plate " + i + " barcode", getFieldEditorParent()));
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
     */
    public void init(IWorkbench workbench) {
    }

}