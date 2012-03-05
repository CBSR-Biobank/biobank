package edu.ualberta.med.biobank.forms;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.PlatformUI;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.action.scanprocess.CellInfo;
import edu.ualberta.med.biobank.common.action.scanprocess.ShipmentReceiveProcessAction;
import edu.ualberta.med.biobank.common.action.scanprocess.data.ShipmentProcessInfo;
import edu.ualberta.med.biobank.common.action.scanprocess.result.CellProcessResult;
import edu.ualberta.med.biobank.common.action.specimen.SpecimenGetInfoAction;
import edu.ualberta.med.biobank.common.exception.BiobankException;
import edu.ualberta.med.biobank.common.permission.dispatch.DispatchChangeStatePermission;
import edu.ualberta.med.biobank.common.util.DispatchSpecimenState;
import edu.ualberta.med.biobank.common.wrappers.SpecimenWrapper;
import edu.ualberta.med.biobank.dialogs.dispatch.DispatchReceiveScanDialog;
import edu.ualberta.med.biobank.gui.common.BgcPlugin;
import edu.ualberta.med.biobank.gui.common.widgets.BgcBaseText;
import edu.ualberta.med.biobank.gui.common.widgets.BgcEntryFormWidgetListener;
import edu.ualberta.med.biobank.gui.common.widgets.MultiSelectEvent;
import edu.ualberta.med.biobank.model.Specimen;
import edu.ualberta.med.biobank.treeview.AdapterBase;
import edu.ualberta.med.biobank.widgets.infotables.CommentCollectionInfoTable;
import edu.ualberta.med.biobank.widgets.trees.DispatchSpecimensTreeTable;
import gov.nih.nci.system.applicationservice.ApplicationException;

public class DispatchReceivingEntryForm extends AbstractDispatchEntryForm {

    public static final String ID =
        "edu.ualberta.med.biobank.forms.DispatchReceivingEntryForm"; //$NON-NLS-1$
    private DispatchSpecimensTreeTable specimensTree;
    private List<SpecimenWrapper> receivedOrExtraSpecimens =
        new ArrayList<SpecimenWrapper>();
    private CommentCollectionInfoTable commentEntryTable;

    private BgcEntryFormWidgetListener listener =
        new BgcEntryFormWidgetListener() {
            @Override
            public void selectionChanged(MultiSelectEvent event) {
                setDirty(true);
            }
        };

    @Override
    protected void createFormContent() throws Exception {
        form.setText(NLS.bind(Messages.DispatchReceivingEntryForm_form_title,
            dispatch.getFormattedPackedAt(), dispatch.getSenderCenter()
                .getNameShort()));
        page.setLayout(new GridLayout(1, false));
        page.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        createMainSection();
        boolean editSpecimens = !dispatch.isInClosedState()
            && !dispatch.isInLostState();

        setFirstControl(form);

        if (editSpecimens)
            createSpecimensSelectionActions(page, true);
        specimensTree =
            new DispatchSpecimensTreeTable(page, dispatch,
                editSpecimens, true);
        specimensTree.addSelectionChangedListener(biobankListener);
        specimensTree.addClickListener();
    }

