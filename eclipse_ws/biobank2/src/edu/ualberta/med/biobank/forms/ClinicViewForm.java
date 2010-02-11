package edu.ualberta.med.biobank.forms;

import org.eclipse.core.runtime.Assert;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

import edu.ualberta.med.biobank.BioBankPlugin;
import edu.ualberta.med.biobank.common.wrappers.ClinicWrapper;
import edu.ualberta.med.biobank.treeview.ClinicAdapter;
import edu.ualberta.med.biobank.widgets.infotables.ClinicStudyInfoTable;
import edu.ualberta.med.biobank.widgets.infotables.ContactInfoTable;
import edu.ualberta.med.biobank.widgets.infotables.ShipmentInfoTable;

public class ClinicViewForm extends AddressViewFormCommon {
    public static final String ID = "edu.ualberta.med.biobank.forms.ClinicViewForm";

    private ClinicAdapter clinicAdapter;

    private ClinicWrapper clinic;

    private ContactInfoTable contactsTable;

    private ClinicStudyInfoTable studiesTable;

    private Text siteLabel;

    private Text activityStatusLabel;

    private Text commentLabel;

    private Text patientTotal;

    private Text visitTotal;

    private Text shipmentTotal;

    private ShipmentInfoTable shipmentsTable;

    @Override
    protected void init() throws Exception {
        Assert.isTrue(adapter instanceof ClinicAdapter,
            "Invalid editor input: object of type "
                + adapter.getClass().getName());

        clinicAdapter = (ClinicAdapter) adapter;
        clinic = clinicAdapter.getWrapper();
        clinic.reload();
        setPartName("Clinic: " + clinic.getName());
    }

    @Override
    protected void createFormContent() throws Exception {
        form.setText("Clinic: " + clinic.getName());

        GridLayout layout = new GridLayout(1, false);
        form.getBody().setLayout(layout);
        form.getBody().setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        form.setImage(BioBankPlugin.getDefault().getImageRegistry().get(
            BioBankPlugin.IMG_CLINIC));
        createClinicSection();
        createAddressSection(clinic);
        createContactsSection();
        createStudiesSection();
        createShipmentsSection();
        createButtonsSection();
    }

    private void createClinicSection() throws Exception {
        Composite client = toolkit.createComposite(form.getBody());
        client.setLayout(new GridLayout(2, false));
        client.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        toolkit.paintBordersFor(client);

        siteLabel = createReadOnlyField(client, SWT.READ_ONLY,
            "Repository Site");
        activityStatusLabel = createReadOnlyField(client, SWT.NONE,
            "Activity Status");
        commentLabel = createReadOnlyField(client, SWT.NONE, "Comments");
        shipmentTotal = createReadOnlyField(client, SWT.NONE, "Total Shipments");
        patientTotal = createReadOnlyField(client, SWT.NONE, "Total Patients");
        visitTotal = createReadOnlyField(client, SWT.NONE,
            "Total Patient Visits");

        setClinicValues();
    }

    private void setClinicValues() throws Exception {
        setTextValue(siteLabel, clinic.getSite().getName());
        setTextValue(activityStatusLabel, clinic.getActivityStatus());
        setTextValue(commentLabel, clinic.getComment());
        setTextValue(shipmentTotal, clinic.getShipmentCollection().size());
        setTextValue(patientTotal, clinic.getPatientCount());
        setTextValue(visitTotal, clinic.getPatientVisitCollection().size());
    }

    private void createContactsSection() {
        Composite client = createSectionWithClient("Contacts");

        contactsTable = new ContactInfoTable(client, clinic
            .getContactCollection());
        contactsTable.adaptToToolkit(toolkit, true);
        toolkit.paintBordersFor(contactsTable);

        contactsTable.getTableViewer().addDoubleClickListener(
            collectionDoubleClickListener);
    }

    protected void createStudiesSection() throws Exception {
        Composite client = createSectionWithClient("Studies");

        studiesTable = new ClinicStudyInfoTable(client, clinic);
        studiesTable.adaptToToolkit(toolkit, true);
        toolkit.paintBordersFor(studiesTable);

        studiesTable.getTableViewer().addDoubleClickListener(
            collectionDoubleClickListener);
    }

    protected void createShipmentsSection() {
        Composite client = createSectionWithClient("Shipments");

        shipmentsTable = new ShipmentInfoTable(client, clinic);
        shipmentsTable.adaptToToolkit(toolkit, true);
        toolkit.paintBordersFor(shipmentsTable);

        shipmentsTable.getTableViewer().addDoubleClickListener(
            collectionDoubleClickListener);
    }

    protected void createButtonsSection() {
        Composite client = toolkit.createComposite(form.getBody());
        client.setLayout(new GridLayout(4, false));
        toolkit.paintBordersFor(client);
    }

    @Override
    protected void reload() throws Exception {
        clinic.reload();
        setPartName("Clinic: " + clinic.getName());
        form.setText("Clinic: " + clinic.getName());
        setClinicValues();
        setAdressValues(clinic);
        studiesTable.setCollection(clinic.getStudyCollection());
    }

    @Override
    protected String getEntryFormId() {
        return ClinicEntryForm.ID;
    }
}
