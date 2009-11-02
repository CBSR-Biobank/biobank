package edu.ualberta.med.biobank.widgets;

import java.util.Collection;
import java.util.HashSet;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.dialogs.Dialog;
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

import edu.ualberta.med.biobank.BioBankPlugin;
import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.wrappers.ClinicWrapper;
import edu.ualberta.med.biobank.common.wrappers.ContactWrapper;
import edu.ualberta.med.biobank.dialogs.ContactAddDialog;
import edu.ualberta.med.biobank.widgets.infotables.ContactInfoTable;
import edu.ualberta.med.biobank.widgets.listener.BiobankEntryFormWidgetListener;
import edu.ualberta.med.biobank.widgets.listener.MultiSelectEvent;

public class ContactEntryWidget extends BiobankWidget {

    private Collection<ContactWrapper> selectedContacts;

    private ContactInfoTable contactInfoTable;

    private Button addClinicButton;

    private ClinicWrapper clinic;

    public ContactEntryWidget(Composite parent, int style,
        ClinicWrapper clinic, FormToolkit toolkit) {
        super(parent, style);
        this.clinic = clinic;
        Assert.isNotNull(toolkit, "toolkit is null");

        selectedContacts = clinic.getContactCollection();
        if (selectedContacts == null) {
            selectedContacts = new HashSet<ContactWrapper>();
        }

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
                addOrEditContact(true, new ContactWrapper(SessionManager
                    .getAppService()));
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
                ContactWrapper contactWrapper = contactInfoTable.getSelection();
                if (contactWrapper == null) {
                    BioBankPlugin.openError("Edit Clinic", "Invalid selection");
                    return;
                }
                addOrEditContact(false, contactWrapper);
            }

            public void widgetDefaultSelected(SelectionEvent e) {
            }
        });

        item = new MenuItem(menu, SWT.PUSH);
        item.setText("Delete");
        item.addSelectionListener(new SelectionListener() {
            public void widgetSelected(SelectionEvent event) {
                ContactWrapper contactWrapper = contactInfoTable.getSelection();
                if (contactWrapper == null) {
                    BioBankPlugin.openError("Delete Clinic",
                        "Invalid selection");
                    return;
                }

                boolean confirm = BioBankPlugin.openConfirm("Delete Clinic",
                    "Are you sure you want to delete clinic \""
                        + contactWrapper.getClinicWrapper().getName() + "\"");

                if (confirm) {
                    Collection<ContactWrapper> contactToDelete = new HashSet<ContactWrapper>();
                    for (ContactWrapper cw : selectedContacts) {
                        if (cw.getName().equals(contactWrapper.getName()))
                            contactToDelete.add(cw);
                    }

                    for (ContactWrapper c : contactToDelete) {
                        selectedContacts.remove(c);
                    }

                    contactInfoTable.setCollection(selectedContacts);
                    notifyListeners();
                }
            }

            public void widgetDefaultSelected(SelectionEvent e) {
            }
        });
    }

    private void addOrEditContact(boolean add, ContactWrapper contactWrapper) {
        ContactAddDialog dlg = new ContactAddDialog(PlatformUI.getWorkbench()
            .getActiveWorkbenchWindow().getShell(), contactWrapper);
        if (dlg.open() == Dialog.OK) {
            if (add) {
                // only add to the collection when adding and not editing
                ContactWrapper contact = dlg.getContactWrapper();
                contact.setClinicWrapper(clinic);
                selectedContacts.add(contact);
            }
            contactInfoTable.setCollection(selectedContacts);
            notifyListeners();
        }
    }

    public Collection<ContactWrapper> getContacts() {
        return contactInfoTable.getCollection();
    }
}
