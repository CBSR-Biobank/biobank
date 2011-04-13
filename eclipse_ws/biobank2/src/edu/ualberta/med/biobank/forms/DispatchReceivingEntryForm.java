package edu.ualberta.med.biobank.forms;

import java.util.Arrays;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.PlatformUI;

import edu.ualberta.med.biobank.BiobankPlugin;
import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.peer.DispatchPeer;
import edu.ualberta.med.biobank.common.scanprocess.Cell;
import edu.ualberta.med.biobank.common.scanprocess.data.DispatchProcessData;
import edu.ualberta.med.biobank.common.scanprocess.result.CellProcessResult;
import edu.ualberta.med.biobank.common.util.DispatchSpecimenState;
import edu.ualberta.med.biobank.common.wrappers.SpecimenWrapper;
import edu.ualberta.med.biobank.dialogs.dispatch.DispatchReceiveScanDialog;
import edu.ualberta.med.biobank.widgets.BiobankText;
import edu.ualberta.med.biobank.widgets.DispatchSpecimensTreeTable;

public class DispatchReceivingEntryForm extends AbstractDispatchEntryForm {

    public static final String ID = "edu.ualberta.med.biobank.forms.DispatchReceivingEntryForm";
    private DispatchSpecimensTreeTable specimensTree;

    @Override
    protected void createFormContent() throws Exception {
        form.setText("Dispatch sent on " + dispatch.getFormattedPackedAt()
            + " from " + dispatch.getSenderCenter().getNameShort());
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
        setTextValue(senderLabel, dispatch.getSenderCenter().getName());
        BiobankText receiverLabel = createReadOnlyLabelledField(client,
            SWT.NONE, "Receiver");
        setTextValue(receiverLabel, dispatch.getReceiverCenter().getName());
        BiobankText departedLabel = createReadOnlyLabelledField(client,
            SWT.NONE, "Departed");
        setTextValue(departedLabel, dispatch.getFormattedPackedAt());
        BiobankText shippingMethodLabel = createReadOnlyLabelledField(client,
            SWT.NONE, "Shipping Method");
        setTextValue(shippingMethodLabel, dispatch.getShipmentInfo()
            .getShippingMethod() == null ? "" : dispatch.getShipmentInfo()
            .getShippingMethod().getName());
        BiobankText waybillLabel = createReadOnlyLabelledField(client,
            SWT.NONE, "Waybill");
        setTextValue(waybillLabel, dispatch.getShipmentInfo().getWaybill());
        BiobankText dateReceivedLabel = createReadOnlyLabelledField(client,
            SWT.NONE, "Date received");
        setTextValue(dateReceivedLabel, dispatch.getShipmentInfo()
            .getFormattedDateReceived());

        createBoundWidgetWithLabel(client, BiobankText.class, SWT.MULTI,
            "Comments", null, dispatch, DispatchPeer.COMMENT.getName(), null);

    }

    @Override
    protected void openScanDialog() {
        DispatchReceiveScanDialog dialog = new DispatchReceiveScanDialog(
            PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
            dispatch, dispatch.getReceiverCenter());
        dialog.open();
        if (dialog.hasReceivedSpecimens()) {
            setDirty(true);
        }
        reloadSpecimens();
    }

    @Override
    protected void doSpecimenTextAction(String inventoryId) {
        try {
            CellProcessResult res = appService.processCellStatus(new Cell(-1,
                -1, inventoryId, null), new DispatchProcessData(null, dispatch,
                false, false), SessionManager.getUser());
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
                BiobankPlugin.openInformation("Specimen already accepted",
                    "Specimen with inventory id " + inventoryId
                        + " is already in received list.");
                break;
            case EXTRA:
                BiobankPlugin.openInformation("Specimen not found",
                    "Specimen with inventory id " + inventoryId
                        + " has not been found in this dispatch."
                        + " It will be moved into the extra-pending list.");
                dispatch.addSpecimens(Arrays.asList(specimen),
                    DispatchSpecimenState.EXTRA);
                reloadSpecimens();
                setDirty(true);
                break;
            // FIXME
            // case NOT_IN_DB:
            // BiobankPlugin.openError("Specimen not found",
            // "This specimen does not exist in the database.");
            // break;
            // case DUPLICATE:
            // BiobankPlugin.openError("Duplicate specimen !",
            // "This specimen exists more that once in the database !");
            // break;
            // case EXTRA:
            // BiobankPlugin.openInformation("Specimen already extra",
            // "Specimen with inventory id " + inventoryId
            // + " is already in extra list.");
            // break;
            }
        } catch (Exception e) {
            BiobankPlugin.openAsyncError("Error",
                "Error receiving the specimen", e);
        }
    }

    @Override
    protected String getOkMessage() {
        return "Receiving dispatch";
    }

    @Override
    public String getNextOpenedFormID() {
        return DispatchViewForm.ID;
    }

    // FIXME remove when doesnt use anymore
    public enum ResType {
        OK, NOT_IN_SHIPMENT, NOT_IN_DB, DUPLICATE, RECEIVED, EXTRA;
    }

    // FIXME remove when doesnt use anymore
    public static class SpecimenInfo {
        public SpecimenWrapper specimen;
        public ResType type;

        public SpecimenInfo(SpecimenWrapper specimen, ResType type) {
            this.specimen = specimen;
            this.type = type;
        }
    }

    @Override
    protected String getTextForPartName() {
        return "Dispatch sent on " + dispatch.getShipmentInfo().getPackedAt();
    }

    @Override
    protected void reloadSpecimens() {
        specimensTree.refresh();
    }

}
