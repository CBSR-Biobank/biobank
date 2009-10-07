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

import edu.ualberta.med.biobank.BioBankPlugin;
import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.wrappers.ClinicWrapper;
import edu.ualberta.med.biobank.common.wrappers.ContactWrapper;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;
import edu.ualberta.med.biobank.common.wrappers.StudyWrapper;
import edu.ualberta.med.biobank.dialogs.SelectClinicContactDialog;
import edu.ualberta.med.biobank.model.StudyContactInfo;
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
        SiteWrapper siteWrapper = new SiteWrapper(SessionManager
            .getAppService(), studyWrapper.getSite());
        allClinics = siteWrapper.getClinicCollection(true);

        selectedContacts = studyWrapper.getContactCollection();

        setLayout(new GridLayout(1, false));
        setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        contactInfoTable = new StudyContactEntryInfoTable(parent, studyWrapper);
        contactInfoTable.adaptToToolkit(toolkit, true);
        addTableMenu();

        addClinicButton = toolkit.createButton(parent, "Add Clinic", SWT.PUSH);
        addClinicButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                if (allClinics != null && selectedContacts != null
                    && allClinics.size() == selectedContacts.size()) {
                    BioBankPlugin.openInformation("All Clinics Selected",
                        "No more clinics available.");
                } else {
                    createClinicContact();
                }
            }
        });
    }

    private void createClinicContact() {
        SelectClinicContactDialog dlg = new SelectClinicContactDialog(
            PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
            getNonDuplicateClinics());
        if (dlg.open() == Dialog.OK) {
            notifyListeners();
            selectedContacts.add(dlg.getSelection());
            contactInfoTable.setCollection(selectedContacts);
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
                ContactWrapper contact = ((StudyContactInfo) item.o).contact;

                boolean confirm = MessageDialog.openConfirm(PlatformUI
                    .getWorkbench().getActiveWorkbenchWindow().getShell(),
                    "Delete Clinic",
                    "Are you sure you want to delete clinic \""
                        + contact.getClinicWrapper().getName() + "\"");

                if (confirm) {
                    Collection<ContactWrapper> contactToDelete = new HashSet<ContactWrapper>();
                    for (ContactWrapper c : selectedContacts) {
                        if (c.getClinicWrapper().getName().equals(
                            contact.getClinicWrapper().getName()))
                            contactToDelete.add(c);
                    }

                    for (ContactWrapper c : contactToDelete) {
                        selectedContacts.remove(c);
                    }

                    contactInfoTable.setCollection(selectedContacts);
                    notifyListeners();
                }
            }

            public void widgetDefaultSelected(SelectionEvent e) {
            }
        });
    }

    // need clinics that have not yet been selected in contactInfoTable
    private Set<ClinicWrapper> getNonDuplicateClinics() {
        Set<ClinicWrapper> clinics = new HashSet<ClinicWrapper>(allClinics);
        Set<ClinicWrapper> dupClinics = new HashSet<ClinicWrapper>();

        // get the IDs of the selected clinics
        List<Integer> clinicIds = new ArrayList<Integer>();
        for (ContactWrapper contact : contactInfoTable.getCollection()) {
            clinicIds.add(contact.getClinicWrapper().getId());
        }

        for (ClinicWrapper clinic : allClinics) {
            if (clinicIds.contains(clinic.getId())) {
                dupClinics.add(clinic);
            }
        }
        clinics.removeAll(dupClinics);
        return clinics;
    }

    public List<ContactWrapper> getContacts() {
        return selectedContacts;
    }
}
