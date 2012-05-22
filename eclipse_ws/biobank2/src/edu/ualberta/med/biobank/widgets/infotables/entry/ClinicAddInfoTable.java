package edu.ualberta.med.biobank.widgets.infotables.entry;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.PlatformUI;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import edu.ualberta.med.biobank.dialogs.select.SelectClinicContactDialog;
import edu.ualberta.med.biobank.gui.common.BgcPlugin;
import edu.ualberta.med.biobank.gui.common.widgets.IInfoTableAddItemListener;
import edu.ualberta.med.biobank.gui.common.widgets.IInfoTableDeleteItemListener;
import edu.ualberta.med.biobank.gui.common.widgets.InfoTableEvent;
import edu.ualberta.med.biobank.model.Contact;

/**
 * Allows the user to select a clinic and a contact from a clinic. Note that
 * some clinics may have more than one contact.
 */
public class ClinicAddInfoTable extends StudyContactEntryInfoTable {
    public static final I18n i18n = I18nFactory
        .getI18n(ClinicAddInfoTable.class);

    private final List<Contact> origContacts;

    public ClinicAddInfoTable(Composite parent,
        List<Contact> contacts) {
        super(parent, contacts);
        this.origContacts = new ArrayList<Contact>(contacts);
        addDeleteSupport();
    }

    @Override
    protected boolean isEditMode() {
        return true;
    }

    @SuppressWarnings("nls")
    public void createClinicContact() {
        SelectClinicContactDialog dlg;
        try {
            dlg = new SelectClinicContactDialog(PlatformUI.getWorkbench()
                .getActiveWorkbenchWindow().getShell(),
                getList());
            if (dlg.open() == Dialog.OK) {
                notifyListeners();
                Contact contact = dlg.getSelection();
                if (contact != null) {
                    getList().add(contact);
                    setList(getList());
                }
            }
        } catch (Exception e) {
            BgcPlugin.openAsyncError(
                i18n.tr("Unable to retrieve available contacts"), e);
        }
    }

    private void addDeleteSupport() {
        addAddItemListener(new IInfoTableAddItemListener<Contact>() {
            @Override
            public void addItem(InfoTableEvent<Contact> event) {
                createClinicContact();
            }
        });

        addDeleteItemListener(new IInfoTableDeleteItemListener<Contact>() {
            @SuppressWarnings("nls")
            @Override
            public void deleteItem(InfoTableEvent<Contact> event) {
                Contact contact = getSelection();
                if (contact != null) {
                    if (!BgcPlugin
                        .openConfirm(
                            // dialog title.
                            i18n.tr("Delete Contact"),
                            // dialog message.
                            i18n.tr(
                                "Are you sure you want to delete contact \"{0}\"?",
                                contact.getName()))) {
                        return;
                    }
                    getList().remove(contact);
                    setList(getList());
                    notifyListeners();
                }
            }
        });
    }

    public void setContacts(List<Contact> contacts) {
        setList(contacts);
    }

    @Override
    public void reload() {
        setList(origContacts);
    }

}
