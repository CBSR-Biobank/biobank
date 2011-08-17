package edu.ualberta.med.biobank.forms;

import java.util.Arrays;
import java.util.Locale;

import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.PlatformUI;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.peer.DispatchPeer;
import edu.ualberta.med.biobank.common.scanprocess.Cell;
import edu.ualberta.med.biobank.common.scanprocess.data.ShipmentProcessData;
import edu.ualberta.med.biobank.common.scanprocess.result.CellProcessResult;
import edu.ualberta.med.biobank.common.util.DispatchSpecimenState;
import edu.ualberta.med.biobank.common.wrappers.SpecimenWrapper;
import edu.ualberta.med.biobank.dialogs.dispatch.DispatchReceiveScanDialog;
import edu.ualberta.med.biobank.gui.common.BgcPlugin;
import edu.ualberta.med.biobank.gui.common.widgets.BgcBaseText;
import edu.ualberta.med.biobank.widgets.trees.DispatchSpecimensTreeTable;

public class DispatchReceivingEntryForm extends AbstractDispatchEntryForm {

    public static final String ID = "edu.ualberta.med.biobank.forms.DispatchReceivingEntryForm"; //$NON-NLS-1$
    private DispatchSpecimensTreeTable specimensTree;

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
        specimensTree = new DispatchSpecimensTreeTable(page, dispatch,
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

        createBoundWidgetWithLabel(client, BgcBaseText.class, SWT.MULTI,
            Messages.DispatchReceivingEntryForm_comments_label, null, dispatch,
            DispatchPeer.COMMENT.getName(), null);

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
            CellProcessResult res = appService
                .processCellStatus(new Cell(-1, -1, inventoryId, null),
                    new ShipmentProcessData(null, dispatch, false, false),
                    SessionManager.getUserOld(), Locale.getDefault());
            SpecimenWrapper specimen = null;
            if (res.getCell().getSpecimenId() != null) {
                specimen = new SpecimenWrapper(appService);
                specimen.getWrappedObject()
                    .setId(res.getCell().getSpecimenId());
                specimen.reload();
            }
            switch (res.getCell().getStatus()) {
            case IN_SHIPMENT_EXPECTED:
                dispatch.receiveSpecimens(Arrays.asList(specimen));
                reloadSpecimens();
                setDirty(true);
                break;
            case IN_SHIPMENT_RECEIVED:
                BgcPlugin
                    .openInformation(
                        Messages.DispatchReceivingEntryForm_already_accepted_title,
                        NLS.bind(
                            Messages.DispatchReceivingEntryForm_already_accepted_msg,
                            inventoryId));
                break;
            case EXTRA:
                BgcPlugin
                    .openInformation(
                        Messages.DispatchReceivingEntryForm_noFound_error_title,
                        NLS.bind(
                            Messages.DispatchReceivingEntryForm_notFound_errror_msg,
                            inventoryId));
                if (specimen == null) {
                    BgcPlugin
                        .openAsyncError(
                            Messages.DispatchReceivingEntryForm_specimen_pb_error_title,
                            Messages.DispatchReceivingEntryForm_specimen_pb_error_msg);
                    break;
                }
                dispatch.addSpecimens(Arrays.asList(specimen),
                    DispatchSpecimenState.EXTRA);
                reloadSpecimens();
                setDirty(true);
                break;
            default:
                BgcPlugin.openInformation(
                    Messages.DispatchReceivingEntryForm_specimen_pb_er, res
                        .getCell().getInformation());
            }
        } catch (Exception e) {
            BgcPlugin.openAsyncError(
                Messages.DispatchReceivingEntryForm_receive_error_title, e);
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

}
