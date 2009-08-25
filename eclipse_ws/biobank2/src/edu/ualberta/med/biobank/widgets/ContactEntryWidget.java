package edu.ualberta.med.biobank.widgets;

import java.util.Collection;
import java.util.HashSet;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.widgets.FormToolkit;

import edu.ualberta.med.biobank.dialogs.ContactAddDialog;
import edu.ualberta.med.biobank.model.Contact;
import edu.ualberta.med.biobank.widgets.infotables.BiobankCollectionModel;
import edu.ualberta.med.biobank.widgets.infotables.ContactInfoTable;
import edu.ualberta.med.biobank.widgets.listener.BiobankEntryFormWidgetListener;
import edu.ualberta.med.biobank.widgets.listener.MultiSelectEvent;

public class ContactEntryWidget extends BiobankWidget {

    private Collection<Contact> selectedContacts;

    private ContactInfoTable contactInfoTable;

    private Button addClinicButton;

    public ContactEntryWidget(Composite parent, int style,
        Collection<Contact> selectedContacts, FormToolkit toolkit) {
        super(parent, style);
        Assert.isNotNull(toolkit, "toolkit is null");

        if (selectedContacts == null) {
            selectedContacts = new HashSet<Contact>();
        }
        this.selectedContacts = selectedContacts;

        setLayout(new GridLayout(1, false));
        setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        contactInfoTable = new ContactInfoTable(parent, selectedContacts);
        contactInfoTable.adaptToToolkit(toolkit, true);
        addTableMenu();
        contactInfoTable
            .addSelectionChangedListener(new BiobankEntryFormWidgetListener() {
                @Override
                public void selectionChanged(MultiSelectEvent event) {
                    ContactEntryWidget.this.notifyListeners();
                }
            });

        addClinicButton = toolkit.createButton(parent, "Add Contact", SWT.PUSH);
        addClinicButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                addOrEditContact(true, new Contact());
            }
        });
    }

    private void addTableMenu() {
        Menu menu = new Menu(PlatformUI.getWorkbench()
            .getActiveWorkbenchWindow().getShell(), SWT.NONE);
        contactInfoTable.getTableViewer().getTable().setMenu(menu);

        MenuItem item = new MenuItem(menu, SWT.PUSH);
        item.setText("Edit");
        item.addSelectionListener(new SelectionListener() {
            public void widgetSelected(SelectionEvent event) {
                IStructuredSelection stSelection = (IStructuredSelection) contactInfoTable
                    .getTableViewer().getSelection();

                BiobankCollectionModel item = (BiobankCollectionModel) stSelection
                    .getFirstElement();
                addOrEditContact(false, (Contact) item.o);
            }

            public void widgetDefaultSelected(SelectionEvent e) {
            }
        });

        item = new MenuItem(menu, SWT.PUSH);
        item.setText("Delete");
        item.addSelectionListener(new SelectionListener() {
            public void widgetSelected(SelectionEvent event) {
                IStructuredSelection stSelection = (IStructuredSelection) contactInfoTable
                    .getTableViewer().getSelection();

                BiobankCollectionModel item = (BiobankCollectionModel) stSelection
                    .getFirstElement();
                Contact contact = (Contact) item.o;

                boolean confirm = MessageDialog.openConfirm(PlatformUI
                    .getWorkbench().getActiveWorkbenchWindow().getShell(),
                    "Delete Clinic",
                    "Are you sure you want to delete clinic \""
                        + contact.getClinic().getName() + "\"");

                if (confirm) {
                    Collection<Contact> contactToDelete = new HashSet<Contact>();
                    for (Contact c : selectedContacts) {
                        if (c.getClinic().getName().equals(
                            contact.getClinic().getName()))
                            contactToDelete.add(c);
                    }

                    for (Contact c : contactToDelete) {
                        selectedContacts.remove(c);
                    }

                    contactInfoTable.setCollection(selectedContacts);
                }
            }

            public void widgetDefaultSelected(SelectionEvent e) {
            }
        });
    }

    private void addOrEditContact(boolean add, Contact contact) {
        ContactAddDialog dlg = new ContactAddDialog(PlatformUI.getWorkbench()
            .getActiveWorkbenchWindow().getShell(), contact);
        if (dlg.open() == Dialog.OK) {
            if (add) {
                // only add to the collection when adding and not editing
                selectedContacts.add(dlg.getContact());
            }
            contactInfoTable.setCollection(selectedContacts);
        }
    }

    public Collection<Contact> getContacts() {
        return contactInfoTable.getCollection();
    }
}
