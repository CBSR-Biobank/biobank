package edu.ualberta.med.biobank.dialogs.dispatch;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.observable.value.WritableValue;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import edu.ualberta.med.biobank.BiobankPlugin;
import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.scanprocess.CellInfo;
import edu.ualberta.med.biobank.common.action.scanprocess.CellInfoStatus;
import edu.ualberta.med.biobank.common.action.scanprocess.result.CellProcessResult;
import edu.ualberta.med.biobank.common.action.scanprocess.result.ProcessResult;
import edu.ualberta.med.biobank.common.action.scanprocess.result.ScanProcessResult;
import edu.ualberta.med.biobank.common.wrappers.CenterWrapper;
import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.forms.utils.PalletScanManagement;
import edu.ualberta.med.biobank.forms.utils.PalletScanManagement.ScanManualOption;
import edu.ualberta.med.biobank.gui.common.BgcPlugin;
import edu.ualberta.med.biobank.gui.common.dialogs.BgcBaseDialog;
import edu.ualberta.med.biobank.gui.common.widgets.BgcBaseText;
import edu.ualberta.med.biobank.model.util.RowColPos;
import edu.ualberta.med.biobank.util.SbsLabeling;
import edu.ualberta.med.biobank.validators.ScannerBarcodeValidator;
import edu.ualberta.med.biobank.widgets.grids.ScanPalletWidget;
import edu.ualberta.med.biobank.widgets.grids.well.PalletWell;
import edu.ualberta.med.biobank.widgets.grids.well.UICellStatus;
import edu.ualberta.med.scannerconfig.dmscanlib.DecodedWell;

