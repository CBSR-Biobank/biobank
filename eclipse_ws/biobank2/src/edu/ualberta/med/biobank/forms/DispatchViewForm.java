package edu.ualberta.med.biobank.forms;

import java.util.List;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableContext;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.action.dispatch.DispatchGetInfoAction;
import edu.ualberta.med.biobank.common.action.info.DispatchReadInfo;
import edu.ualberta.med.biobank.common.permission.dispatch.DispatchChangeStatePermission;
import edu.ualberta.med.biobank.common.wrappers.DispatchSpecimenWrapper;
import edu.ualberta.med.biobank.common.wrappers.DispatchWrapper;
import edu.ualberta.med.biobank.common.wrappers.ShipmentInfoWrapper;
import edu.ualberta.med.biobank.dialogs.dispatch.SendDispatchDialog;
import edu.ualberta.med.biobank.gui.common.BgcLogger;
import edu.ualberta.med.biobank.gui.common.BgcPlugin;
import edu.ualberta.med.biobank.gui.common.widgets.BgcBaseText;
import edu.ualberta.med.biobank.gui.common.widgets.BgcEntryFormWidgetListener;
import edu.ualberta.med.biobank.gui.common.widgets.InfoTableSelection;
import edu.ualberta.med.biobank.gui.common.widgets.MultiSelectEvent;
import edu.ualberta.med.biobank.treeview.dispatch.DispatchAdapter;
import edu.ualberta.med.biobank.views.SpecimenTransitView;
import edu.ualberta.med.biobank.widgets.infotables.CommentCollectionInfoTable;
import edu.ualberta.med.biobank.widgets.infotables.DispatchSpecimenListInfoTable;
import edu.ualberta.med.biobank.widgets.trees.DispatchSpecimensTreeTable;

public class DispatchViewForm extends BiobankViewForm {

    private static BgcLogger logger = BgcLogger
        .getLogger(DispatchViewForm.class.getName());

    public static final String ID =
        "edu.ualberta.med.biobank.forms.DispatchViewForm"; //$NON-NLS-1$

    private DispatchAdapter dispatchAdapter;

    private DispatchWrapper dispatch;

    private BgcBaseText senderLabel;

    private BgcBaseText receiverLabel;

    private BgcBaseText departedLabel;

    private BgcBaseText shippingMethodLabel;

    private BgcBaseText waybillLabel;

    private BgcBaseText dateReceivedLabel;

    private DispatchSpecimensTreeTable specimensTree;

    private DispatchSpecimenListInfoTable specimensNonProcessedTable;

    private CommentCollectionInfoTable commentTable;

    private DispatchReadInfo dispatchInfo;

    @Override
    protected void init() throws Exception {
        Assert.isTrue((adapter instanceof DispatchAdapter),
            "Invalid editor input: object of type " //$NON-NLS-1$
                + adapter.getClass().getName());

        dispatchAdapter = (DispatchAdapter) adapter;
        dispatchInfo = SessionManager.getAppService().doAction(
            new DispatchGetInfoAction(adapter.getId()));
        dispatch =
            new DispatchWrapper(SessionManager.getAppService(),
                dispatchInfo.dispatch);
        SessionManager.logLookup(dispatch);
        setPartName(Messages.DispatchViewForm_title);
    }

    @Override
    public void setValues() throws Exception {
        commentTable.setList(dispatch.getCommentCollection(false));
        setDispatchValues();
        specimensTree.refresh();
    }

    @Override
    protected void createFormContent() throws Exception {
        String dateString = null;
        if (dispatch.getShipmentInfo() != null
            && dispatch.getShipmentInfo().getPackedAt() != null) {
            dateString = dispatch.getFormattedPackedAt();
        }
        if (dateString == null)
            form.setText(NLS.bind(
                Messages.DispatchViewForm_preparation_title, dateString,
                dispatch.getSenderCenter().getNameShort()));
        else
            form.setText(NLS.bind(Messages.DispatchViewForm_sent_title,
                dateString, dispatch.getSenderCenter().getNameShort()));
        page.setLayout(new GridLayout(1, false));
        page.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        createMainSection();

        createTreeTableSection();

        setDispatchValues();

        DispatchChangeStatePermission perm =
            new DispatchChangeStatePermission(dispatch.getId());
        if (SessionManager.getAppService().isAllowed(perm)) {
            if (dispatch.isInCreationState())
                createSendButton();
            else if (dispatch.isInTransitState())
                createReceiveButtons();
            else if (dispatch.isInReceivedState()
                && dispatch.getNonProcessedDispatchSpecimenCollection().size() == 0)
                createCloseButton();
        }

        commentTable.setList(dispatch.getCommentCollection(false));
    }

    @Override
    protected void addEditAction() {
        super.addEditAction();
    }

