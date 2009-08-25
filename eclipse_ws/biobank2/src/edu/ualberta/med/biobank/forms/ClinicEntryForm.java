package edu.ualberta.med.biobank.forms;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.core.databinding.beans.PojoObservables;
import org.eclipse.core.runtime.Assert;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

import edu.ualberta.med.biobank.BioBankPlugin;
import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.utils.ModelUtils;
import edu.ualberta.med.biobank.model.Address;
import edu.ualberta.med.biobank.model.Clinic;
import edu.ualberta.med.biobank.model.Contact;
import edu.ualberta.med.biobank.model.Site;
import edu.ualberta.med.biobank.treeview.ClinicAdapter;
import edu.ualberta.med.biobank.validators.NonEmptyString;
import edu.ualberta.med.biobank.widgets.ContactEntryWidget;
import edu.ualberta.med.biobank.widgets.listener.BiobankEntryFormWidgetListener;
import edu.ualberta.med.biobank.widgets.listener.MultiSelectEvent;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.query.SDKQuery;
import gov.nih.nci.system.query.SDKQueryResult;
import gov.nih.nci.system.query.example.DeleteExampleQuery;
import gov.nih.nci.system.query.example.InsertExampleQuery;
import gov.nih.nci.system.query.example.UpdateExampleQuery;
import gov.nih.nci.system.query.hibernate.HQLCriteria;

public class ClinicEntryForm extends AddressEntryFormCommon {
    public static final String ID = "edu.ualberta.med.biobank.forms.ClinicEntryForm";

    private static final String MSG_NEW_CLINIC_OK = "New clinic information.";

    private static final String MSG_CLINIC_OK = "Clinic information.";

    private static final String MSG_NO_CLINIC_NAME = "Clinic must have a name";

    private ClinicAdapter clinicAdapter;
    private Clinic clinic;

    private ContactEntryWidget contactEntryWidget;

    protected Combo session;
    private Text name;

    private BiobankEntryFormWidgetListener listener = new BiobankEntryFormWidgetListener() {
        @Override
        public void selectionChanged(MultiSelectEvent event) {
            setDirty(true);
        }
    };

