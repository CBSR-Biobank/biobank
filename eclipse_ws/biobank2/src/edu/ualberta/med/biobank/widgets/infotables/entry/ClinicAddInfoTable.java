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

    private StudyWrapper study;

    public ClinicAddInfoTable(Composite parent, StudyWrapper study) {
        super(parent, study.getContactCollection(true));
        this.study = study;
        SiteWrapper site = study.getSite();
        Assert.isNotNull(site, "site is null");
        addDeleteSupport();
    }

    @Override
    protected boolean isEditMode() {
        return true;
    }

    public void createClinicContact() {
        SelectClinicContactDialog dlg;
        try {
            List<ContactWrapper> availableContacts = study
                .getContactsNotAssoc();
            availableContacts.removeAll(study.getContactCollection(true));
            dlg = new SelectClinicContactDialog(PlatformUI.getWorkbench()
                .getActiveWorkbenchWindow().getShell(), availableContacts);
            if (dlg.open() == Dialog.OK) {
                notifyListeners();
                ContactWrapper contact = dlg.getSelection();
                if (contact != null) {
                    List<ContactWrapper> dummyList = new ArrayList<ContactWrapper>();
                    dummyList.add(contact);
                    study.addContacts(dummyList);
                    setCollection(study.getContactCollection(true));
                }
            }
        } catch (Exception e) {
            BioBankPlugin.openAsyncError(
                "Unable to retrieve available contacts", e);
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
                if (contact != null) {
                    if (!BioBankPlugin.openConfirm("Delete Contact",
                        "Are you sure you want to delete contact \""
                            + contact.getName() + "\"")) {
                        return;
                    }
                    List<ContactWrapper> dummyList = new ArrayList<ContactWrapper>();
                    dummyList.add(contact);
                    study.removeContacts(dummyList);
                    setCollection(study.getContactCollection(true));
                    notifyListeners();
                }
            }
        });
    }

    public void setContacts(List<ContactWrapper> contacts) {
        setCollection(contacts);
    }

    public void reload() {
        setCollection(study.getContactCollection(true));
    }

}
