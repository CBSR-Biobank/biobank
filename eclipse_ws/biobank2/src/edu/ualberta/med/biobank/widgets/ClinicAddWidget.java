package edu.ualberta.med.biobank.widgets;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
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

import edu.ualberta.med.biobank.common.wrappers.ClinicWrapper;
import edu.ualberta.med.biobank.common.wrappers.ContactWrapper;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;
import edu.ualberta.med.biobank.common.wrappers.StudyWrapper;
import edu.ualberta.med.biobank.dialogs.SelectClinicContactDialog;
import edu.ualberta.med.biobank.widgets.infotables.BiobankCollectionModel;
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
        addTableMenu();

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

    private void addTableMenu() {
        Menu menu = new Menu(PlatformUI.getWorkbench()
            .getActiveWorkbenchWindow().getShell(), SWT.NONE);
        contactInfoTable.getTableViewer().getTable().setMenu(menu);

        MenuItem item = new MenuItem(menu, SWT.PUSH);
        item.setText("Delete");
        item.addSelectionListener(new SelectionListener() {
            public void widgetSelected(SelectionEvent event) {
                IStructuredSelection stSelection = (IStructuredSelection) contactInfoTable
                    .getTableViewer().getSelection();

                BiobankCollectionModel item = (BiobankCollectionModel) stSelection
                    .getFirstElement();
                ContactWrapper contact = (ContactWrapper) item.o;

                boolean confirm = MessageDialog.openConfirm(PlatformUI
                    .getWorkbench().getActiveWorkbenchWindow().getShell(),
                    "Delete Contact",
                    "Are you sure you want to delete contact \""
                        + contact.getName() + "\" from clinic \""
                        + contact.getClinic().getName() + "\"");

                if (confirm) {
                    selectedContacts.remove(contact);
                    contactInfoTable.setCollection(selectedContacts);
                    notifyListeners();
                }
            }

            public void widgetDefaultSelected(SelectionEvent e) {
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
