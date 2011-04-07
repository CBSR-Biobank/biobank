package edu.ualberta.med.biobank.forms.utils;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.ui.PlatformUI;
import org.springframework.remoting.RemoteConnectFailureException;

import edu.ualberta.med.biobank.BiobankPlugin;
import edu.ualberta.med.biobank.Messages;
import edu.ualberta.med.biobank.common.util.RowColPos;
import edu.ualberta.med.biobank.model.PalletCell;
import edu.ualberta.med.scannerconfig.ScannerConfigPlugin;
import edu.ualberta.med.scannerconfig.dmscanlib.ScanCell;
import edu.ualberta.med.scannerconfig.preferences.scanner.profiles.ProfileManager;

public class PalletScanManagement {

    protected Map<RowColPos, PalletCell> cells;
    private int successfulScansCount = 0;

    public void launchScanAndProcessResult(final String plateToScan) {
        launchScanAndProcessResult(plateToScan,
            ProfileManager.ALL_PROFILE_NAME, false);
    }

    public void launchScanAndProcessResult(final String plateToScan,
        final String profile, final boolean isRescanMode) {
        IRunnableWithProgress op = new IRunnableWithProgress() {
            @Override
            public void run(IProgressMonitor monitor) {
                monitor.beginTask("Scan and process...",
                    IProgressMonitor.UNKNOWN);
                try {
                    launchScan(monitor, plateToScan, profile, isRescanMode);
                    processScanResult(monitor);
                    afterScanAndProcess();
                } catch (RemoteConnectFailureException exp) {
                    BiobankPlugin.openRemoteConnectErrorMessage(exp);
                    scanAndProcessError(null);
                } catch (Exception e) {
                    BiobankPlugin
                        .openAsyncError(Messages
                            .getString("linkAssign.dialog.scanError.title"), //$NON-NLS-1$
                            e);
                    String msg = e.getMessage();
                    if ((msg == null || msg.isEmpty()) && e.getCause() != null) {
                        msg = e.getCause().getMessage();
                    }
                    scanAndProcessError("ERROR: " + msg);
                }
                monitor.done();
            }
        };
        try {
            beforeThreadStart();
            new ProgressMonitorDialog(PlatformUI.getWorkbench()
                .getActiveWorkbenchWindow().getShell()).run(true, false, op);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void launchScan(IProgressMonitor monitor, String plateToScan,
        String profile, boolean rescanMode) throws Exception {
        monitor.subTask("Launching scan");
        beforeScan();
        Map<RowColPos, PalletCell> oldCells = cells;
        if (BiobankPlugin.isRealScanEnabled()) {
            int plateNum = BiobankPlugin.getDefault().getPlateNumber(
                plateToScan);
            if (plateNum == -1) {
                plateError();
                BiobankPlugin.openAsyncError("Scan error",
                    "Plate with barcode " + plateToScan + " is not enabled");
                return;
            } else {
                ScanCell[][] scanCells = null;
                try {
                    scanCells = ScannerConfigPlugin.scan(plateNum, profile);
                    cells = PalletCell.convertArray(scanCells);
                    successfulScansCount++;
                } catch (Exception ex) {
                    BiobankPlugin
                        .openAsyncError(
                            "Scan error", //$NON-NLS-1$
                            ex,
                            "Barcodes can still be scanned with the handheld 2D scanner.");
                    return;
                }
            }
        } else {
            cells = getFakeScanCells();
            successfulScansCount++;
        }
        beforeScanMerge();
        if (cells == null) {
            cells = new HashMap<RowColPos, PalletCell>();
        } else {
            if (rescanMode && oldCells != null) {
                // rescan: merge previous scan with new in case the scanner
                // wasn't
                // able to scan well
                for (RowColPos rcp : oldCells.keySet()) {
                    PalletCell oldScannedCell = oldCells.get(rcp);
                    PalletCell newScannedCell = cells.get(rcp);
                    if (PalletCell.hasValue(oldScannedCell)
                        && PalletCell.hasValue(newScannedCell)
                        && !oldScannedCell.getValue().equals(
                            newScannedCell.getValue())) {
                        cells = oldCells;
                        throw new Exception(
                            "Scan Aborted: previously scanned aliquot has been replaced. "
                                + "If this is not a re-scan, reset and start again.");
                    }
                    if (PalletCell.hasValue(oldScannedCell)) {
                        cells.put(rcp, oldScannedCell);
                    }
                }
            }
            afterScan();
        }
    }

    protected void beforeThreadStart() {

    }

    protected void beforeScan() {

    }

    @SuppressWarnings("unused")
    protected Map<RowColPos, PalletCell> getFakeScanCells() throws Exception {
        return null;
    }

    @SuppressWarnings("unused")
    protected void processScanResult(IProgressMonitor monitor) throws Exception {

    }

    protected void beforeScanMerge() {

    }

    protected void afterScan() {

    }

    protected void afterScanAndProcess() {

    }

    protected void plateError() {

    }

    protected void scanAndProcessError(
        @SuppressWarnings("unused") String errorMsg) {

    }

    public Map<RowColPos, PalletCell> getCells() {
        return cells;
    }

    public void reset() {
        cells = null;
        successfulScansCount = 0;
    }

    public int getSuccessfulScansCount() {
        return successfulScansCount;
    }
}