    private void createTreeTableSection() {
        if (dispatch.isInCreationState()) {
            Composite parent =
                createSectionWithClient(Messages.DispatchViewForm_specimen_section_label);
            specimensNonProcessedTable =
                new DispatchSpecimenListInfoTable(parent, dispatch, false) {
                    @Override
                    public List<DispatchSpecimenWrapper> getInternalDispatchSpecimens() {
                        return dispatch
                            .getNonProcessedDispatchSpecimenCollection();
                    }

                };
            specimensNonProcessedTable.adaptToToolkit(toolkit, true);
            specimensNonProcessedTable
                .addClickListener(new IDoubleClickListener() {
                    @Override
                    public void doubleClick(DoubleClickEvent event) {
                        Object selection = event.getSelection();
                        if (selection instanceof InfoTableSelection) {
                            InfoTableSelection tableSelection =
                                (InfoTableSelection) selection;
                            DispatchSpecimenWrapper dsa =
                                (DispatchSpecimenWrapper) tableSelection
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
            specimensNonProcessedTable.createDefaultEditItem();
        } else {
            specimensTree =
                new DispatchSpecimensTreeTable(page, dispatch, false, false);
            specimensTree.addClickListener();
        }
    }

    private void createReceiveButtons() {
        Composite composite = toolkit.createComposite(page);
        composite.setLayout(new GridLayout(3, false));
        Button sendButton =
            toolkit.createButton(composite,
                Messages.DispatchViewForm_receive_button_label, SWT.PUSH);
        sendButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                dispatchAdapter.doReceive();
            }
        });

        Button sendProcessButton =
            toolkit.createButton(composite,
                Messages.DispatchViewForm_receive_process_button_label,
                SWT.PUSH);
        sendProcessButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                dispatchAdapter.doReceiveAndProcess();
            }
        });

        Button lostProcessButton =
            toolkit.createButton(composite,
                Messages.DispatchViewForm_lost_button_label, SWT.PUSH);
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
        Button sendButton =
            toolkit.createButton(composite,
                Messages.DispatchViewForm_done_button_label, SWT.PUSH);
        sendButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                dispatchAdapter.doClose();
            }
        });
    }

    private void createSendButton() {
        final Button sendButton =
            toolkit.createButton(page,
                Messages.DispatchViewForm_send_button_label, SWT.PUSH);
        sendButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                if (new SendDispatchDialog(Display.getDefault()
                    .getActiveShell(), dispatch).open() == Dialog.OK) {
                    IRunnableContext context =
                        new ProgressMonitorDialog(Display.getDefault()
                            .getActiveShell());
                    try {
                        context.run(true, true, new IRunnableWithProgress() {
                            @Override
                            public void run(final IProgressMonitor monitor) {
                                monitor.beginTask(
                                    Messages.DispatchViewForm_saving_text,
                                    IProgressMonitor.UNKNOWN);
                                try {
                                    dispatchAdapter.setModelObject(dispatch);
                                    dispatchAdapter.doSend();
                                } catch (Exception ex) {
                                    saveErrorCatch(ex, monitor, false);
                                    return;
                                }
                                monitor.done();
                            }
                        });
                    } catch (Exception e1) {
                        BgcPlugin.openAsyncError(
                            Messages.DispatchViewForm_save_error_title, e1);
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
            stateMessage = Messages.DispatchViewForm_lost_msg;
        else if (dispatch.isInClosedState())
            stateMessage = Messages.DispatchViewForm_complete_msg;
        if (stateMessage != null) {
            Label label =
                widgetCreator.createLabel(client, stateMessage, SWT.CENTER,
                    false);
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

        senderLabel =
            createReadOnlyLabelledField(client, SWT.NONE,
                Messages.DispatchViewForm_sender_label);
        receiverLabel =
            createReadOnlyLabelledField(client, SWT.NONE,
                Messages.DispatchViewForm_receiver_label);
        if (!dispatch.isInCreationState()) {
            departedLabel =
                createReadOnlyLabelledField(client, SWT.NONE,
                    Messages.DispatchViewForm_packedAt_label);
            shippingMethodLabel =
                createReadOnlyLabelledField(client, SWT.NONE,
                    Messages.DispatchViewForm_shippingMethod_label);
            waybillLabel =
                createReadOnlyLabelledField(client, SWT.NONE,
                    Messages.DispatchViewForm_waybill_label);
        }
        if (dispatch.hasBeenReceived()) {
            dateReceivedLabel =
                createReadOnlyLabelledField(client, SWT.NONE,
                    Messages.DispatchViewForm_received_label);
        }
        createCommentsSection();
    }

    private void createCommentsSection() {
        Composite client = createSectionWithClient(Messages.label_comments);
        commentTable =
            new CommentCollectionInfoTable(client,
                dispatch.getCommentCollection(false));
        commentTable.adaptToToolkit(toolkit, true);
        toolkit.paintBordersFor(commentTable);
    }

    private void setDispatchValues() {
        setTextValue(
            senderLabel,
            dispatch.getSenderCenter().getName());
        setTextValue(
            receiverLabel,
            dispatch.getReceiverCenter().getName());
        if (departedLabel != null)
            setTextValue(departedLabel, dispatch.getFormattedPackedAt());

        ShipmentInfoWrapper shipInfo = dispatch.getShipmentInfo();

        if (shipInfo != null) {
            if (shippingMethodLabel != null)
                setTextValue(shippingMethodLabel,
                    shipInfo.getShippingMethod() == null ? "" : shipInfo //$NON-NLS-1$
                        .getShippingMethod().getName());
            if (waybillLabel != null)
                setTextValue(waybillLabel, shipInfo.getWaybill());
            if (dateReceivedLabel != null)
                setTextValue(dateReceivedLabel,
                    shipInfo.getFormattedDateReceived());
        }
    }

}
