package edu.ualberta.med.biobank.widgets.infotables.entry;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.PlatformUI;

import edu.ualberta.med.biobank.common.wrappers.ContactWrapper;
import edu.ualberta.med.biobank.common.wrappers.StudyWrapper;
import edu.ualberta.med.biobank.dialogs.select.SelectClinicContactDialog;
import edu.ualberta.med.biobank.gui.common.BgcPlugin;
import edu.ualberta.med.biobank.gui.common.widgets.IInfoTableAddItemListener;
import edu.ualberta.med.biobank.gui.common.widgets.IInfoTableDeleteItemListener;
import edu.ualberta.med.biobank.gui.common.widgets.InfoTableEvent;

/**
 * Allows the user to select a clinic and a contact from a clinic. Note that
 * some clinics may have more than one contact.
 */
public class ClinicAddInfoTable extends StudyContactEntryInfoTable {

    private StudyWrapper study;

    public ClinicAddInfoTable(Composite parent, StudyWrapper study) {
        super(parent, study.getContactCollection(true));
        this.study = study;
        addDeleteSupport();
    }

    @Override
    protected boolean isEditMode() {
        return true;
    }

    public void createClinicContact() {
        SelectClinicContactDialog dlg;
        try {
            dlg = new SelectClinicContactDialog(PlatformUI.getWorkbench()
                .getActiveWorkbenchWindow().getShell(),
                study.getContactCollection(true));
            if (dlg.open() == Dialog.OK) {
                notifyListeners();
                ContactWrapper contact = dlg.getSelection();
                if (contact != null) {
                    List<ContactWrapper> dummyList = new ArrayList<ContactWrapper>();
                    dummyList.add(contact);
                    study.addToContactCollection(dummyList);
                    setList(study.getContactCollection(true));
                }
            }
        } catch (Exception e) {
            BgcPlugin.openAsyncError(
                Messages.ClinicAddInfoTable_retrieve_error_title, e);
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
                    if (!BgcPlugin.openConfirm(
                        Messages.ClinicAddInfoTable_delete_confirm_title, NLS
                            .bind(
                                Messages.ClinicAddInfoTable_delete_confirm_msg,
                                contact.getName()))) {
                        return;
                    }

                    study.removeFromContactCollection(Arrays.asList(contact));
                    setList(study.getContactCollection(true));
                    notifyListeners();
                }
            }
        });
    }

    public void setContacts(List<ContactWrapper> contacts) {
        setList(contacts);
    }

    @Override
    public void reload() {
        setList(study.getContactCollection(true));
    }

}
