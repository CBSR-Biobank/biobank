package edu.ualberta.med.biobank.forms;

import java.util.List;

import org.acegisecurity.AccessDeniedException;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableContext;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
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
import edu.ualberta.med.biobank.widgets.DispatchAliquotsTreeTable;
import edu.ualberta.med.biobank.widgets.infotables.DispatchAliquotListInfoTable;
import edu.ualberta.med.biobank.widgets.listeners.BiobankEntryFormWidgetListener;
import edu.ualberta.med.biobank.widgets.listeners.MultiSelectEvent;

public class DispatchShipmentViewForm extends BiobankViewForm {

    private static BiobankLogger logger = BiobankLogger
        .getLogger(DispatchShipmentViewForm.class.getName());

    public static final String ID = "edu.ualberta.med.biobank.forms.DispatchShipmentViewForm";

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

    private DispatchAliquotsTreeTable aliquotsTree;

    private DispatchAliquotListInfoTable aliquotsNonProcessedTable;

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
        aliquotsTree.refresh();
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

        createTreeTableSection();

        setShipmentValues();

        User user = SessionManager.getUser();
        SiteWrapper currentSite = SessionManager.getInstance().getCurrentSite();
        if (shipment.canBeSentBy(user, currentSite))
            createSendButton();
        else if (shipment.canBeReceivedBy(user, currentSite))
            createReceiveButtons();
        else if (shipment.canBeClosedBy(user, currentSite))
            createCloseButton();
    }

    private void createTreeTableSection() {
        if (shipment.isInCreationState()) {
            Composite parent = createSectionWithClient("Aliquot added");
            aliquotsNonProcessedTable = new DispatchAliquotListInfoTable(
                parent, shipment, false) {
                @Override
                public List<DispatchShipmentAliquotWrapper> getInternalDispatchShipmentAliquots() {
                    return shipment
                        .getNonProcessedDispatchShipmentAliquotCollection();
                }

            };
            aliquotsNonProcessedTable.adaptToToolkit(toolkit, true);
            aliquotsNonProcessedTable
                .addDoubleClickListener(collectionDoubleClickListener);
            aliquotsNonProcessedTable
                .addSelectionChangedListener(new BiobankEntryFormWidgetListener() {
                    @Override
                    public void selectionChanged(MultiSelectEvent event) {
                        aliquotsNonProcessedTable.reloadCollection();
                    }
                });
        } else {
            aliquotsTree = new DispatchAliquotsTreeTable(page, shipment, false);
        }
    }

    private void createReceiveButtons() {
        Composite composite = toolkit.createComposite(page);
        composite.setLayout(new GridLayout(3, false));
        Button sendButton = toolkit
            .createButton(composite, "Receive", SWT.PUSH);
        sendButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                shipmentAdapter.doReceive();
            }
        });

        Button sendProcessButton = toolkit.createButton(composite,
            "Receive and Process", SWT.PUSH);
        sendProcessButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                shipmentAdapter.doReceiveAndProcess();
            }
        });

        Button lostProcessButton = toolkit.createButton(composite, "Lost",
            SWT.PUSH);
        lostProcessButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                shipmentAdapter.doSetAsLost();
            }
        });
    }

    private void createCloseButton() {
        Composite composite = toolkit.createComposite(page);
        composite.setLayout(new GridLayout(2, false));
        Button sendButton = toolkit.createButton(composite, "Close", SWT.PUSH);
        sendButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                shipmentAdapter.doClose();
            }
        });
    }

    private void createSendButton() {
        final Button sendButton = toolkit.createButton(page, "Send", SWT.PUSH);
        sendButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                if (new SendDispatchShipmentDialog(Display.getDefault()
                    .getActiveShell(), shipment).open() == Dialog.OK) {
                    IRunnableContext context = new ProgressMonitorDialog(
                        Display.getDefault().getActiveShell());
                    try {
                        context.run(true, true, new IRunnableWithProgress() {
                            @Override
                            public void run(final IProgressMonitor monitor) {
                                monitor.beginTask("Saving...",
                                    IProgressMonitor.UNKNOWN);
                                shipment.setNextState();
                                try {
                                    shipment.persist();
                                } catch (final RemoteConnectFailureException exp) {
                                    BioBankPlugin
                                        .openRemoteConnectErrorMessage(exp);
                                    return;
                                } catch (final RemoteAccessException exp) {
                                    BioBankPlugin
                                        .openRemoteAccessErrorMessage(exp);
                                    return;
                                } catch (final AccessDeniedException ade) {
                                    BioBankPlugin
                                        .openAccessDeniedErrorMessage(ade);
                                    return;
                                } catch (Exception ex) {
                                    BioBankPlugin.openAsyncError("Save error",
                                        ex);
                                    return;
                                }
                                monitor.done();
                            }
                        });
                    } catch (Exception e1) {
                        BioBankPlugin.openAsyncError("Save error", e1);
                    }
                    DispatchShipmentAdministrationView.getCurrent().reload();
                    shipmentAdapter.openViewForm();
                }
            }
        });
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
        if (!shipment.isInCreationState()) {
            dateShippedLabel = createReadOnlyLabelledField(client, SWT.NONE,
                "Date Shipped");
            shippingMethodLabel = createReadOnlyLabelledField(client, SWT.NONE,
                "Shipping Method");
            waybillLabel = createReadOnlyLabelledField(client, SWT.NONE,
                "Waybill");
        }
        if (shipment.hasBeenReceived()) {
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
