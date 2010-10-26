package edu.ualberta.med.biobank.dialogs.dispatch;

import java.util.List;
import java.util.Map;

import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.observable.value.WritableValue;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
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
import org.eclipse.ui.PlatformUI;

import edu.ualberta.med.biobank.BioBankPlugin;
import edu.ualberta.med.biobank.common.util.RowColPos;
import edu.ualberta.med.biobank.common.wrappers.DispatchShipmentWrapper;
import edu.ualberta.med.biobank.dialogs.BiobankDialog;
import edu.ualberta.med.biobank.dialogs.ScanOneTubeDialog;
import edu.ualberta.med.biobank.forms.Messages;
import edu.ualberta.med.biobank.forms.utils.PalletScanManagement;
import edu.ualberta.med.biobank.model.CellStatus;
import edu.ualberta.med.biobank.model.PalletCell;
import edu.ualberta.med.biobank.validators.ScannerBarcodeValidator;
import edu.ualberta.med.biobank.widgets.BiobankText;
import edu.ualberta.med.biobank.widgets.grids.ScanPalletWidget;
import edu.ualberta.med.scannerconfig.dmscanlib.ScanCell;

public abstract class AbstractDispatchScanDialog extends BiobankDialog {

    private BiobankText plateToScanText;

    private String plateToScan;

    private PalletScanManagement palletScanManagement;
    protected ScanPalletWidget spw;
    protected DispatchShipmentWrapper currentShipment;
    private IObservableValue scanHasBeenLaunchedValue = new WritableValue(
        Boolean.FALSE, Boolean.class);
    private IObservableValue scanOkValue = new WritableValue(Boolean.TRUE,
        Boolean.class);

    private Button scanButton;
    private Button scanTubeAloneSwitch;
    private boolean scanTubeAloneMode = false;
    private boolean rescanMode = false;

    public AbstractDispatchScanDialog(Shell parentShell,
        final DispatchShipmentWrapper currentShipment) {
        super(parentShell);
        this.currentShipment = currentShipment;
        palletScanManagement = new PalletScanManagement() {

            @Override
            protected void beforeThreadStart() {
                AbstractDispatchScanDialog.this.beforeScanThreadStart();
            }

            @Override
            protected void processScanResult(IProgressMonitor monitor)
                throws Exception {
                setScanHasBeenLaunched(true);
                AbstractDispatchScanDialog.this.processScanResult(monitor);
            }

            @Override
            protected Map<RowColPos, PalletCell> getFakeScanCells()
                throws Exception {
                return AbstractDispatchScanDialog.this.getFakeScanCells();
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
        };
    }

    protected void setRescanMode(boolean isOn) {
        if (isOn) {
            scanButton.setText("Retry scan");
        } else {
            String scanButtonText = "Launch Scan";
            if (!BioBankPlugin.isRealScanEnabled()) {
                scanButtonText = "Fake scan";
            }
            scanButton.setText(scanButtonText);
        }
        rescanMode = isOn;
    }

    protected void beforeScanThreadStart() {

    }

    protected boolean isRescanMode() {
        return rescanMode;
    }

    protected abstract Map<RowColPos, PalletCell> getFakeScanCells()
        throws Exception;

    protected abstract void processScanResult(IProgressMonitor monitor)
        throws Exception;

    @Override
    protected void createDialogAreaInternal(Composite parent) throws Exception {
        Composite contents = new Composite(parent, SWT.NONE);
        contents.setLayout(new GridLayout(3, false));
        contents.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        createCustomDialogContents(contents);

        plateToScanText = (BiobankText) createBoundWidgetWithLabel(contents,
            BiobankText.class, SWT.NONE,
            Messages.getString("linkAssign.plateToScan.label"), //$NON-NLS-1$
            new String[0], this, "plateToScan", new ScannerBarcodeValidator(
                Messages.getString("linkAssign.plateToScan.validationMsg"))); //$NON-NLS-1$
        plateToScanText.addListener(SWT.DefaultSelection, new Listener() {
            @Override
            public void handleEvent(Event e) {
                if (scanButton.isEnabled()) {
                    launchScan();
                }
            }
        });

        String scanButtonText = "Launch Scan";
        if (!BioBankPlugin.isRealScanEnabled()) {
            scanButtonText = "Fake scan"; //$NON-NLS-1$
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
        GridData gd = new GridData();
        gd.horizontalSpan = 3;
        gd.horizontalAlignment = SWT.RIGHT;
        scanTubeAloneSwitch.setLayoutData(gd);

        spw = new ScanPalletWidget(contents, getPalletCellStatus());
        gd = new GridData();
        gd.horizontalSpan = 3;
        spw.setLayoutData(gd);

        spw.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseDoubleClick(MouseEvent e) {
                if (scanTubeAloneMode) {
                    scanTubeAlone(e);
                }
            }
        });

        widgetCreator.addBooleanBinding(new WritableValue(Boolean.FALSE,
            Boolean.class), scanOkValue,
            "Error in scan result. Please keep only aliquots with no errors.",
            IStatus.ERROR);
        widgetCreator.addBooleanBinding(new WritableValue(Boolean.FALSE,
            Boolean.class), scanHasBeenLaunchedValue,
            "Scan should be launched", IStatus.ERROR);

    }

