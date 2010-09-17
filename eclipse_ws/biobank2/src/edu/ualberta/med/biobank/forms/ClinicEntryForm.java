package edu.ualberta.med.biobank.forms;

import org.eclipse.core.databinding.beans.BeansObservables;
import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.Section;

import edu.ualberta.med.biobank.common.wrappers.ActivityStatusWrapper;
import edu.ualberta.med.biobank.common.wrappers.ClinicWrapper;
import edu.ualberta.med.biobank.common.wrappers.ContactWrapper;
import edu.ualberta.med.biobank.treeview.ClinicAdapter;
import edu.ualberta.med.biobank.validators.NonEmptyStringValidator;
import edu.ualberta.med.biobank.widgets.BiobankText;
import edu.ualberta.med.biobank.widgets.infotables.entry.ContactEntryInfoTable;
import edu.ualberta.med.biobank.widgets.listeners.BiobankEntryFormWidgetListener;
import edu.ualberta.med.biobank.widgets.listeners.MultiSelectEvent;
import gov.nih.nci.system.applicationservice.ApplicationException;

public class ClinicEntryForm extends AddressEntryFormCommon {
    public static final String ID = "edu.ualberta.med.biobank.forms.ClinicEntryForm";

    private static final String MSG_NEW_CLINIC_OK = "New clinic information.";

    private static final String MSG_CLINIC_OK = "Clinic information.";

    private static final String MSG_NO_CLINIC_NAME = "Clinic must have a name";

    private ClinicAdapter clinicAdapter;

    private ClinicWrapper clinic;

    private ContactEntryInfoTable contactEntryWidget;

    protected Combo session;

    private BiobankEntryFormWidgetListener listener = new BiobankEntryFormWidgetListener() {
        @Override
        public void selectionChanged(MultiSelectEvent event) {
            setDirty(true);
        }
    };

    private ComboViewer activityStatusComboViewer;

    @Override
    protected void init() throws Exception {
        Assert.isTrue((adapter instanceof ClinicAdapter),
            "Invalid editor input: object of type "
                + adapter.getClass().getName());
        clinicAdapter = (ClinicAdapter) adapter;
        clinic = clinicAdapter.getWrapper();
        clinic.reload();

        String tabName;
        if (clinic.getId() == null)
            tabName = "New Clinic";
        else
            tabName = "Clinic " + clinic.getNameShort();
        setPartName(tabName);
    }

    @Override
    protected String getOkMessage() {
        if (clinic.getId() == null) {
            return MSG_NEW_CLINIC_OK;
        }
        return MSG_CLINIC_OK;
    }

    @Override
    protected void createFormContent() throws ApplicationException {
        form.setText("Clinic Information");
        page.setLayout(new GridLayout(1, false));
        toolkit
            .createLabel(
                page,
                "Clinics can be associated with studies after submitting this initial information.",
                SWT.LEFT);
        createClinicInfoSection();
        createAddressArea(clinic);
        createContactSection();
        createButtonsSection();

    }

    private void createClinicInfoSection() throws ApplicationException {
        Composite client = toolkit.createComposite(page);
        GridLayout layout = new GridLayout(2, false);
        layout.horizontalSpacing = 10;
        client.setLayout(layout);
        client.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        toolkit.paintBordersFor(client);

        setFirstControl(createBoundWidgetWithLabel(client, BiobankText.class,
            SWT.NONE, "Name", null,
            BeansObservables.observeValue(clinic, "name"),
            new NonEmptyStringValidator(MSG_NO_CLINIC_NAME)));

        createBoundWidgetWithLabel(client, BiobankText.class, SWT.NONE,
            "Short Name", null,
            BeansObservables.observeValue(clinic, "nameShort"),
            new NonEmptyStringValidator(MSG_NO_CLINIC_NAME));

        if (clinic.getSendsShipments() == null) {
            clinic.setSendsShipments(false);
        }
        createBoundWidgetWithLabel(client, Button.class, SWT.CHECK,
            "Sends Shipments", null,
            BeansObservables.observeValue(clinic, "sendsShipments"), null);
        toolkit.paintBordersFor(client);

        activityStatusComboViewer = createComboViewerWithNoSelectionValidator(
            client, "Activity Status",
            ActivityStatusWrapper.getAllActivityStatuses(appService),
            clinic.getActivityStatus(), "Clinic must have an activity status");

        createBoundWidgetWithLabel(client, BiobankText.class, SWT.MULTI,
            "Comments", null, BeansObservables.observeValue(clinic, "comment"),
            null);
    }

    private void createContactSection() {
        Section section = createSection("Contacts");

        contactEntryWidget = new ContactEntryInfoTable(section, clinic);
        contactEntryWidget.adaptToToolkit(toolkit, true);
        contactEntryWidget.addSelectionChangedListener(listener);

        addSectionToolbar(section, "Add Contact", new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                contactEntryWidget.addContact();
            }
        }, ContactWrapper.class);
        section.setClient(contactEntryWidget);
    }

    private void createButtonsSection() {
        Composite client = toolkit.createComposite(page);
        GridLayout layout = new GridLayout(2, false);
        layout.horizontalSpacing = 10;
        client.setLayout(layout);
        client.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        toolkit.paintBordersFor(client);
    }

    @Override
    public void saveForm() throws Exception {
        clinic.addContacts(contactEntryWidget.getAddedOrModifedContacts());
        ActivityStatusWrapper activity = (ActivityStatusWrapper) ((StructuredSelection) activityStatusComboViewer
            .getSelection()).getFirstElement();
        clinic.setActivityStatus(activity);
        clinic.removeContacts(contactEntryWidget.getDeletedContacts());
        clinic.persist();
        clinicAdapter.getParent().performExpand();
    }

    @Override
    public String getNextOpenedFormID() {
        return ClinicViewForm.ID;
    }

    @Override
    public void reset() throws Exception {
        super.reset();
        ActivityStatusWrapper currentActivityStatus = clinic
            .getActivityStatus();
        if (currentActivityStatus != null) {
            activityStatusComboViewer.setSelection(new StructuredSelection(
                currentActivityStatus));
        } else if (activityStatusComboViewer.getCombo().getItemCount() > 1) {
            activityStatusComboViewer.getCombo().deselectAll();
        }
        contactEntryWidget.reload();
    }
}
