package edu.ualberta.med.biobank.forms;

import java.util.Arrays;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.PlatformUI;

import edu.ualberta.med.biobank.BioBankPlugin;
import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.exception.BiobankCheckException;
import edu.ualberta.med.biobank.common.util.DispatchAliquotState;
import edu.ualberta.med.biobank.common.wrappers.AliquotWrapper;
import edu.ualberta.med.biobank.common.wrappers.DispatchAliquotWrapper;
import edu.ualberta.med.biobank.common.wrappers.DispatchWrapper;
import edu.ualberta.med.biobank.dialogs.dispatch.DispatchReceiveScanDialog;
import edu.ualberta.med.biobank.widgets.BiobankText;
import edu.ualberta.med.biobank.widgets.DispatchAliquotsTreeTable;

public class DispatchReceivingEntryForm extends AbstractShipmentEntryForm {

    public static final String ID = "edu.ualberta.med.biobank.forms.DispatchReceivingEntryForm";
    private DispatchAliquotsTreeTable aliquotsTree;

    @Override
    protected void createFormContent() throws Exception {
        form.setText("Shipment sent on " + shipment.getFormattedDeparted()
            + " from " + shipment.getSender().getNameShort());
        page.setLayout(new GridLayout(1, false));
        page.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        createMainSection();
        boolean editAliquots = !shipment.isInClosedState()
            && !shipment.isInLostState();
        if (editAliquots)
            createAliquotsSelectionActions(page, true);
        aliquotsTree = new DispatchAliquotsTreeTable(page, shipment,
            editAliquots, true);
        aliquotsTree.addSelectionChangedListener(biobankListener);
    }

    @Override
    protected void doAliquotTextAction(String text) {
        receiveAliquot(text);
    }

    @Override
    protected void openScanDialog() {
        DispatchReceiveScanDialog dialog = new DispatchReceiveScanDialog(
            PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
            shipment);
        dialog.open();
        if (dialog.hasReceivedAliquots()) {
            setDirty(true);
        }
        aliquotsTree.refresh();
    }

    private void createMainSection() {
        Composite client = toolkit.createComposite(page);
        GridLayout layout = new GridLayout(2, false);
        layout.horizontalSpacing = 10;
        client.setLayout(layout);
        client.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        toolkit.paintBordersFor(client);

        BiobankText studyLabel = createReadOnlyLabelledField(client, SWT.NONE,
            "Study");
        setTextValue(studyLabel, shipment.getStudy().getName());
        BiobankText senderLabel = createReadOnlyLabelledField(client, SWT.NONE,
            "Sender");
        setTextValue(senderLabel, shipment.getSender().getName());
        BiobankText receiverLabel = createReadOnlyLabelledField(client,
            SWT.NONE, "Receiver");
        setTextValue(receiverLabel, shipment.getReceiver().getName());
        BiobankText departedLabel = createReadOnlyLabelledField(client,
            SWT.NONE, "Departed");
        setTextValue(departedLabel, shipment.getFormattedDeparted());
        BiobankText shippingMethodLabel = createReadOnlyLabelledField(client,
            SWT.NONE, "Shipping Method");
        setTextValue(shippingMethodLabel,
            shipment.getShippingMethod() == null ? "" : shipment
                .getShippingMethod().getName());
        BiobankText waybillLabel = createReadOnlyLabelledField(client,
            SWT.NONE, "Waybill");
        setTextValue(waybillLabel, shipment.getWaybill());
        BiobankText dateReceivedLabel = createReadOnlyLabelledField(client,
            SWT.NONE, "Date received");
        setTextValue(dateReceivedLabel, shipment.getFormattedDateReceived());

        Control commentsWidget = createBoundWidgetWithLabel(client,
            BiobankText.class, SWT.MULTI, "Comments", null, shipment,
            "comment", null);
        setFirstControl(commentsWidget);
    }

    @Override
    protected String getOkMessage() {
        return "Receiving dispatch";
    }

    @Override
    public String getNextOpenedFormID() {
        return DispatchViewForm.ID;
    }

    public enum ResType {
        OK, NOT_IN_SHIPMENT, NOT_IN_DB, DUPLICATE, RECEIVED, EXTRA;
    }

    public static class AliquotInfo {
        public AliquotWrapper aliquot;
        public ResType type;

        public AliquotInfo(AliquotWrapper aliquot, ResType type) {
            this.aliquot = aliquot;
            this.type = type;
        }
    }

    public static AliquotInfo getInfoForInventoryId(DispatchWrapper shipment,
        String inventoryId) {
        DispatchAliquotWrapper dsa = shipment.getDispatchAliquot(inventoryId);
        if (dsa == null) {
            // aliquot not in shipment. Check if exists in DB:
            AliquotWrapper aliquot = null;
            try {
                aliquot = AliquotWrapper.getAliquot(shipment.getAppService(),
                    inventoryId, SessionManager.getUser());
            } catch (Exception ae) {
                BioBankPlugin.openAsyncError("Error retrieving aliquot", ae);
            }
            if (aliquot == null) {
                return new AliquotInfo(null, ResType.NOT_IN_DB);
            }
            return new AliquotInfo(aliquot, ResType.NOT_IN_SHIPMENT);
        }
        if (DispatchAliquotState.RECEIVED_STATE.isEquals(dsa.getState())) {
            return new AliquotInfo(dsa.getAliquot(), ResType.RECEIVED);
        }
        if (DispatchAliquotState.EXTRA.isEquals(dsa.getState())) {
            return new AliquotInfo(dsa.getAliquot(), ResType.EXTRA);
        }
        return new AliquotInfo(dsa.getAliquot(), ResType.OK);
    }

    protected void receiveAliquot(String inventoryId) {
        AliquotInfo info = getInfoForInventoryId(shipment, inventoryId);
        switch (info.type) {
        case OK:
            shipment.receiveAliquots(Arrays.asList(info.aliquot));
            aliquotsTree.refresh();
            setDirty(true);
            break;
        case RECEIVED:
            BioBankPlugin.openInformation("Aliquot already accepted",
                "Aliquot with inventory id " + inventoryId
                    + " is already in received list.");
            break;
        case NOT_IN_SHIPMENT:
            BioBankPlugin.openInformation("Aliquot not found",
                "Aliquot with inventory id " + inventoryId
                    + " has not been found in this shipment."
                    + " It will be moved into the extra-pending list.");
            try {
                shipment.addExtraAliquots(Arrays.asList(info.aliquot), false);
            } catch (BiobankCheckException e) {
                BioBankPlugin.openAsyncError("Eror adding extra aliquot", e);
            }
            aliquotsTree.refresh();
            setDirty(true);
            break;
        case NOT_IN_DB:
            BioBankPlugin.openError("Aliquot not found",
                "This aliquot does not exists in the database.");
            break;
        case DUPLICATE:
            BioBankPlugin.openError("Duplicate aliquot !",
                "This aliquot exists more that once in the database !");
            break;
        case EXTRA:
            BioBankPlugin.openInformation("Aliquot already extra",
                "Aliquot with inventory id " + inventoryId
                    + " is already in extra list.");
            break;
        }
    }

    @Override
    protected String getTextForPartName() {
        return "Dispatch sent on " + shipment.getDeparted();
    }

    @Override
    protected void reloadAliquots() {
        aliquotsTree.refresh();
    }

}
