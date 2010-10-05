package edu.ualberta.med.biobank.dialogs.dispatch;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import edu.ualberta.med.biobank.BioBankPlugin;
import edu.ualberta.med.biobank.common.util.DispatchAliquotState;
import edu.ualberta.med.biobank.common.util.RowColPos;
import edu.ualberta.med.biobank.common.wrappers.AliquotWrapper;
import edu.ualberta.med.biobank.common.wrappers.ContainerLabelingSchemeWrapper;
import edu.ualberta.med.biobank.common.wrappers.DispatchShipmentAliquotWrapper;
import edu.ualberta.med.biobank.common.wrappers.DispatchShipmentWrapper;
import edu.ualberta.med.biobank.forms.DispatchShipmentReceivingEntryForm;
import edu.ualberta.med.biobank.forms.DispatchShipmentReceivingEntryForm.AliquotInfo;
import edu.ualberta.med.biobank.model.CellStatus;
import edu.ualberta.med.biobank.model.PalletCell;
import edu.ualberta.med.scannerconfig.dmscanlib.ScanCell;

public class DispatchReceiveScanDialog extends AbstractDispatchScanDialog {

    private static final String TITLE = "Scanning received pallets";

    private int pendingAliquotsNumber = 0;

    private boolean aliquotsReceived = false;

    private int errors;

    public DispatchReceiveScanDialog(Shell parentShell,
        final DispatchShipmentWrapper currentShipment) {
        super(parentShell, currentShipment);
    }

    @Override
    protected String getTitleAreaMessage() {
        return "Scan one pallet received in the shipment.";
    }

    @Override
    protected String getTitleAreaTitle() {
        return TITLE;
    }

    @Override
    protected String getDialogShellTitle() {
        return TITLE;
    }

    /**
     * set the status of the cell
     */
    protected void processCellStatus(PalletCell cell) throws Exception {
        AliquotInfo info = DispatchShipmentReceivingEntryForm
            .getInfoForInventoryId(currentShipment, cell.getValue());
        if (info.aliquot != null) {
            cell.setAliquot(info.aliquot);
            cell.setTitle(info.aliquot.getPatientVisit().getPatient()
                .getPnumber());
        }
        switch (info.type) {
        case RECEIVED:
            cell.setStatus(CellStatus.IN_SHIPMENT_RECEIVED);
            break;
        case DUPLICATE:
            cell.setStatus(CellStatus.ERROR);
            cell.setInformation("Found more than one aliquot with inventoryId "
                + cell.getValue());
            cell.setTitle("!");
            errors++;
            break;
        case NOT_IN_DB:
            cell.setStatus(CellStatus.ERROR);
            cell.setInformation("Aliquot " + cell.getValue()
                + " not found in database");
            cell.setTitle("!");
            errors++;
            break;
        case NOT_IN_SHIPMENT:
            cell.setStatus(CellStatus.NOT_IN_SHIPMENT);
            cell.setInformation("Aliquot should not be in shipment");
            pendingAliquotsNumber++;
            break;
        case OK:
            cell.setStatus(CellStatus.IN_SHIPMENT_EXPECTED);
            pendingAliquotsNumber++;
            break;
        case EXTRA:
            cell.setStatus(CellStatus.EXTRA);
            pendingAliquotsNumber++;
            break;
        }
    }

    @Override
    protected void processScanResult(IProgressMonitor monitor) throws Exception {
        Map<RowColPos, PalletCell> cells = getCells();
        pendingAliquotsNumber = 0;
        errors = 0;
        final List<AliquotWrapper> notInShipmentAliquots = new ArrayList<AliquotWrapper>();
        final List<PalletCell> notInShipmentCells = new ArrayList<PalletCell>();
        if (cells != null) {
            for (RowColPos rcp : cells.keySet()) {
                monitor.subTask("Processing position "
                    + ContainerLabelingSchemeWrapper.rowColToSbs(rcp));
                PalletCell cell = cells.get(rcp);
                processCellStatus(cell);
                if (cell.getStatus().equals(CellStatus.NOT_IN_SHIPMENT)) {
                    notInShipmentAliquots.add(cell.getAliquot());
                    notInShipmentCells.add(cell);
                }
            }
            if (notInShipmentAliquots.size() > 0) {
                Display.getDefault().asyncExec(new Runnable() {
                    @Override
                    public void run() {
                        BioBankPlugin
                            .openInformation(
                                "Not in shipment aliquots",
                                "Some of the aliquots in this pallet were not supposed"
                                    + " to be in this shipment. They will be added to the"
                                    + " extra-pending list.");
                        try {
                            currentShipment
                                .addExtraPendingAliquots(notInShipmentAliquots);
                        } catch (Exception e) {
                            BioBankPlugin.openAsyncError(
                                "Error flagging aliquots", e);
                        }
                        for (PalletCell cell : notInShipmentCells) {
                            cell.setStatus(CellStatus.EXTRA);
                        }
                        setScanOkValue(errors == 0);
                        redrawPallet();
                    }
                });
            }
            setScanOkValue(errors == 0);
        }
    }

