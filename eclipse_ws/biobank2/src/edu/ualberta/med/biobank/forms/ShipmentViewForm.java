package edu.ualberta.med.biobank.forms;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.Assert;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;

import edu.ualberta.med.biobank.BioBankPlugin;
import edu.ualberta.med.biobank.common.wrappers.ShipmentWrapper;
import edu.ualberta.med.biobank.treeview.ShipmentAdapter;

public class ShipmentViewForm extends BiobankViewForm {

    private static Logger LOGGER = Logger.getLogger(ShipmentViewForm.class
        .getName());

    public static final String ID = "edu.ualberta.med.biobank.forms.ShipmentViewForm";
    private ShipmentAdapter shipmentAdapter;
    private ShipmentWrapper shipmentWrapper;

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
