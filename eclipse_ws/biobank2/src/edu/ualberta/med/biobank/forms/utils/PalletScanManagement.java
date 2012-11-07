package edu.ualberta.med.biobank.forms.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.ui.PlatformUI;
import org.springframework.remoting.RemoteConnectFailureException;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import edu.ualberta.med.biobank.BiobankPlugin;
import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.action.scanprocess.CellInfoStatus;
import edu.ualberta.med.biobank.common.util.StringUtil;
import edu.ualberta.med.biobank.common.wrappers.ContainerLabelingSchemeWrapper;
import edu.ualberta.med.biobank.common.wrappers.ContainerWrapper;
import edu.ualberta.med.biobank.common.wrappers.SpecimenWrapper;
import edu.ualberta.med.biobank.dialogs.ScanOneTubeDialog;
import edu.ualberta.med.biobank.gui.common.BgcPlugin;
import edu.ualberta.med.biobank.model.Capacity;
import edu.ualberta.med.biobank.model.ContainerType;
import edu.ualberta.med.biobank.model.util.RowColPos;
import edu.ualberta.med.biobank.widgets.grids.ScanPalletWidget;
import edu.ualberta.med.biobank.widgets.grids.well.PalletWell;
import edu.ualberta.med.biobank.widgets.grids.well.UICellStatus;
import edu.ualberta.med.scannerconfig.ScannerConfigPlugin;
import edu.ualberta.med.scannerconfig.dmscanlib.DecodedWell;
import gov.nih.nci.system.applicationservice.ApplicationException;

public class PalletScanManagement {
    private static final I18n i18n = I18nFactory
        .getI18n(PalletScanManagement.class);

    protected Map<RowColPos, PalletWell> wells = new HashMap<RowColPos, PalletWell>();
    private int scansCount = 0;
    private boolean useScanner = true;

    private final boolean scanTubeAloneMode = true;
    private ContainerType type;

    @SuppressWarnings("nls")
    public PalletScanManagement() {
        try {
            this.type = getFakePallet96();
        } catch (ApplicationException e) {
            BgcPlugin.openAsyncError(
                // TR: dialog title
                i18n.tr("Error"),
                // TR: dialog message
                i18n.tr("Unable to load pallet type 96"),
                e);
        }
    }

    private ContainerType getFakePallet96() throws ApplicationException {
        ContainerType ct = new ContainerType();
        ct.setCapacity(new Capacity(8, 12));
        ct.setChildLabelingScheme(ContainerLabelingSchemeWrapper
            .getLabelingSchemeById(SessionManager.getAppService(),
                ContainerLabelingSchemeWrapper.SCHEME_SBS).getWrappedObject());
        return ct;
    }

    public PalletScanManagement(ContainerType containerType) {
        this.type = containerType;
    }

    public void launchScanAndProcessResult(final String plateToScan) {
        launchScanAndProcessResult(plateToScan, false);
    }

