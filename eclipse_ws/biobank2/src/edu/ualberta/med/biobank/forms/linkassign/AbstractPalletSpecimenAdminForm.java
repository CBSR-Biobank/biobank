package edu.ualberta.med.biobank.forms.linkassign;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.observable.value.WritableValue;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.ComboViewer;
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
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;

import edu.ualberta.med.biobank.BiobankPlugin;
import edu.ualberta.med.biobank.Messages;
import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.scanprocess.Cell;
import edu.ualberta.med.biobank.common.scanprocess.data.ProcessData;
import edu.ualberta.med.biobank.common.scanprocess.result.CellProcessResult;
import edu.ualberta.med.biobank.common.scanprocess.result.ScanProcessResult;
import edu.ualberta.med.biobank.common.util.RowColPos;
import edu.ualberta.med.biobank.common.wrappers.ContainerLabelingSchemeWrapper;
import edu.ualberta.med.biobank.common.wrappers.ContainerWrapper;
import edu.ualberta.med.biobank.forms.utils.PalletScanManagement;
import edu.ualberta.med.biobank.gui.common.BgcPlugin;
import edu.ualberta.med.biobank.gui.common.widgets.BgcBaseText;
import edu.ualberta.med.biobank.validators.ScannerBarcodeValidator;
import edu.ualberta.med.biobank.widgets.BiobankLabelProvider;
import edu.ualberta.med.biobank.widgets.CancelConfirmWidget;
import edu.ualberta.med.biobank.widgets.grids.cell.PalletCell;
import edu.ualberta.med.biobank.widgets.grids.cell.UICellStatus;
import edu.ualberta.med.scannerconfig.ScannerConfigPlugin;
import edu.ualberta.med.scannerconfig.dmscanlib.ScanCell;
import edu.ualberta.med.scannerconfig.preferences.scanner.profiles.ProfileManager;

