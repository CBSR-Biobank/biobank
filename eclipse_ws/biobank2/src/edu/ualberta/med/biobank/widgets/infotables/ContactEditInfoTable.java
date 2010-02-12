package edu.ualberta.med.biobank.widgets.infotables;

import java.util.Collection;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.MenuItem;

import edu.ualberta.med.biobank.BioBankPlugin;
import edu.ualberta.med.biobank.common.wrappers.ContactWrapper;

public class ContactEditInfoTable extends ContactInfoTable {

    IEditInfoTable<ContactWrapper> editor;

    public ContactEditInfoTable(Composite parent,
        Collection<ContactWrapper> contacts,
        IEditInfoTable<ContactWrapper> editor) {
        super(parent, contacts);

        this.editor = editor;

        MenuItem item = new MenuItem(menu, SWT.PUSH);
        item.setText("Edit");
        item.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent event) {
                ContactWrapper contact = ((TableRowData) getSelection().o).contact;
                if (contact == null) {
                    BioBankPlugin.openError("Edit Clinic", "Invalid selection");
                    return;
                }
                ContactEditInfoTable.this.editor.editItem(contact);
            }
        });

        item = new MenuItem(menu, SWT.PUSH);
        item.setText("Delete");
        item.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent event) {
                ContactWrapper contact = ((TableRowData) getSelection().o).contact;
                if (contact == null) {
                    BioBankPlugin.openError("Delete Clinic",
                        "Invalid selection");
                    return;
                }

                boolean confirm = BioBankPlugin.openConfirm("Delete Clinic",
                    "Are you sure you want to delete clinic \""
                        + contact.getClinic().getName() + "\"");

                if (confirm) {
                    ContactEditInfoTable.this.editor.deleteItem(contact);
                }
            }
        });
    }

}
