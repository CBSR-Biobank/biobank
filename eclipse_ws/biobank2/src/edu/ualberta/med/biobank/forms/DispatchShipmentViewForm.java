package edu.ualberta.med.biobank.forms;

import java.util.List;

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
import edu.ualberta.med.biobank.common.security.User;
import edu.ualberta.med.biobank.common.wrappers.DispatchShipmentAliquotWrapper;
import edu.ualberta.med.biobank.common.wrappers.DispatchShipmentWrapper;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;
import edu.ualberta.med.biobank.dialogs.dispatch.SendDispatchShipmentDialog;
import edu.ualberta.med.biobank.logs.BiobankLogger;
import edu.ualberta.med.biobank.treeview.dispatch.DispatchShipmentAdapter;
import edu.ualberta.med.biobank.views.DispatchShipmentAdministrationView;
import edu.ualberta.med.biobank.widgets.BiobankText;
import edu.ualberta.med.biobank.widgets.infotables.DispatchAliquotListInfoTable;

public class DispatchShipmentViewForm extends BiobankViewForm {

    private static BiobankLogger logger = BiobankLogger
        .getLogger(DispatchShipmentViewForm.class.getName());

    public static final String ID =
        "edu.ualberta.med.biobank.forms.DispatchShipmentViewForm";

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

    private DispatchAliquotListInfoTable aliquotsExpectedTable;

    private DispatchAliquotListInfoTable aliquotsAcceptedTable;

    private DispatchAliquotListInfoTable aliquotsExtraTable;

    private DispatchAliquotListInfoTable aliquotsMissingTable;

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
        aliquotsExpectedTable.reloadCollection();
        if (aliquotsAcceptedTable != null)
            aliquotsAcceptedTable.reloadCollection();
        if (aliquotsExtraTable != null)
            aliquotsExtraTable.reloadCollection();
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
        createAliquotsExtraSection();
        createAliquotsMissingSection();
        setShipmentValues();