    private void createMainSection() {
        Composite client = toolkit.createComposite(page);
        GridLayout layout = new GridLayout(2, false);
        layout.horizontalSpacing = 10;
        client.setLayout(layout);
        client.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        toolkit.paintBordersFor(client);

        BgcBaseText senderLabel = createReadOnlyLabelledField(client, SWT.NONE,
            Messages.DispatchReceivingEntryForm_sender_label);
        setTextValue(senderLabel, dispatch.getSenderCenter().getName());
        BgcBaseText receiverLabel = createReadOnlyLabelledField(client,
            SWT.NONE, Messages.DispatchReceivingEntryForm_receiver_label);
        setTextValue(receiverLabel, dispatch.getReceiverCenter().getName());
        BgcBaseText departedLabel = createReadOnlyLabelledField(client,
            SWT.NONE, Messages.DispatchReceivingEntryForm_departed_label);
        setTextValue(departedLabel, dispatch.getFormattedPackedAt());
        BgcBaseText shippingMethodLabel = createReadOnlyLabelledField(client,
            SWT.NONE, Messages.DispatchReceivingEntryForm_shipMethod_label);
        setTextValue(shippingMethodLabel, dispatch.getShipmentInfo()
            .getShippingMethod() == null ? "" : dispatch.getShipmentInfo() //$NON-NLS-1$
            .getShippingMethod().getName());
        BgcBaseText waybillLabel = createReadOnlyLabelledField(client,
            SWT.NONE, Messages.DispatchReceivingEntryForm_waybill_label);
        setTextValue(waybillLabel, dispatch.getShipmentInfo().getWaybill());
        BgcBaseText dateReceivedLabel = createReadOnlyLabelledField(client,
            SWT.NONE, Messages.DispatchReceivingEntryForm_received_label);
        setTextValue(dateReceivedLabel, dispatch.getShipmentInfo()
            .getFormattedDateReceived());

        createCommentSection();

    }

    private void createCommentSection() {
        Composite client = createSectionWithClient(Messages.Comments_title);
        GridLayout gl = new GridLayout(2, false);

        client.setLayout(gl);
        commentEntryTable = new CommentCollectionInfoTable(client,
            dispatch.getCommentCollection(false));
        GridData gd = new GridData();
        gd.horizontalSpan = 2;
        gd.grabExcessHorizontalSpace = true;
        gd.horizontalAlignment = SWT.FILL;
        commentEntryTable.setLayoutData(gd);
        createLabelledWidget(client, BgcBaseText.class, SWT.MULTI,
            Messages.Comments_add);

    }

    @Override
    protected void openScanDialog() {
        DispatchReceiveScanDialog dialog = new DispatchReceiveScanDialog(
            PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
            dispatch, dispatch.getReceiverCenter());
        dialog.open();
        if (dispatch.hasNewSpecimens() || dispatch.hasSpecimenStatesChanged())
            setDirty(true);
        reloadSpecimens();
    }

    @Override
    protected void doSpecimenTextAction(String inventoryId) {
        try {
            doSpecimenTextAction(inventoryId, true);
        } catch (Exception e) {
            BgcPlugin.openAsyncError(
                Messages.DispatchReceivingEntryForm_problem_spec_error, e);
        }
    }