public abstract class AbstractPalletSpecimenAdminForm extends
    AbstractSpecimenAdminForm {

    private static final String PLATE_VALIDATOR = "plate-validator";
    private BgcBaseText plateToScanText;
    protected Button scanButton;
    private String scanButtonTitle;

    protected CancelConfirmWidget cancelConfirmWidget;

    private static String plateToScanSessionString = "";

    private IObservableValue plateToScanValue = new WritableValue(
        plateToScanSessionString, String.class);
    private IObservableValue canLaunchScanValue = new WritableValue(
        Boolean.TRUE, Boolean.class);
    private IObservableValue scanHasBeenLaunchedValue = new WritableValue(
        Boolean.FALSE, Boolean.class);
    private IObservableValue scanValidValue = new WritableValue(Boolean.TRUE,
        Boolean.class);

    private boolean rescanMode = false;

    private PalletScanManagement palletScanManagement;

    private Label scanTubeAloneSwitch;

    protected ComboViewer profilesCombo;

    private IPropertyChangeListener propertyListener;

    protected String currentPlateToScan;

    // global state of the pallet process
    protected UICellStatus currentScanState;
    private Label plateToScanLabel;

    @Override
    protected void init() throws Exception {
        super.init();
        currentPlateToScan = plateToScanSessionString;
        addScannerPreferencesPropertyListener();
        palletScanManagement = new PalletScanManagement() {
            @Override
            public boolean isScanTubeAloneMode() {
                // FIXME: see issue #1230. always activate this mode
                return true;
            }

            @Override
            protected void beforeThreadStart() {
                currentPlateToScan = plateToScanValue.getValue().toString();
                AbstractPalletSpecimenAdminForm.this.beforeScanThreadStart();
            }

            @Override
            protected void beforeScan() {
                setScanHasBeenLaunched(false, true);
                String msgKey = "linkAssign.activitylog.scanning";//$NON-NLS-1$
                if (isRescanMode()) {
                    msgKey = "linkAssign.activitylog.rescanning";//$NON-NLS-1$
                }
                appendLog(Messages.getString(msgKey, currentPlateToScan));

            }

            @Override
            protected Map<RowColPos, PalletCell> getFakeScanCells()
                throws Exception {
                return AbstractPalletSpecimenAdminForm.this.getFakeScanCells();
            }

            @Override
            protected void processScanResult(IProgressMonitor monitor)
                throws Exception {
                AbstractPalletSpecimenAdminForm.this.processScanResult(monitor);
            }

            @Override
            protected void beforeScanMerge() {
                setScanHasBeenLaunched(true, true);
            }

            @Override
            protected void afterScan() {
                appendLog(Messages.getString(
                    "linkAssign.activitylog.scanRes.total", //$NON-NLS-1$
                    cells.keySet().size()));
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
            protected void postprocessScanTubeAlone(PalletCell cell)
                throws Exception {
                AbstractPalletSpecimenAdminForm.this
                    .postprocessScanTubeAlone(cell);
            }

            @Override
            protected boolean canScanTubeAlone(PalletCell cell) {
                return AbstractPalletSpecimenAdminForm.this
                    .canScanTubeAlone(cell);
            }
        };
    }

    private void addScannerPreferencesPropertyListener() {
        propertyListener = new IPropertyChangeListener() {
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
                    if (ScannerConfigPlugin.getDefault().getPlateEnabled(
                        plateId)) {
                        ++plateEnabledCount;
                    }
                }

                // force an error check
                String plateText = plateToScanText.getText();
                plateToScanText.setText("");
                plateToScanText.setText(plateText);
            }
        };
        ScannerConfigPlugin.getDefault().getPreferenceStore()
            .addPropertyChangeListener(propertyListener);

    }

    protected void beforeScanThreadStart() {
        // default does nothing
    }

    protected void afterScanAndProcess(
        @SuppressWarnings("unused") Integer rowToProcess) {
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
                plateToScanSessionString = "";
            }
        }
        return super.onClose();
    }

    protected void setRescanMode() {
        if (palletScanManagement.getSuccessfulScansCount() > 0) {
            scanButton.setText("Retry scan");
            rescanMode = true;
            enableFields(false);
        }
    }

    protected abstract void enableFields(boolean enable);

    protected void createScanButton(Composite parent) {
        scanButtonTitle = Messages.getString("linkAssign.scanButton.text");//$NON-NLS-1$
        if (!BiobankPlugin.isRealScanEnabled()) {
            createFakeOptions(parent);
            scanButtonTitle = "Fake scan"; //$NON-NLS-1$
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
            Messages.getString("linkAssign.canLaunchScanValidationMsg")); //$NON-NLS-1$
        addBooleanBinding(new WritableValue(Boolean.FALSE, Boolean.class),
            scanHasBeenLaunchedValue,
            Messages.getString("linkAssign.scanHasBeenLaunchedValidationMsg")); //$NON-NLS-1$
        addBooleanBinding(new WritableValue(Boolean.TRUE, Boolean.class),
            scanValidValue,
            Messages.getString("linkAssign.scanValidValidationMsg")); //$NON-NLS-1$
    }

    protected void launchScanAndProcessResult() {
        palletScanManagement.launchScanAndProcessResult(plateToScanValue
            .getValue().toString(), getProfile(), isRescanMode());
    }

    protected String getProfile() {
        if (profilesCombo == null
            || profilesCombo.getCombo().getItemCount() <= 0
            || profilesCombo.getCombo().getSelectionIndex() < 0)
            return ProfileManager.ALL_PROFILE_NAME;
        else
            return profilesCombo.getCombo().getItem(
                profilesCombo.getCombo().getSelectionIndex());
    }

    protected void createProfileComboBox(Composite fieldsComposite) {
        Label lbl = widgetCreator.createLabel(fieldsComposite, "Profile");
        profilesCombo = widgetCreator
            .createComboViewer(
                fieldsComposite,
                lbl,
                null,
                null,
                "Invalid profile selected", false, null, null, new BiobankLabelProvider()); //$NON-NLS-1$

        GridData gd = new GridData();
        gd.horizontalAlignment = SWT.FILL;
        gd.horizontalSpan = 2;
        profilesCombo.getCombo().setLayoutData(gd);
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
        plateToScanLabel = widgetCreator.createLabel(fieldsComposite,
            Messages.getString("linkAssign.plateToScan.label")); //$NON-NLS-1$);
        plateToScanText = (BgcBaseText) widgetCreator
            .createBoundWidget(
                fieldsComposite,
                BgcBaseText.class,
                SWT.NONE,
                plateToScanLabel,
                new String[0],
                plateToScanValue,
                new ScannerBarcodeValidator(Messages
                    .getString("linkAssign.plateToScan.validationMsg")), PLATE_VALIDATOR); //$NON-NLS-1$
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
                    scanButton.setEnabled((Boolean) canLaunchScanValue
                        .getValue() && fieldsValid());
            }
        });
        GridData gd = (GridData) plateToScanText.getLayoutData();
        gd.horizontalAlignment = SWT.FILL;
        int parentNumColumns = ((GridLayout) fieldsComposite.getLayout()).numColumns;
        if (parentNumColumns > 2)
            gd.horizontalSpan = parentNumColumns - 1;
        plateToScanText.setLayoutData(gd);
    }

    protected void showPlateToScanField(boolean show) {
        widgetCreator.showWidget(plateToScanLabel, show);
        widgetCreator.showWidget(plateToScanText, show);
        widgetCreator.setBinding(PLATE_VALIDATOR, show && needPlate());
    }

    protected void createFakeOptions(
        @SuppressWarnings("unused") Composite fieldsComposite) {
        // default does nothing
    }

    protected void createCancelConfirmWidget(Composite parent) {
        cancelConfirmWidget = new CancelConfirmWidget(parent, this, true);
    }

    @SuppressWarnings("unused")
    protected Map<RowColPos, PalletCell> getFakeScanCells() throws Exception {
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
        return scanValidValue.getValue().equals(true);
    }

    protected void setScanHasBeenLaunched(boolean launched) {
        scanHasBeenLaunchedValue.setValue(launched);
    }

    protected boolean isScanHasBeenLaunched() {
        return scanHasBeenLaunchedValue.getValue().equals(true);
    }

    protected void setScanHasBeenLaunched(final boolean launched, boolean async) {
        if (async)
            Display.getDefault().asyncExec(new Runnable() {
                @Override
                public void run() {
                    setScanHasBeenLaunched(launched);
                }
            });
        else
            setScanHasBeenLaunched(launched);
    }

    protected boolean isRescanMode() {
        return rescanMode;
    }

    protected void removeRescanMode() {
        scanButton.setText(scanButtonTitle);
        rescanMode = false;
    }

    protected boolean isPlateValid() {
        return BiobankPlugin.getDefault().isValidPlateBarcode(
            plateToScanText.getText());
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

    protected void scanTubeAlone(MouseEvent e) {
        if (isScanHasBeenLaunched())
            palletScanManagement.scanTubeAlone(e);
    }

    protected void postprocessScanTubeAlone(PalletCell palletCell)
        throws Exception {
        appendLog(Messages.getString("linkAssign.activitylog.scanTubeAlone",
            palletCell.getValue(), ContainerLabelingSchemeWrapper
                .rowColToSbs(palletCell.getRowColPos())));
        beforeScanTubeAlone();
        CellProcessResult res = appService.processCellStatus(
            palletCell.transformIntoServerCell(), getProcessData(),
            SessionManager.getUser());
        palletCell.merge(appService, res.getCell());
        appendLogs(res.getLogs());
        processCellResult(palletCell.getRowColPos(), palletCell);
        currentScanState = currentScanState.mergeWith(palletCell.getStatus());
        boolean ok = isScanValid()
            && (palletCell.getStatus() != UICellStatus.ERROR);
        setScanValid(ok);
        afterScanAndProcess(palletCell.getRow());
    }

    protected void beforeScanTubeAlone() {
        // default does nothing
    }

    protected boolean isScanTubeAloneMode() {
        return palletScanManagement.isScanTubeAloneMode();
    }

    protected void toggleScanTubeAloneMode() {
        palletScanManagement.toggleScanTubeAloneMode();
    }

    protected void createScanTubeAloneButton(Composite parent) {
        scanTubeAloneSwitch = toolkit.createLabel(parent, "", SWT.NONE);
        GridData gd = new GridData();
        gd.verticalAlignment = SWT.TOP;
        scanTubeAloneSwitch.setLayoutData(gd);
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
        // FIXME: see issue #1230. deactivate this button until the users say we
        // can really remove it
        scanTubeAloneSwitch.setVisible(false);
    }

    protected void showScanTubeAloneSwitch(
        @SuppressWarnings("unused") boolean show) {
        // FIXME: see issue #1230. deactivate this button until the users say we
        // can really remove it
        // widgetCreator.showWidget(scanTubeAloneSwitch, show);
    }

    protected Map<RowColPos, PalletCell> getCells() {
        return palletScanManagement.getCells();
    }

    @Override
    protected void onReset() throws Exception {
        scanValidValue.setValue(true);
        palletScanManagement.onReset();
        enableFields(true);
        resetPlateToScan();
    }

    protected void setUseScanner(boolean useScanner) {
        palletScanManagement.setUseScanner(useScanner);
        if (useScanner)
            currentScanState = null;
        else
            currentScanState = UICellStatus.EMPTY;
    }

    /**
     * go through cells retrieved from scan, set status and update the types
     * combos components
     */
    protected void processScanResult(IProgressMonitor monitor) throws Exception {
        Map<RowColPos, PalletCell> cells = getCells();
        // conversion for server side call
        Map<RowColPos, edu.ualberta.med.biobank.common.scanprocess.Cell> serverCells = null;
        if (cells != null) {
            serverCells = new HashMap<RowColPos, edu.ualberta.med.biobank.common.scanprocess.Cell>();
            for (Entry<RowColPos, PalletCell> entry : cells.entrySet()) {
                serverCells.put(entry.getKey(), entry.getValue()
                    .transformIntoServerCell());
            }
        }
        // server side call
        ScanProcessResult res = appService.processScanResult(serverCells,
            getProcessData(), isRescanMode(), SessionManager.getUser());
        // print result logs
        appendLogs(res.getLogs());

        if (cells != null) {
            // for each cell, convert into a client side cell
            for (Entry<RowColPos, edu.ualberta.med.biobank.common.scanprocess.Cell> entry : res
                .getCells().entrySet()) {
                RowColPos rcp = entry.getKey();
                monitor.subTask(Messages.getString(
                    "ScanLink.scan.monitor.position", //$NON-NLS-1$
                    ContainerLabelingSchemeWrapper.rowColToSbs(rcp)));
                PalletCell palletCell = cells.get(entry.getKey());
                Cell servercell = entry.getValue();
                if (palletCell == null) { // can happened if missing
                    palletCell = new PalletCell(new ScanCell(
                        servercell.getRow(), servercell.getCol(),
                        servercell.getValue()));
                    cells.put(rcp, palletCell);
                }
                palletCell.merge(appService, servercell);
                // additional cell specific client conversion if needed
                processCellResult(rcp, palletCell);
            }
        }
        currentScanState = UICellStatus.valueOf(res.getProcessStatus().name());
        setScanValid(getCells() != null && !getCells().isEmpty()
            && currentScanState != UICellStatus.ERROR);
    }

    protected abstract ProcessData getProcessData();

    protected void processCellResult(@SuppressWarnings("unused") RowColPos rcp,
        @SuppressWarnings("unused") PalletCell palletCell) {
        // nothing done by default
    }

    protected boolean isFirstSuccessfulScan() {
        return palletScanManagement.getSuccessfulScansCount() == 1;
    }

    protected boolean canScanTubeAlone(PalletCell cell) {
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
        if (currentMultipleContainer != null)
            palletScanManagement
                .initCellsWithContainer(currentMultipleContainer);
    }

}
