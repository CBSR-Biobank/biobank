package edu.ualberta.med.biobank.forms.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.ui.PlatformUI;
import org.springframework.remoting.RemoteConnectFailureException;

import edu.ualberta.med.biobank.BiobankPlugin;
import edu.ualberta.med.biobank.Messages;
import edu.ualberta.med.biobank.common.util.RowColPos;
import edu.ualberta.med.biobank.common.wrappers.ContainerWrapper;
import edu.ualberta.med.biobank.common.wrappers.SpecimenWrapper;
import edu.ualberta.med.biobank.dialogs.ScanOneTubeDialog;
import edu.ualberta.med.biobank.gui.common.BiobankGuiCommonPlugin;
import edu.ualberta.med.biobank.widgets.grids.ScanPalletWidget;
import edu.ualberta.med.biobank.widgets.grids.cell.PalletCell;
import edu.ualberta.med.biobank.widgets.grids.cell.UICellStatus;
import edu.ualberta.med.scannerconfig.ScannerConfigPlugin;
import edu.ualberta.med.scannerconfig.dmscanlib.ScanCell;
import edu.ualberta.med.scannerconfig.preferences.scanner.profiles.ProfileManager;
import gov.nih.nci.system.applicationservice.ApplicationException;

public class PalletScanManagement {

    protected Map<RowColPos, PalletCell> cells;
    private int successfulScansCount = 0;
    private boolean useScanner = true;

    private boolean scanTubeAloneMode = false;

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
                    BiobankGuiCommonPlugin.openRemoteConnectErrorMessage(exp);
                    scanAndProcessError(null);
                } catch (Exception e) {
                    BiobankGuiCommonPlugin
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
                BiobankGuiCommonPlugin.openAsyncError("Scan error",
                    "Plate with barcode " + plateToScan + " is not enabled");
                return;
            } else {
                ScanCell[][] scanCells = null;
                try {
                    scanCells = ScannerConfigPlugin.scan(plateNum, profile);
                    cells = PalletCell.convertArray(scanCells);
                    successfulScansCount++;
                } catch (Exception ex) {
                    BiobankGuiCommonPlugin
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
            Map<String, PalletCell> cellValues = getValuesMap(cells);
            if (rescanMode && oldCells != null) {
                // rescan: merge previous scan with new in case the scanner
                // wasn't able to scan well
                for (Entry<RowColPos, PalletCell> entry : oldCells.entrySet()) {
                    RowColPos rcp = entry.getKey();
                    PalletCell oldScannedCell = entry.getValue();
                    PalletCell newScannedCell = cells.get(rcp);
                    boolean copyOldValue = false;
                    if (PalletCell.hasValue(oldScannedCell)) {
                        copyOldValue = true;
                        if (PalletCell.hasValue(newScannedCell)
                            && !oldScannedCell.getValue().equals(
                                newScannedCell.getValue())) {
                            // Different values at same position
                            cells = oldCells;
                            throw new Exception(
                                "Scan Aborted: previously scanned specimens has been replaced. "
                                    + "If this is not a re-scan, reset and start again.");
                        } else if (!PalletCell.hasValue(newScannedCell)) {
                            // previous position has value - new has none
                            PalletCell newPosition = cellValues
                                .get(oldScannedCell.getValue());
                            if (newPosition != null) {
                                // still there but moved to another position, so
                                // don't copy previous scanned position
                                copyOldValue = false;
                            }
                        }
                    }
                    if (copyOldValue) {
                        cells.put(rcp, oldScannedCell);
                    }
                }
            }
            afterScan();
        }
    }

    public void scanTubeAlone(MouseEvent e) {
        if (isScanTubeAloneMode()) {
            RowColPos rcp = ((ScanPalletWidget) e.widget)
                .getPositionAtCoordinates(e.x, e.y);
            if (rcp != null) {
                PalletCell cell = cells.get(rcp);
                if (canScanTubeAlone(cell)) {
                    String value = scanTubeAloneDialog(rcp);
                    if (value != null && !value.isEmpty()) {
                        if (cell == null) {
                            cell = new PalletCell(new ScanCell(rcp.row,
                                rcp.col, value));
                            cells.put(rcp, cell);
                        } else {
                            cell.setValue(value);
                        }
                        try {
                            postprocessScanTubeAlone(cell);
                        } catch (Exception ex) {
                            BiobankGuiCommonPlugin.openAsyncError(
                                "Scan tube error", ex);
                        }
                    }
                }
            }
        }
    }

    protected boolean canScanTubeAlone(
        @SuppressWarnings("unused") PalletCell cell) {
        return true;
    }

    private String scanTubeAloneDialog(RowColPos rcp) {
        ScanOneTubeDialog dlg = new ScanOneTubeDialog(PlatformUI.getWorkbench()
            .getActiveWorkbenchWindow().getShell(), cells, rcp);
        if (dlg.open() == Dialog.OK) {
            return dlg.getScannedValue();
        }
        return null;
    }

    private Map<String, PalletCell> getValuesMap(
        Map<RowColPos, PalletCell> cells) {
        Map<String, PalletCell> valuesMap = new HashMap<String, PalletCell>();
        for (Entry<RowColPos, PalletCell> entry : cells.entrySet()) {
            PalletCell cell = entry.getValue();
            valuesMap.put(cell.getValue(), cell);
        }
        return valuesMap;
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

    @SuppressWarnings("unused")
    protected void postprocessScanTubeAlone(PalletCell cell) throws Exception {

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

    public void onReset() {
        successfulScansCount = 0;
        initCells();
    }

    public void setUseScanner(boolean useScanner) {
        this.useScanner = useScanner;
        initCells();
    }

    private void initCells() {
        if (useScanner)
            cells = null;
        else
            cells = new HashMap<RowColPos, PalletCell>();
    }

    public int getSuccessfulScansCount() {
        return successfulScansCount;
    }

    public void toggleScanTubeAloneMode() {
        scanTubeAloneMode = !scanTubeAloneMode;
    }

    public boolean isScanTubeAloneMode() {
        return scanTubeAloneMode;
    }

    public void initCellsWithContainer(ContainerWrapper container) {
        if (!useScanner) {
            cells.clear();
            try {
                for (Entry<RowColPos, SpecimenWrapper> entry : container
                    .getSpecimens(true).entrySet()) {
                    RowColPos rcp = entry.getKey();
                    PalletCell cell = new PalletCell(new ScanCell(rcp.row,
                        rcp.col, entry.getValue().getInventoryId()));
                    cell.setSpecimen(entry.getValue());
                    cell.setStatus(UICellStatus.FILLED);
                    cells.put(rcp, cell);
                }
            } catch (ApplicationException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
