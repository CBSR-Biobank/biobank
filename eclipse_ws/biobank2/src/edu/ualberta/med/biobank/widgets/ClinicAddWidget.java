package edu.ualberta.med.biobank.widgets;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

import edu.ualberta.med.biobank.dialogs.SelectClinicContactDialog;
import edu.ualberta.med.biobank.model.Clinic;
import edu.ualberta.med.biobank.model.Contact;
import edu.ualberta.med.biobank.model.Study;
import edu.ualberta.med.biobank.widgets.infotables.BiobankCollectionModel;
import edu.ualberta.med.biobank.widgets.infotables.StudyContactEntryInfoTable;
import edu.ualberta.med.biobank.widgets.listener.BiobankEntryFormWidgetListener;
import edu.ualberta.med.biobank.widgets.listener.MultiSelectEvent;

/**
 * Allows the user to select a clinic and a contact from a clinic. Note that
 * some clinics may have more than one contact.
 */
public class ClinicAddWidget extends BiobankWidget {

    private Collection<Contact> selectedContacts;

    private Collection<Clinic> allClinics;

    private StudyContactEntryInfoTable contactInfoTable;

    private Button addClinicButton;

    public ClinicAddWidget(Composite parent, int style, Study study,
        FormToolkit toolkit) {
        super(parent, style);
        Assert.isNotNull(toolkit, "toolkit is null");
        allClinics = study.getSite().getClinicCollection();

        selectedContacts = study.getContactCollection();
        if (selectedContacts == null) {
            selectedContacts = new HashSet<Contact>();
        }

        setLayout(new GridLayout(1, false));
        setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        contactInfoTable = new StudyContactEntryInfoTable(parent, study);
        contactInfoTable.adaptToToolkit(toolkit, true);
        addTableMenu();
        contactInfoTable
            .addSelectionChangedListener(new BiobankEntryFormWidgetListener() {
                @Override
                public void selectionChanged(MultiSelectEvent event) {
                    ClinicAddWidget.this.notifyListeners();
                }
            });

        addClinicButton = toolkit.createButton(parent, "Add Clinic", SWT.PUSH);
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
            allClinics);
        if (dlg.open() == Dialog.OK) {
            selectedContacts.add(dlg.getSelection());
            contactInfoTable.setCollection(selectedContacts);
        }
    }

    private void addTableMenu() {
        Menu menu = new Menu(PlatformUI.getWorkbench()
            .getActiveWorkbenchWindow().getShell(), SWT.NONE);
        contactInfoTable.getTableViewer().getTable().setMenu(menu);

        MenuItem item = new MenuItem(menu, SWT.PUSH);
        item.setText("Edit");
        item.addSelectionListener(new SelectionListener() {
            public void widgetSelected(SelectionEvent event) {
                IStructuredSelection stSelection = (IStructuredSelection) contactInfoTable
                    .getTableViewer().getSelection();

                BiobankCollectionModel item = (BiobankCollectionModel) stSelection
                    .getFirstElement();
                Contact contact = (Contact) item.o;

                Set<Clinic> allowedClinics = getNonDuplicateClinics();
                allowedClinics.add(contact.getClinic());
                // addOrEditContact(false, contact, allowedClinics);
            }

            public void widgetDefaultSelected(SelectionEvent e) {
            }
        });

        item = new MenuItem(menu, SWT.PUSH);
        item.setText("Delete");
        item.addSelectionListener(new SelectionListener() {
            public void widgetSelected(SelectionEvent event) {
                IStructuredSelection stSelection = (IStructuredSelection) contactInfoTable
                    .getTableViewer().getSelection();

                BiobankCollectionModel item = (BiobankCollectionModel) stSelection
                    .getFirstElement();
                Contact contact = (Contact) item.o;

                boolean confirm = MessageDialog.openConfirm(PlatformUI
                    .getWorkbench().getActiveWorkbenchWindow().getShell(),
                    "Delete Clinic",
                    "Are you sure you want to delete clinic \""
                        + contact.getClinic().getName() + "\"");

                if (confirm) {
                    Collection<Contact> contactToDelete = new HashSet<Contact>();
                    for (Contact c : selectedContacts) {
                        if (c.getClinic().getName().equals(
                            contact.getClinic().getName()))
                            contactToDelete.add(c);
                    }

                    for (Contact c : contactToDelete) {
                        selectedContacts.remove(c);
                    }

                    contactInfoTable.setCollection(selectedContacts);
                }
            }

            public void widgetDefaultSelected(SelectionEvent e) {
            }
        });
    }

    // need clinics that have not yet been selected in contactInfoTable
    private Set<Clinic> getNonDuplicateClinics() {
        Set<Clinic> clinics = new HashSet<Clinic>(allClinics);
        Set<Clinic> dupClinics = new HashSet<Clinic>();

        // get the IDs of the selected clinics
        List<Integer> clinicIds = new ArrayList<Integer>();
        for (Contact contact : contactInfoTable.getCollection()) {
            clinicIds.add(contact.getClinic().getId());
        }

        for (Clinic clinic : allClinics) {
            if (clinicIds.contains(clinic.getId())) {
                dupClinics.add(clinic);
            }
        }
        clinics.removeAll(dupClinics);
        return clinics;
    }

    public Collection<Contact> getContacts() {
        return selectedContacts;
    }
}
