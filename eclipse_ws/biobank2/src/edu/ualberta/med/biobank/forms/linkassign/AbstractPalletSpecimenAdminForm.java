package edu.ualberta.med.biobank.forms.linkassign;

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
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import edu.ualberta.med.biobank.BiobankPlugin;
import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.scanprocess.CellInfo;
import edu.ualberta.med.biobank.common.action.scanprocess.result.CellProcessResult;
import edu.ualberta.med.biobank.common.action.scanprocess.result.ProcessResult;
import edu.ualberta.med.biobank.common.action.scanprocess.result.ScanProcessResult;
import edu.ualberta.med.biobank.common.util.StringUtil;
import edu.ualberta.med.biobank.common.wrappers.ContainerWrapper;
import edu.ualberta.med.biobank.forms.utils.PalletScanManagement;
import edu.ualberta.med.biobank.gui.common.widgets.BgcBaseText;
import edu.ualberta.med.biobank.model.ContainerType;
import edu.ualberta.med.biobank.model.util.RowColPos;
import edu.ualberta.med.biobank.validators.ScannerBarcodeValidator;
import edu.ualberta.med.biobank.widgets.CancelConfirmWidget;
import edu.ualberta.med.biobank.widgets.grids.well.PalletWell;
import edu.ualberta.med.biobank.widgets.grids.well.UICellStatus;
import edu.ualberta.med.scannerconfig.ScannerConfigPlugin;
import edu.ualberta.med.scannerconfig.dmscanlib.DecodedWell;

