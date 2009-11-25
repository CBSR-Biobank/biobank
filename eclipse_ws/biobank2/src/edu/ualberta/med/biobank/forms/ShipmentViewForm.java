package edu.ualberta.med.biobank.forms;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.Assert;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import edu.ualberta.med.biobank.BioBankPlugin;
import edu.ualberta.med.biobank.common.wrappers.ShipmentWrapper;
import edu.ualberta.med.biobank.treeview.ShipmentAdapter;
import edu.ualberta.med.biobank.widgets.infotables.ShptSampleSourceInfoTable;

public class ShipmentViewForm extends BiobankViewForm {

    private static Logger LOGGER = Logger.getLogger(ShipmentViewForm.class
        .getName());

    public static final String ID = "edu.ualberta.med.biobank.forms.ShipmentViewForm";
    private ShipmentAdapter shipmentAdapter;
    private ShipmentWrapper shipmentWrapper;

    private Label siteLabel;

    private Label dateDrawnLabel;

    private Label dateReceivedLabel;

    private Label commentLabel;

    private Label clinicLabel;

    @Override
    protected void init() throws Exception {
        Assert.isTrue((adapter instanceof ShipmentAdapter),
            "Invalid editor input: object of type "
                + adapter.getClass().getName());

        shipmentAdapter = (ShipmentAdapter) adapter;
        shipmentWrapper = shipmentAdapter.getWrapper();
        retrieveShipment();

        setPartName("Shipment " + shipmentWrapper.getFormattedDateDrawn());
    }

    private void retrieveShipment() {
        try {
            shipmentWrapper.reload();
        } catch (Exception ex) {
            LOGGER.error("Error while retrieving shipment "
                + shipmentWrapper.getDateDrawn(), ex);
        }
    }

    @Override
    protected void createFormContent() throws Exception {
        form.setText("Shipment Drawn Date: "
            + shipmentWrapper.getFormattedDateDrawn());
        form.getBody().setLayout(new GridLayout(1, false));
        form.getBody().setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        form.setImage(BioBankPlugin.getDefault().getImageRegistry().get(
            BioBankPlugin.IMG_SHIPMENT));
        createMainSection();
        createSourcesSection();
    }

    private void createMainSection() {
        Composite client = toolkit.createComposite(form.getBody());
        GridLayout layout = new GridLayout(2, false);
        layout.horizontalSpacing = 10;
        client.setLayout(layout);
        client.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        toolkit.paintBordersFor(client);

        siteLabel = (Label) createWidget(client, Label.class, SWT.NONE, "Site");

        clinicLabel = (Label) createWidget(client, Label.class, SWT.NONE,
            "Clinic");

        dateDrawnLabel = (Label) createWidget(client, Label.class, SWT.NONE,
            "Date Drawn");

        dateReceivedLabel = (Label) createWidget(client, Label.class, SWT.NONE,
            "Date received");

        commentLabel = (Label) createWidget(client, Label.class, SWT.WRAP,
            "Comments");

        setShipmentValues();

    }

    private void createSourcesSection() {
        Composite client = createSectionWithClient("Source Vessels");
        new ShptSampleSourceInfoTable(client, shipmentWrapper
            .getShptSampleSourceCollection());
    }

    private void setShipmentValues() {
        FormUtils.setTextValue(siteLabel, shipmentWrapper.getClinic().getSite()
            .getName());
        FormUtils.setTextValue(clinicLabel,
            shipmentWrapper.getClinic() == null ? "" : shipmentWrapper
                .getClinic().getName());
        FormUtils.setTextValue(dateDrawnLabel, shipmentWrapper
            .getFormattedDateDrawn());
        FormUtils.setTextValue(dateReceivedLabel, shipmentWrapper
            .getFormattedDateReceived());
        FormUtils.setTextValue(commentLabel, shipmentWrapper.getComment());
    }

    @Override
    protected String getEntryFormId() {
        return ShipmentEntryForm.ID;
    }

    @Override
    protected void reload() throws Exception {
        // TODO Auto-generated method stub

    }

}
