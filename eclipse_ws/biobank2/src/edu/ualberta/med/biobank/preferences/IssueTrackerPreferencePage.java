package edu.ualberta.med.biobank.preferences;

import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IntegerFieldEditor;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import edu.ualberta.med.biobank.BiobankPlugin;

public class IssueTrackerPreferencePage extends FieldEditorPreferencePage
    implements IWorkbenchPreferencePage {

    public IssueTrackerPreferencePage() {
        super(GRID);
        setPreferenceStore(BiobankPlugin.getDefault().getPreferenceStore());
    }

    @Override
    protected void createFieldEditors() {
        addField(new StringFieldEditor(PreferenceConstants.ISSUE_TRACKER_EMAIL,
            "Tracker email:", getFieldEditorParent()));
        addField(new StringFieldEditor(
            PreferenceConstants.ISSUE_TRACKER_SMTP_SERVER, "SMTP server:",
            getFieldEditorParent()));
        addField(new IntegerFieldEditor(
            PreferenceConstants.ISSUE_TRACKER_SMTP_SERVER_PORT,
            "SMTP server port:", getFieldEditorParent()));
        addField(new StringFieldEditor(
            PreferenceConstants.ISSUE_TRACKER_SMTP_SERVER_USER,
            "SMTP server username:", getFieldEditorParent()));
        StringFieldEditor field = new StringFieldEditor(
            PreferenceConstants.ISSUE_TRACKER_SMTP_SERVER_PASSWORD,
            "SMTP server password:", getFieldEditorParent());
        field.getTextControl(getFieldEditorParent()).setEchoChar('*');
        addField(field);
    }

    @Override
    public void init(IWorkbench workbench) {
        //
    }

}