    public void launchScanAndProcessResult(final String plateToScan,
        final boolean isRescanMode) {
        IRunnableWithProgress op = new IRunnableWithProgress() {
            @SuppressWarnings("nls")
            @Override
            public void run(IProgressMonitor monitor) {
                monitor.beginTask(
                    // progress monitor message
                    i18n.tr("Scan and process..."),
                    IProgressMonitor.UNKNOWN);
                try {
                    launchScan(monitor, plateToScan, isRescanMode);
                    processScanResult(monitor);
                    afterScanAndProcess();
                } catch (RemoteConnectFailureException exp) {
                    BgcPlugin.openRemoteConnectErrorMessage(exp);
                    scanAndProcessError(null);
                } catch (Exception e) {
                    BgcPlugin.openAsyncError(
                        // dialog title
                        i18n.tr("Scan result error"),
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

    @SuppressWarnings("nls")
    private void launchScan(IProgressMonitor monitor, String plateToScan,
        boolean rescanMode) throws Exception {
        monitor.subTask(
            // progress monitor text
            i18n.tr("Launching scan"));
        beforeScan();
        Map<RowColPos, PalletWell> oldCells = wells;
        if (BiobankPlugin.isRealScanEnabled()) {
            int plateNum = BiobankPlugin.getDefault().getPlateNumber(plateToScan);
            if (plateNum == -1) {
                plateError();
                BgcPlugin.openAsyncError(
                    // dialog title
                    i18n.tr("Scan error"),
                    // dialog message
                    i18n.tr("Plate with barcode {0} is not enabled",plateToScan));
                return;
            }
            Set<DecodedWell> scanCells = null;
            try {
                scanCells = ScannerConfigPlugin.decodePlate(plateNum);
                wells = PalletWell.convertArray(scanCells);
            } catch (Exception ex) {
                BgcPlugin
                .openAsyncError(
                    // dialog title
                    i18n.tr("Scan error"),
                    ex,
                    // dialog message
                    i18n.tr("Barcodes can still be scanned with the handheld 2D scanner."));
                return;
            } finally {
                scansCount++;
                afterScanBeforeMerge();
            }
        } else {
            wells = getFakeDecodedWells();
            scansCount++;
            afterScanBeforeMerge();
        }
        Map<String, PalletWell> cellValues = getValuesMap(wells);
        if (rescanMode && oldCells != null) {
            // rescan: merge previous scan with new in case the scanner
            // wasn't able to scan well
            boolean rescanDifferent = false;
            for (Entry<RowColPos, PalletWell> entry : oldCells.entrySet()) {
                RowColPos rcp = entry.getKey();
                PalletWell oldScannedCell = entry.getValue();
                PalletWell newScannedCell = wells.get(rcp);
                boolean copyOldValue = false;
                if (PalletWell.hasValue(oldScannedCell)) {
                    copyOldValue = true;
                    if (PalletWell.hasValue(newScannedCell)
                        && !oldScannedCell.getValue().equals(
                            newScannedCell.getValue())) {
                        // Different values at same position
                        oldScannedCell
                        .setInformation((oldScannedCell.getInformation() != null ? oldScannedCell
                            .getInformation()
                            : StringUtil.EMPTY_STRING)
                            + " "
                            + i18n.tr("Rescanned value is different"));
                        oldScannedCell.setStatus(CellInfoStatus.ERROR);
                        rescanDifferent = true;

                    } else if (!PalletWell.hasValue(newScannedCell)) {
                        // previous position has value - new has none
                        PalletWell newPosition = cellValues.get(oldScannedCell
                            .getValue());
                        if (newPosition != null) {
                            // still there but moved to another position, so
                            // don't copy previous scanned position
                            copyOldValue = false;
                        }
                    }
                }
                if (copyOldValue) {
                    wells.put(rcp, oldScannedCell);
                }
            }
            if (rescanDifferent)
                throw new Exception(
                    // exception message
                    i18n.tr("Scan error: Previously scanned specimens has been replaced. Please cancel and start again."));
        }
        afterSuccessfulScan();
    }

    @SuppressWarnings("nls")
    public void scanTubeAlone(MouseEvent e) {
        if (isScanTubeAloneMode()) {
            RowColPos rcp = ((ScanPalletWidget) e.widget).getPositionAtCoordinates(e.x, e.y);
            if (rcp != null) {
                PalletWell cell = wells.get(rcp);
                if (canScanTubeAlone(cell)) {
                    String value = scanTubeAloneDialog(rcp);
                    if (value != null && !value.isEmpty()) {
                        if (cell == null) {
                            cell = new PalletWell(new DecodedWell(rcp.getRow(), rcp.getCol(), value));
                            wells.put(rcp, cell);
                        } else {
                            cell.setValue(value);
                        }
                        try {
                            postprocessScanTubeAlone(cell);
                        } catch (Exception ex) {
                            BgcPlugin.openAsyncError(
                                // dialog title
                                i18n.tr("Scan tube error"),
                                ex);
                        }
                    }
                }
            }
        }
    }

    protected boolean canScanTubeAlone(
        @SuppressWarnings("unused") PalletWell cell) {
        return true;
    }

    private String scanTubeAloneDialog(RowColPos rcp) {
        ScanOneTubeDialog dlg = new ScanOneTubeDialog(PlatformUI.getWorkbench()
            .getActiveWorkbenchWindow().getShell(), wells, rcp, type);
        if (dlg.open() == Dialog.OK) {
            return dlg.getScannedValue();
        }
        return null;
    }

    private Map<String, PalletWell> getValuesMap(
        Map<RowColPos, PalletWell> cells) {
        Map<String, PalletWell> valuesMap = new HashMap<String, PalletWell>();
        for (Entry<RowColPos, PalletWell> entry : cells.entrySet()) {
            PalletWell cell = entry.getValue();
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

    protected Map<RowColPos, PalletWell> getFakeDecodedWells() throws Exception {
        return null;
    }

    @SuppressWarnings("unused")
    protected void processScanResult(IProgressMonitor monitor) throws Exception {
        // default does nothing
    }

    @SuppressWarnings("unused")
    protected void postprocessScanTubeAlone(PalletWell cell) throws Exception {
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

    public Map<RowColPos, PalletWell> getCells() {
        return wells;
    }

    public void onReset() {
        scansCount = 0;
        initCells();
    }

    public void setUseScanner(boolean useScanner) {
        this.useScanner = useScanner;
    }

    private void initCells() {
        wells = new HashMap<RowColPos, PalletWell>();
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
            wells.clear();
            for (Entry<RowColPos, SpecimenWrapper> entry : container
                .getSpecimens().entrySet()) {
                RowColPos rcp = entry.getKey();
                PalletWell cell =
                    new PalletWell(new DecodedWell(rcp.getRow(), rcp.getCol(),
                        entry.getValue().getInventoryId()));
                cell.setSpecimen(entry.getValue());
                cell.setStatus(UICellStatus.FILLED);
                wells.put(rcp, cell);
            }
        }
    }

    public void setContainerType(ContainerType containerType) {
        this.type = containerType;
    }

    public ContainerType getContainerType() {
        return type;
    }
}
