package edu.ualberta.med.biobank.widgets;

import java.util.ArrayList;
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
import edu.ualberta.med.biobank.common.wrappers.ClinicWrapper;
import edu.ualberta.med.biobank.common.wrappers.ContactWrapper;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;
import edu.ualberta.med.biobank.common.wrappers.StudyWrapper;
import edu.ualberta.med.biobank.dialogs.SelectClinicContactDialog;
import edu.ualberta.med.biobank.widgets.infotables.IInfoTableDeleteItemListener;
import edu.ualberta.med.biobank.widgets.infotables.InfoTableEvent;
import edu.ualberta.med.biobank.widgets.infotables.StudyContactEntryInfoTable;

/**
 * Allows the user to select a clinic and a contact from a clinic. Note that
 * some clinics may have more than one contact.
 */
public class ClinicAddWidget extends BiobankWidget {

    private List<ContactWrapper> selectedContacts;

    private List<ClinicWrapper> allClinics;

    private StudyContactEntryInfoTable contactInfoTable;

    private Button addClinicButton;

    public ClinicAddWidget(Composite parent, int style,
        StudyWrapper studyWrapper, FormToolkit toolkit) {
        super(parent, style);
        Assert.isNotNull(toolkit, "toolkit is null");
        SiteWrapper site = studyWrapper.getSite();
        Assert.isNotNull(site, "site is null");
        allClinics = site.getClinicCollection(true);

        selectedContacts = studyWrapper.getContactCollection();
        if (selectedContacts == null) {
            selectedContacts = new ArrayList<ContactWrapper>();
        }

        setLayout(new GridLayout(1, false));
        setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        contactInfoTable = new StudyContactEntryInfoTable(parent, studyWrapper
            .getContactCollection());
        contactInfoTable.adaptToToolkit(toolkit, true);
        addDeleteSupprt();

        addClinicButton = toolkit.createButton(parent, "Add Contact", SWT.PUSH);
        addClinicButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                createClinicContact();
            }
        });
    }

    private void createClinicContact() {
        SelectClinicContactDialog dlg = new SelectClinicContactDialog(
            PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
            allClinics, selectedContacts);
        if (dlg.open() == Dialog.OK) {
            notifyListeners();
            ContactWrapper contact = dlg.getSelection();
            if (contact != null) {
                if (!selectedContacts.contains(contact)) {
                    selectedContacts.add(contact);
                }
                contactInfoTable.setCollection(selectedContacts);
            }
        }
    }

    private void addDeleteSupprt() {
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
                    selectedContacts.remove(contact);
                    contactInfoTable.setCollection(selectedContacts);
                    notifyListeners();
                }
            });
    }

    public List<ContactWrapper> getContacts() {
        return selectedContacts;
    }

    public void setContacts(List<ContactWrapper> contacts) {
        this.selectedContacts = contacts;
        contactInfoTable.setCollection(selectedContacts);
    }
}