    @Override
    protected String getProceedButtonlabel() {
        return "Accept aliquots";
    }

    @Override
    protected boolean canActivateProceedButton() {
        return pendingAliquotsNumber != 0;
    }

    @Override
    protected boolean canActivateNextAndFinishButton() {
        return pendingAliquotsNumber == 0;
    }

    @Override
    protected void doProceed() {
        List<AliquotWrapper> aliquots = new ArrayList<AliquotWrapper>();
        for (PalletCell cell : getCells().values()) {
            if (cell.getStatus() == CellStatus.IN_SHIPMENT_EXPECTED) {
                aliquots.add(cell.getAliquot());
                cell.setStatus(CellStatus.IN_SHIPMENT_RECEIVED);
            }
        }
        try {
            currentShipment.receiveAliquots(aliquots);
            redrawPallet();
            pendingAliquotsNumber = 0;
            setOkButtonEnabled(true);
            aliquotsReceived = true;
        } catch (Exception e) {
            BioBankPlugin.openAsyncError("Error receiving aliquots", e);
        }
        Button cancelButton = getButton(IDialogConstants.CANCEL_ID);
        cancelButton.setEnabled(false);
    }

    @Override
    protected List<CellStatus> getPalletCellStatus() {
        return CellStatus.DEFAULT_PALLET_DISPATCH_RECEIVE_STATUS_LIST;
    }

    @Override
    protected Map<RowColPos, PalletCell> getFakeScanCells() {
        Map<RowColPos, PalletCell> palletScanned = new TreeMap<RowColPos, PalletCell>();
        if (currentShipment.getAliquotCollection().size() > 0) {
            // AliquotWrapper aliquotNotReceived = null;
            int i = 0;
            for (DispatchShipmentAliquotWrapper dsa : currentShipment
                .getDispatchShipmentAliquotCollection()) {
                // if (dsa.getState() != DispatchAliquotState.RECEIVED_STATE) {
                // aliquotFlagged = aliquot;
                // }
                int row = i / 12;
                int col = i % 12;
                if (dsa.getState() != DispatchAliquotState.MISSING.ordinal()
                    && dsa.getState() != DispatchAliquotState.MISSING_PENDING_STATE
                        .ordinal())
                    palletScanned.put(new RowColPos(row, col), new PalletCell(
                        new ScanCell(row, col, dsa.getAliquot()
                            .getInventoryId())));
                i++;
            }
            // if (aliquotNotReceived != null) {
            // palletScanned.put(new RowColPos(0, 0), new PalletCell(
            // new ScanCell(0, 0, aliquotNotReceived.getInventoryId())));
            // }
            // if (aliquotFlagged != null) {
            // palletScanned.put(new RowColPos(0, 3), new PalletCell(
            // new ScanCell(0, 3, aliquotFlagged.getInventoryId())));
            // }
        }
        // palletScanned.put(new RowColPos(0, 1), new PalletCell(new ScanCell(0,
        // 1, "dddz")));
        // palletScanned.put(new RowColPos(0, 2), new PalletCell(new ScanCell(0,
        // 2, "NUBR019021")));
        return palletScanned;
    }

    public boolean hasReceivedAliquots() {
        return aliquotsReceived;
    }

    @Override
    protected void postprocessScanTubeAlone(PalletCell cell) throws Exception {
        processCellStatus(cell);
        super.postprocessScanTubeAlone(cell);
    }
}
