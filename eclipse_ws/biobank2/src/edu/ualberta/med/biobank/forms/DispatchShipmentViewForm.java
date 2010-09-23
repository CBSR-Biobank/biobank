package edu.ualberta.med.biobank.forms;

import org.acegisecurity.AccessDeniedException;
import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.PlatformUI;
import org.springframework.remoting.RemoteAccessException;
import org.springframework.remoting.RemoteConnectFailureException;

import edu.ualberta.med.biobank.BioBankPlugin;
import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.wrappers.DispatchShipmentWrapper;
import edu.ualberta.med.biobank.dialogs.dispatch.SendDispatchShipmentDialog;
import edu.ualberta.med.biobank.logs.BiobankLogger;
import edu.ualberta.med.biobank.treeview.dispatch.DispatchShipmentAdapter;
import edu.ualberta.med.biobank.widgets.BiobankText;
import edu.ualberta.med.biobank.widgets.infotables.DispatchAliquotListInfoTable;

public class DispatchShipmentViewForm extends BiobankViewForm {

    private static BiobankLogger logger = BiobankLogger
        .getLogger(DispatchShipmentViewForm.class.getName());

    public static final String ID = "edu.ualberta.med.biobank.forms.DispatchShipmentViewForm";

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

    private DispatchAliquotListInfoTable aliquotsToBeReceivedTable;

    private DispatchAliquotListInfoTable aliquotsReceivedTable;

    @Override
    protected void init() throws Exception {
        Assert.isTrue((adapter instanceof DispatchShipmentAdapter),
            "Invalid editor input: object of type "
                + adapter.getClass().getName());

        shipmentAdapter = (DispatchShipmentAdapter) adapter;
        shipment = (DispatchShipmentWrapper) adapter.getModelObject();
        retrieveShipment();
        setPartName("Dispatch Shipment");
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
    public void reload() throws Exception {
        retrieveShipment();
        setPartName("Dispatch Shipment sent on " + shipment.getDateShipped());
        setShipmentValues();
    }

    @Override
    protected void createFormContent() throws Exception {
        String dateString = "";
        if (shipment.getDateShipped() != null) {
            dateString = " on " + shipment.getFormattedDateShipped();
        }
        form.setText("Shipment sent" + dateString + " from "
            + shipment.getSender().getNameShort());
        page.setLayout(new GridLayout(1, false));
        page.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        createMainSection();
        createAliquotsNotReceivedSection();
        createAliquotsReceivedSection();
        setShipmentValues();

        if (shipment.canBeSentBy(SessionManager.getInstance().getCurrentSite())) {
            Button sendButton = toolkit.createButton(page, "Send", SWT.PUSH);
            sendButton.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent e) {
                    if (new SendDispatchShipmentDialog(PlatformUI
                        .getWorkbench().getActiveWorkbenchWindow().getShell(),
                        shipment).open() == Dialog.OK) {
                        shipment.setNextState();

                        try {
                            shipment.persist();
                        } catch (final RemoteConnectFailureException exp) {
                            BioBankPlugin.openRemoteConnectErrorMessage(exp);
                        } catch (final RemoteAccessException exp) {
                            BioBankPlugin.openRemoteAccessErrorMessage(exp);
                        } catch (final AccessDeniedException ade) {
                            BioBankPlugin.openAccessDeniedErrorMessage(ade);
                        } catch (Exception ex) {
                            BioBankPlugin.openAsyncError("Save error", ex);
                        }
                    }
                }
            });
        }
    }

    private void createAliquotsNotReceivedSection() {
        String title = "";
        if (shipment.isInCreation()) {
            title = "Aliquots added";
        } else {
            title = "Aliquots to be received";
        }
        Composite parent = createSectionWithClient(title);
        aliquotsToBeReceivedTable = new DispatchAliquotListInfoTable(parent,
            shipment, null, false);
        aliquotsToBeReceivedTable.adaptToToolkit(toolkit, true);
        aliquotsToBeReceivedTable
            .addDoubleClickListener(collectionDoubleClickListener);
    }

    private void createAliquotsReceivedSection() {
        if (!shipment.isInCreation()) {
            Composite parent = createSectionWithClient("Aliquots received");
            aliquotsReceivedTable = new DispatchAliquotListInfoTable(parent,
                shipment, null, false);
            aliquotsReceivedTable.adaptToToolkit(toolkit, true);
            aliquotsReceivedTable
                .addDoubleClickListener(collectionDoubleClickListener);
        }
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
        if (!shipment.isInCreation()) {
            dateShippedLabel = createReadOnlyLabelledField(client, SWT.NONE,
                "Date Shipped");
            shippingMethodLabel = createReadOnlyLabelledField(client, SWT.NONE,
                "Shipping Method");
            waybillLabel = createReadOnlyLabelledField(client, SWT.NONE,
                "Waybill");
            dateReceivedLabel = createReadOnlyLabelledField(client, SWT.NONE,
                "Date received");
        }
        commentLabel = createReadOnlyLabelledField(client, SWT.MULTI,
            "Comments");
    }

    private void setShipmentValues() {
        setTextValue(studyLabel, shipment.getStudy().getName());
        setTextValue(senderLabel, shipment.getSender().getName());
        setTextValue(receiverLabel, shipment.getReceiver().getName());
        if (!shipment.isInCreation()) {
            setTextValue(dateShippedLabel, shipment.getFormattedDateShipped());
            setTextValue(shippingMethodLabel,
                shipment.getShippingMethod() == null ? "" : shipment
                    .getShippingMethod().getName());
            setTextValue(waybillLabel, shipment.getWaybill());
            setTextValue(dateReceivedLabel, shipment.getFormattedDateReceived());
        }
        setTextValue(commentLabel, shipment.getComment());
        aliquotsToBeReceivedTable.reloadCollection(shipment
            .getNotReceivedAliquots());
        if (aliquotsReceivedTable != null) {
            aliquotsReceivedTable.reloadCollection(shipment
                .getReceivedAliquots());
        }
    }
}
