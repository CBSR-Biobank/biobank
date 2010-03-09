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
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.Section;

import edu.ualberta.med.biobank.BioBankPlugin;
import edu.ualberta.med.biobank.common.wrappers.ActivityStatusWrapper;
import edu.ualberta.med.biobank.common.wrappers.ClinicWrapper;
import edu.ualberta.med.biobank.treeview.ClinicAdapter;
import edu.ualberta.med.biobank.treeview.SiteAdapter;
import edu.ualberta.med.biobank.validators.NonEmptyStringValidator;
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
        GridLayout layout = new GridLayout(1, false);
        form.getBody().setLayout(layout);
        form.setImage(BioBankPlugin.getDefault().getImageRegistry().get(
            BioBankPlugin.IMG_CLINIC));

        toolkit
            .createLabel(
                form.getBody(),
                "Clinics can be associated with studies after submitting this initial information.",
                SWT.LEFT);
        createClinicInfoSection();
        createAddressArea(clinic);
        createContactSection();
        createButtonsSection();

        // TODO: When adding help uncomment line below
        // PlatformUI.getWorkbench().getHelpSystem().setHelp(composite,
        // IJavaHelpContextIds.XXXXX);
    }

    private void createClinicInfoSection() throws ApplicationException {
        Composite client = toolkit.createComposite(form.getBody());
        GridLayout layout = new GridLayout(2, false);
        layout.horizontalSpacing = 10;
        client.setLayout(layout);
        client.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        toolkit.paintBordersFor(client);

        Text siteLabel = createReadOnlyField(client, SWT.NONE,
            "Repository Site");
        setTextValue(siteLabel, clinic.getSite().getName());

        firstControl = createBoundWidgetWithLabel(client, Text.class, SWT.NONE,
            "Name", null, BeansObservables.observeValue(clinic, "name"),
            new NonEmptyStringValidator(MSG_NO_CLINIC_NAME));

        createBoundWidgetWithLabel(client, Text.class, SWT.NONE, "Short Name",
            null, BeansObservables.observeValue(clinic, "nameShort"),
            new NonEmptyStringValidator(MSG_NO_CLINIC_NAME));

        activityStatusComboViewer = createComboViewerWithNoSelectionValidator(
            client, "Activity Status", ActivityStatusWrapper
                .getAllActivityStatuses(appService),
            clinic.getActivityStatus(), "Clinic must have an activity status",
            true);

        Text comment = (Text) createBoundWidgetWithLabel(client, Text.class,
            SWT.MULTI, "Comments", null, BeansObservables.observeValue(clinic,
                "comment"), null);
        GridData gd = new GridData(GridData.FILL_HORIZONTAL);
        gd.heightHint = 40;
        comment.setLayoutData(gd);
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
        });
        section.setClient(contactEntryWidget);
    }

    private void createButtonsSection() {
        Composite client = toolkit.createComposite(form.getBody());
        GridLayout layout = new GridLayout(2, false);
        layout.horizontalSpacing = 10;
        client.setLayout(layout);
        client.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        toolkit.paintBordersFor(client);
    }

    @Override
    public void saveForm() throws Exception {
        SiteAdapter siteAdapter = clinicAdapter
            .getParentFromClass(SiteAdapter.class);
        clinic.setSite(siteAdapter.getWrapper());
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
