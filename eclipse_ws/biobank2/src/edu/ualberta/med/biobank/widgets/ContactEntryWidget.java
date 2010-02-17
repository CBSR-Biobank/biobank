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
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.widgets.FormToolkit;

import edu.ualberta.med.biobank.BioBankPlugin;
import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.wrappers.ClinicWrapper;
import edu.ualberta.med.biobank.common.wrappers.ContactWrapper;
import edu.ualberta.med.biobank.dialogs.ContactAddDialog;
import edu.ualberta.med.biobank.widgets.infotables.ContactInfoTable;
import edu.ualberta.med.biobank.widgets.infotables.IInfoTableDeleteItemListener;
import edu.ualberta.med.biobank.widgets.infotables.IInfoTableEditItemListener;
import edu.ualberta.med.biobank.widgets.infotables.InfoTableEvent;
import edu.ualberta.med.biobank.widgets.listeners.BiobankEntryFormWidgetListener;
import edu.ualberta.med.biobank.widgets.listeners.MultiSelectEvent;

public class ContactEntryWidget extends BiobankWidget {

    private Collection<ContactWrapper> selectedContacts;

    private List<ContactWrapper> addedOrModifiedContacts;

    private List<ContactWrapper> deletedContacts;

    private ContactInfoTable contactInfoTable;

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

        contactInfoTable = new ContactInfoTable(parent, selectedContacts);
        contactInfoTable.adaptToToolkit(toolkit, true);
        contactInfoTable
            .addSelectionChangedListener(new BiobankEntryFormWidgetListener() {
                @Override
                public void selectionChanged(MultiSelectEvent event) {
                    ContactEntryWidget.this.notifyListeners();
                }
            });

        contactInfoTable.addEditItemListener(new IInfoTableEditItemListener() {
            @Override
            public void editItem(InfoTableEvent event) {
                addOrEditContact(false, contactInfoTable.getSelection());
            }
        });

        contactInfoTable
            .addDeleteItemListener(new IInfoTableDeleteItemListener() {
                @Override
                public void deleteItem(InfoTableEvent event) {
                    ContactWrapper contact = contactInfoTable.getSelection();
                    if (!contact.deleteAllowed()) {
                        BioBankPlugin
                            .openError(
                                "Contact Delete Error",
                                "Cannot delete contact \""
                                    + contact.getName()
                                    + "\" since it is associated with one or more studies");
                        return;
                    }

                    if (!BioBankPlugin.openConfirm("Delete Contact",
                        "Are you sure you want to delete contact \""
                            + contact.getName() + "\"")) {
                        return;
                    }

                    deletedContacts.add(contact);
                    selectedContacts.remove(contact);
                    contactInfoTable.setCollection(selectedContacts);
                    notifyListeners();
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

    public List<ContactWrapper> getAddedOrModifedContacts() {
        return addedOrModifiedContacts;
    }

    public List<ContactWrapper> getDeletedContacts() {
        return deletedContacts;
    }
}
