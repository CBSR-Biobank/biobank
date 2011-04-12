package edu.ualberta.med.biobank.forms;

import java.util.Arrays;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.PlatformUI;

import edu.ualberta.med.biobank.BiobankPlugin;
import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.util.DispatchSpecimenState;
import edu.ualberta.med.biobank.common.wrappers.DispatchSpecimenWrapper;
import edu.ualberta.med.biobank.common.wrappers.DispatchWrapper;
import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.common.wrappers.SpecimenWrapper;
import edu.ualberta.med.biobank.dialogs.dispatch.DispatchReceiveScanDialog;
import edu.ualberta.med.biobank.widgets.BiobankText;
import edu.ualberta.med.biobank.widgets.DispatchAliquotsTreeTable;

public class DispatchReceivingEntryForm extends AbstractDispatchEntryForm {

    public static final String ID = "edu.ualberta.med.biobank.forms.DispatchReceivingEntryForm";
    private DispatchAliquotsTreeTable aliquotsTree;

    @Override
    protected void createFormContent() throws Exception {
        form.setText("Dispatch sent on " + modelObject.getFormattedPackedAt()
            + " from " + modelObject.getSenderCenter().getNameShort());
        page.setLayout(new GridLayout(1, false));
        page.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        createMainSection();
        boolean editAliquots = !modelObject.isInClosedState()
            && !modelObject.isInLostState();
        if (editAliquots)
            createAliquotsSelectionActions(page, true);
        aliquotsTree = new DispatchAliquotsTreeTable(page, modelObject,
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
            modelObject, modelObject.getReceiverCenter());
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

        BiobankText senderLabel = createReadOnlyLabelledField(client, SWT.NONE,
            "Sender");
        setTextValue(senderLabel, modelObject.getSenderCenter().getName());
        BiobankText receiverLabel = createReadOnlyLabelledField(client,
            SWT.NONE, "Receiver");
        setTextValue(receiverLabel, modelObject.getReceiverCenter().getName());
        BiobankText departedLabel = createReadOnlyLabelledField(client,
            SWT.NONE, "Departed");
        setTextValue(departedLabel, modelObject.getFormattedPackedAt());
        BiobankText shippingMethodLabel = createReadOnlyLabelledField(client,
            SWT.NONE, "Shipping Method");
        setTextValue(shippingMethodLabel, modelObject.getShipmentInfo()
            .getShippingMethod() == null ? "" : modelObject.getShipmentInfo()
            .getShippingMethod().getName());
        BiobankText waybillLabel = createReadOnlyLabelledField(client,
            SWT.NONE, "Waybill");
        setTextValue(waybillLabel, modelObject.getShipmentInfo().getWaybill());
        BiobankText dateReceivedLabel = createReadOnlyLabelledField(client,
            SWT.NONE, "Date received");
        setTextValue(dateReceivedLabel, modelObject.getShipmentInfo()
            .getFormattedDateReceived());

        createBoundWidgetWithLabel(client, BiobankText.class, SWT.MULTI,
            "Comments", null, modelObject, "comment", null);

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
        public SpecimenWrapper aliquot;
        public ResType type;

        public AliquotInfo(SpecimenWrapper aliquot, ResType type) {
            this.aliquot = aliquot;
            this.type = type;
        }
    }

    public static AliquotInfo getInfoForInventoryId(
        ModelWrapper<?> currentShipment, String inventoryId) {
        DispatchSpecimenWrapper dsa = ((DispatchWrapper) currentShipment)
            .getDispatchSpecimen(inventoryId);
        if (dsa == null) {
            // aliquot not in shipment. Check if exists in DB:
            SpecimenWrapper aliquot = null;
            try {
                aliquot = SpecimenWrapper.getSpecimen(
                    currentShipment.getAppService(), inventoryId,
                    SessionManager.getUser());
            } catch (Exception ae) {
                BiobankPlugin.openAsyncError("Error retrieving aliquot", ae);
            }
            if (aliquot == null) {
                return new AliquotInfo(null, ResType.NOT_IN_DB);
            }
            return new AliquotInfo(aliquot, ResType.NOT_IN_SHIPMENT);
        }
        if (DispatchSpecimenState.RECEIVED.equals(dsa
            .getDispatchSpecimenState())) {
            return new AliquotInfo(dsa.getSpecimen(), ResType.RECEIVED);
        }
        if (DispatchSpecimenState.EXTRA.isEquals(dsa.getState())) {
            return new AliquotInfo(dsa.getSpecimen(), ResType.EXTRA);
        }
        return new AliquotInfo(dsa.getSpecimen(), ResType.OK);
    }

    protected void receiveAliquot(String inventoryId) {
        AliquotInfo info = getInfoForInventoryId(modelObject, inventoryId);
        switch (info.type) {
        case OK:
            modelObject.receiveSpecimens(Arrays.asList(info.aliquot));
            aliquotsTree.refresh();
            setDirty(true);
            break;
        case RECEIVED:
            BiobankPlugin.openInformation("Aliquot already accepted",
                "Aliquot with inventory id " + inventoryId
                    + " is already in received list.");
            break;
        case NOT_IN_SHIPMENT:
            BiobankPlugin.openInformation("Aliquot not found",
                "Aliquot with inventory id " + inventoryId
                    + " has not been found in this dispatch."
                    + " It will be moved into the extra-pending list.");
            modelObject.addExtraAliquots(Arrays.asList(info.aliquot));
            aliquotsTree.refresh();
            setDirty(true);
            break;
        case NOT_IN_DB:
            BiobankPlugin.openError("Aliquot not found",
                "This aliquot does not exist in the database.");
            break;
        case DUPLICATE:
            BiobankPlugin.openError("Duplicate aliquot !",
                "This aliquot exists more that once in the database !");
            break;
        case EXTRA:
            BiobankPlugin.openInformation("Aliquot already extra",
                "Aliquot with inventory id " + inventoryId
                    + " is already in extra list.");
            break;
        }
    }

    @Override
    public void reset() throws Exception {
        modelObject.reset();
        aliquotsTree.refresh();
        setDirty(false);
    }

    @Override
    protected String getTextForPartName() {
        return "Dispatch sent on " + modelObject.getPackedAt();
    }

    @Override
    protected void reloadAliquots() {
        aliquotsTree.refresh();
    }

}
