package edu.ualberta.med.biobank.forms;

import java.util.List;

import org.eclipse.core.databinding.beans.PojoObservables;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.springframework.remoting.RemoteAccessException;

import edu.ualberta.med.biobank.BioBankPlugin;
import edu.ualberta.med.biobank.forms.input.FormInput;
import edu.ualberta.med.biobank.model.Address;
import edu.ualberta.med.biobank.model.Clinic;
import edu.ualberta.med.biobank.model.Site;
import edu.ualberta.med.biobank.treeview.ClinicAdapter;
import edu.ualberta.med.biobank.treeview.SiteAdapter;
import edu.ualberta.med.biobank.validators.NonEmptyString;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.WritableApplicationService;
import gov.nih.nci.system.query.SDKQuery;
import gov.nih.nci.system.query.SDKQueryResult;
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

    protected Combo session;
    private Text name;

    @Override
    public void init(IEditorSite editorSite, IEditorInput input)
        throws PartInitException {
        super.init(editorSite, input);

        FormInput clinicInput = (FormInput) input;

        clinicAdapter = (ClinicAdapter) clinicInput.getNode();
        clinic = clinicAdapter.getClinic();
        setAppService(clinicAdapter.getAppService());

        address = clinic.getAddress();
        if (address == null) {
            address = new Address();
            clinic.setAddress(address);
        }

        if (clinic.getId() == null) {
            setPartName("New Clinic");
        } else {
            setPartName("Clinic " + clinic.getName());
        }
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

    private void createButtonsSection() {
        Composite client = toolkit.createComposite(form.getBody());
        GridLayout layout = new GridLayout(2, false);
        layout.horizontalSpacing = 10;
        client.setLayout(layout);
        client.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        toolkit.paintBordersFor(client);

        initConfirmButton(client, false, true);
    }

    @Override
    protected void handleStatusChanged(IStatus status) {
        if (status.getSeverity() == IStatus.OK) {
            form.setMessage(getOkMessage(), IMessageProvider.NONE);
            getConfirmButton().setEnabled(true);
        } else {
            form.setMessage(status.getMessage(), IMessageProvider.ERROR);
            getConfirmButton().setEnabled(false);
        }
    }

    @Override
    public void setFocus() {
        form.setFocus();
    }

    @Override
    public void saveForm() {
        clinic.setAddress(address);
        SiteAdapter siteAdapter = (SiteAdapter) clinicAdapter
            .getParentFromClass(SiteAdapter.class);
        clinic.setSite(siteAdapter.getSite());

        try {
            SDKQuery query;
            SDKQueryResult result;

            if ((clinic.getId() == null) && !checkClinicNameUnique()) {
                setDirty(true);
                return;
            }

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

            clinicAdapter.getParent().performExpand();
            getSite().getPage().closeEditor(this, false);
        } catch (final RemoteAccessException exp) {
            BioBankPlugin.openRemoteAccessErrorMessage();
        } catch (Exception exp) {
            exp.printStackTrace();
        }
    }

    private boolean checkClinicNameUnique() throws ApplicationException {
        WritableApplicationService appService = clinicAdapter.getAppService();
        Site site = ((SiteAdapter) clinicAdapter
            .getParentFromClass(SiteAdapter.class)).getSite();

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
    protected void cancelForm() {
        // TODO Auto-generated method stub

    }
}
