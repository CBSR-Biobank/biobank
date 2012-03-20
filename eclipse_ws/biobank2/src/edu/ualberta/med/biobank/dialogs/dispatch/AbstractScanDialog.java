package edu.ualberta.med.biobank.dialogs.dispatch;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.observable.value.WritableValue;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
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

import edu.ualberta.med.biobank.BiobankPlugin;
import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.scanprocess.CellInfo;
import edu.ualberta.med.biobank.common.action.scanprocess.CellInfoStatus;
import edu.ualberta.med.biobank.common.action.scanprocess.result.CellProcessResult;
import edu.ualberta.med.biobank.common.action.scanprocess.result.ProcessResult;
import edu.ualberta.med.biobank.common.action.scanprocess.result.ScanProcessResult;
import edu.ualberta.med.biobank.common.util.RowColPos;
import edu.ualberta.med.biobank.common.wrappers.CenterWrapper;
import edu.ualberta.med.biobank.common.wrappers.ContainerLabelingSchemeWrapper;
import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.forms.utils.PalletScanManagement;
import edu.ualberta.med.biobank.gui.common.BgcPlugin;
import edu.ualberta.med.biobank.gui.common.dialogs.BgcBaseDialog;
import edu.ualberta.med.biobank.gui.common.widgets.BgcBaseText;
import edu.ualberta.med.biobank.validators.ScannerBarcodeValidator;
import edu.ualberta.med.biobank.widgets.grids.ScanPalletWidget;
import edu.ualberta.med.biobank.widgets.grids.cell.PalletCell;
import edu.ualberta.med.biobank.widgets.grids.cell.UICellStatus;
import edu.ualberta.med.scannerconfig.dmscanlib.ScanCell;
import edu.ualberta.med.scannerconfig.preferences.scanner.profiles.ProfileManager;

