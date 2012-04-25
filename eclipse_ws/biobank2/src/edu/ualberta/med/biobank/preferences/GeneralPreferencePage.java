package edu.ualberta.med.biobank.preferences;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferenceConstants;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.PlatformUI;

import edu.ualberta.med.biobank.BiobankPlugin;
import edu.ualberta.med.biobank.common.util.StringUtil;

@SuppressWarnings("restriction")
public class GeneralPreferencePage extends FieldEditorPreferencePage implements
    IWorkbenchPreferencePage {

    private BooleanFieldEditor showVersionFieldEditor;

    private BooleanFieldEditor showHeapFieldEditor;

    public GeneralPreferencePage() {
        super(GRID);
        setPreferenceStore(BiobankPlugin.getDefault().getPreferenceStore());
    }

    @Override
    public void init(IWorkbench workbench) {
        //
    }

    @Override
    protected void createFieldEditors() {
        showVersionFieldEditor = new BooleanFieldEditor(
            PreferenceConstants.GENERAL_SHOW_VERSION,
            "Show software version in main window title",
            getFieldEditorParent());
        addField(showVersionFieldEditor);

        showHeapFieldEditor = new BooleanFieldEditor(
            IWorkbenchPreferenceConstants.SHOW_MEMORY_MONITOR,
            StringUtil.EMPTY_STRING,
            getFieldEditorParent());
        showHeapFieldEditor.getDescriptionControl(getFieldEditorParent())
            .setToolTipText(
                StringUtil.EMPTY_STRING);
        addField(showHeapFieldEditor);
    }

    @Override
    public boolean performOk() {
        super.performOk();
        PlatformUI.getPreferenceStore().setValue(
            IWorkbenchPreferenceConstants.SHOW_MEMORY_MONITOR,
            showHeapFieldEditor.getBooleanValue());
        BiobankPlugin.getDefault().updateHeapStatus(
            showHeapFieldEditor.getBooleanValue());
        return true;
    }

}
