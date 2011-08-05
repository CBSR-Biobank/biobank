package edu.ualberta.med.biobank.forms.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.ui.PlatformUI;
import org.springframework.remoting.RemoteConnectFailureException;

import edu.ualberta.med.biobank.BiobankPlugin;
import edu.ualberta.med.biobank.common.scanprocess.CellStatus;
import edu.ualberta.med.biobank.common.util.RowColPos;
import edu.ualberta.med.biobank.common.wrappers.ContainerWrapper;
import edu.ualberta.med.biobank.common.wrappers.SpecimenWrapper;
import edu.ualberta.med.biobank.dialogs.ScanOneTubeDialog;
import edu.ualberta.med.biobank.gui.common.BgcPlugin;
import edu.ualberta.med.biobank.widgets.grids.ScanPalletWidget;
import edu.ualberta.med.biobank.widgets.grids.cell.PalletCell;
import edu.ualberta.med.biobank.widgets.grids.cell.UICellStatus;
import edu.ualberta.med.scannerconfig.ScannerConfigPlugin;
import edu.ualberta.med.scannerconfig.dmscanlib.ScanCell;
import edu.ualberta.med.scannerconfig.dmscanlib.ScanCellPos;
import edu.ualberta.med.scannerconfig.preferences.scanner.profiles.ProfileManager;
import gov.nih.nci.system.applicationservice.ApplicationException;

public class PalletScanManagement {

    protected Map<RowColPos, PalletCell> cells = new HashMap<RowColPos, PalletCell>();
    private int scansCount = 0;
    private boolean useScanner = true;

    private boolean scanTubeAloneMode = true;

    public void launchScanAndProcessResult(final String plateToScan) {
        launchScanAndProcessResult(plateToScan,
            ProfileManager.ALL_PROFILE_NAME, false);
    }

    public void launchScanAndProcessResult(final String plateToScan,
        final String profile, final boolean isRescanMode) {
        IRunnableWithProgress op = new IRunnableWithProgress() {
            @Override
            public void run(IProgressMonitor monitor) {
                monitor.beginTask(Messages.PalletScanManagement_scan_progress,
                    IProgressMonitor.UNKNOWN);
                try {
                    launchScan(monitor, plateToScan, profile, isRescanMode);
                    processScanResult(monitor);
                    afterScanAndProcess();
                } catch (RemoteConnectFailureException exp) {
                    BgcPlugin.openRemoteConnectErrorMessage(exp);
                    scanAndProcessError(null);
                } catch (Exception e) {
                    BgcPlugin
                        .openAsyncError(
                            Messages.PalletScanManagement_dialog_scanError_title,
                            e);
                    String msg = e.getMessage();
                    if ((msg == null || msg.isEmpty()) && e.getCause() != null) {
                        msg = e.getCause().getMessage();
                    }
                    scanAndProcessError(Messages.PalletScanManagement_error_title
                        + msg);
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
        monitor.subTask(Messages.PalletScanManagement_launching);
        beforeScan();
        Map<RowColPos, PalletCell> oldCells = cells;
        if (BiobankPlugin.isRealScanEnabled()) {
            int plateNum = BiobankPlugin.getDefault().getPlateNumber(
                plateToScan);
            if (plateNum == -1) {
                plateError();
                BgcPlugin
                    .openAsyncError(
                        Messages.PalletScanManagement_scan_error_title,
                        NLS.bind(
                            Messages.PalletScanManagement_scan_error_msg_notenabled,
                            plateToScan));
                return;
            } else {
                Map<ScanCellPos, ScanCell> scanCells = null;
                try {
                    scanCells = ScannerConfigPlugin.decodePlate(plateNum,
                        profile);
                    cells = PalletCell.convertArray(scanCells);
                } catch (Exception ex) {
                    BgcPlugin.openAsyncError(
                        Messages.PalletScanManagement_scan_error_title, ex,
                        Messages.PalletScanManagement_scan_error_msg_2dScanner);
                    return;
                } finally {
                    scansCount++;
                    afterScanBeforeMerge();
                }
            }
        } else {
            cells = getFakeScanCells();
            scansCount++;
            afterScanBeforeMerge();
        }
        Map<String, PalletCell> cellValues = getValuesMap(cells);
        if (rescanMode && oldCells != null) {
            // rescan: merge previous scan with new in case the scanner
            // wasn't able to scan well
            boolean rescanDifferent = false;
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
                        oldScannedCell
                            .setInformation((oldScannedCell.getInformation() != null ? oldScannedCell
                                .getInformation() : "") //$NON-NLS-1$
                                + " " + Messages.PalletScanManagement_rescan_differnt_msg); //$NON-NLS-1$
                        oldScannedCell.setStatus(CellStatus.ERROR);
                        rescanDifferent = true;

                    } else if (!PalletCell.hasValue(newScannedCell)) {
                        // previous position has value - new has none
                        PalletCell newPosition = cellValues.get(oldScannedCell
                            .getValue());
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
            if (rescanDifferent)
                throw new Exception(
                    Messages.PalletScanManagement_scan_error_previous_different_msg);
        }
        afterSuccessfulScan();
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
                            BgcPlugin.openAsyncError(
                                Messages.PalletScanManagement_tube_error_title,
                                ex);
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
        // default does nothing
    }

    protected void beforeScan() {
        // default does nothing
    }

    @SuppressWarnings("unused")
    protected Map<RowColPos, PalletCell> getFakeScanCells() throws Exception {
        return null;
    }

    @SuppressWarnings("unused")
    protected void processScanResult(IProgressMonitor monitor) throws Exception {
        // default does nothing
    }

    @SuppressWarnings("unused")
    protected void postprocessScanTubeAlone(PalletCell cell) throws Exception {
        // default does nothing
    }

    protected void afterScanBeforeMerge() {
        // default does nothing
    }

    protected void afterSuccessfulScan() {
        // default does nothing
    }

    protected void afterScanAndProcess() {
        // default does nothing
    }

    protected void plateError() {
        // default does nothing
    }

    protected void scanAndProcessError(
        @SuppressWarnings("unused") String errorMsg) {
        // default does nothing
    }

    public Map<RowColPos, PalletCell> getCells() {
        return cells;
    }

    public void onReset() {
        scansCount = 0;
        initCells();
    }

    public void setUseScanner(boolean useScanner) {
        this.useScanner = useScanner;
    }

    private void initCells() {
        cells = new HashMap<RowColPos, PalletCell>();
    }

    public int getScansCount() {
        return scansCount;
    }

    public void toggleScanTubeAloneMode() {
        // might want to remove this toggle thing if users don't ask for it back
        // scanTubeAloneMode = !scanTubeAloneMode;
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
