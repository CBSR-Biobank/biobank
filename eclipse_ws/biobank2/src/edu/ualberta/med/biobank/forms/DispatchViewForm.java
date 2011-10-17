package edu.ualberta.med.biobank.forms;

import java.util.List;

import org.acegisecurity.AccessDeniedException;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableContext;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.springframework.remoting.RemoteAccessException;
import org.springframework.remoting.RemoteConnectFailureException;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.security.User;
import edu.ualberta.med.biobank.common.util.DispatchState;
import edu.ualberta.med.biobank.common.wrappers.DispatchSpecimenWrapper;
import edu.ualberta.med.biobank.common.wrappers.DispatchWrapper;
import edu.ualberta.med.biobank.common.wrappers.ShipmentInfoWrapper;
import edu.ualberta.med.biobank.dialogs.dispatch.SendDispatchDialog;
import edu.ualberta.med.biobank.gui.common.BgcLogger;
import edu.ualberta.med.biobank.gui.common.BgcPlugin;
import edu.ualberta.med.biobank.gui.common.widgets.BgcBaseText;
import edu.ualberta.med.biobank.gui.common.widgets.BgcEntryFormWidgetListener;
import edu.ualberta.med.biobank.gui.common.widgets.MultiSelectEvent;
import edu.ualberta.med.biobank.treeview.dispatch.DispatchAdapter;
import edu.ualberta.med.biobank.views.SpecimenTransitView;
import edu.ualberta.med.biobank.widgets.infotables.DispatchSpecimenListInfoTable;
import edu.ualberta.med.biobank.widgets.infotables.InfoTableSelection;
import edu.ualberta.med.biobank.widgets.trees.DispatchSpecimensTreeTable;

public class DispatchViewForm extends BiobankViewForm {

    private static BgcLogger logger = BgcLogger
        .getLogger(DispatchViewForm.class.getName());

    public static final String ID = "edu.ualberta.med.biobank.forms.DispatchViewForm";

    private DispatchAdapter dispatchAdapter;

    private DispatchWrapper dispatch;

    private BgcBaseText senderLabel;

    private BgcBaseText receiverLabel;

    private BgcBaseText departedLabel;

    private BgcBaseText shippingMethodLabel;

    private BgcBaseText waybillLabel;

    private BgcBaseText dateReceivedLabel;

    private BgcBaseText commentLabel;

    private BgcBaseText shpTempLoggerIDLabel;

    private BgcBaseText shpTempLoggerHiLabel;

    private BgcBaseText shpTempLoggerLowLabel;

    private BgcBaseText shpTempLoggerResultLabel;

    private BgcBaseText shpTempLoggerAboveLabel;

    private BgcBaseText shpTempLoggerBelowLabel;

    private BgcBaseText shpTempLoggerReportabel;

    private DispatchSpecimensTreeTable specimensTree;

    private DispatchSpecimenListInfoTable specimensNonProcessedTable;

    private boolean canSeeEverything;

    @Override
    protected void init() throws Exception {
        Assert.isTrue((adapter instanceof DispatchAdapter),
            "Invalid editor input: object of type "
                + adapter.getClass().getName());

        dispatchAdapter = (DispatchAdapter) adapter;
        dispatch = (DispatchWrapper) adapter.getModelObject();
        SessionManager.logLookup(dispatch);
        retrieveDispatch();
        setPartName("Dispatch");
    }

    private void retrieveDispatch() {
        try {
            dispatch.reload();
        } catch (Exception ex) {
            logger.error("Error while retrieving shipment "
                + dispatch.getShipmentInfo().getWaybill(), ex);
        }
    }

    @Override
    public void reload() throws Exception {
        retrieveDispatch();
        setPartName("Dispatch sent on "
            + dispatch.getShipmentInfo().getPackedAt());
        setDispatchValues();
        specimensTree.refresh();
    }

    @Override
    protected void createFormContent() throws Exception {
        String dateString = "";
        if (dispatch.getShipmentInfo() != null
            && dispatch.getShipmentInfo().getPackedAt() != null) {
            dateString = " on " + dispatch.getFormattedPackedAt();
        }
        canSeeEverything = true;
        if (dispatch.getSenderCenter() == null) {
            canSeeEverything = false;
            BgcPlugin
                .openAsyncError(
                    "Access Denied",
                    "It seems you don't have access to the sender site. Please see administrator to resolve this problem.");
        } else {
            form.setText("Dispatch sent" + dateString + " from "
                + dispatch.getSenderCenter().getNameShort());
        }
        if (dispatch.getReceiverCenter() == null) {
            canSeeEverything = false;
            BgcPlugin
                .openAsyncError(
                    "Access Denied",
                    "It seems you don't have access to the receiver site. Please see administrator to resolve this problem.");
        }
        page.setLayout(new GridLayout(1, false));
        page.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        createMainSection();

        if (canSeeEverything) {
            createTreeTableSection();
        }

        setDispatchValues();

        if (canSeeEverything) {
            User user = SessionManager.getUser();
            if (dispatch.canBeSentBy(user))
                createSendButton();
            else if (dispatch.canBeReceivedBy(user))
                createReceiveButtons();
            else if (dispatch.canBeClosedBy(user)
                && dispatch.isInReceivedState()
                && dispatch.getNonProcessedDispatchSpecimenCollection().size() == 0)
                createCloseButton();
        }
    }

