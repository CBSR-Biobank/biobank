package edu.ualberta.med.biobank.forms;

import org.eclipse.core.runtime.Assert;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

import edu.ualberta.med.biobank.common.wrappers.ShipmentWrapper;
import edu.ualberta.med.biobank.logs.BiobankLogger;
import edu.ualberta.med.biobank.treeview.shipment.ShipmentAdapter;
import edu.ualberta.med.biobank.widgets.BiobankText;
import edu.ualberta.med.biobank.widgets.ShipmentPatientsWidget;

public class ShipmentViewForm extends BiobankViewForm {

    private static BiobankLogger logger = BiobankLogger
        .getLogger(ShipmentViewForm.class.getName());

    public static final String ID = "edu.ualberta.med.biobank.forms.ShipmentViewForm";

    private ShipmentAdapter shipmentAdapter;

    private ShipmentWrapper shipment;

    private BiobankText siteLabel;

    private BiobankText clinicLabel;

    private BiobankText waybillLabel;

    private BiobankText departedLabel;

    private BiobankText dateReceivedLabel;

    private BiobankText commentLabel;

    private BiobankText shippingMethodLabel;

    private BiobankText boxNumberLabel;

    private BiobankText patientCountLabel;

    private BiobankText patientVisitCountLabel;

    private BiobankText activityStatusLabel;

    @Override
    protected void init() throws Exception {
        Assert.isTrue((adapter instanceof ShipmentAdapter),
            "Invalid editor input: object of type "
                + adapter.getClass().getName());

        shipmentAdapter = (ShipmentAdapter) adapter;
        shipment = shipmentAdapter.getWrapper();
        retrieveShipment();

        setPartName("Shipment " + shipment.getFormattedDateReceived());
    }

    private void retrieveShipment() {
        try {
            shipment.reload();
        } catch (Exception ex) {
            logger.error(
                "Error while retrieving shipment " + shipment.getWaybill(), ex);
        }
    }

    @Override
    protected void createFormContent() throws Exception {
        form.setText("Shipment received on "
            + shipment.getFormattedDateReceived() + " from "
            + shipment.getClinic().getNameShort());
        page.setLayout(new GridLayout(1, false));
        page.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
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
            client, SWT.NONE, shipment, toolkit, false);
        shipPatientsWidget
            .addDoubleClickListener(collectionDoubleClickListener);
    }

    private void createMainSection() {
        Composite client = toolkit.createComposite(page);
        GridLayout layout = new GridLayout(2, false);
        layout.horizontalSpacing = 10;
        client.setLayout(layout);
        client.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        toolkit.paintBordersFor(client);

        siteLabel = createReadOnlyLabelledField(client, SWT.NONE, "Site");
        waybillLabel = createReadOnlyLabelledField(client, SWT.NONE, "Waybill");
        clinicLabel = createReadOnlyLabelledField(client, SWT.NONE, "Clinic");
        shippingMethodLabel = createReadOnlyLabelledField(client, SWT.NONE,
            "Shipping Method");
        if (shipment.needDeparted()) {
            departedLabel = createReadOnlyLabelledField(client, SWT.NONE,
                "Departed");
        }
        boxNumberLabel = createReadOnlyLabelledField(client, SWT.NONE,
            "Box number");
        dateReceivedLabel = createReadOnlyLabelledField(client, SWT.NONE,
            "Date received");
        activityStatusLabel = createReadOnlyLabelledField(client, SWT.NONE,
            "Activity Status");
        commentLabel = createReadOnlyLabelledField(client, SWT.MULTI,
            "Comments");
        patientCountLabel = createReadOnlyLabelledField(client, SWT.NONE,
            "Patients");
        patientVisitCountLabel = createReadOnlyLabelledField(client, SWT.NONE,
            "Patient Visits");

        setShipmentValues();
    }

    private void setShipmentValues() {
        setTextValue(siteLabel, shipment.getSite().getName());
        setTextValue(waybillLabel, shipment.getWaybill());
        setTextValue(clinicLabel, shipment.getClinic() == null ? "" : shipment
            .getClinic().getName());
        if (departedLabel != null)
            setTextValue(departedLabel, shipment.getFormattedDeparted());
        setTextValue(shippingMethodLabel,
            shipment.getShippingMethod() == null ? "" : shipment
                .getShippingMethod().getName());

        setTextValue(boxNumberLabel, shipment.getBoxNumber());
        setTextValue(dateReceivedLabel, shipment.getFormattedDateReceived());
        setTextValue(activityStatusLabel, shipment.getActivityStatus());
        setTextValue(commentLabel, shipment.getComment());
        setTextValue(patientCountLabel, shipment.getPatientCollection().size());
        setTextValue(patientVisitCountLabel, shipment
            .getPatientVisitCollection().size());
    }

    @Override
    public void reload() throws Exception {
        retrieveShipment();
        setPartName("Shipment " + shipment.getWaybill());
        if (!form.isDisposed()) {
            form.setText("Shipment waybill: " + shipment.getWaybill());
        }
        setShipmentValues();
    }

}
