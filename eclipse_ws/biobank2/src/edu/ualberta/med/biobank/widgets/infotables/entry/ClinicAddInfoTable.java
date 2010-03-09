package edu.ualberta.med.biobank.widgets.infotables.entry;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.PlatformUI;

import edu.ualberta.med.biobank.BioBankPlugin;
import edu.ualberta.med.biobank.common.wrappers.ContactWrapper;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;
import edu.ualberta.med.biobank.common.wrappers.StudyWrapper;
import edu.ualberta.med.biobank.dialogs.SelectClinicContactDialog;
import edu.ualberta.med.biobank.widgets.infotables.IInfoTableAddItemListener;
import edu.ualberta.med.biobank.widgets.infotables.IInfoTableDeleteItemListener;
import edu.ualberta.med.biobank.widgets.infotables.InfoTableEvent;
import edu.ualberta.med.biobank.widgets.infotables.StudyContactEntryInfoTable;

/**
 * Allows the user to select a clinic and a contact from a clinic. Note that
 * some clinics may have more than one contact.
 */
public class ClinicAddInfoTable extends StudyContactEntryInfoTable {

    private List<ContactWrapper> selectedContacts;

    private List<ContactWrapper> addedContacts;

    private List<ContactWrapper> removedContacts;

    private StudyWrapper study;

    public ClinicAddInfoTable(Composite parent, StudyWrapper study) {
        super(parent, study.getContactCollection(true));
        this.study = study;
        SiteWrapper site = study.getSite();
        Assert.isNotNull(site, "site is null");
        loadContacts(study);
        addDeleteSupport();
    }

    public void createClinicContact() {
        SelectClinicContactDialog dlg = new SelectClinicContactDialog(
            PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
            study);
        if (dlg.open() == Dialog.OK) {
            notifyListeners();
            ContactWrapper contact = dlg.getSelection();
            if (contact != null) {
                if (!selectedContacts.contains(contact)) {
                    selectedContacts.add(contact);
                    addedContacts.add(contact);
                    removedContacts.remove(contact);
                }
                setCollection(selectedContacts);
            }
        }
    }

    private void addDeleteSupport() {
        addAddItemListener(new IInfoTableAddItemListener() {
            @Override
            public void addItem(InfoTableEvent event) {
                createClinicContact();
            }
        });

        addDeleteItemListener(new IInfoTableDeleteItemListener() {
            @Override
            public void deleteItem(InfoTableEvent event) {
                ContactWrapper contact = getSelection();
                if (!BioBankPlugin.openConfirm("Delete Contact",
                    "Are you sure you want to delete contact \""
                        + contact.getName() + "\"")) {
                    return;
                }
                selectedContacts.remove(contact);
                addedContacts.remove(contact);
                removedContacts.add(contact);
                setCollection(selectedContacts);
                notifyListeners();
            }
        });
    }

    public List<ContactWrapper> getAllSelectedContacts() {
        return selectedContacts;
    }

    public List<ContactWrapper> getAddedContacts() {
        return addedContacts;
    }

    public List<ContactWrapper> getRemovedContacts() {
        return removedContacts;
    }

    public void setContacts(List<ContactWrapper> contacts) {
        this.selectedContacts = contacts;
        setCollection(selectedContacts);
    }

    private void loadContacts(StudyWrapper studyWrapper) {
        selectedContacts = studyWrapper.getContactCollection();
        if (selectedContacts == null) {
            selectedContacts = new ArrayList<ContactWrapper>();
        }
        addedContacts = new ArrayList<ContactWrapper>();
        removedContacts = new ArrayList<ContactWrapper>();
    }

    public void reload() {
        loadContacts(study);
        setCollection(study.getContactCollection());
    }

}
