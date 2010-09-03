package edu.ualberta.med.biobank.forms;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.observable.value.WritableValue;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.ComboViewer;
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
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.ui.PlatformUI;
import org.springframework.remoting.RemoteConnectFailureException;

import edu.ualberta.med.biobank.BioBankPlugin;
import edu.ualberta.med.biobank.common.util.LabelingScheme;
import edu.ualberta.med.biobank.common.util.RowColPos;
import edu.ualberta.med.biobank.dialogs.ScanOneTubeDialog;
import edu.ualberta.med.biobank.model.AliquotCellStatus;
import edu.ualberta.med.biobank.model.PalletCell;
import edu.ualberta.med.biobank.preferences.PreferenceConstants;
import edu.ualberta.med.biobank.validators.ScannerBarcodeValidator;
import edu.ualberta.med.biobank.widgets.BiobankText;
import edu.ualberta.med.biobank.widgets.CancelConfirmWidget;
import edu.ualberta.med.biobank.widgets.grids.ScanPalletWidget;
import edu.ualberta.med.scannerconfig.ScannerConfigPlugin;
import edu.ualberta.med.scannerconfig.dmscanlib.ScanCell;
import edu.ualberta.med.scannerconfig.preferences.profiles.ProfileManager;

public abstract class AbstractPalletAliquotAdminForm extends
    AbstractAliquotAdminForm {

    private BiobankText plateToScanText;
    private Button scanButton;
    private String scanButtonTitle;

    protected CancelConfirmWidget cancelConfirmWidget;

    private static IObservableValue plateToScanValue = new WritableValue("", //$NON-NLS-1$
        String.class);
    private IObservableValue canLaunchScanValue = new WritableValue(
        Boolean.FALSE, Boolean.class);
    private IObservableValue scanHasBeenLaunchedValue = new WritableValue(
        Boolean.FALSE, Boolean.class);
    private IObservableValue scanValidValue = new WritableValue(Boolean.TRUE,
        Boolean.class);

    private String currentPlateToScan;

    private boolean rescanMode = false;

    protected Map<RowColPos, PalletCell> cells;

    // the pallet container type name contains this text
    protected String palletNameContains = ""; //$NON-NLS-1$

    private boolean scanTubeAloneMode = false;

    private Label scanTubeAloneSwitch;

    protected ComboViewer profilesCombo;
    private String selectedProfile;

    IPropertyChangeListener propertyListener = new IPropertyChangeListener() {

        @Override
        public void propertyChange(PropertyChangeEvent event) {
            int plateEnabledCount = 0;

            for (int i = 0; i < edu.ualberta.med.scannerconfig.preferences.PreferenceConstants.SCANNER_PALLET_ENABLED.length; ++i) {
                if (!event
                    .getProperty()
                    .equals(
                        edu.ualberta.med.scannerconfig.preferences.PreferenceConstants.SCANNER_PALLET_ENABLED[i]))
                    continue;

                int plateId = i + 1;
                if (ScannerConfigPlugin.getDefault().getPlateEnabled(plateId)) {
                    ++plateEnabledCount;
                }
            }

            // force an error check
            String plateText = plateToScanText.getText();
            plateToScanText.setText("");
            plateToScanText.setText(plateText);
        }
    };

    @Override
    protected void init() throws Exception {
        super.init();
        IPreferenceStore store = BioBankPlugin.getDefault()
            .getPreferenceStore();
        palletNameContains = store
            .getString(PreferenceConstants.PALLET_SCAN_CONTAINER_NAME_CONTAINS);

        ScannerConfigPlugin.getDefault().getPreferenceStore()
            .addPropertyChangeListener(propertyListener);
    }

    @Override
    public void dispose() {
        ScannerConfigPlugin.getDefault().getPreferenceStore()
            .removePropertyChangeListener(propertyListener);
        super.dispose();
    }

    @Override
    public boolean onClose() {
        if (finished || BioBankPlugin.getPlatesEnabledCount() != 1) {
            plateToScanValue.setValue("");
        }
        return super.onClose();
    }

    protected void setRescanMode() {
        scanButton.setText("Retry scan");
        rescanMode = true;
        disableFields();
    }

    protected abstract void disableFields();

    protected void createScanButton(Composite parent) {
        scanButtonTitle = Messages.getString("linkAssign.scanButton.text");
        if (BioBankPlugin.isRealScanEnabled()) {
            toolkit.createLabel(parent, "Decode Type:");

            Composite composite = toolkit.createComposite(parent);
            composite.setLayout(new GridLayout(2, false));
        } else {
            createFakeOptions(parent);
            scanButtonTitle = "Fake scan"; //$NON-NLS-1$
        }
        scanButton = toolkit.createButton(parent, scanButtonTitle, SWT.PUSH);
        GridData gd = new GridData();
        gd.horizontalSpan = 3;
        gd.widthHint = 100;
        scanButton.setLayoutData(gd);
        scanButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                internalScanAndProcessResult();
            }
        });
        scanButton.setEnabled(false);

        addBooleanBinding(new WritableValue(Boolean.FALSE, Boolean.class),
            canLaunchScanValue,
            Messages.getString("linkAssign.canLaunchScanValidationMsg")); //$NON-NLS-1$
        addBooleanBinding(new WritableValue(Boolean.FALSE, Boolean.class),
            scanHasBeenLaunchedValue,
            Messages.getString("linkAssign.scanHasBeenLaunchedValidationMsg")); //$NON-NLS-1$
        addBooleanBinding(new WritableValue(Boolean.TRUE, Boolean.class),
            scanValidValue,
            Messages.getString("linkAssign.scanValidValidationMsg")); //$NON-NLS-1$
    }

    protected String getProfile() {
        if (profilesCombo == null
            || profilesCombo.getCombo().getItemCount() <= 0
            || profilesCombo.getCombo().getSelectionIndex() < 0)
            return "All";
        else
            return profilesCombo.getCombo().getItem(
                profilesCombo.getCombo().getSelectionIndex());
    }

    protected void createProfileComboBox(Composite fieldsComposite) {

        Label lbl = widgetCreator.createLabel(fieldsComposite, "Profile");
        profilesCombo = widgetCreator
            .createComboViewerWithNoSelectionValidator(fieldsComposite, lbl,
                null, null, "Invalid profile selected", false, null); //$NON-NLS-1$

        GridData gridData = new GridData();
        gridData.grabExcessHorizontalSpace = true;
        gridData.horizontalAlignment = SWT.FILL;
        profilesCombo.getCombo().setLayoutData(gridData);
        loadProfileCombo();
    }

    private void loadProfileCombo() {
        profilesCombo.getCombo().removeAll();

        ArrayList<String> profileList = new ArrayList<String>();
        for (String element : ProfileManager.instance().getProfiles().keySet()) {
            profileList.add(element);

        }
        Collections.sort(profileList); // Alphabetic sort
        for (String element : profileList) {
            profilesCombo.add(element);
        }
        profilesCombo.getCombo().select(0);

    }

    protected void createPlateToScanField(Composite fieldsComposite) {
        plateToScanText = (BiobankText) createBoundWidgetWithLabel(
            fieldsComposite, BiobankText.class, SWT.NONE,
            Messages.getString("linkAssign.plateToScan.label"), //$NON-NLS-1$
            new String[0], plateToScanValue, new ScannerBarcodeValidator(
                Messages.getString("linkAssign.plateToScan.validationMsg"))); //$NON-NLS-1$
        plateToScanText.addListener(SWT.DefaultSelection, new Listener() {
            @Override
            public void handleEvent(Event e) {
                if (scanButton.isEnabled()) {
                    internalScanAndProcessResult();
                }
            }
        });
        GridData gd = (GridData) plateToScanText.getLayoutData();
        gd.horizontalAlignment = SWT.FILL;
        if (((GridLayout) fieldsComposite.getLayout()).numColumns == 3) {
            gd.horizontalSpan = 2;
        }
        plateToScanText.setLayoutData(gd);
    }

    protected void createFakeOptions(
        @SuppressWarnings("unused") Composite fieldsComposite) {

    }

    protected void createCancelConfirmWidget() {
        cancelConfirmWidget = new CancelConfirmWidget(page, this, true);
    }

    protected void internalScanAndProcessResult() {
        saveUINeededInformation();
        selectedProfile = this.getProfile();

        IRunnableWithProgress op = new IRunnableWithProgress() {
            @Override
            public void run(IProgressMonitor monitor) {
                monitor.beginTask("Scan and process...",
                    IProgressMonitor.UNKNOWN);
                try {
                    scanAndProcessResult(monitor);
                } catch (RemoteConnectFailureException exp) {
                    BioBankPlugin.openRemoteConnectErrorMessage(exp);
                    setScanValid(false);
                } catch (Exception e) {
                    BioBankPlugin
                        .openAsyncError(Messages
                            .getString("linkAssign.dialog.scanError.title"), //$NON-NLS-1$
                            e);
                    setScanValid(false);
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
                .getActiveWorkbenchWindow().getShell()).run(true, false, op);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    protected void saveUINeededInformation() {
        currentPlateToScan = plateToScanValue.getValue().toString();
    }

    protected abstract void scanAndProcessResult(IProgressMonitor monitor)
        throws Exception;

    protected void launchScan(IProgressMonitor monitor) throws Exception {
        monitor.subTask("Launching scan");
        setScanNotLauched(true);
        Map<RowColPos, PalletCell> oldCells = cells;
        String msgKey = "linkAssign.activitylog.scanning";//$NON-NLS-1$
        if (isRescanMode()) {
            msgKey = "linkAssign.activitylog.rescanning";//$NON-NLS-1$
        }
        appendLogNLS(msgKey, currentPlateToScan);
        if (BioBankPlugin.isRealScanEnabled()) {
            int plateNum = BioBankPlugin.getDefault().getPlateNumber(
                currentPlateToScan);
            if (plateNum == -1) {
                setScanNotLauched(true);
                BioBankPlugin.openAsyncError("Scan error",
                    "Plate with barcode " + currentPlateToScan
                        + " is not enabled");
                return;
            } else {
                ScanCell[][] scanCells = null;
                try {
                    scanCells = ScannerConfigPlugin.scan(plateNum,
                        selectedProfile);
                    cells = PalletCell.convertArray(scanCells);
                } catch (Exception ex) {
                    BioBankPlugin.openAsyncError("Scan error", //$NON-NLS-1$
                        ex, "You can still define barcodes one  by one.");
                }
            }
        } else {
            launchFakeScan();
        }
        setScanHasBeenLauched(true);
        if (cells == null) {
            cells = new HashMap<RowColPos, PalletCell>();
        } else {
            if (isRescanMode() && oldCells != null) {
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
            appendLogNLS("linkAssign.activitylog.scanRes.total", //$NON-NLS-1$
                cells.keySet().size());
        }
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
        } else {
            form.setMessage(status.getMessage(), IMessageProvider.ERROR);
            cancelConfirmWidget.setConfirmEnabled(false);
            setConfirmEnabled(false);
            scanButton.setEnabled((Boolean) canLaunchScanValue.getValue()
                && fieldsValid());
        }
    }

    protected abstract boolean fieldsValid();

    protected void setScanNotLauched() {
        scanHasBeenLaunchedValue.setValue(false);
        // scanTubeAloneSwitch.setVisible(false);
    }

    protected void setScanNotLauched(boolean async) {
        if (async)
            Display.getDefault().asyncExec(new Runnable() {
                @Override
                public void run() {
                    setScanNotLauched();
                }
            });
        else
            setScanNotLauched();
    }

    protected void setScanValid(final boolean valid) {
        Display.getDefault().asyncExec(new Runnable() {
            @Override
            public void run() {
                scanValidValue.setValue(valid);
            }
        });
    }

    protected boolean isScanValid() {
        return scanValidValue.getValue().equals(true);
    }

    protected void setScanHasBeenLauched() {
        scanHasBeenLaunchedValue.setValue(true);
        // scanTubeAloneSwitch.setVisible(true);
    }

    protected boolean isScanHasBeenLaunched() {
        return scanHasBeenLaunchedValue.getValue().equals(true);
    }

    protected void setScanHasBeenLauched(boolean async) {
        if (async)
            Display.getDefault().asyncExec(new Runnable() {
                @Override
                public void run() {
                    setScanHasBeenLauched();
                }
            });
        else
            setScanHasBeenLauched();
    }

    protected boolean isRescanMode() {
        return rescanMode;
    }

    protected void removeRescanMode() {
        scanButton.setText(scanButtonTitle);
        rescanMode = false;
    }

    protected boolean isPlateValid() {
        return BioBankPlugin.getDefault().isValidPlateBarcode(
            plateToScanText.getText());
    }

    protected void resetPlateToScan() {
        plateToScanText.setText(""); //$NON-NLS-1$
        plateToScanValue.setValue(""); //$NON-NLS-1$
    }

    protected void setCanLaunchScan(boolean canLauch) {
        canLaunchScanValue.setValue(canLauch);
    }

    private String scanTubeAloneDialog(RowColPos rcp) {
        ScanOneTubeDialog dlg = new ScanOneTubeDialog(PlatformUI.getWorkbench()
            .getActiveWorkbenchWindow().getShell(), cells, rcp);
        if (dlg.open() == Dialog.OK) {
            return dlg.getScannedValue();
        }
        return null;
    }

    protected void scanTubeAlone(MouseEvent e) {
        if (scanTubeAloneMode && isScanHasBeenLaunched()) {
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
                        appendLogNLS("linkAssign.activitylog.scanTubeAlone",
                            value, LabelingScheme.rowColToSbs(rcp));
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
        return cell == null || cell.getStatus() == AliquotCellStatus.EMPTY;
    }

    protected abstract void postprocessScanTubeAlone(PalletCell cell)
        throws Exception;

    protected boolean isScanTubeAloneMode() {
        return scanTubeAloneMode;
    }

    protected void createScanTubeAloneButton(Composite parent) {
        scanTubeAloneSwitch = toolkit.createLabel(parent, "", SWT.NONE);
        GridData gd = new GridData();
        gd.verticalAlignment = SWT.TOP;
        scanTubeAloneSwitch.setLayoutData(gd);
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
        // scanTubeAloneSwitch.setVisible(false);
    }

    @Override
    public void reset() throws Exception {
        scanValidValue.setValue(true);
        cells = null;
    }
}