    @Override
    protected void addEditAction() {
        if (canSeeEverything) {
            super.addEditAction();
        }
    }

    private void createTreeTableSection() {
        if (dispatch.isInCreationState()) {
            Composite parent = createSectionWithClient("Specimen added");
            specimensNonProcessedTable = new DispatchSpecimenListInfoTable(
                parent, dispatch, false) {
                @Override
                public List<DispatchSpecimenWrapper> getInternalDispatchSpecimens() {
                    return dispatch.getNonProcessedDispatchSpecimenCollection();
                }

            };
            specimensNonProcessedTable.adaptToToolkit(toolkit, true);
            specimensNonProcessedTable
                .addClickListener(new IDoubleClickListener() {
                    @Override
                    public void doubleClick(DoubleClickEvent event) {
                        Object selection = event.getSelection();
                        if (selection instanceof InfoTableSelection) {
                            InfoTableSelection tableSelection = (InfoTableSelection) selection;
                            DispatchSpecimenWrapper dsa = (DispatchSpecimenWrapper) tableSelection
                                .getObject();
                            if (dsa != null) {
                                SessionManager.openViewForm(dsa.getSpecimen());
                            }
                        }
                    }
                });
            specimensNonProcessedTable
                .addSelectionChangedListener(new BgcEntryFormWidgetListener() {
                    @Override
                    public void selectionChanged(MultiSelectEvent event) {
                        specimensNonProcessedTable.reloadCollection();
                    }
                });
        } else {
            specimensTree = new DispatchSpecimensTreeTable(page, dispatch,
                false, false);
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
                dispatchAdapter.doReceive();
            }
        });

        Button sendProcessButton = toolkit.createButton(composite,
            "Receive and Process", SWT.PUSH);
        sendProcessButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                dispatchAdapter.doReceiveAndProcess();
            }
        });

        Button lostProcessButton = toolkit.createButton(composite, "Lost",
            SWT.PUSH);
        lostProcessButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                dispatchAdapter.doSetAsLost();
            }
        });
    }

    private void createCloseButton() {
        Composite composite = toolkit.createComposite(page);
        composite.setLayout(new GridLayout(2, false));
        Button sendButton = toolkit.createButton(composite, "Done", SWT.PUSH);
        sendButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                dispatchAdapter.doClose();
            }
        });
    }

    private void createSendButton() {
        final Button sendButton = toolkit.createButton(page, "Send", SWT.PUSH);
        sendButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                if (new SendDispatchDialog(Display.getDefault()
                    .getActiveShell(), dispatch).open() == Dialog.OK) {
                    IRunnableContext context = new ProgressMonitorDialog(
                        Display.getDefault().getActiveShell());
                    try {
                        context.run(true, true, new IRunnableWithProgress() {
                            @Override
                            public void run(final IProgressMonitor monitor) {
                                monitor.beginTask("Saving...",
                                    IProgressMonitor.UNKNOWN);
                                dispatch.setState(DispatchState.IN_TRANSIT);

                                try {
                                    // Make sure no record created if no device
                                    // is entered
                                    if (dispatch.getShipmentInfo()
                                        .getShipmentTempLogger().getDeviceId() == null
                                        || dispatch.getShipmentInfo()
                                            .getShipmentTempLogger()
                                            .getDeviceId().isEmpty()) {
                                        dispatch.getShipmentInfo()
                                            .setShipmentTempLogger(null);
                                    }
                                    dispatch.persist();
                                } catch (final RemoteConnectFailureException exp) {
                                    BgcPlugin
                                        .openRemoteConnectErrorMessage(exp);
                                    return;
                                } catch (final RemoteAccessException exp) {
                                    BgcPlugin.openRemoteAccessErrorMessage(exp);
                                    return;
                                } catch (final AccessDeniedException ade) {
                                    BgcPlugin.openAccessDeniedErrorMessage(ade);
                                    return;
                                } catch (Exception ex) {
                                    BgcPlugin.openAsyncError("Save error", ex);
                                    return;
                                }
                                monitor.done();
                            }
                        });
                    } catch (Exception e1) {
                        BgcPlugin.openAsyncError("Save error", e1);
                    }
                    SpecimenTransitView.getCurrent().reload();
                    dispatchAdapter.openViewForm();
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

        String stateMessage = null;
        if (dispatch.isInLostState())
            stateMessage = " Dispatch Lost ";
        else if (dispatch.isInClosedState())
            stateMessage = " Dispatch Complete ";
        if (stateMessage != null) {
            Label label = widgetCreator.createLabel(client, stateMessage,
                SWT.CENTER, false);
            label.setBackground(Display.getDefault().getSystemColor(
                SWT.COLOR_RED));
            label.setForeground(Display.getDefault().getSystemColor(
                SWT.COLOR_WHITE));
            GridData gd = new GridData();
            gd.horizontalAlignment = SWT.CENTER;
            gd.grabExcessHorizontalSpace = true;
            gd.horizontalSpan = 2;
            label.setLayoutData(gd);
        }

        senderLabel = createReadOnlyLabelledField(client, SWT.NONE, "Sender");
        receiverLabel = createReadOnlyLabelledField(client, SWT.NONE,
            "Receiver");

        if (!dispatch.isInCreationState()) {
            departedLabel = createReadOnlyLabelledField(client, SWT.NONE,
                "Packed at");
            shippingMethodLabel = createReadOnlyLabelledField(client, SWT.NONE,
                "Shipping Method");
            waybillLabel = createReadOnlyLabelledField(client, SWT.NONE,
                "Waybill");
            if (dispatch.getShipmentInfo().getShipmentTempLogger() != null
                && dispatch.getShipmentInfo().getShipmentTempLogger()
                    .getDeviceId() != null) {
                shpTempLoggerIDLabel = createReadOnlyLabelledField(client,
                    SWT.NONE, "Logger Device ID");
                if (!dispatch.isInTransitState()) {
                    shpTempLoggerHiLabel = createReadOnlyLabelledField(client,
                        SWT.NONE,
                        "Highest temperature during transport (Celcius)");
                    shpTempLoggerLowLabel = createReadOnlyLabelledField(client,
                        SWT.NONE,
                        "Lowest temperature during transport (Celcius)");
                    shpTempLoggerResultLabel = createReadOnlyLabelledField(
                        client, SWT.NONE, "Shipment temperature result");
                    shpTempLoggerAboveLabel = createReadOnlyLabelledField(
                        client, SWT.NONE,
                        "Number of minutes above maximum threshold");
                    shpTempLoggerBelowLabel = createReadOnlyLabelledField(
                        client, SWT.NONE,
                        "Number of minutes below maximum threshold");
                    shpTempLoggerReportabel = createReadOnlyLabelledField(
                        client, SWT.NONE, "Tempurature logger report");
                }

            }

        }

        if (dispatch.hasBeenReceived()) {
            dateReceivedLabel = createReadOnlyLabelledField(client, SWT.NONE,
                "Date received");
        }
        commentLabel = createReadOnlyLabelledField(client, SWT.MULTI,
            "Comments");
    }

    private void setDispatchValues() {
        setTextValue(senderLabel,
            dispatch.getSenderCenter() == null ? " ACCESS DENIED" : dispatch
                .getSenderCenter().getName());
        setTextValue(receiverLabel,
            dispatch.getReceiverCenter() == null ? "ACCESS DENIED" : dispatch
                .getReceiverCenter().getName());
        if (departedLabel != null)
            setTextValue(departedLabel, dispatch.getFormattedPackedAt());

        ShipmentInfoWrapper shipInfo = dispatch.getShipmentInfo();

        if (shipInfo != null) {
            if (shippingMethodLabel != null)
                setTextValue(shippingMethodLabel,
                    shipInfo.getShippingMethod() == null ? "" : shipInfo
                        .getShippingMethod().getName());
            if (waybillLabel != null)
                setTextValue(waybillLabel, shipInfo.getWaybill());
            if (dateReceivedLabel != null)
                setTextValue(dateReceivedLabel,
                    shipInfo.getFormattedDateReceived());

            if (shipInfo.getShipmentTempLogger() != null
                && shipInfo.getShipmentTempLogger().getDeviceId() != null) {
                if (shpTempLoggerIDLabel != null)
                    setTextValue(shpTempLoggerIDLabel, shipInfo
                        .getShipmentTempLogger().getDeviceId());

                if (!dispatch.isInTransitState()) {
                    if (shpTempLoggerHiLabel != null)
                        setTextValue(shpTempLoggerHiLabel, shipInfo
                            .getShipmentTempLogger().getHighTemperature());

                    if (shpTempLoggerLowLabel != null)
                        setTextValue(shpTempLoggerLowLabel, shipInfo
                            .getShipmentTempLogger().getLowTemperature());

                    if (shpTempLoggerResultLabel != null) {
                        if (shipInfo.getShipmentTempLogger()
                            .getTemperatureResult() != null) {
                            if (shipInfo.getShipmentTempLogger()
                                .getTemperatureResult()) {
                                setTextValue(shpTempLoggerResultLabel, "Pass");
                            } else {
                                setTextValue(shpTempLoggerResultLabel, "Fail");
                            }
                        }
                    }
                    if (shpTempLoggerAboveLabel != null)
                        setTextValue(shpTempLoggerAboveLabel, shipInfo
                            .getShipmentTempLogger().getMinutesAboveMax());

                    if (shpTempLoggerBelowLabel != null)
                        setTextValue(shpTempLoggerBelowLabel, shipInfo
                            .getShipmentTempLogger().getMinutesBelowMax());

                    if (shpTempLoggerReportabel != null)
                        setTextValue(shpTempLoggerReportabel, shipInfo
                            .getShipmentTempLogger().getReport());
                }
            }

        }
        setTextValue(commentLabel, dispatch.getComment());

    }

}
