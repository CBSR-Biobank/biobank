package edu.ualberta.med.biobank.forms;

import java.util.Map;

import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.observable.value.WritableValue;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;
import org.springframework.remoting.RemoteConnectFailureException;

import edu.ualberta.med.biobank.BioBankPlugin;
import edu.ualberta.med.biobank.common.RowColPos;
import edu.ualberta.med.biobank.model.PalletCell;
import edu.ualberta.med.biobank.preferences.PreferenceConstants;
import edu.ualberta.med.biobank.validators.ScannerBarcodeValidator;
import edu.ualberta.med.biobank.widgets.CancelConfirmWidget;
import edu.ualberta.med.scannerconfig.ScannerConfigPlugin;

public abstract class AbstractPalletAliquotAdminForm extends
    AbstractAliquotAdminForm {

    private Text plateToScanText;
    private Button scanButton;
    private String scanButtonTitle;

    private CancelConfirmWidget cancelConfirmWidget;

    private IObservableValue plateToScanValue = new WritableValue("", //$NON-NLS-1$
        String.class);
    private IObservableValue scanLaunchedValue = new WritableValue(
        Boolean.FALSE, Boolean.class);

    private boolean rescanMode = false;

    protected Map<RowColPos, PalletCell> cells;

    // the pallet container type name contains this text
    protected String palletNameContains = ""; //$NON-NLS-1$

    @Override
    protected void init() {
        super.init();
        IPreferenceStore store = BioBankPlugin.getDefault()
            .getPreferenceStore();
        palletNameContains = store
            .getString(PreferenceConstants.PALLET_SCAN_CONTAINER_NAME_CONTAINS);
    }

    protected void setRescanMode() {
        scanButton.setText("Retry scan");
        rescanMode = true;
        disableFields();
    }

    protected abstract void disableFields();

    protected boolean canLaunchScan() {
        return scanButton.isEnabled();
    }

    protected void addScanBindings() {
        addBooleanBinding(new WritableValue(Boolean.FALSE, Boolean.class),
            scanLaunchedValue, Messages
                .getString("linkAssign.scanLaunchValidationMsg")); //$NON-NLS-1$
    }

    protected void createScanButton(Composite oarent) {
        GridData gd = new GridData();
        gd.horizontalAlignment = SWT.FILL;
        plateToScanText.setLayoutData(gd);

        scanButtonTitle = Messages.getString("linkAssign.scanButton.text");
        if (!BioBankPlugin.isRealScanEnabled()) {
            createFakeOptions(oarent);
            scanButtonTitle = "Fake scan"; //$NON-NLS-1$
        }
        scanButton = toolkit.createButton(oarent, scanButtonTitle, SWT.PUSH);
        gd = new GridData();
        gd.horizontalSpan = 2;
        gd.widthHint = 100;
        scanButton.setLayoutData(gd);
        scanButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                internalScanAndProcessResult();
            }
        });
        addScanBindings();
    }

    protected void createPlateToScanField(Composite fieldsComposite) {
        plateToScanText = (Text) createBoundWidgetWithLabel(fieldsComposite,
            Text.class, SWT.NONE, Messages
                .getString("linkAssign.plateToScan.label"), //$NON-NLS-1$
            new String[0], plateToScanValue, new ScannerBarcodeValidator(
                Messages.getString("linkAssign.plateToScan.validationMsg"))); //$NON-NLS-1$
        plateToScanText.addListener(SWT.DefaultSelection, new Listener() {
            public void handleEvent(Event e) {
                if (scanButton.isEnabled()) {
                    internalScanAndProcessResult();
                }
            }
        });
    }

    protected void createFakeOptions(
        @SuppressWarnings("unused") Composite fieldsComposite) {

    }

    protected void createCancelConfirmWidget() {
        cancelConfirmWidget = new CancelConfirmWidget(form.getBody(), this,
            true);
    }

    protected void internalScanAndProcessResult() {
        IRunnableWithProgress op = new IRunnableWithProgress() {
            public void run(IProgressMonitor monitor) {
                monitor.beginTask("Scan and process...",
                    IProgressMonitor.UNKNOWN);
                try {
                    if (isRescanMode()) {
                        appendLog("--- Rescan ---");
                    } else {
                        appendLog("--- New Scan session ---");
                    }
                    scanAndProcessResult(monitor);
                } catch (RemoteConnectFailureException exp) {
                    BioBankPlugin.openRemoteConnectErrorMessage();
                    setScanOk(false);
                } catch (Exception e) {
                    BioBankPlugin.openAsyncError(Messages
                        .getString("linkAssign.dialog.scanError.title"), //$NON-NLS-1$
                        e);
                    setScanOk(false);
                    String msg = e.getMessage();
                    if ((msg == null || msg.isEmpty()) && e.getCause() != null) {
                        msg = e.getCause().getMessage();
                    }
                    appendLog("ERROR: " + msg); //$NON-NLS-1$
                }
                monitor.done();
            }
        };
        try {
            new ProgressMonitorDialog(PlatformUI.getWorkbench()
                .getActiveWorkbenchWindow().getShell()).run(false, false, op);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    protected void setScanOk(@SuppressWarnings("unused") boolean scanOk) {
    }

    protected abstract void scanAndProcessResult(IProgressMonitor monitor)
        throws Exception;

    protected void launchScan(IProgressMonitor monitor) throws Exception {
        monitor.subTask("Launching scan");
        setScanNotLauched();
        Map<RowColPos, PalletCell> oldCells = cells;
        appendLogNLS("linkAssign.activitylog.scanning", //$NON-NLS-1$
            plateToScanValue.getValue().toString());
        if (BioBankPlugin.isRealScanEnabled()) {
            int plateNum = BioBankPlugin.getDefault().getPlateNumber(
                plateToScanValue.getValue().toString());
            cells = PalletCell.convertArray(ScannerConfigPlugin.scan(plateNum));
        } else {
            launchFakeScan();
        }
        if (isRescanMode() && oldCells != null) {
            // rescan: merge previous scan with new in case the scanner wasn't
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
                        "Scan canceled: found different aliquot in previously scanned position. "
                            + "Are you sure this is a rescan. If any doubt, reset page and restart process.");
                }
                if (PalletCell.hasValue(oldScannedCell)) {
                    cells.put(rcp, oldScannedCell);
                }
            }
        }
        setScanHasBeenLauched();
        appendLogNLS("linkAssign.activitylog.scanRes.total", //$NON-NLS-1$
            cells.keySet().size());
    }

    @SuppressWarnings("unused")
    protected void launchFakeScan() throws Exception {

    }

    @Override
    protected void handleStatusChanged(IStatus status) {
        if (status.getSeverity() == IStatus.OK) {
            form.setMessage(getOkMessage(), IMessageProvider.NONE);
            cancelConfirmWidget.setConfirmEnabled(true);
            setConfirmEnabled(true);
            enableScan(true);
        } else {
            form.setMessage(status.getMessage(), IMessageProvider.ERROR);
            cancelConfirmWidget.setConfirmEnabled(false);
            setConfirmEnabled(false);
            enableScan(isPlateValid());
        }
    }

    protected void setScanNotLauched() {
        scanLaunchedValue.setValue(false);
    }

    protected void setScanHasBeenLauched() {
        scanLaunchedValue.setValue(true);
    }

    protected boolean isRescanMode() {
        return rescanMode;
    }

    protected void removeRescanMode() {
        scanButton.setText(scanButtonTitle);
        rescanMode = false;
    }

    protected void enableScan(boolean enabled) {
        scanButton.setEnabled(enabled);
    }

    protected boolean isPlateValid() {
        return BioBankPlugin.getDefault().isValidPlateBarcode(
            plateToScanText.getText());
    }

    protected void resetPlateToScan() {
        plateToScanText.setText(""); //$NON-NLS-1$
        plateToScanValue.setValue(""); //$NON-NLS-1$
    }

    protected void focusOnPlateToScanText() {
        plateToScanText.setFocus();
    }

    protected void focusOnCancelConfirmText() {
        cancelConfirmWidget.setFocus();
    }

    protected CancelConfirmWidget getCancelConfirmWidget() {
        return cancelConfirmWidget;
    }
}
