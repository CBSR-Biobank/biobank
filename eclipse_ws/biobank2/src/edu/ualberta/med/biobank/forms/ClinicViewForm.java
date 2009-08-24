package edu.ualberta.med.biobank.forms;

import org.eclipse.core.runtime.Assert;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.springframework.remoting.RemoteConnectFailureException;

import edu.ualberta.med.biobank.BioBankPlugin;
import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.utils.ModelUtils;
import edu.ualberta.med.biobank.forms.input.FormInput;
import edu.ualberta.med.biobank.model.Clinic;
import edu.ualberta.med.biobank.treeview.AdapterBase;
import edu.ualberta.med.biobank.treeview.ClinicAdapter;
import edu.ualberta.med.biobank.widgets.infotables.ClinicStudyInfoTable;

public class ClinicViewForm extends AddressViewFormCommon {
    public static final String ID = "edu.ualberta.med.biobank.forms.ClinicViewForm";

    private ClinicAdapter clinicAdapter;

    private Clinic clinic;

    private ClinicStudyInfoTable studiesTable;

    private Label activityStatusLabel;

    private Label commentLabel;

    @Override
    protected void init() {
        Assert.isTrue(adapter instanceof ClinicAdapter,
            "Invalid editor input: object of type "
                + adapter.getClass().getName());

        clinicAdapter = (ClinicAdapter) adapter;
        retrieveClinic();
        address = clinic.getAddress();
        setPartName("Clinic: " + clinic.getName());
    }

    private void retrieveClinic() {
        try {
            clinic = ModelUtils.getObjectWithId(appService, Clinic.class,
                clinicAdapter.getClinic().getId());
            Assert.isNotNull(clinic, "clinic not in database");
            clinicAdapter.setClinic(clinic);
        } catch (final RemoteConnectFailureException exp) {
            BioBankPlugin.openRemoteConnectErrorMessage();
        } catch (Exception e) {
            SessionManager.getLogger().error(
                "Error while retrieving the clinic", e);
        }
    }

    @Override
    protected void createFormContent() {
        form.setText("Clinic: " + clinic.getName());
        addRefreshToolbarAction();

        GridLayout layout = new GridLayout(1, false);
        form.getBody().setLayout(layout);
        form.getBody().setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        createClinicSection();
        createAddressSection();
        createStudiesSection();
        createButtonsSection();
    }

    private void createClinicSection() {
        Composite client = toolkit.createComposite(form.getBody());
        client.setLayout(new GridLayout(2, false));
        client.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        toolkit.paintBordersFor(client);

        activityStatusLabel = (Label) createWidget(client, Label.class,
            SWT.NONE, "Activity Status");
        commentLabel = (Label) createWidget(client, Label.class, SWT.NONE,
            "Comments");

        setClinicValues();
    }

    private void setClinicValues() {
        FormUtils.setTextValue(activityStatusLabel, clinic.getActivityStatus());
        FormUtils.setTextValue(commentLabel, clinic.getComment());
    }

    protected void createStudiesSection() {
        try {
            Composite client = createSectionWithClient("Studies");

            studiesTable = new ClinicStudyInfoTable(client, appService, clinic);
            studiesTable.adaptToToolkit(toolkit, true);
            toolkit.paintBordersFor(studiesTable);

            studiesTable.getTableViewer().addDoubleClickListener(
                FormUtils.getBiobankCollectionDoubleClickListener());
        } catch (final RemoteConnectFailureException exp) {
            BioBankPlugin.openRemoteConnectErrorMessage();
        } catch (Exception e) {
            SessionManager.getLogger().error(
                "Error while retrieving the clinic", e);
        }
    }

    protected void createButtonsSection() {
        Composite client = toolkit.createComposite(form.getBody());
        client.setLayout(new GridLayout(4, false));
        toolkit.paintBordersFor(client);

        final Button edit = toolkit.createButton(client, "Edit Clinic Info",
            SWT.PUSH);
        edit.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                AdapterBase.openForm(new FormInput(clinicAdapter),
                    ClinicEntryForm.ID);
            }
        });
    }

    @Override
    protected void reload() {
        retrieveClinic();
        setPartName("Clinic: " + clinic.getName());
        form.setText("Clinic: " + clinic.getName());
        setClinicValues();
        setAdressValues();
        studiesTable.setCollection(null);
    }
}
