package edu.ualberta.med.biobank.preferences;

import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IntegerFieldEditor;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import edu.ualberta.med.biobank.BiobankPlugin;

public class IssueTrackerPreferencePage extends FieldEditorPreferencePage
    implements IWorkbenchPreferencePage {
    private static final I18n i18n = I18nFactory
        .getI18n(IssueTrackerPreferencePage.class);

    public IssueTrackerPreferencePage() {
        super(GRID);
        setPreferenceStore(BiobankPlugin.getDefault().getPreferenceStore());
    }

    @SuppressWarnings("nls")
    @Override
    protected void createFieldEditors() {
        addField(new StringFieldEditor(PreferenceConstants.ISSUE_TRACKER_EMAIL,
            // label.
            i18n.tr("Tracker email:"), getFieldEditorParent()));
        addField(new StringFieldEditor(
            PreferenceConstants.ISSUE_TRACKER_SMTP_SERVER,
            // label.
            i18n.tr("SMTP server:"),
            getFieldEditorParent()));
        addField(new IntegerFieldEditor(
            PreferenceConstants.ISSUE_TRACKER_SMTP_SERVER_PORT,
            // label.
            i18n.tr("SMTP server port:"), getFieldEditorParent()));
        addField(new StringFieldEditor(
            PreferenceConstants.ISSUE_TRACKER_SMTP_SERVER_USER,
            // label.
            i18n.tr("SMTP server username:"), getFieldEditorParent()));
        StringFieldEditor field = new StringFieldEditor(
            PreferenceConstants.ISSUE_TRACKER_SMTP_SERVER_PASSWORD,
            // label.
            i18n.tr("SMTP server password:"), getFieldEditorParent());
        field.getTextControl(getFieldEditorParent()).setEchoChar('*');
        addField(field);
    }

    @Override
    public void init(IWorkbench workbench) {
        //
    }

}