    protected void createCustomDialogContents(
        @SuppressWarnings("unused") Composite parent) {
    }

    protected abstract List<CellStatus> getPalletCellStatus();

    private void launchScan() {
        setScanOkValue(false);
        palletScanManagement.launchScanAndProcessResult(plateToScan, "All",
            isRescanMode());
    }

    protected void startNewPallet() {
        spw.setCells(null);
        scanHasBeenLaunchedValue.setValue(false);
    }

    protected void setScanOkValue(final boolean resOk) {
        Display.getDefault().asyncExec(new Runnable() {
            @Override
            public void run() {
                scanOkValue.setValue(resOk);
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
            "Cancel current pallet", false);
        createButton(parent, IDialogConstants.PROCEED_ID,
            getProceedButtonlabel(), false);
        createButton(parent, IDialogConstants.NEXT_ID, "Start next Pallet",
            false);
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
        return BioBankPlugin.getDefault().isValidPlateBarcode(
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
                BioBankPlugin.openAsyncError("Error", e);
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
        scanTubeAloneSwitch.setLayoutData(gd);
        scanTubeAloneSwitch.setText("");
        scanTubeAloneSwitch.setImage(BioBankPlugin.getDefault()
            .getImageRegistry().get(BioBankPlugin.IMG_SCAN_EDIT));
        scanTubeAloneSwitch.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseDown(MouseEvent e) {
                if (isScanHasBeenLaunched()) {
                    scanTubeAloneMode = !scanTubeAloneMode;
                    if (scanTubeAloneMode) {
                        scanTubeAloneSwitch.setImage(BioBankPlugin.getDefault()
                            .getImageRegistry()
                            .get(BioBankPlugin.IMG_SCAN_CLOSE_EDIT));
                    } else {
                        scanTubeAloneSwitch.setImage(BioBankPlugin.getDefault()
                            .getImageRegistry()
                            .get(BioBankPlugin.IMG_SCAN_EDIT));
                    }
                }
            }
        });
    }

    protected void scanTubeAlone(MouseEvent e) {
        if (scanTubeAloneMode && isScanHasBeenLaunched()) {
            RowColPos rcp = ((ScanPalletWidget) e.widget)
                .getPositionAtCoordinates(e.x, e.y);
            Map<RowColPos, PalletCell> cells = palletScanManagement.getCells();
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
                        // TODO: log this?
                        // appendLogNLS("linkAssign.activitylog.scanTubeAlone",
                        // value,
                        // ContainerLabelingSchemeWrapper.rowColToSbs(rcp));
                        try {
                            postprocessScanTubeAlone(cell);
                        } catch (Exception ex) {
                            BioBankPlugin.openAsyncError("Scan tube error", ex);
                        }
                    }
                }
            }
        }
    }

    protected boolean canScanTubeAlone(PalletCell cell) {
        return cell == null || cell.getStatus() == CellStatus.EMPTY
            || cell.getStatus() == CellStatus.MISSING;
    }

    @SuppressWarnings("unused")
    protected void postprocessScanTubeAlone(PalletCell cell) throws Exception {
        spw.redraw();
    }

    private String scanTubeAloneDialog(RowColPos rcp) {
        ScanOneTubeDialog dlg = new ScanOneTubeDialog(PlatformUI.getWorkbench()
            .getActiveWorkbenchWindow().getShell(),
            palletScanManagement.getCells(), rcp);
        if (dlg.open() == Dialog.OK) {
            return dlg.getScannedValue();
        }
        return null;
    }

    protected void resetScan() {
        spw.setCells(null);
        palletScanManagement.reset();
    }
}
