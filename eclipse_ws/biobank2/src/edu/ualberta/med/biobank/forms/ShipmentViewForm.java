package edu.ualberta.med.biobank.forms;

import org.eclipse.core.runtime.Assert;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

import edu.ualberta.med.biobank.BioBankPlugin;
import edu.ualberta.med.biobank.common.wrappers.ShipmentWrapper;
import edu.ualberta.med.biobank.logs.BiobankLogger;
import edu.ualberta.med.biobank.treeview.ShipmentAdapter;
import edu.ualberta.med.biobank.widgets.ShipmentPatientsWidget;

public class ShipmentViewForm extends BiobankViewForm {

    private static BiobankLogger logger = BiobankLogger
        .getLogger(ShipmentViewForm.class.getName());

    public static final String ID = "edu.ualberta.med.biobank.forms.ShipmentViewForm";
    private ShipmentAdapter shipmentAdapter;
    private ShipmentWrapper shipment;

    private Text siteLabel;

    private Text clinicLabel;

    private Text waybillLabel;

    private Text dateShippedLabel;

    private Text dateReceivedLabel;

    private Text commentLabel;

    private Text companyLabel;

    private Text boxNumberLabel;

    private Text patientCountLabel;

    private Text patientVisitCountLabel;

    @Override
    protected void init() throws Exception {
        Assert.isTrue((adapter instanceof ShipmentAdapter),
            "Invalid editor input: object of type "
                + adapter.getClass().getName());

        shipmentAdapter = (ShipmentAdapter) adapter;
        shipment = shipmentAdapter.getWrapper();
        retrieveShipment();

        setPartName("Shipment " + shipment.getWaybill());
    }

    private void retrieveShipment() {
        try {
            shipment.reload();
        } catch (Exception ex) {
            logger.error("Error while retrieving shipment "
                + shipment.getWaybill(), ex);
        }
    }

    @Override
    protected void createFormContent() throws Exception {
        form.setText("Shipment waybill: " + shipment.getWaybill());
        form.getBody().setLayout(new GridLayout(1, false));
        form.getBody().setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        form.setImage(BioBankPlugin.getDefault().getImageRegistry().get(
            BioBankPlugin.IMG_SHIPMENT));
        createMainSection();
        createPatientsSection();
    }

    private void createPatientsSection() {
        Composite client = createSectionWithClient("Patients");
        GridLayout layout = new GridLayout(1, false);
        client.setLayout(layout);
        client.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        toolkit.paintBordersFor(client);
        ShipmentPatientsWidget shipPatientsWidget = new ShipmentPatientsWidget(
            client, SWT.NONE, shipment, null, toolkit, false);
        shipPatientsWidget
            .addDoubleClickListener(collectionDoubleClickListener);
    }

    private void createMainSection() {
        Composite client = toolkit.createComposite(form.getBody());
        GridLayout layout = new GridLayout(2, false);
        layout.horizontalSpacing = 10;
        client.setLayout(layout);
        client.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        toolkit.paintBordersFor(client);

        siteLabel = createReadOnlyField(client, SWT.NONE, "Site");
        waybillLabel = createReadOnlyField(client, SWT.NONE, "Waybill");
        clinicLabel = createReadOnlyField(client, SWT.NONE, "Clinic");
        dateShippedLabel = createReadOnlyField(client, SWT.NONE, "Date Shipped");
        companyLabel = createReadOnlyField(client, SWT.NONE, "Shipping company");
        boxNumberLabel = createReadOnlyField(client, SWT.NONE, "Box number");
        dateReceivedLabel = createReadOnlyField(client, SWT.NONE,
            "Date received");
        commentLabel = createReadOnlyField(client, SWT.WRAP, "Comments");
        patientCountLabel = createReadOnlyField(client, SWT.WRAP, "Patients");
        patientVisitCountLabel = createReadOnlyField(client, SWT.WRAP,
            "Patient Visits");

        setShipmentValues();
    }

    private void setShipmentValues() {
        setTextValue(siteLabel, shipment.getClinic().getSite().getName());
        setTextValue(waybillLabel, shipment.getWaybill());
        setTextValue(clinicLabel, shipment.getClinic() == null ? "" : shipment
            .getClinic().getName());
        setTextValue(dateShippedLabel, shipment.getFormattedDateShipped());
        setTextValue(companyLabel, shipment.getShippingCompany() == null ? ""
            : shipment.getShippingCompany().getName());
        setTextValue(boxNumberLabel, shipment.getBoxNumber());
        setTextValue(dateReceivedLabel, shipment.getFormattedDateReceived());
        setTextValue(commentLabel, shipment.getComment());
        setTextValue(patientCountLabel, shipment.getPatientCollection().size());
        setTextValue(patientVisitCountLabel, shipment
            .getPatientVisitCollection().size());
    }

    @Override
    protected void reload() throws Exception {
        retrieveShipment();
        setPartName("Shipment " + shipment.getWaybill());
        if (!form.isDisposed()) {
            form.setText("Shipment waybill: " + shipment.getWaybill());
        }
        setShipmentValues();
    }

}
