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

import edu.ualberta.med.biobank.BiobankPlugin;
import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.security.User;
import edu.ualberta.med.biobank.common.util.DispatchState;
import edu.ualberta.med.biobank.common.wrappers.DispatchSpecimenWrapper;
import edu.ualberta.med.biobank.common.wrappers.DispatchWrapper;
import edu.ualberta.med.biobank.common.wrappers.ShipmentInfoWrapper;
import edu.ualberta.med.biobank.dialogs.dispatch.SendDispatchDialog;
import edu.ualberta.med.biobank.logs.BiobankLogger;
import edu.ualberta.med.biobank.treeview.dispatch.DispatchAdapter;
import edu.ualberta.med.biobank.views.SpecimenTransitView;
import edu.ualberta.med.biobank.widgets.BiobankText;
import edu.ualberta.med.biobank.widgets.infotables.DispatchSpecimenListInfoTable;
import edu.ualberta.med.biobank.widgets.infotables.InfoTableSelection;
import edu.ualberta.med.biobank.widgets.listeners.BiobankEntryFormWidgetListener;
import edu.ualberta.med.biobank.widgets.listeners.MultiSelectEvent;
import edu.ualberta.med.biobank.widgets.trees.DispatchSpecimensTreeTable;

public class DispatchViewForm extends BiobankViewForm {

    private static BiobankLogger logger = BiobankLogger
        .getLogger(DispatchViewForm.class.getName());

    public static final String ID = "edu.ualberta.med.biobank.forms.DispatchViewForm";

    private DispatchAdapter dispatchAdapter;

    private DispatchWrapper dispatch;

    private BiobankText senderLabel;

    private BiobankText receiverLabel;

    private BiobankText departedLabel;

    private BiobankText shippingMethodLabel;

    private BiobankText waybillLabel;

    private BiobankText dateReceivedLabel;

    private BiobankText commentLabel;

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
            BiobankPlugin
                .openAsyncError(
                    "Access Denied",
                    "It seems you don't have access to the sender site. Please see administrator to resolve this problem.");
        } else {
            form.setText("Dispatch sent" + dateString + " from "
                + dispatch.getSenderCenter().getNameShort());
        }
        if (dispatch.getReceiverCenter() == null) {
            canSeeEverything = false;
            BiobankPlugin
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
                .addSelectionChangedListener(new BiobankEntryFormWidgetListener() {
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
                                    dispatch.persist();
                                } catch (final RemoteConnectFailureException exp) {
                                    BiobankPlugin
                                        .openRemoteConnectErrorMessage(exp);
                                    return;
                                } catch (final RemoteAccessException exp) {
                                    BiobankPlugin
                                        .openRemoteAccessErrorMessage(exp);
                                    return;
                                } catch (final AccessDeniedException ade) {
                                    BiobankPlugin
                                        .openAccessDeniedErrorMessage(ade);
                                    return;
                                } catch (Exception ex) {
                                    BiobankPlugin.openAsyncError("Save error",
                                        ex);
                                    return;
                                }
                                monitor.done();
                            }
                        });
                    } catch (Exception e1) {
                        BiobankPlugin.openAsyncError("Save error", e1);
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
        }
        setTextValue(commentLabel, dispatch.getComment());
    }

}