public abstract class AbstractPalletSpecimenAdminForm extends
AbstractSpecimenAdminForm {
    private static final I18n i18n = I18nFactory
        .getI18n(AbstractPalletSpecimenAdminForm.class);

    @SuppressWarnings("nls")
    protected static final String PLATE_VALIDATOR = "plate-validator";
    protected BgcBaseText plateToScanText;
    protected Button scanButton;
    private String scanButtonTitle;

    @SuppressWarnings("nls")
    private final ScannerBarcodeValidator scannerBarcodeValidator = new ScannerBarcodeValidator(
        // TR: validation error message
        i18n.tr("Enter a valid plate barcode"));

    protected CancelConfirmWidget cancelConfirmWidget;

    private static String plateToScanSessionString = StringUtil.EMPTY_STRING;

    private final IObservableValue plateToScanValue = new WritableValue(
        plateToScanSessionString, String.class);
    private final IObservableValue canLaunchScanValue = new WritableValue(
        Boolean.TRUE, Boolean.class);
    private final IObservableValue scanHasBeenLaunchedValue =
        new WritableValue(
            Boolean.FALSE, Boolean.class);
    private final IObservableValue scanValidValue = new WritableValue(
        Boolean.TRUE,
        Boolean.class);

    private boolean rescanMode = false;

    private PalletScanManagement palletScanManagement;

    protected ComboViewer profilesCombo;

    private IPropertyChangeListener propertyListener;

    protected String currentPlateToScan;

    // global state of the pallet process
    protected UICellStatus currentScanState = UICellStatus.NOT_INITIALIZED;
    private Label plateToScanLabel;

    @Override
    protected void init() throws Exception {
        super.init();
        Assert.isNotNull(SessionManager.getUser().getCurrentWorkingCenter());
        currentPlateToScan = plateToScanSessionString;
        addScannerPreferencesPropertyListener();
        palletScanManagement = new PalletScanManagement() {

            @Override
            protected void beforeThreadStart() {
                currentPlateToScan = plateToScanValue.getValue().toString();
                AbstractPalletSpecimenAdminForm.this.beforeScanThreadStart();
            }

            @SuppressWarnings("nls")
            @Override
            protected void beforeScan() {
                setScanHasBeenLaunched(false, true);
                String msg =
                    "----SCAN on plate {0}----";
                if (isRescanMode()) {
                    msg =
                        "----RESCAN on plate {0}----";
                }
                appendLog(NLS.bind(msg, currentPlateToScan));

            }

            @Override
            protected Map<RowColPos, PalletWell> getFakeDecodedWells()
                throws Exception {
                return AbstractPalletSpecimenAdminForm.this.getFakeDecodedWells();
            }

            @Override
            protected void processScanResult(IProgressMonitor monitor)
                throws Exception {
                AbstractPalletSpecimenAdminForm.this.processScanResult(monitor);
            }

            @Override
            protected void afterScanBeforeMerge() {
                setScanHasBeenLaunched(true, true);
            }

            @SuppressWarnings("nls")
            @Override
            protected void afterSuccessfulScan() {
                appendLog(NLS.bind("Scan completed - {0} specimens found", wells.keySet().size()));
            }

            @Override
            protected void afterScanAndProcess() {
                AbstractPalletSpecimenAdminForm.this.afterScanAndProcess(null);
            }

            @Override
            protected void scanAndProcessError(String errorMsg) {
                setScanValid(false);
                if (errorMsg != null && !errorMsg.isEmpty()) {
                    appendLog(errorMsg);
                }
            }

            @Override
            protected void plateError() {
                setScanHasBeenLaunched(false, true);
            }

            @Override
            protected void postprocessScanTubesManually(Set<PalletWell> cells) throws Exception {
                AbstractPalletSpecimenAdminForm.this.postprocessScanTubeAlone(cells);
            }

            @Override
            protected boolean canScanTubeAlone(PalletWell cell) {
                return AbstractPalletSpecimenAdminForm.this.canScanTubesManually(cell);
            }
        };
    }

    private void addScannerPreferencesPropertyListener() {
        propertyListener = new IPropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent event) {
                // force a check on available plates
                String plateText = plateToScanText.getText();
                plateToScanText.setText(StringUtil.EMPTY_STRING);
                plateToScanText.setText(plateText);
            }
        };
        ScannerConfigPlugin.getDefault().getPreferenceStore()
        .addPropertyChangeListener(propertyListener);

    }

    protected void beforeScanThreadStart() {
        // default does nothing
    }

    @SuppressWarnings("unused")
    protected void afterScanAndProcess(Integer rowToProcess) {
        // default does nothing
    }

    @Override
    public void dispose() {
        ScannerConfigPlugin.getDefault().getPreferenceStore()
        .removePropertyChangeListener(propertyListener);
        super.dispose();
    }

    @Override
    public boolean onClose() {
        synchronized (plateToScanSessionString) {
            plateToScanSessionString = (String) plateToScanValue.getValue();
            if (finished || BiobankPlugin.getPlatesEnabledCount() != 1) {
                plateToScanSessionString = StringUtil.EMPTY_STRING;
            }
        }
        return super.onClose();
    }

    @SuppressWarnings("nls")
    protected void setRescanMode() {
        if (palletScanManagement.getScansCount() > 0) {
            // TR: button text
            scanButton.setText(i18n.tr("Retry scan"));
            rescanMode = true;
            enableFields(false);
        }
    }

    protected abstract void enableFields(boolean enable);

    @SuppressWarnings("nls")
    protected void createScanButton(Composite parent) {
        // TR: button text
        scanButtonTitle = i18n.tr("Launch scan");
        if (!BiobankPlugin.isRealScanEnabled()) {
            createFakeOptions(parent);
            scanButtonTitle = "Fake scan";
        }
        scanButton = toolkit.createButton(parent, scanButtonTitle, SWT.PUSH);
        GridData gd = new GridData();
        gd.widthHint = 100;
        scanButton.setLayoutData(gd);
        scanButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                launchScanAndProcessResult();
            }
        });
        scanButton.setEnabled(false);

        addBooleanBinding(new WritableValue(Boolean.FALSE, Boolean.class),
            canLaunchScanValue,
            // TR: validation error message
            i18n.tr("Errors have been previously detected. Cannot launch scan."));
        addBooleanBinding(
            new WritableValue(Boolean.FALSE, Boolean.class),
            scanHasBeenLaunchedValue,
            // TR: validation error message
            i18n.tr("Scanner should be launched"));
        addBooleanBinding(new WritableValue(Boolean.TRUE, Boolean.class),
            scanValidValue,
            // TR: validation error message
            i18n.tr("Errors in scanning result"));
    }

    protected void launchScanAndProcessResult() {
        palletScanManagement.launchScanAndProcessResult(plateToScanValue
            .getValue().toString(), isRescanMode());
        refreshPalletDisplay();
    }

    protected abstract void refreshPalletDisplay();

    @SuppressWarnings("nls")
    protected void createPlateToScanField(Composite fieldsComposite) {
        plateToScanLabel = widgetCreator.createLabel(fieldsComposite,
            // TR: label;
            i18n.tr("Plate to scan"));
        plateToScanText = (BgcBaseText) widgetCreator.createBoundWidget(
            fieldsComposite, BgcBaseText.class, SWT.NONE, plateToScanLabel, new String[0],
            plateToScanValue, scannerBarcodeValidator, PLATE_VALIDATOR);
        plateToScanText.addListener(SWT.DefaultSelection, new Listener() {
            @Override
            public void handleEvent(Event e) {
                if (scanButton.isEnabled()) {
                    launchScanAndProcessResult();
                }
            }
        });
        plateToScanText.addModifyListener(new ModifyListener() {
            @Override
            public void modifyText(ModifyEvent e) {
                if (scanButton != null)
                    scanButton.setEnabled((Boolean) canLaunchScanValue.getValue() && fieldsValid());
            }
        });
        // TR: tooltip
        String tooltip =
            i18n.tr("No barcodes availables. See the preferences to complete the configuration.");
        List<String> barcodes = BiobankPlugin.getDefault()
            .getPossibleBarcodes();
        if (barcodes.size() > 0) {
            // TR: tooltip
            tooltip = i18n.tr("Available barcodes are: {0}",
                StringUtil.join(barcodes, ", "));
        }
        plateToScanText.setToolTipText(tooltip);
        GridData gd = (GridData) plateToScanText.getLayoutData();
        gd.horizontalAlignment = SWT.FILL;
        int parentNumColumns =
            ((GridLayout) fieldsComposite.getLayout()).numColumns;
        if (parentNumColumns > 2)
            gd.horizontalSpan = parentNumColumns - 1;
        plateToScanText.setLayoutData(gd);
    }

    protected void showPlateToScanField(boolean show) {
        widgetCreator.showWidget(plateToScanLabel, show);
        widgetCreator.showWidget(plateToScanText, show);
        widgetCreator.setBinding(PLATE_VALIDATOR, show && needPlate());
    }

    @SuppressWarnings("unused")
    protected void createFakeOptions(Composite fieldsComposite) {
        // default does nothing
    }

    protected void createCancelConfirmWidget(Composite parent) {
        cancelConfirmWidget = new CancelConfirmWidget(parent, this, true);
    }

    protected Map<RowColPos, PalletWell> getFakeDecodedWells() throws Exception {
        return null;
    }

    @Override
    protected void handleStatusChanged(IStatus status) {
        if (status.getSeverity() == IStatus.OK) {
            setFormHeaderErrorMessage(getOkMessage(), IMessageProvider.NONE);
            cancelConfirmWidget.setConfirmEnabled(true);
            setConfirmEnabled(true);
            setDirty(true);
        } else {
            scanButton.setEnabled((Boolean) canLaunchScanValue.getValue()
                && fieldsValid());
            setFormHeaderErrorMessage(status.getMessage(),
                IMessageProvider.ERROR);
            cancelConfirmWidget.setConfirmEnabled(false);
            setConfirmEnabled(false);
        }
    }

    protected abstract boolean fieldsValid();

    protected void setScanValid(final boolean valid) {
        Display.getDefault().asyncExec(new Runnable() {
            @Override
            public void run() {
                scanValidValue.setValue(valid);
            }
        });
    }

    protected boolean isScanValid() {
        return scanValidValue.getValue().equals(Boolean.TRUE);
    }

    protected void setScanHasBeenLaunched(boolean launched) {
        scanHasBeenLaunchedValue.setValue(launched);
    }

    protected boolean isScanHasBeenLaunched() {
        return scanHasBeenLaunchedValue.getValue().equals(true);
    }

    protected void setScanHasBeenLaunched(final boolean launched, boolean async) {
        if (async) {
            Display.getDefault().asyncExec(new Runnable() {
                @Override
                public void run() {
                    setScanHasBeenLaunched(launched);
                }
            });
        } else {
            setScanHasBeenLaunched(launched);
        }
    }

    protected boolean isRescanMode() {
        return rescanMode;
    }

    protected void removeRescanMode() {
        scanButton.setText(scanButtonTitle);
        rescanMode = false;
    }

    protected boolean isPlateValid() {
        return BiobankPlugin.getDefault().isValidPlateBarcode(plateToScanText.getText());
    }

    protected void resetPlateToScan() {
        plateToScanText.setText(plateToScanSessionString);
        plateToScanValue.setValue(plateToScanSessionString);
    }

    protected void setBindings(boolean isSingleMode) {
        setScanHasBeenLaunched(isSingleMode);
        widgetCreator.setBinding(PLATE_VALIDATOR, !isSingleMode && needPlate());
    }

    protected boolean needPlate() {
        return true;
    }

    protected void setCanLaunchScan(boolean canLauch) {
        canLaunchScanValue.setValue(canLauch);
    }

    protected void scanTubesManually(MouseEvent e) {
        palletScanManagement.scanTubesManually(e);
    }

    @SuppressWarnings("nls")
    protected void postprocessScanTubeAlone(Set<PalletWell> palletCells) throws Exception {

        for (PalletWell palletCell : palletCells) {
            appendLog(NLS.bind("Tube {0} scanned and set to position {1}", palletCell.getValue(),
                palletScanManagement.getContainerType().getPositionString(palletCell.getRowColPos())));
            beforeScanTubeAlone();
            CellProcessResult res = (CellProcessResult) SessionManager.getAppService().doAction(
                getCellProcessAction(SessionManager.getUser().getCurrentWorkingCenter().getId(),
                    palletCell.transformIntoServerCell(), Locale.getDefault()));
            palletCell.merge(SessionManager.getAppService(), res.getCell());
            appendLogs(res.getLogs());
            processCellResult(palletCell.getRowColPos(), palletCell);
            currentScanState = currentScanState.mergeWith(palletCell.getStatus());
            setScanValid(getCells() != null && !getCells().isEmpty()
                && currentScanState != UICellStatus.ERROR);
            // boolean ok = isScanValid()
            // && (palletCell.getStatus() != UICellStatus.ERROR);
            // setScanValid(ok);
            afterScanAndProcess(palletCell.getRow());
            setScanHasBeenLaunched(true);
        }
    }

    protected abstract Action<ProcessResult> getCellProcessAction(
        Integer centerId, CellInfo cell, Locale locale);

    protected abstract Action<ProcessResult> getPalletProcessAction(
        Integer centerId, Map<RowColPos, CellInfo> cells, boolean isRescanMode,
        Locale locale);

    protected void beforeScanTubeAlone() {
        // default does nothing
    }

    protected Map<RowColPos, PalletWell> getCells() {
        return palletScanManagement.getCells();
    }

    @Override
    public void setValues() throws Exception {
        scanValidValue.setValue(true);
        palletScanManagement.onReset();
        enableFields(true);
        resetPlateToScan();
    }

    protected void setUseScanner(boolean useScanner) {
        palletScanManagement.setUseScanner(useScanner);
        if (useScanner) {
            currentScanState = null;
        } else {
            currentScanState = UICellStatus.EMPTY;
        }
    }

    /**
     * go through cells retrieved from scan, set status and update the types
     * combos components
     */
    @SuppressWarnings("nls")
    protected void processScanResult(IProgressMonitor monitor) throws Exception {
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
                    .getCurrentWorkingCenter().getId(), serverCells,
                    isRescanMode(), Locale.getDefault()));
        // print result logs
        appendLogs(res.getLogs());

        if (cells != null) {
            // for each cell, convert into a client side cell
            for (Entry<RowColPos, CellInfo> entry : res.getCells().entrySet()) {
                RowColPos pos = entry.getKey();
                monitor.subTask(
                    // TR: progress monitor message
                    i18n.tr("Processing position {0}",
                        palletScanManagement.getContainerType()
                        .getPositionString(pos)));
                PalletWell palletCell = cells.get(entry.getKey());
                CellInfo servercell = entry.getValue();
                if (palletCell == null) { // can happened if missing
                    palletCell = new PalletWell(pos.getRow(), pos.getCol(),
                        new DecodedWell(servercell.getRow(), servercell.getCol(),
                            servercell.getValue()));
                    cells.put(pos, palletCell);
                }
                palletCell.merge(SessionManager.getAppService(), servercell);
                // additional cell specific client conversion if needed
                processCellResult(pos, palletCell);
            }
        }
        currentScanState = UICellStatus.valueOf(res.getProcessStatus().name());
        setScanValid(getCells() != null && !getCells().isEmpty()
            && currentScanState != UICellStatus.ERROR);
    }

    @SuppressWarnings("unused")
    protected void processCellResult(RowColPos rcp, PalletWell palletCell) {
        // nothing done by default
    }

    protected boolean isAtLeastOneScanLaunched() {
        return palletScanManagement.getScansCount() > 0;
    }

    protected boolean canScanTubesManually(PalletWell cell) {
        return cell == null || cell.getStatus() == UICellStatus.EMPTY;
    }

    protected void focusControl(final Control control) {
        Display.getDefault().asyncExec(new Runnable() {
            @Override
            public void run() {
                control.setFocus();
            }
        });
    }

    protected void focusPlateToScan() {
        focusControl(plateToScanText);
    }

    protected void initCellsWithContainer(
        ContainerWrapper currentMultipleContainer) {
        if (currentMultipleContainer != null) {
            palletScanManagement
            .initCellsWithContainer(currentMultipleContainer);
        }
    }

    protected void setContainerType(ContainerType type) {
        palletScanManagement.setContainerType(type);
    }

    protected void hideScannerBarcodeDecoration() {
        scannerBarcodeValidator.hideDecoration();
    }
}