        User user = SessionManager.getUser();
        SiteWrapper currentSite = SessionManager.getInstance().getCurrentSite();
        if (shipment.canBeSentBy(user, currentSite))
            createSendButton();
        else if (shipment.canBeReceivedBy(user, currentSite))
            createReceiveButton();
    }

    private void createReceiveButton() {
        Composite composite = toolkit.createComposite(page);
        composite.setLayout(new GridLayout(2, false));
        Button sendButton =
            toolkit.createButton(composite, "Receive", SWT.PUSH);
        sendButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                shipmentAdapter.doReceive();
            }
        });

        Button sendProcessButton =
            toolkit.createButton(composite, "Receive and Process", SWT.PUSH);
        sendProcessButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                shipmentAdapter.doReceiveAndProcess();
            }
        });
    }

    private void createSendButton() {
        final Button sendButton = toolkit.createButton(page, "Send", SWT.PUSH);
        sendButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                if (new SendDispatchShipmentDialog(PlatformUI.getWorkbench()
                    .getActiveWorkbenchWindow().getShell(), shipment).open() == Dialog.OK) {
                    shipment.setNextState();
                    try {
                        shipment.persist();
                    } catch (final RemoteConnectFailureException exp) {
                        BioBankPlugin.openRemoteConnectErrorMessage(exp);
                        return;
                    } catch (final RemoteAccessException exp) {
                        BioBankPlugin.openRemoteAccessErrorMessage(exp);
                        return;
                    } catch (final AccessDeniedException ade) {
                        BioBankPlugin.openAccessDeniedErrorMessage(ade);
                        return;
                    } catch (Exception ex) {
                        BioBankPlugin.openAsyncError("Save error", ex);
                        return;
                    }
                    DispatchShipmentAdministrationView.getCurrent().reload();
                    shipmentAdapter.openViewForm();
                }
            }
        });
    }

    private void createAliquotsNotReceivedSection() {
        String title = "";
        if (shipment.isInCreationState()) {
            title = "Aliquots added";
        } else {
            title = "Non processed aliquots";
        }
        Composite parent = createSectionWithClient(title);
        aliquotsExpectedTable =
            new DispatchAliquotListInfoTable(parent, shipment, false) {
                @Override
                public List<DispatchShipmentAliquotWrapper> getInternalDispatchShipmentAliquots() {
                    return shipment
                        .getNonProcessedDispatchShipmentAliquotCollection();
                }
            };
        aliquotsExpectedTable.adaptToToolkit(toolkit, true);
        aliquotsExpectedTable
            .addDoubleClickListener(collectionDoubleClickListener);
    }

    private void createAliquotsReceivedSection() {
        if (shipment.hasBeenReceived()) {
            Composite parent = createSectionWithClient("Aliquots received");
            aliquotsAcceptedTable =
                new DispatchAliquotListInfoTable(parent, shipment, false) {
                    @Override
                    public List<DispatchShipmentAliquotWrapper> getInternalDispatchShipmentAliquots() {
                        return shipment.getReceivedDispatchShipmentAliquots();
                    }
                };
            aliquotsAcceptedTable.adaptToToolkit(toolkit, true);
            aliquotsAcceptedTable
                .addDoubleClickListener(collectionDoubleClickListener);
        }
    }

    private void createAliquotsExtraSection() {
        if (shipment.hasBeenReceived()) {
            Composite parent = createSectionWithClient("Extra aliquots");
            aliquotsExtraTable =
                new DispatchAliquotListInfoTable(parent, shipment, false) {
                    @Override
                    public List<DispatchShipmentAliquotWrapper> getInternalDispatchShipmentAliquots() {
                        return shipment.getExtraDispatchShipmentAliquots();
                    }
                };
            aliquotsExtraTable.adaptToToolkit(toolkit, true);
            aliquotsExtraTable
                .addDoubleClickListener(collectionDoubleClickListener);
        }
    }

    private void createAliquotsMissingSection() {
        if (shipment.hasBeenReceived()) {
            Composite parent = createSectionWithClient("Missing aliquots");
            aliquotsMissingTable =
                new DispatchAliquotListInfoTable(parent, shipment, false) {
                    @Override
                    public List<DispatchShipmentAliquotWrapper> getInternalDispatchShipmentAliquots() {
                        return shipment.getMissingDispatchShipmentAliquots();
                    }
                };
            aliquotsMissingTable.adaptToToolkit(toolkit, true);
            aliquotsMissingTable
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
        receiverLabel =
            createReadOnlyLabelledField(client, SWT.NONE, "Receiver");
        if (!shipment.isInCreationState()) {
            dateShippedLabel =
                createReadOnlyLabelledField(client, SWT.NONE, "Date Shipped");
            shippingMethodLabel =
                createReadOnlyLabelledField(client, SWT.NONE, "Shipping Method");
            waybillLabel =
                createReadOnlyLabelledField(client, SWT.NONE, "Waybill");
        }
        if (shipment.hasBeenReceived()) {
            dateReceivedLabel =
                createReadOnlyLabelledField(client, SWT.NONE, "Date received");
        }
        commentLabel =
            createReadOnlyLabelledField(client, SWT.MULTI, "Comments");
    }

    private void setShipmentValues() {
        setTextValue(studyLabel, shipment.getStudy().getName());
        setTextValue(senderLabel, shipment.getSender().getName());
        setTextValue(receiverLabel, shipment.getReceiver().getName());
        if (dateShippedLabel != null)
            setTextValue(dateShippedLabel, shipment.getFormattedDateShipped());
        if (shippingMethodLabel != null)
            setTextValue(shippingMethodLabel,
                shipment.getShippingMethod() == null ? "" : shipment
                    .getShippingMethod().getName());
        if (waybillLabel != null)
            setTextValue(waybillLabel, shipment.getWaybill());
        if (dateReceivedLabel != null)
            setTextValue(dateReceivedLabel, shipment.getFormattedDateReceived());
        setTextValue(commentLabel, shipment.getComment());
    }

}
