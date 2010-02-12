package edu.ualberta.med.biobank.widgets;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.widgets.FormToolkit;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.wrappers.ClinicWrapper;
import edu.ualberta.med.biobank.common.wrappers.ContactWrapper;
import edu.ualberta.med.biobank.dialogs.ContactAddDialog;
import edu.ualberta.med.biobank.widgets.infotables.ContactEditInfoTable;
import edu.ualberta.med.biobank.widgets.infotables.IEditInfoTable;
import edu.ualberta.med.biobank.widgets.listeners.BiobankEntryFormWidgetListener;
import edu.ualberta.med.biobank.widgets.listeners.MultiSelectEvent;

public class ContactEntryWidget extends BiobankWidget implements
    IEditInfoTable<ContactWrapper> {

    private Collection<ContactWrapper> selectedContacts;

    private List<ContactWrapper> addedOrModifiedContacts;

    private List<ContactWrapper> deletedContacts;

    private ContactEditInfoTable contactInfoTable;

    private Button addClinicButton;

    private ClinicWrapper clinic;

    public ContactEntryWidget(Composite parent, int style,
        ClinicWrapper clinic, FormToolkit toolkit) {
        super(parent, style);
        this.clinic = clinic;
        Assert.isNotNull(toolkit, "toolkit is null");

        setContacts(clinic);

        setLayout(new GridLayout(1, false));
        setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        contactInfoTable = new ContactEditInfoTable(parent, selectedContacts,
            this);
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

    private void setContacts(ClinicWrapper clinic) {
        selectedContacts = clinic.getContactCollection();
        if (selectedContacts == null) {
            selectedContacts = new HashSet<ContactWrapper>();
        }
        addedOrModifiedContacts = new ArrayList<ContactWrapper>();
        deletedContacts = new ArrayList<ContactWrapper>();
    }

    private void addTableMenu() {
        Menu menu = new Menu(PlatformUI.getWorkbench()
            .getActiveWorkbenchWindow().getShell(), SWT.NONE);
        contactInfoTable.getTableViewer().getTable().setMenu(menu);
    }

    private void addOrEditContact(boolean add, ContactWrapper contactWrapper) {
        ContactAddDialog dlg = new ContactAddDialog(PlatformUI.getWorkbench()
            .getActiveWorkbenchWindow().getShell(), contactWrapper);
        if (dlg.open() == Dialog.OK) {
            if (add) {
                // only add to the collection when adding and not editing
                ContactWrapper contact = dlg.getContactWrapper();
                contact.setClinic(clinic);
                selectedContacts.add(contact);
                addedOrModifiedContacts.add(contact);
            }
            contactInfoTable.setCollection(selectedContacts);
            notifyListeners();
        }
    }

    // public Collection<ContactWrapper> getContacts() {
    // return contactInfoTable.getCollection();
    // }

    public void editItem(ContactWrapper contact) {
        addOrEditContact(false, contact);
    }

    public void deleteItem(ContactWrapper contact) {
        deletedContacts.add(contact);
        selectedContacts.remove(contact);
        contactInfoTable.setCollection(selectedContacts);
        notifyListeners();
    }

    public List<ContactWrapper> getAddedOrModifedContacts() {
        return addedOrModifiedContacts;
    }

    public List<ContactWrapper> getDeletedContacts() {
        return deletedContacts;
    }
}