public abstract class AbstractScanDialog<T extends ModelWrapper<?>> extends
    BgcBaseDialog {

    private static final String TITLE = Messages.AbstractScanDialog_title;

    private BgcBaseText plateToScanText;

    private String plateToScan;

    private PalletScanManagement palletScanManagement;
    protected ScanPalletWidget spw;
    protected T currentShipment;
    private IObservableValue scanHasBeenLaunchedValue = new WritableValue(
        Boolean.FALSE, Boolean.class);
    private IObservableValue scanOkValue = new WritableValue(Boolean.TRUE,
        Boolean.class);
    private IObservableValue hasValues = new WritableValue(Boolean.FALSE,
        Boolean.class);

    private Button scanButton;
    private Button scanTubeAloneSwitch;
    private boolean rescanMode = false;

    protected CenterWrapper<?> currentSite;

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
                setScanHasBeenLaunched(true);
                AbstractScanDialog.this.processScanResult(monitor,
                    AbstractScanDialog.this.currentSite);
                setHasValues();
            }

            @Override
            protected Map<RowColPos, PalletCell> getFakeScanCells()
                throws Exception {
                return AbstractScanDialog.this.getFakeScanCells();
            }

            @Override
            protected void afterScanAndProcess() {
                Display.getDefault().asyncExec(new Runnable() {
                    @Override
                    public void run() {
                        spw.setCells(getCells());
                        setRescanMode(true);
                    }
                });
            }

            @Override
            protected void postprocessScanTubeAlone(PalletCell cell)
                throws Exception {
                AbstractScanDialog.this.postprocessScanTubeAlone(cell);
                setHasValues();
            }

            @Override
            protected boolean canScanTubeAlone(PalletCell cell) {
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
            scanButton.setText(Messages.AbstractScanDialog_retryScan_label);
        } else {
            String scanButtonText =
                Messages.AbstractScanDialog_launchScan_label;
            if (!BiobankPlugin.isRealScanEnabled()) {
                scanButtonText = Messages.AbstractScanDialog_fakeScan_label;
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

    protected abstract Map<RowColPos, PalletCell> getFakeScanCells()
        throws Exception;

    protected void processScanResult(IProgressMonitor monitor,
        CenterWrapper<?> currentCenter) throws Exception {
        if (checkBeforeProcessing(currentCenter)) {
            Map<RowColPos, PalletCell> cells = getCells();
            // conversion for server side call
            Map<RowColPos, edu.ualberta.med.biobank.common.action.scanprocess.CellInfo> serverCells =
                null;
            if (cells != null) {
                serverCells =
                    new HashMap<RowColPos, edu.ualberta.med.biobank.common.action.scanprocess.CellInfo>();
                for (Entry<RowColPos, PalletCell> entry : cells.entrySet()) {
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
                    RowColPos rcp = entry.getKey();
                    monitor.subTask(NLS.bind(
                        Messages.AbstractScanDialog_processCell_task_position,
                        ContainerLabelingSchemeWrapper.rowColToSbs(rcp)));
                    PalletCell palletCell = cells.get(entry.getKey());
                    CellInfo servercell = entry.getValue();
                    if (palletCell == null) { // can happened if missing
                        palletCell = new PalletCell(new ScanCell(
                            servercell.getRow(), servercell.getCol(),
                            servercell.getValue()));
                        cells.put(rcp, palletCell);
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

    protected void specificScanPosProcess(PalletCell palletCell) {
        // default do nothing
    }

    protected boolean checkBeforeProcessing(CenterWrapper<?> currentCenter)
        throws Exception {
        return true;
    }

    @Override
    protected void createDialogAreaInternal(Composite parent) throws Exception {
        Composite contents = new Composite(parent, SWT.NONE);
        contents.setLayout(new GridLayout(2, false));
        contents.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        createCustomDialogPreContents(contents);

        plateToScanText =
            (BgcBaseText) createBoundWidgetWithLabel(contents,
                BgcBaseText.class, SWT.NONE,
                Messages.AbstractScanDialog_plateToScan_label, new String[0],
                this, "plateToScan", new ScannerBarcodeValidator( //$NON-NLS-1$
                    Messages.AbstractScanDialog_plateToScan_validationMsg));
        plateToScanText.addListener(SWT.DefaultSelection, new Listener() {
            @Override
            public void handleEvent(Event e) {
                if (scanButton.isEnabled()) {
                    launchScan();
                }
            }
        });

        String scanButtonText = Messages.AbstractScanDialog_launchScan_label;
        if (!BiobankPlugin.isRealScanEnabled()) {
            scanButtonText = Messages.AbstractScanDialog_fakeScan_label;
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

        createScanTubeAloneButton(contents);

        spw = new ScanPalletWidget(contents, getPalletCellStatus());
        GridData gd = new GridData();
        gd.horizontalSpan = 2;
        spw.setLayoutData(gd);

        spw.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseDoubleClick(MouseEvent e) {
                if (isScanHasBeenLaunched())
                    palletScanManagement.scanTubeAlone(e);
            }
        });

        widgetCreator.addBooleanBinding(new WritableValue(Boolean.FALSE,
            Boolean.class), scanOkValue,
            Messages.AbstractScanDialog_scanResults_errors_msg, IStatus.ERROR);
        widgetCreator.addBooleanBinding(new WritableValue(Boolean.FALSE,
            Boolean.class), scanHasBeenLaunchedValue,
            Messages.AbstractScanDialog_scan_validation_msg, IStatus.ERROR);
        widgetCreator.addBooleanBinding(new WritableValue(Boolean.FALSE,
            Boolean.class), hasValues, Messages.AbstractScanDialog_no_values,
            IStatus.ERROR);

    }

    protected void createCustomDialogPreContents(Composite parent) {
        // default does nothing
    }

    protected abstract List<UICellStatus> getPalletCellStatus();

    private void launchScan() {
        setScanOkValue(false);
        palletScanManagement.launchScanAndProcessResult(plateToScan,
            ProfileManager.ALL_PROFILE_NAME, isRescanMode());
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
            }
        });
    }

    private boolean isScanHasBeenLaunched() {
        return scanHasBeenLaunchedValue.getValue().equals(true);
    }

    protected Map<RowColPos, PalletCell> getCells() {
        return palletScanManagement.getCells();
    }

    protected void redrawPallet() {
        spw.redraw();
    }

    @Override
    protected void createButtonsForButtonBar(Composite parent) {
        createButton(parent, IDialogConstants.CANCEL_ID,
            Messages.AbstractScanDialog_cancelPallet_label, false);
        createButton(parent, IDialogConstants.PROCEED_ID,
            getProceedButtonlabel(), false);
        createButton(parent, IDialogConstants.NEXT_ID,
            Messages.AbstractScanDialog_startPallet_label, false);
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

    @Override
    protected void buttonPressed(int buttonId) {
        if (IDialogConstants.CANCEL_ID == buttonId)
            super.buttonPressed(buttonId);
        else if (IDialogConstants.PROCEED_ID == buttonId) {
            try {
                doProceed();
            } catch (Exception e) {
                BgcPlugin.openAsyncError(
                    Messages.AbstractScanDialog_proceedError_title, e);
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

    protected void createScanTubeAloneButton(Composite parent) {
        scanTubeAloneSwitch = new Button(parent, SWT.NONE);
        GridData gd = new GridData();
        gd.verticalAlignment = SWT.TOP;
        gd.horizontalSpan = 2;
        gd.horizontalAlignment = SWT.RIGHT;
        scanTubeAloneSwitch.setLayoutData(gd);
        scanTubeAloneSwitch.setText(""); //$NON-NLS-1$
        scanTubeAloneSwitch.setImage(BgcPlugin.getDefault().getImageRegistry()
            .get(BgcPlugin.IMG_SCAN_EDIT));
        scanTubeAloneSwitch.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseDown(MouseEvent e) {
                if (isScanHasBeenLaunched()) {
                    palletScanManagement.toggleScanTubeAloneMode();
                    if (palletScanManagement.isScanTubeAloneMode()) {
                        scanTubeAloneSwitch.setImage(BgcPlugin.getDefault()
                            .getImageRegistry()
                            .get(BgcPlugin.IMG_SCAN_CLOSE_EDIT));
                    } else {
                        scanTubeAloneSwitch.setImage(BgcPlugin.getDefault()
                            .getImageRegistry().get(BgcPlugin.IMG_SCAN_EDIT));
                    }
                }
            }
        });
        scanTubeAloneSwitch.setVisible(false);
    }

    protected boolean canScanTubeAlone(PalletCell cell) {
        return cell == null || cell.getStatus() == UICellStatus.EMPTY
            || cell.getStatus() == UICellStatus.MISSING;
    }

    protected void postprocessScanTubeAlone(PalletCell cell) throws Exception {
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
        palletScanManagement.onReset();
    }
}
