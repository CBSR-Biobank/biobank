package edu.ualberta.med.biobank.preferences;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import edu.ualberta.med.biobank.BioBankPlugin;

public class LinkAssignPreferencePage extends FieldEditorPreferencePage
    implements IWorkbenchPreferencePage {

    public LinkAssignPreferencePage() {
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
            PreferenceConstants.LINK_ASSIGN_ASK_PRINT,
            "Ask to print activity log", getFieldEditorParent()));
        // PrinterData[] datas = Printer.getPrinterList();
        // String[][] list = new String[datas.length][2];
        // for (int i = 0; i < datas.length; i++) {
        // list[i][0] = datas[i].name;
        // list[i][1] = datas[i].name;
        // }
        // ComboFieldEditor printerCombo = new ComboFieldEditor(
        // PreferenceConstants.LINK_ASSIGN_PRINTER, "Printer", list,
        // getFieldEditorParent());
        // addField(printerCombo);
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