public abstract class AbstractScanDialog<T extends ModelWrapper<?>> extends
    BgcBaseDialog {
    private static final I18n i18n = I18nFactory
        .getI18n(AbstractScanDialog.class);

    @SuppressWarnings("nls")
    private static final String TITLE = i18n.tr("Scanning specimens");
    @SuppressWarnings("nls")
    private static final String SCAN_BUTTON_RETRY = i18n.tr("Retry scan");
    @SuppressWarnings("nls")
    private static final String SCAN_BUTTON_LAUNCH = i18n.tr("Launch Scan");
    @SuppressWarnings("nls")
    private static final String SCAN_BUTTON_FAKE = i18n.tr("Fake scan");
    @SuppressWarnings("nls")
    private static final String MONITOR_PROCESSING = i18n
        .tr("Processing position {0}");

    private BgcBaseText plateToScanText;

    private String plateToScan;

    private PalletScanManagement palletScanManagement;
    protected ScanPalletWidget spw;
    protected T currentShipment;
    private final IObservableValue scanHasBeenLaunchedValue =
        new WritableValue(
            Boolean.FALSE, Boolean.class);
    private final IObservableValue scanOkValue = new WritableValue(
        Boolean.TRUE,
        Boolean.class);
    private final IObservableValue hasValues = new WritableValue(Boolean.FALSE,
        Boolean.class);

    private Button scanButton;
    private boolean rescanMode = false;

    protected CenterWrapper<?> currentSite;

    protected RowColPos currentGridDimensions = new RowColPos(RowColPos.ROWS_DEFAULT,
        RowColPos.COLS_DEFAULT);

    public AbstractScanDialog(Shell parentShell, final T currentShipment,
        CenterWrapper<?> currentSite) {
        super(parentShell);
        this.currentShipment = currentShipment;
        this.currentSite = currentSite;
        palletScanManagement = new PalletScanManagement() {

            @Override
            protected void beforeThreadStart() {
                AbstractScanDialog.this.beforeScanThreadStart();
            }

            @Override
            protected void processScanResult(IProgressMonitor monitor)
                throws Exception {
                AbstractScanDialog.this.processScanResult(monitor,
                    AbstractScanDialog.this.currentSite);
                setHasValues();
            }

            @Override
            protected Map<RowColPos, PalletWell> getFakeDecodedWells(String plateToScan)
                throws Exception {
                return AbstractScanDialog.this.getFakeDecodedWells(plateToScan);
            }

            @Override
            protected void afterScanAndProcess() {
                Display.getDefault().asyncExec(new Runnable() {
                    @Override
                    public void run() {
                        spw.setCells(getCells());
                        setScanHasBeenLaunched(true);
                    }
                });
            }

            @Override
            protected void postprocessScanTubesManually(Set<PalletWell> cells)
                throws Exception {
                AbstractScanDialog.this.postprocessScanTubeAlone(cells);
                setHasValues();
            }

            @Override
            protected boolean canScanTubeAlone(PalletWell cell) {
                return AbstractScanDialog.this.canScanTubeAlone(cell);
            }
        };
    }

    @Override
    protected String getTitleAreaTitle() {
        return TITLE;
    }

    @Override
    protected String getDialogShellTitle() {
        return TITLE;
    }

    protected void setRescanMode(boolean isOn) {
        if (isOn) {
            scanButton.setText(SCAN_BUTTON_RETRY);
        } else {
            String scanButtonText = SCAN_BUTTON_LAUNCH;
            if (!BiobankPlugin.isRealScanEnabled()) {
                scanButtonText = SCAN_BUTTON_FAKE;
            }
            scanButton.setText(scanButtonText);
        }
        rescanMode = isOn;
    }

    protected void beforeScanThreadStart() {
        // default does nothing
    }

    protected boolean isRescanMode() {
        return rescanMode;
    }

    protected abstract Map<RowColPos, PalletWell> getFakeDecodedWells(String plateToScan)
        throws Exception;

    protected void processScanResult(IProgressMonitor monitor,
        CenterWrapper<?> currentCenter) throws Exception {
        Assert.isNotNull(SessionManager.getUser().getCurrentWorkingCenter());

        if (checkBeforeProcessing(currentCenter)) {
            Map<RowColPos, PalletWell> cells = getCells();
            // conversion for server side call
            Map<RowColPos, edu.ualberta.med.biobank.common.action.scanprocess.CellInfo> serverCells =
                null;
            if (cells != null) {
                serverCells =
                    new HashMap<RowColPos, edu.ualberta.med.biobank.common.action.scanprocess.CellInfo>();
                for (Entry<RowColPos, PalletWell> entry : cells.entrySet()) {
                    serverCells.put(entry.getKey(), entry.getValue()
                        .transformIntoServerCell());
                }
            }
            // server side call
            ScanProcessResult res = (ScanProcessResult) SessionManager
                .getAppService().doAction(
                    getPalletProcessAction(SessionManager.getUser()
                        .getCurrentWorkingCenter().getId(),
                        serverCells, isRescanMode(),
                        Locale.getDefault()));

            if (cells != null) {
                // for each cell, convert into a client side cell
                for (Entry<RowColPos, edu.ualberta.med.biobank.common.action.scanprocess.CellInfo> entry : res
                    .getCells().entrySet()) {
                    RowColPos pos = entry.getKey();
                    monitor.subTask(MessageFormat.format(MONITOR_PROCESSING,
                        SbsLabeling.fromRowCol(pos)));
                    PalletWell palletCell = cells.get(entry.getKey());
                    CellInfo servercell = entry.getValue();
                    if (palletCell == null) { // can happened if missing
                        palletCell = new PalletWell(pos.getRow(), pos.getCol(), new DecodedWell(
                            servercell.getRow(), servercell.getCol(),
                            servercell.getValue()));
                        cells.put(pos, palletCell);
                    }
                    palletCell
                        .merge(SessionManager.getAppService(), servercell);
                    specificScanPosProcess(palletCell);
                }
            }
            setScanOkValue(res.getProcessStatus() != CellInfoStatus.ERROR);
        } else {
            setScanOkValue(false);
        }
    }

    @SuppressWarnings("unused")
    protected void specificScanPosProcess(PalletWell palletCell) {
        // default do nothing
    }

    @SuppressWarnings("unused")
    protected boolean checkBeforeProcessing(CenterWrapper<?> currentCenter)
        throws Exception {
        return true;
    }

    @SuppressWarnings("nls")
    @Override
    protected void createDialogAreaInternal(Composite parent) throws Exception {
        final Composite contents = new Composite(parent, SWT.NONE);
        contents.setLayout(new GridLayout(2, false));
        contents.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        createCustomDialogPreContents(contents);

        plateToScanText =
            (BgcBaseText) createBoundWidgetWithLabel(contents,
                BgcBaseText.class, SWT.NONE,
                i18n.tr("Plate to scan"), new String[0],
                this, "plateToScan", new ScannerBarcodeValidator(
                    i18n.tr("Enter a valid plate barcode")));
        plateToScanText.addListener(SWT.DefaultSelection, new Listener() {
            @Override
            public void handleEvent(Event e) {
                if (scanButton.isEnabled()) {
                    launchScan();
                }
            }
        });
        plateToScanText.addModifyListener(new ModifyListener() {
            @Override
            public void modifyText(ModifyEvent e) {
                if (checkGridDimensionsChanged()) {
                    spw.dispose();
                    createScanPalletWidget(contents, currentGridDimensions.getRow(),
                        currentGridDimensions.getCol());
                    initializeBounds();
                    contents.layout(true, true);
                }
            }
        });

        String scanButtonText = SCAN_BUTTON_LAUNCH;
        if (!BiobankPlugin.isRealScanEnabled()) {
            scanButtonText = SCAN_BUTTON_FAKE;
        }
        scanButton = new Button(contents, SWT.PUSH);
        scanButton.setText(scanButtonText);
        scanButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                launchScan();
            }
        });
        scanButton.setEnabled(false);

        createScanPalletWidget(contents, SbsLabeling.ROW_DEFAULT, SbsLabeling.COL_DEFAULT);

        widgetCreator
            .addBooleanBinding(
                new WritableValue(Boolean.FALSE, Boolean.class),
                scanOkValue,
                i18n.tr("Error in scan result. Please keep only specimens with no errors."),
                IStatus.ERROR);
        widgetCreator.addBooleanBinding(new WritableValue(Boolean.FALSE,
            Boolean.class), scanHasBeenLaunchedValue,
            i18n.tr("Scan should be launched"), IStatus.ERROR);
        widgetCreator.addBooleanBinding(new WritableValue(Boolean.FALSE,
            Boolean.class), hasValues, i18n.tr("No values scanned"),
            IStatus.ERROR);

    }

    @SuppressWarnings("unused")
    protected void createCustomDialogPreContents(Composite parent) {
        // default does nothing
    }

    protected abstract List<UICellStatus> getPalletCellStatus();

    private void launchScan() {
        setScanOkValue(false);
        palletScanManagement.launchScanAndProcessResult(plateToScan, isRescanMode());
    }

    protected void startNewPallet() {
        spw.setCells(null);
        scanHasBeenLaunchedValue.setValue(false);
        setRescanMode(false);
    }

    protected void setScanOkValue(final boolean resOk) {
        Display.getDefault().asyncExec(new Runnable() {
            @Override
            public void run() {
                scanOkValue.setValue(resOk);
            }
        });
    }

    protected void setHasValues() {
        Display.getDefault().asyncExec(new Runnable() {
            @Override
            public void run() {
                hasValues.setValue(getCells().size() > 0);
            }
        });
    }

    private void setScanHasBeenLaunched(final boolean launched) {
        Display.getDefault().asyncExec(new Runnable() {
            @Override
            public void run() {
                scanHasBeenLaunchedValue.setValue(launched);
                setRescanMode(launched);
                plateToScanText.setEnabled(!launched);
            }
        });
    }

    private boolean isScanHasBeenLaunched() {
        return scanHasBeenLaunchedValue.getValue().equals(true);
    }

    protected Map<RowColPos, PalletWell> getCells() {
        return palletScanManagement.getCells();
    }

    protected void redrawPallet() {
        spw.redraw();
    }

    @SuppressWarnings("nls")
    @Override
    protected void createButtonsForButtonBar(Composite parent) {
        createButton(parent, IDialogConstants.CANCEL_ID,
            i18n.tr("Cancel"), false);
        createButton(parent, IDialogConstants.PROCEED_ID,
            getProceedButtonlabel(), false);
        createButton(parent, IDialogConstants.NEXT_ID,
            i18n.tr("Start next Pallet"), false);
        createButton(parent, IDialogConstants.FINISH_ID,
            IDialogConstants.FINISH_LABEL, false);
    }

    protected abstract String getProceedButtonlabel();

    @Override
    protected void setOkButtonEnabled(boolean enabled) {
        Button proceedButton = getButton(IDialogConstants.PROCEED_ID);
        Button finishButton = getButton(IDialogConstants.FINISH_ID);
        Button nextButton = getButton(IDialogConstants.NEXT_ID);
        if (finishButton != null && !finishButton.isDisposed()) {
            if (canActivateProceedButton())
                proceedButton.setEnabled(enabled);
            else
                proceedButton.setEnabled(false);
            if (canActivateNextAndFinishButton()) {
                finishButton.setEnabled(enabled);
                nextButton.setEnabled(enabled);
            } else {
                finishButton.setEnabled(false);
                nextButton.setEnabled(false);
            }

        } else {
            okButtonEnabled = enabled;
        }
    }

    protected boolean canActivateNextAndFinishButton() {
        return true;
    }

    protected boolean canActivateProceedButton() {
        return true;
    }

    @Override
    protected void handleStatusChanged(IStatus status) {
        super.handleStatusChanged(status);
        scanButton.setEnabled(fieldsValid());
    }

    protected boolean fieldsValid() {
        return isPlateValid();
    }

    private boolean isPlateValid() {
        return BiobankPlugin.getDefault().isValidPlateBarcode(
            plateToScanText.getText());
    };

    @SuppressWarnings("nls")
    @Override
    protected void buttonPressed(int buttonId) {
        if (IDialogConstants.CANCEL_ID == buttonId)
            super.buttonPressed(buttonId);
        else if (IDialogConstants.PROCEED_ID == buttonId) {
            try {
                doProceed();
            } catch (Exception e) {
                BgcPlugin.openAsyncError(
                    i18n.tr("Error"), e);
            }
        } else if (IDialogConstants.FINISH_ID == buttonId) {
            setReturnCode(OK);
            close();
        } else if (IDialogConstants.NEXT_ID == buttonId) {
            startNewPallet();
        }
    }

    public String getPlateToScan() {
        return plateToScan;
    }

    public void setPlateToScan(String plateToScan) {
        this.plateToScan = plateToScan;
    }

    protected abstract void doProceed() throws Exception;

    protected boolean canScanTubeAlone(PalletWell cell) {
        return ((cell == null) || (cell.getStatus() == UICellStatus.EMPTY)
            || (cell.getStatus() == UICellStatus.ERROR)
            || (cell.getStatus() == UICellStatus.MISSING));
    }

    protected void postprocessScanTubeAlone(Set<PalletWell> cells) throws Exception {
        for (PalletWell cell : cells) {
            Assert.isNotNull(SessionManager.getUser().getCurrentWorkingCenter());
            CellProcessResult res = (CellProcessResult) SessionManager
                .getAppService().doAction(
                    getCellProcessAction(SessionManager.getUser()
                        .getCurrentWorkingCenter().getId(),
                        cell.transformIntoServerCell(),
                        Locale.getDefault()));
            cell.merge(SessionManager.getAppService(), res.getCell());
            if (res.getProcessStatus() == CellInfoStatus.ERROR) {
                Button okButton = getButton(IDialogConstants.PROCEED_ID);
                okButton.setEnabled(false);
            }
            specificScanPosProcess(cell);
        }
        spw.redraw();
    }

    protected abstract Action<ProcessResult> getCellProcessAction(
        Integer centerId, CellInfo cell, Locale locale);

    protected abstract Action<ProcessResult> getPalletProcessAction(
        Integer centerId, Map<RowColPos, CellInfo> cells, boolean isRescanMode,
        Locale locale);

    protected void resetScan() {
        if (spw != null)
            spw.setCells(null);
        setScanHasBeenLaunched(false);
        palletScanManagement.onReset();
    }

    private void createScanPalletWidget(Composite contents, int rows, int cols) {
        spw = new ScanPalletWidget(contents, getPalletCellStatus(), rows, cols);
        GridData gd = new GridData();
        gd.horizontalSpan = 2;
        spw.setLayoutData(gd);

        spw.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseDoubleClick(MouseEvent e) {
                if (isScanHasBeenLaunched())
                    palletScanManagement.scanTubesManually(e, ScanManualOption.NO_DUPLICATES);
            }
        });
    }

    /**
     * Returns true if the grid dimensions have changed.
     */
    protected boolean checkGridDimensionsChanged() {
        RowColPos plateDimensions = BiobankPlugin.getDefault().getGridDimensions(
            plateToScanText.getText());

        if (plateDimensions == null) return false;

        if (!currentGridDimensions.equals(plateDimensions)) {
            currentGridDimensions = plateDimensions;
            return true;
        }

        return false;
    }
}
