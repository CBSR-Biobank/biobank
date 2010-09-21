package edu.ualberta.med.biobank.forms;

import org.eclipse.core.runtime.Assert;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.PlatformUI;

import edu.ualberta.med.biobank.common.wrappers.DispatchShipmentWrapper;
import edu.ualberta.med.biobank.dialogs.DispatchReceiveScanDialog;
import edu.ualberta.med.biobank.logs.BiobankLogger;
import edu.ualberta.med.biobank.treeview.DispatchShipmentAdapter;
import edu.ualberta.med.biobank.widgets.BiobankText;
import edu.ualberta.med.biobank.widgets.infotables.DispatchAliquotListInfoTable;

public class DispatchShipmentReceivingEntryForm extends BiobankEntryForm {

    private static BiobankLogger logger = BiobankLogger
        .getLogger(DispatchShipmentReceivingEntryForm.class.getName());

    public static final String ID = "edu.ualberta.med.biobank.forms.DispatchShipmentReceivingEntryForm";

    @SuppressWarnings("unused")
    private DispatchShipmentAdapter shipmentAdapter;

    private DispatchShipmentWrapper shipment;

    private BiobankText studyLabel;

    private BiobankText senderLabel;

    private BiobankText receiverLabel;

    private BiobankText dateShippedLabel;

    private BiobankText shippingMethodLabel;

    private BiobankText waybillLabel;

    private BiobankText dateReceivedLabel;

    private BiobankText commentLabel;

    private BiobankText statusLabel;

    private DispatchAliquotListInfoTable aliquotsToBeReceivedTable;

    private DispatchAliquotListInfoTable aliquotsReceivedTable;

    @Override
    protected void init() throws Exception {
        Assert.isNotNull(adapter, "Adapter should be no null");
        Assert.isTrue((adapter instanceof DispatchShipmentAdapter),
            "Invalid editor input: object of type "
                + adapter.getClass().getName());

        shipmentAdapter = (DispatchShipmentAdapter) adapter;
        shipment = (DispatchShipmentWrapper) adapter.getModelObject();
        retrieveShipment();
        setPartName("Dispatch Shipment sent on " + shipment.getDateShipped());
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
        form.setText("Shipment sent on " + shipment.getFormattedDateShipped()
            + " from " + shipment.getSender().getNameShort());
        page.setLayout(new GridLayout(1, false));
        page.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        createMainSection();
        createAliquotsNotReceivedSection();
        createAliquotsReceivedSection();

        Button palletButton = toolkit.createButton(page, "Scan pallet",
            SWT.PUSH);
        palletButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                new DispatchReceiveScanDialog(PlatformUI.getWorkbench()
                    .getActiveWorkbenchWindow().getShell(), shipment).open();
                setDirty(true); // FIXME need to do this better !
                reloadAliquotsTables();
            }
        });
        setFirstControl(palletButton);
        toolkit.createButton(page, "Scan individual aliquots", SWT.PUSH);
    }

    private void createAliquotsNotReceivedSection() {
        Composite parent = createSectionWithClient("Aliquots not yet "
            + "received");
        aliquotsToBeReceivedTable = new DispatchAliquotListInfoTable(parent,
            shipment, shipment.getNotReceivedAliquots(), false);
        aliquotsToBeReceivedTable.adaptToToolkit(toolkit, true);
        aliquotsToBeReceivedTable
            .addDoubleClickListener(collectionDoubleClickListener);
    }

    private void createAliquotsReceivedSection() {
        Composite parent = createSectionWithClient("Aliquots received");
        aliquotsReceivedTable = new DispatchAliquotListInfoTable(parent,
            shipment, shipment.getReceivedAliquots(), false);
        aliquotsReceivedTable.adaptToToolkit(toolkit, true);
        aliquotsReceivedTable
            .addDoubleClickListener(collectionDoubleClickListener);
    }

    private void createMainSection() {
        Composite client = toolkit.createComposite(page);
        GridLayout layout = new GridLayout(2, false);
        layout.horizontalSpacing = 10;
        client.setLayout(layout);
        client.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        toolkit.paintBordersFor(client);

        studyLabel = createReadOnlyLabelledField(client, SWT.NONE, "Study");
        senderLabel = createReadOnlyLabelledField(client, SWT.NONE, "Sender");
        receiverLabel = createReadOnlyLabelledField(client, SWT.NONE,
            "Receiver");
        dateShippedLabel = createReadOnlyLabelledField(client, SWT.NONE,
            "Date Shipped");
        shippingMethodLabel = createReadOnlyLabelledField(client, SWT.NONE,
            "Shipping Method");
        waybillLabel = createReadOnlyLabelledField(client, SWT.NONE, "Waybill");
        dateReceivedLabel = createReadOnlyLabelledField(client, SWT.NONE,
            "Date received");
        statusLabel = createReadOnlyLabelledField(client, SWT.NONE, "Status");
        commentLabel = createReadOnlyLabelledField(client, SWT.MULTI,
            "Comments");

        setShipmentValues();
    }

    private void setShipmentValues() {
        setTextValue(studyLabel, shipment.getStudy().getName());
        setTextValue(senderLabel, shipment.getSender().getName());
        setTextValue(receiverLabel, shipment.getReceiver().getName());
        setTextValue(dateShippedLabel, shipment.getFormattedDateShipped());
        setTextValue(shippingMethodLabel,
            shipment.getShippingMethod() == null ? "" : shipment
                .getShippingMethod().getName());
        setTextValue(waybillLabel, shipment.getWaybill());
        setTextValue(dateReceivedLabel, shipment.getFormattedDateReceived());
        setTextValue(statusLabel, shipment.getActivityStatus().getName());
        setTextValue(commentLabel, shipment.getComment());
    }

    @Override
    protected void saveForm() throws Exception {
        shipment.persist();
    }

    @Override
    protected String getOkMessage() {
        return "Receiving dispatch shipment";
    }

    @Override
    public String getNextOpenedFormID() {
        return DispatchShipmentViewForm.ID;
    }

    private void reloadAliquotsTables() {
        aliquotsToBeReceivedTable.reloadCollection(shipment
            .getNotReceivedAliquots());
        aliquotsReceivedTable.reloadCollection(shipment.getReceivedAliquots());
    }
}
