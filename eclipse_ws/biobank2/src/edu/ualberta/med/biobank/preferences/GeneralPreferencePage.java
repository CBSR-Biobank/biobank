package edu.ualberta.med.biobank.preferences;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferenceConstants;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.internal.WorkbenchMessages;

import edu.ualberta.med.biobank.BiobankPlugin;

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
    }

    @Override
    protected void createFieldEditors() {
        showVersionFieldEditor = new BooleanFieldEditor(
            PreferenceConstants.GENERAL_SHOW_VERSION,
            Messages.GeneralPreferencePage_version_label,
            getFieldEditorParent());
        addField(showVersionFieldEditor);

        showHeapFieldEditor = new BooleanFieldEditor(
            IWorkbenchPreferenceConstants.SHOW_MEMORY_MONITOR,
            WorkbenchMessages.WorkbenchPreference_HeapStatusButton,
            getFieldEditorParent());
        showHeapFieldEditor.getDescriptionControl(getFieldEditorParent())
            .setToolTipText(
                WorkbenchMessages.WorkbenchPreference_HeapStatusButtonToolTip);
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
