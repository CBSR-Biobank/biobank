package edu.ualberta.med.biobank.forms;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.observable.value.WritableValue;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IMessageProvider;
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

import edu.ualberta.med.biobank.BiobankPlugin;
import edu.ualberta.med.biobank.Messages;
import edu.ualberta.med.biobank.common.util.RowColPos;
import edu.ualberta.med.biobank.common.wrappers.ContainerLabelingSchemeWrapper;
import edu.ualberta.med.biobank.dialogs.ScanOneTubeDialog;
import edu.ualberta.med.biobank.forms.utils.PalletScanManagement;
import edu.ualberta.med.biobank.model.CellStatus;
import edu.ualberta.med.biobank.model.PalletCell;
import edu.ualberta.med.biobank.validators.ScannerBarcodeValidator;
import edu.ualberta.med.biobank.widgets.BiobankText;
import edu.ualberta.med.biobank.widgets.CancelConfirmWidget;
import edu.ualberta.med.biobank.widgets.grids.ScanPalletWidget;
import edu.ualberta.med.scannerconfig.ScannerConfigPlugin;
import edu.ualberta.med.scannerconfig.dmscanlib.ScanCell;
import edu.ualberta.med.scannerconfig.preferences.scanner.profiles.ProfileManager;

public abstract class AbstractPalletSpecimenAdminForm extends
    AbstractSpecimenAdminForm {

    private BiobankText plateToScanText;
    protected Button scanButton;
    private String scanButtonTitle;

    protected CancelConfirmWidget cancelConfirmWidget;

    private static String plateToScanSessionString = "";

    private IObservableValue plateToScanValue = new WritableValue(
        plateToScanSessionString, String.class);
    private IObservableValue canLaunchScanValue = new WritableValue(
        Boolean.FALSE, Boolean.class);
    private IObservableValue scanHasBeenLaunchedValue = new WritableValue(
        Boolean.FALSE, Boolean.class);
    private IObservableValue scanValidValue = new WritableValue(Boolean.TRUE,
        Boolean.class);

    private boolean rescanMode = false;

    private PalletScanManagement palletScanManagement;

    private boolean scanTubeAloneMode = false;

    private Label scanTubeAloneSwitch;

    protected ComboViewer profilesCombo;

    private IPropertyChangeListener propertyListener;

    protected String currentPlateToScan;

    @Override
    protected void init() throws Exception {
        super.init();
        addScannerPreferencesPropertyListener();

        palletScanManagement = new PalletScanManagement() {

            @Override
            protected void beforeThreadStart() {
                currentPlateToScan = plateToScanValue.getValue().toString();
                AbstractPalletSpecimenAdminForm.this.beforeScanThreadStart();
            }

            @Override
            protected void beforeScan() {
                setScanHasBeenLauched(false, true);
                String msgKey = "linkAssign.activitylog.scanning";//$NON-NLS-1$
                if (isRescanMode()) {
                    msgKey = "linkAssign.activitylog.rescanning";//$NON-NLS-1$
                }
                appendLogNLS(msgKey, currentPlateToScan);

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
                setScanHasBeenLauched(true, true);
            }

            @Override
            protected void afterScan() {
                appendLogNLS("linkAssign.activitylog.scanRes.total", //$NON-NLS-1$
                    cells.keySet().size());
            }

            @Override
            protected void afterScanAndProcess() {
                AbstractPalletSpecimenAdminForm.this.afterScanAndProcess();
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
                setScanHasBeenLauched(false, true);
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

    }

    protected void afterScanAndProcess() {

    }

    @Override
    public void dispose() {
        ScannerConfigPlugin.getDefault().getPreferenceStore()
            .removePropertyChangeListener(propertyListener);
        super.dispose();
    }

    @Override
    public boolean onClose() {
        plateToScanSessionString = (String) plateToScanValue.getValue();
        if (finished || BiobankPlugin.getPlatesEnabledCount() != 1) {
            plateToScanSessionString = "";
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
        scanButtonTitle = Messages.getString("linkAssign.scanButton.text");//$NON-NLS-1$
        if (!BiobankPlugin.isRealScanEnabled()) {
            createFakeOptions(parent);
            scanButtonTitle = "Fake scan"; //$NON-NLS-1$
        }
        scanButton = toolkit.createButton(parent, scanButtonTitle, SWT.PUSH);
        GridData gd = new GridData();
        gd.horizontalSpan = ((GridLayout) parent.getLayout()).numColumns;
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
        profilesCombo = widgetCreator.createComboViewer(fieldsComposite, lbl,
            null, null, "Invalid profile selected", false, null, null); //$NON-NLS-1$

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
        plateToScanText = (BiobankText) createBoundWidgetWithLabel(
            fieldsComposite, BiobankText.class, SWT.NONE,
            Messages.getString("linkAssign.plateToScan.label"), //$NON-NLS-1$
            new String[0], plateToScanValue, new ScannerBarcodeValidator(
                Messages.getString("linkAssign.plateToScan.validationMsg"))); //$NON-NLS-1$
        plateToScanText.addListener(SWT.DefaultSelection, new Listener() {
            @Override
            public void handleEvent(Event e) {
                if (scanButton.isEnabled()) {
                    launchScanAndProcessResult();
                }
            }
        });
        GridData gd = (GridData) plateToScanText.getLayoutData();
        gd.horizontalAlignment = SWT.FILL;
        gd.horizontalSpan = 2;
        plateToScanText.setLayoutData(gd);
    }

    protected void createFakeOptions(
        @SuppressWarnings("unused") Composite fieldsComposite) {

    }

    protected void createCancelConfirmWidget() {
        cancelConfirmWidget = new CancelConfirmWidget(page, this, true);
    }

    @SuppressWarnings("unused")
    protected Map<RowColPos, PalletCell> getFakeScanCells() throws Exception {
        return null;
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

    protected void setScanHasBeenLauched(boolean launched) {
        scanHasBeenLaunchedValue.setValue(launched);
    }

    protected boolean isScanHasBeenLaunched() {
        return scanHasBeenLaunchedValue.getValue().equals(true);
    }

    protected void setScanHasBeenLauched(final boolean launched, boolean async) {
        if (async)
            Display.getDefault().asyncExec(new Runnable() {
                @Override
                public void run() {
                    setScanHasBeenLauched(launched);
                }
            });
        else
            setScanHasBeenLauched(launched);
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
        plateToScanText.setText(""); //$NON-NLS-1$
        plateToScanValue.setValue(""); //$NON-NLS-1$
    }

    protected void setCanLaunchScan(boolean canLauch) {
        canLaunchScanValue.setValue(canLauch);
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
                        appendLogNLS("linkAssign.activitylog.scanTubeAlone",
                            value,
                            ContainerLabelingSchemeWrapper.rowColToSbs(rcp));
                        try {
                            postprocessScanTubeAlone(cell);
                        } catch (Exception ex) {
                            BiobankPlugin.openAsyncError("Scan tube error", ex);
                        }
                    }
                }
            }
        }
    }

    protected boolean canScanTubeAlone(PalletCell cell) {
        return cell == null || cell.getStatus() == CellStatus.EMPTY;
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
        scanTubeAloneSwitch.setImage(BiobankPlugin.getDefault()
            .getImageRegistry().get(BiobankPlugin.IMG_SCAN_EDIT));
        scanTubeAloneSwitch.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseDown(MouseEvent e) {
                if (isScanHasBeenLaunched()) {
                    scanTubeAloneMode = !scanTubeAloneMode;
                    if (scanTubeAloneMode) {
                        scanTubeAloneSwitch.setImage(BiobankPlugin.getDefault()
                            .getImageRegistry()
                            .get(BiobankPlugin.IMG_SCAN_CLOSE_EDIT));
                    } else {
                        scanTubeAloneSwitch.setImage(BiobankPlugin.getDefault()
                            .getImageRegistry()
                            .get(BiobankPlugin.IMG_SCAN_EDIT));
                    }
                }
            }
        });
    }

    protected Map<RowColPos, PalletCell> getCells() {
        return palletScanManagement.getCells();
    }

    @Override
    public void reset() throws Exception {
        scanValidValue.setValue(true);
        palletScanManagement.reset();
    }

    protected abstract void processScanResult(IProgressMonitor monitor)
        throws Exception;

    protected boolean isFirstSuccessfulScan() {
        return palletScanManagement.getSuccessfulScansCount() == 1;
    }
}