    @Override
    protected void init() {
        Assert.isTrue((adapter instanceof ClinicAdapter),
            "Invalid editor input: object of type "
                + adapter.getClass().getName());
        clinicAdapter = (ClinicAdapter) adapter;
        clinic = clinicAdapter.loadClinic();

        address = clinic.getAddress();
        if (address == null) {
            address = new Address();
            clinic.setAddress(address);
        }

        String tabName;
        if (clinic.getId() == null)
            tabName = "New Clinic";
        else
            tabName = "Clinic " + clinic.getName();
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
    protected void createFormContent() {
        form.setText("Clinic Information");
        GridLayout layout = new GridLayout(1, false);
        form.getBody().setLayout(layout);

        toolkit
            .createLabel(
                form.getBody(),
                "Clinics can be associated with studies after submitting this initial information.",
                SWT.LEFT);
        createClinicInfoSection();
        createAddressArea();
        createContactSection();
        createButtonsSection();

        // When adding help uncomment line below
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

        name = (Text) createBoundWidgetWithLabel(client, Text.class, SWT.NONE,
            "Name", null, PojoObservables.observeValue(clinic, "name"),
            NonEmptyString.class, MSG_NO_CLINIC_NAME);
        name.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        createBoundWidgetWithLabel(client, Combo.class, SWT.NONE,
            "Activity Status", FormConstants.ACTIVITY_STATUS, PojoObservables
                .observeValue(clinic, "activityStatus"), null, null);

        Text comment = (Text) createBoundWidgetWithLabel(client, Text.class,
            SWT.MULTI, "Comments", null, PojoObservables.observeValue(clinic,
                "comment"), null, null);
        GridData gd = new GridData(GridData.FILL_HORIZONTAL);
        gd.heightHint = 40;
        comment.setLayoutData(gd);
    }

    private void createContactSection() {
        Composite client = createSectionWithClient("Contacts");

        GridLayout layout = new GridLayout(1, false);
        client.setLayout(layout);
        client.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        contactEntryWidget = new ContactEntryWidget(client, SWT.NONE, clinic
            .getContactCollection(), toolkit);
        contactEntryWidget.addSelectionChangedListener(listener);
    }

    private void createButtonsSection() {
        Composite client = toolkit.createComposite(form.getBody());
        GridLayout layout = new GridLayout(2, false);
        layout.horizontalSpacing = 10;
        client.setLayout(layout);
        client.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        toolkit.paintBordersFor(client);

        initCancelConfirmWidget(client);
    }

    @Override
    public void setFocus() {
        form.setFocus();
    }

    @Override
    public void saveForm() throws Exception {
        clinic.setAddress(address);
        clinic.setSite(SessionManager.getInstance().getCurrentSite());

        SDKQuery query;
        SDKQueryResult result;

        if ((clinic.getId() == null) && !checkClinicNameUnique()) {
            setDirty(true);
            return;
        }

        saveContacts();
        clinic.setAddress(address);

        if ((clinic.getId() == null) || (clinic.getId() == 0)) {
            Assert.isTrue(clinic.getAddress().getId() == null,
                "insert invoked on address already in database");

            query = new InsertExampleQuery(clinic.getAddress());
            result = appService.executeQuery(query);
            clinic.setAddress((Address) result.getObjectResult());
            query = new InsertExampleQuery(clinic);
        } else {
            Assert.isNotNull(clinic.getAddress().getId(),
                "update invoked on address not in database");

            query = new UpdateExampleQuery(clinic.getAddress());
            result = appService.executeQuery(query);
            clinic.setAddress((Address) result.getObjectResult());
            query = new UpdateExampleQuery(clinic);
        }

        result = appService.executeQuery(query);
        clinic = (Clinic) result.getObjectResult();

        clinicAdapter.setClinic(clinic);
        clinicAdapter.getParent().performExpand();
    }

    private void saveContacts() throws Exception {
        SDKQuery query;

        Collection<Contact> contactCollection = contactEntryWidget
            .getContacts();
        removeDeletedContacts(contactCollection);

        for (Contact c : contactCollection) {
            c.setClinic(clinic);
            if ((c.getId() == null) || (c.getId() == 0)) {
                query = new InsertExampleQuery(c);
            } else {
                query = new UpdateExampleQuery(c);
            }

            appService.executeQuery(query);
        }
    }

    private void removeDeletedContacts(Collection<Contact> contactCollection)
        throws Exception {
        // no need to remove if clinic is not yet in the database
        if (clinic.getId() == null)
            return;

        List<Integer> selectedContactIds = new ArrayList<Integer>();
        for (Contact c : contactCollection) {
            selectedContactIds.add(c.getId());
        }

        SDKQuery query;

        // query from database again
        Clinic dbClinic = ModelUtils.getObjectWithId(appService, Clinic.class,
            clinic.getId());

        for (Contact c : dbClinic.getContactCollection()) {
            if (!selectedContactIds.contains(c.getId())) {
                query = new DeleteExampleQuery(c);
                appService.executeQuery(query);
            }
        }
    }

    private boolean checkClinicNameUnique() throws ApplicationException {
        Site site = SessionManager.getInstance().getCurrentSite();

        HQLCriteria c = new HQLCriteria(
            "from edu.ualberta.med.biobank.model.Clinic as clinic "
                + "inner join fetch clinic.site " + "where clinic.site.id='"
                + site.getId() + "' " + "and clinic.name = '"
                + clinic.getName() + "'");

        List<Object> results = appService.query(c);
        if (results.size() == 0)
            return true;

        BioBankPlugin.openAsyncError("Site Name Problem",
            "A clinic with name \"" + clinic.getName() + "\" already exists.");
        return false;
    }

    @Override
    public void cancelForm() {
        // TODO Auto-generated method stub

    }

    @Override
    public String getNextOpenedFormID() {
        return ClinicViewForm.ID;
    }
}
