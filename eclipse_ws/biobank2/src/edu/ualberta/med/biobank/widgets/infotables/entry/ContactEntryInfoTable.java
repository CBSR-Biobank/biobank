package edu.ualberta.med.biobank.widgets.infotables.entry;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.PlatformUI;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.wrappers.ClinicWrapper;
import edu.ualberta.med.biobank.common.wrappers.ContactWrapper;
import edu.ualberta.med.biobank.dialogs.select.ContactAddDialog;
import edu.ualberta.med.biobank.gui.common.BgcPlugin;
import edu.ualberta.med.biobank.gui.common.widgets.IInfoTableAddItemListener;
import edu.ualberta.med.biobank.gui.common.widgets.IInfoTableDeleteItemListener;
import edu.ualberta.med.biobank.gui.common.widgets.IInfoTableEditItemListener;
import edu.ualberta.med.biobank.gui.common.widgets.InfoTableEvent;
import edu.ualberta.med.biobank.widgets.infotables.BiobankTableSorter;
import edu.ualberta.med.biobank.widgets.infotables.ContactInfoTable;

public class ContactEntryInfoTable extends ContactInfoTable {

    private List<ContactWrapper> selectedContacts;

    private List<ContactWrapper> addedOrModifiedContacts;

    private List<ContactWrapper> deletedContacts;

    private ClinicWrapper clinic;

    public ContactEntryInfoTable(Composite parent, ClinicWrapper clinic) {
        super(parent, clinic.getContactCollection(true));
        this.clinic = clinic;
        selectedContacts = clinic.getContactCollection(false);
        if (selectedContacts == null) {
            selectedContacts = new ArrayList<ContactWrapper>();
        }
        addedOrModifiedContacts = new ArrayList<ContactWrapper>();
        deletedContacts = new ArrayList<ContactWrapper>();

        if (SessionManager.canCreate(ContactWrapper.class)) {
            addAddItemListener(new IInfoTableAddItemListener<ContactWrapper>() {
                @Override
                public void addItem(InfoTableEvent<ContactWrapper> event) {
                    addContact();
                }
            });
        }
        if (SessionManager.canUpdate(ContactWrapper.class)) {
            addEditItemListener(new IInfoTableEditItemListener<ContactWrapper>() {
                @Override
                public void editItem(InfoTableEvent<ContactWrapper> event) {
                    ContactWrapper contact = getSelection();
                    if (contact != null)
                        addOrEditContact(false, contact);
                }
            });
        }
        if (SessionManager.canDelete(ContactWrapper.class)) {
            addDeleteItemListener(new IInfoTableDeleteItemListener<ContactWrapper>() {
                @Override
                public void deleteItem(InfoTableEvent<ContactWrapper> event) {
                    ContactWrapper contact = getSelection();
                    if (contact != null) {
                        if (!contact.deleteAllowed()) {
                            BgcPlugin
                                .openError(
                                    Messages.ContactEntryInfoTable_delete_error_title,
                                    NLS.bind(
                                        Messages.ContactEntryInfoTable_delete_error_msg,
                                        contact.getName()));
                            return;
                        }

                        if (!BgcPlugin
                            .openConfirm(
                                Messages.ContactEntryInfoTable_delete_confirm_title,
                                NLS.bind(
                                    Messages.ContactEntryInfoTable_delete_confirm_msg,
                                    contact.getName()))) {
                            return;
                        }

                        deletedContacts.add(contact);
                        selectedContacts.remove(contact);
                        setList(selectedContacts);
                        notifyListeners();
                    }
                }
            });
        }
    }

    @Override
    protected boolean isEditMode() {
        return true;
    }

    private void addOrEditContact(boolean add, ContactWrapper contactWrapper) {
        ContactAddDialog dlg = new ContactAddDialog(PlatformUI.getWorkbench()
            .getActiveWorkbenchWindow().getShell(), contactWrapper);
        int res = dlg.open();
        if (res == Dialog.OK) {
            ContactWrapper contact = dlg.getContactWrapper();
            if (add) {
                // only add to the collection when adding and not editing
                contact.setClinic(clinic);
                selectedContacts.add(contact);
                addedOrModifiedContacts.add(contact);
            }
            reloadCollection(selectedContacts, contact);
            notifyListeners();
        } else if (!add && res == Dialog.CANCEL) {
            try {
                contactWrapper.reload();
            } catch (Exception e) {
                BgcPlugin.openAsyncError(
                    Messages.ContactEntryInfoTable_cancel_error_title, e);
            }
            reloadCollection(selectedContacts, null);
        }
    }

    public void addContact() {
        addOrEditContact(true,
            new ContactWrapper(SessionManager.getAppService()));
    }

    public List<ContactWrapper> getAddedOrModifedContacts() {
        return addedOrModifiedContacts;
    }

    public List<ContactWrapper> getDeletedContacts() {
        return deletedContacts;
    }

    @Override
    public void reload() {
        selectedContacts = clinic.getContactCollection(false);
        if (selectedContacts == null) {
            selectedContacts = new ArrayList<ContactWrapper>();
        }
        addedOrModifiedContacts = new ArrayList<ContactWrapper>();
        deletedContacts = new ArrayList<ContactWrapper>();
        reloadCollection(selectedContacts, null);
    }

    @SuppressWarnings("serial")
    @Override
    protected BiobankTableSorter getComparator() {
        return new BiobankTableSorter() {
            @Override
            public int compare(Object e1, Object e2) {
                try {
                    TableRowData i1 = getCollectionModelObject(e1);
                    TableRowData i2 = getCollectionModelObject(e2);
                    return super.compare(i1.name, i2.name);
                } catch (Exception e) {
                    return 0;
                }
            }
        };
    }
}
