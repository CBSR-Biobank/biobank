package edu.ualberta.med.biobank.forms;

import org.eclipse.core.databinding.beans.BeansObservables;
import org.eclipse.core.runtime.Assert;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import edu.ualberta.med.biobank.BioBankPlugin;
import edu.ualberta.med.biobank.common.wrappers.ClinicWrapper;
import edu.ualberta.med.biobank.treeview.ClinicAdapter;
import edu.ualberta.med.biobank.treeview.SiteAdapter;
import edu.ualberta.med.biobank.validators.NonEmptyStringValidator;
import edu.ualberta.med.biobank.widgets.ContactEntryWidget;
import edu.ualberta.med.biobank.widgets.listeners.BiobankEntryFormWidgetListener;
import edu.ualberta.med.biobank.widgets.listeners.MultiSelectEvent;

public class ClinicEntryForm extends AddressEntryFormCommon {
    public static final String ID = "edu.ualberta.med.biobank.forms.ClinicEntryForm";

    private static final String MSG_NEW_CLINIC_OK = "New clinic information.";

    private static final String MSG_CLINIC_OK = "Clinic information.";

    private static final String MSG_NO_CLINIC_NAME = "Clinic must have a name";

    private ClinicAdapter clinicAdapter;

    private ClinicWrapper clinicWrapper;

    private ContactEntryWidget contactEntryWidget;

    protected Combo session;

    private BiobankEntryFormWidgetListener listener = new BiobankEntryFormWidgetListener() {
        @Override
        public void selectionChanged(MultiSelectEvent event) {
            setDirty(true);
        }
    };

    @Override
    protected void init() throws Exception {
        Assert.isTrue((adapter instanceof ClinicAdapter),
            "Invalid editor input: object of type "
                + adapter.getClass().getName());
        clinicAdapter = (ClinicAdapter) adapter;
        clinicWrapper = clinicAdapter.getWrapper();

        String tabName;
        if (clinicWrapper.getId() == null)
            tabName = "New Clinic";
        else
            tabName = "Clinic " + clinicWrapper.getName();
        setPartName(tabName);
    }

    @Override
    protected String getOkMessage() {
        if (clinicWrapper.getId() == null) {
            return MSG_NEW_CLINIC_OK;
        }
        return MSG_CLINIC_OK;
    }

    @Override
    protected void createFormContent() {
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
        createAddressArea(clinicWrapper);
        createContactSection();
        createButtonsSection();

        // TODO: When adding help uncomment line below
        // PlatformUI.getWorkbench().getHelpSystem().setHelp(composite,
        // IJavaHelpContextIds.XXXXX);
    }

    private void createClinicInfoSection() {
        Composite client = toolkit.createComposite(form.getBody());
        GridLayout layout = new GridLayout(2, false);
        layout.horizontalSpacing = 10;
        client.setLayout(layout);
        client.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        toolkit.paintBordersFor(client);

        Label siteLabel = (Label) createWidget(client, Label.class, SWT.NONE,
            "Site");
        FormUtils.setTextValue(siteLabel, clinicWrapper.getSite().getName());

        firstControl = createBoundWidgetWithLabel(client, Text.class, SWT.NONE,
            "Name", null, BeansObservables.observeValue(clinicWrapper, "name"),
            new NonEmptyStringValidator(MSG_NO_CLINIC_NAME));

        createBoundWidgetWithLabel(client, Combo.class, SWT.NONE,
            "Activity Status", FormConstants.ACTIVITY_STATUS, BeansObservables
                .observeValue(clinicWrapper, "activityStatus"), null);

        Text comment = (Text) createBoundWidgetWithLabel(client, Text.class,
            SWT.MULTI, "Comments", null, BeansObservables.observeValue(
                clinicWrapper, "comment"), null);
        GridData gd = new GridData(GridData.FILL_HORIZONTAL);
        gd.heightHint = 40;
        comment.setLayoutData(gd);
    }

    private void createContactSection() {
        Composite client = createSectionWithClient("Contacts");

        GridLayout layout = new GridLayout(1, false);
        client.setLayout(layout);
        client.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        contactEntryWidget = new ContactEntryWidget(client, SWT.NONE,
            clinicWrapper, toolkit);
        contactEntryWidget.addSelectionChangedListener(listener);
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
        clinicWrapper.setSite(siteAdapter.getWrapper());
        clinicWrapper.setContactCollection(contactEntryWidget.getContacts());
        clinicWrapper.persist();

        // clinicAdapter.getParent().performExpand();
    }

    @Override
    public String getNextOpenedFormID() {
        return ClinicViewForm.ID;
    }
}
