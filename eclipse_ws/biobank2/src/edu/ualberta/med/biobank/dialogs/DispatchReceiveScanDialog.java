package edu.ualberta.med.biobank.dialogs;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Shell;

import edu.ualberta.med.biobank.BioBankPlugin;
import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.util.LabelingScheme;
import edu.ualberta.med.biobank.common.util.RowColPos;
import edu.ualberta.med.biobank.common.wrappers.AliquotWrapper;
import edu.ualberta.med.biobank.common.wrappers.DispatchShipmentWrapper;
import edu.ualberta.med.biobank.model.CellStatus;
import edu.ualberta.med.biobank.model.PalletCell;
import edu.ualberta.med.scannerconfig.dmscanlib.ScanCell;

public class DispatchReceiveScanDialog extends AbstractDispatchScanDialog {

    private static final String TITLE = "Scanning received pallets";

    private int pendingAliquotsNumber = 0;

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

    @Override
    protected void processScanResult(IProgressMonitor monitor) throws Exception {
        Map<RowColPos, PalletCell> cells = getCells();
        pendingAliquotsNumber = 0;
        if (cells != null) {
            boolean resOk = true;
            for (RowColPos rcp : cells.keySet()) {
                monitor.subTask("Processing position "
                    + LabelingScheme.rowColToSbs(rcp));
                PalletCell cell = cells.get(rcp);
                List<AliquotWrapper> aliquots = AliquotWrapper.getAliquots(
                    SessionManager.getAppService(), cell.getValue());
                if (aliquots == null || aliquots.size() == 0) {
                    cell.setStatus(CellStatus.ERROR);
                    cell.setInformation("Aliquot not found in database");
                    resOk = false;
                    continue;
                }
                if (aliquots.size() > 1) {
                    cell.setStatus(CellStatus.ERROR);
                    cell.setInformation("Found more than one aliquot with inventoryId "
                        + cell.getValue());
                    resOk = false;
                    continue;
                }
                AliquotWrapper aliquot = aliquots.get(0);
                cell.setAliquot(aliquot);
                cell.setTitle(aliquot.getPatientVisit().getPatient()
                    .getPnumber());
                if (currentShipment.getAliquotCollection().contains(aliquot)) {
                    if (aliquot.isActive()) {
                        cell.setStatus(CellStatus.IN_SHIPMENT_ACCEPTED);
                    } else {
                        cell.setStatus(CellStatus.IN_SHIPMENT_PENDING);
                        pendingAliquotsNumber++;
                    }
                } else {
                    cell.setStatus(CellStatus.NOT_IN_SHIPMENT);
                    cell.setInformation("Aliquot should not be in shipment");
                }
            }
            setScanOkValue(resOk);
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
    protected boolean canActivateNextButton() {
        return pendingAliquotsNumber == 0;
    }

    @Override
    protected void doProceed() {
        List<AliquotWrapper> aliquots = new ArrayList<AliquotWrapper>();
        for (PalletCell cell : getCells().values()) {
            aliquots.add(cell.getAliquot());
            cell.setStatus(CellStatus.IN_SHIPMENT_ACCEPTED);
        }
        try {
            currentShipment.receiveAliquots(aliquots);
            redrawPallet();
            pendingAliquotsNumber = 0;
            setOkButtonEnabled(true);
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
            AliquotWrapper aliquotNotReceived = null;
            for (AliquotWrapper aliquot : currentShipment
                .getAliquotCollection()) {
                if (aliquot.isDispatched()) {
                    aliquotNotReceived = aliquot;
                    break;
                }
            }
            if (aliquotNotReceived != null) {
                palletScanned.put(new RowColPos(0, 0), new PalletCell(
                    new ScanCell(0, 0, aliquotNotReceived.getInventoryId())));
            }
        }
        return palletScanned;
    }
}
