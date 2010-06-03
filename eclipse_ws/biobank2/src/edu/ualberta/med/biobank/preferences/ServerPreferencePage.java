package edu.ualberta.med.biobank.preferences;

import java.util.StringTokenizer;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.ListEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import edu.ualberta.med.biobank.BioBankPlugin;
import edu.ualberta.med.biobank.dialogs.NewServerDialog;

public class ServerPreferencePage extends FieldEditorPreferencePage implements
    IWorkbenchPreferencePage {

    public ServerPreferencePage() {
        super(GRID);
        setPreferenceStore(BioBankPlugin.getDefault().getPreferenceStore());
    }

    @Override
    protected void createFieldEditors() {
        addField(new ListEditor(PreferenceConstants.SERVER_LIST, "Servers:",
            getFieldEditorParent()) {
            @Override
            protected String createList(String[] items) {
                String appendedList = "";
                for (int i = 0; i < items.length; i++) {
                    appendedList += items[i];
                    if (i < items.length - 1)
                        appendedList += "\n";
                }
                return appendedList;
            }

            @Override
            protected String getNewInputObject() {
                NewServerDialog dlg = new NewServerDialog(
                    getFieldEditorParent().getShell());
                if (dlg.open() == Dialog.OK) {
                    return dlg.getText();
                } else
                    return null;
            }

            @Override
            protected String[] parseString(String stringList) {
                StringTokenizer st = new StringTokenizer(stringList, "\n");
                String[] items = new String[st.countTokens()];
                int i = 0;
                while (st.hasMoreTokens()) {
                    items[i++] = st.nextToken();
                }
                return items;
            }

        });
    }

    @Override
    public void init(IWorkbench workbench) {

    }

}