    /**
     * when called from gui, errors will show a dialog. Otherwise, will throw an
     * exception
     */
    protected void doSpecimenTextAction(String inventoryId, boolean showMessages)
        throws Exception {
        try {
            CellProcessResult res = (CellProcessResult) SessionManager
                .getAppService().doAction(
                    new ShipmentReceiveProcessAction(
                        new ShipmentProcessInfo(null, dispatch, false),
                        SessionManager.getUser().getCurrentWorkingCenter()
                            .getId(),
                        new CellInfo(-1, -1, inventoryId, null),
                        Locale.getDefault()));
            SpecimenWrapper specimen = null;
            if (res.getCell().getSpecimenId() != null) {
                Specimen spec = SessionManager.getAppService()
                    .doAction(new SpecimenGetInfoAction(res
                        .getCell().getSpecimenId())).getSpecimen();
                specimen =
                    new SpecimenWrapper(SessionManager.getAppService(), spec);
            }
            switch (res.getCell().getStatus()) {
            case IN_SHIPMENT_EXPECTED:
                dispatch.receiveSpecimens(Arrays.asList(specimen));
                receivedOrExtraSpecimens.add(specimen);
                reloadSpecimens();
                setDirty(true);
                break;
            case IN_SHIPMENT_RECEIVED:
                if (showMessages)
                    BgcPlugin
                        .openInformation(
                            Messages.DispatchReceivingEntryForm_already_accepted_title,
                            NLS.bind(
                                Messages.DispatchReceivingEntryForm_already_accepted_msg,
                                inventoryId));
                break;
            case EXTRA:
                if (showMessages)
                    BgcPlugin
                        .openInformation(
                            Messages.DispatchReceivingEntryForm_noFound_error_title,
                            NLS.bind(
                                Messages.DispatchReceivingEntryForm_notFound_errror_msg,
                                inventoryId));
                if (specimen == null) {
                    if (showMessages)
                        BgcPlugin
                            .openAsyncError(
                                Messages.DispatchReceivingEntryForm_specimen_pb_error_title,
                                Messages.DispatchReceivingEntryForm_specimen_pb_error_msg);
                    else
                        throw new Exception(
                            Messages.DispatchReceivingEntryForm_specimen_pb_error_msg);
                    break;
                }
                dispatch.addSpecimens(Arrays.asList(specimen),
                    DispatchSpecimenState.EXTRA);
                receivedOrExtraSpecimens.add(specimen);
                reloadSpecimens();
                setDirty(true);
                break;
            default:
                if (showMessages)
                    BgcPlugin.openInformation(
                        Messages.DispatchReceivingEntryForm_specimen_pb_er, res
                            .getCell().getInformation());
                else
                    throw new Exception(
                        Messages.DispatchReceivingEntryForm_specimen_pb_er);
            }
        } catch (Exception e) {
            if (showMessages)
                BgcPlugin.openAsyncError(
                    Messages.DispatchReceivingEntryForm_receive_error_title, e);
            else
                throw e;
        }
    }

    @Override
    protected String getOkMessage() {
        return Messages.DispatchReceivingEntryForm_ok_msg;
    }

    @Override
    public String getNextOpenedFormID() {
        return DispatchViewForm.ID;
    }

    @Override
    protected String getTextForPartName() {
        return NLS.bind(Messages.DispatchReceivingEntryForm_title, dispatch
            .getShipmentInfo().getPackedAt());
    }

    @Override
    protected void reloadSpecimens() {
        specimensTree.refresh();
    }

    @Override
    protected boolean needToTryAgainIfConcurrency() {
        return true;
    }

    @Override
    protected void doTrySettingAgain() throws Exception {
        dispatch.reloadDispatchSpecimens();
        Map<String, String> problems = new HashMap<String, String>();
        // work on a copy of the list to avoid concurrency pb on list
        List<SpecimenWrapper> receveidOrExtrasCopy =
            new ArrayList<SpecimenWrapper>(
                receivedOrExtraSpecimens);
        receivedOrExtraSpecimens.clear();
        for (SpecimenWrapper spec : receveidOrExtrasCopy) {
            try {
                doSpecimenTextAction(spec.getInventoryId(), false);
            } catch (Exception ex) {
                problems.put(spec.getInventoryId(), ex.getMessage());
            }
        }

        if (problems.size() != 0) {
            StringBuffer msg = new StringBuffer();
            for (Entry<String, String> entry : problems.entrySet()) {
                if (msg.length() > 0)
                    msg.append("\n"); //$NON-NLS-1$
                msg.append(entry.getKey()).append(": ") //$NON-NLS-1$
                    .append(entry.getValue());
            }
            throw new BiobankException(
                Messages.ProcessingEventEntryForm_try_again_error_msg
                    + msg.toString());
        }
    }

    @Override
    protected void checkEditAccess() {
        try {
            if (adapter != null
                && ((AdapterBase) adapter).getId() != null
                && !SessionManager.getAppService().isAllowed(
                    new DispatchChangeStatePermission(((AdapterBase) adapter)
                        .getId()))) {
                BgcPlugin.openAccessDeniedErrorMessage();
            }
        } catch (ApplicationException e) {
        }
        throw new RuntimeException(
            Messages.BiobankEntryForm_access_denied_error_msg);
    }
}
