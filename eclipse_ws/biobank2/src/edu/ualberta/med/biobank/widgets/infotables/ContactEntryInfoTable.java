package edu.ualberta.med.biobank.widgets.infotables;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.PlatformUI;

import edu.ualberta.med.biobank.BioBankPlugin;
import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.wrappers.ClinicWrapper;
import edu.ualberta.med.biobank.common.wrappers.ContactWrapper;
import edu.ualberta.med.biobank.dialogs.ContactAddDialog;

public class ContactEntryInfoTable extends ContactInfoTable {

    private List<ContactWrapper> selectedContacts;

    private List<ContactWrapper> addedOrModifiedContacts;

    private List<ContactWrapper> deletedContacts;

    private ClinicWrapper clinic;

    public ContactEntryInfoTable(Composite parent, ClinicWrapper clinic) {
        super(parent, clinic.getContactCollection(true));
        this.clinic = clinic;
        setContacts(clinic);

        addAddItemListener(new IInfoTableAddItemListener() {
            @Override
            public void addItem(InfoTableEvent event) {
                addContact();
            }
        });

        addEditItemListener(new IInfoTableEditItemListener() {
            @Override
            public void editItem(InfoTableEvent event) {
                addOrEditContact(false, getSelection());
            }
        });

        addDeleteItemListener(new IInfoTableDeleteItemListener() {
            @Override
            public void deleteItem(InfoTableEvent event) {
                ContactWrapper contact = getSelection();
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
                setCollection(selectedContacts);
                notifyListeners();
            }
        });
    }

    private void setContacts(ClinicWrapper clinic) {
        selectedContacts = clinic.getContactCollection();
        if (selectedContacts == null) {
            selectedContacts = new ArrayList<ContactWrapper>();
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
            setCollection(selectedContacts);
            notifyListeners();
        }
    }

    public void addContact() {
        addOrEditContact(true, new ContactWrapper(SessionManager
            .getAppService()));
    }

    public List<ContactWrapper> getAddedOrModifedContacts() {
        return addedOrModifiedContacts;
    }

    public List<ContactWrapper> getDeletedContacts() {
        return deletedContacts;
    }
}
