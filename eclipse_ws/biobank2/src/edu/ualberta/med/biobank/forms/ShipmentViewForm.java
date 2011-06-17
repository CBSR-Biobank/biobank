package edu.ualberta.med.biobank.forms;

import org.eclipse.core.runtime.Assert;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

import edu.ualberta.med.biobank.common.wrappers.OriginInfoWrapper;
import edu.ualberta.med.biobank.common.wrappers.ShipmentInfoWrapper;
import edu.ualberta.med.biobank.common.wrappers.ShippingMethodWrapper;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;
import edu.ualberta.med.biobank.gui.common.BiobankLogger;
import edu.ualberta.med.biobank.treeview.shipment.ShipmentAdapter;
import edu.ualberta.med.biobank.widgets.SpecimenEntryWidget;

public class ShipmentViewForm extends BiobankViewForm {

    private static BgcLogger logger = BgcLogger
        .getLogger(ShipmentViewForm.class.getName());

    public static final String ID = "edu.ualberta.med.biobank.forms.ShipmentViewForm";

    private ShipmentAdapter shipmentAdapter;

    private OriginInfoWrapper originInfo;

    private BgcBaseText senderLabel;

    private BgcBaseText receiverLabel;

    private BgcBaseText waybillLabel;

    private BgcBaseText departedLabel;

    private BgcBaseText dateReceivedLabel;

    private BgcBaseText shippingMethodLabel;

    private BgcBaseText boxNumberLabel;

    private SpecimenEntryWidget specimenWidget;

    @Override
    protected void init() throws Exception {
        Assert.isTrue((adapter instanceof ShipmentAdapter),
            "Invalid editor input: object of type "
                + adapter.getClass().getName());

        shipmentAdapter = (ShipmentAdapter) adapter;
        originInfo = shipmentAdapter.getWrapper();

        setPartName();
    }

    private void retrieveShipment() {
        try {
            originInfo.reload();
        } catch (Exception ex) {
            logger.error("Error while retrieving shipment "
                + originInfo.getShipmentInfo().getWaybill(), ex);
        }
    }

    @Override
    protected void createFormContent() throws Exception {
        setFormText();
        page.setLayout(new GridLayout(1, false));
        page.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        createMainSection();
        createSpecimensSection();
    }

    private void createSpecimensSection() {
        Composite client = createSectionWithClient("Specimens");
        GridLayout layout = new GridLayout(1, false);
        client.setLayout(layout);
        client.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        toolkit.paintBordersFor(client);
        specimenWidget = new SpecimenEntryWidget(client, SWT.NONE, toolkit,
            appService, false);
        specimenWidget.setSpecimens(originInfo.getSpecimenCollection());
        specimenWidget.addDoubleClickListener(collectionDoubleClickListener);
    }

    private void createMainSection() {
        Composite client = toolkit.createComposite(page);
        GridLayout layout = new GridLayout(2, false);
        layout.horizontalSpacing = 10;
        client.setLayout(layout);
        client.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        toolkit.paintBordersFor(client);

        senderLabel = createReadOnlyLabelledField(client, SWT.NONE, "Sender");
        receiverLabel = createReadOnlyLabelledField(client, SWT.NONE,
            "Receiver");
        waybillLabel = createReadOnlyLabelledField(client, SWT.NONE, "Waybill");
        shippingMethodLabel = createReadOnlyLabelledField(client, SWT.NONE,
            "Shipping Method");
        if (originInfo.getShipmentInfo().getShippingMethod().needDate()) {
            departedLabel = createReadOnlyLabelledField(client, SWT.NONE,
                "Packed");
        }
        boxNumberLabel = createReadOnlyLabelledField(client, SWT.NONE,
            "Box number");
        dateReceivedLabel = createReadOnlyLabelledField(client, SWT.NONE,
            "Received");

        setShipmentValues();
    }

    private void setShipmentValues() {
        ShipmentInfoWrapper shipInfo = originInfo.getShipmentInfo();
        ShippingMethodWrapper shipMethod = shipInfo.getShippingMethod();

        setTextValue(senderLabel, originInfo.getCenter().getName());

        SiteWrapper rcvSite = originInfo.getReceiverSite();
        setTextValue(receiverLabel, rcvSite != null ? rcvSite.getName() : "");

        setTextValue(waybillLabel, originInfo.getShipmentInfo().getWaybill());
        if (departedLabel != null) {
            setTextValue(departedLabel, shipInfo.getFormattedDatePacked());
        }
        setTextValue(shippingMethodLabel,
            shipMethod == null ? "" : shipMethod.getName());

        setTextValue(boxNumberLabel, shipInfo.getBoxNumber());
        setTextValue(dateReceivedLabel, shipInfo.getFormattedDateReceived());
    }

    @Override
    public void reload() throws Exception {
        retrieveShipment();
        setPartName();
        setFormText();
        setShipmentValues();

        specimenWidget.setSpecimens(originInfo.getSpecimenCollection());
    }

    private void setPartName() {
        setPartName("Shipment "
            + originInfo.getShipmentInfo().getFormattedDateReceived());
    }

    private void setFormText() {
        if (!form.isDisposed()) {
            form.setText("Shipment received on "
                + originInfo.getShipmentInfo().getFormattedDateReceived()
                + " from " + originInfo.getCenter().getNameShort());
        }
    }

}
