package edu.ualberta.med.biobank.dialogs;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.observable.value.WritableValue;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
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

import edu.ualberta.med.biobank.BioBankPlugin;
import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.util.LabelingScheme;
import edu.ualberta.med.biobank.common.util.RowColPos;
import edu.ualberta.med.biobank.common.wrappers.AliquotWrapper;
import edu.ualberta.med.biobank.common.wrappers.DispatchShipmentWrapper;
import edu.ualberta.med.biobank.forms.Messages;
import edu.ualberta.med.biobank.forms.utils.PalletScanManagement;
import edu.ualberta.med.biobank.model.CellStatus;
import edu.ualberta.med.biobank.model.PalletCell;
import edu.ualberta.med.biobank.validators.ScannerBarcodeValidator;
import edu.ualberta.med.biobank.widgets.BiobankText;
import edu.ualberta.med.biobank.widgets.grids.ScanPalletWidget;
import edu.ualberta.med.scannerconfig.dmscanlib.ScanCell;

public class DispatchReceiveScanDialog extends BiobankDialog {

    private static final String TITLE = "Scanning received pallets";
    private BiobankText plateToScanText;
    private IObservableValue plateToScanValue = new WritableValue("", //$NON-NLS-1$
        String.class);
    private PalletScanManagement palletScanManagement;
    private ScanPalletWidget spw;
    private DispatchShipmentWrapper currentShipment;
    private IObservableValue scanHasBeenLaunchedValue = new WritableValue(
        Boolean.FALSE, Boolean.class);
    private IObservableValue scanOkValue = new WritableValue(Boolean.TRUE,
        Boolean.class);
    private int pendingAliquotsNumber = 0;
    private Button scanButton;

    public DispatchReceiveScanDialog(Shell parentShell,
        final DispatchShipmentWrapper currentShipment) {
        super(parentShell);
        this.currentShipment = currentShipment;
        palletScanManagement = new PalletScanManagement() {
            @Override
            protected void processScanResult(IProgressMonitor monitor)
                throws Exception {
                DispatchReceiveScanDialog.this.processScanResult(monitor);
            }

            @Override
            protected Map<RowColPos, PalletCell> getFakeScanCells()
                throws Exception {
                // return PalletCell.getRandomScanLinkWithAliquotsAlreadyLinked(
                // SessionManager.getAppService(), currentShipment.getSender()
                // .getId());
                Map<RowColPos, PalletCell> palletScanned = new TreeMap<RowColPos, PalletCell>();
                AliquotWrapper aliquot = currentShipment.getAliquotCollection()
                    .get(0);
                palletScanned.put(new RowColPos(0, 0), new PalletCell(
                    new ScanCell(0, 0, aliquot.getInventoryId())));
                return palletScanned;
            }

            @Override
            protected void afterScanAndProcess() {
                Display.getDefault().asyncExec(new Runnable() {
                    @Override
                    public void run() {
                        spw.setCells(getCells());
                    }
                });
            }
        };
        widgetCreator.addBooleanBinding(new WritableValue(Boolean.FALSE,
            Boolean.class), scanOkValue,
            "Error in scan result. Please keep only aliquots with no errors.",
            IStatus.ERROR);
        widgetCreator.addBooleanBinding(new WritableValue(Boolean.FALSE,
            Boolean.class), scanHasBeenLaunchedValue,
            "Scan should be launched", IStatus.ERROR);
    }

    @Override
    protected String getTitleAreaMessage() {
        return "Scan one pallet received in the shipment.";
    }

    @Override
    protected String getTitleAreaTitle() {
        return TITLE;
    }

    @Override
    protected String getDialogShellTitle() {
        return TITLE;
    }

    @Override
    protected void createDialogAreaInternal(Composite parent) throws Exception {
        Composite contents = new Composite(parent, SWT.NONE);
        contents.setLayout(new GridLayout(3, false));
        contents.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        ScannerBarcodeValidator validator = new ScannerBarcodeValidator(
            Messages.getString("linkAssign.plateToScan.validationMsg")) {
            @Override
            public IStatus validate(Object value) {
                IStatus status = super.validate(value);
                scanButton.setEnabled(status == Status.OK_STATUS);
                return status;
            }
        };
        plateToScanText = (BiobankText) createBoundWidgetWithLabel(contents,
            BiobankText.class, SWT.NONE,
            Messages.getString("linkAssign.plateToScan.label"), //$NON-NLS-1$
            new String[0], plateToScanValue, validator); //$NON-NLS-1$
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

        spw = new ScanPalletWidget(contents,
            CellStatus.DEFAULT_PALLET_DISPATCH_STATUS_LIST);
        GridData gd = new GridData();
        gd.horizontalSpan = 3;
        spw.setLayoutData(gd);

    }

    private void launchScan() {
        setScanOkValue(false);
        palletScanManagement.launchScanAndProcessResult(plateToScanValue
            .getValue().toString());
    }

    @Override
    protected void createButtonsForButtonBar(Composite parent) {
        createButton(parent, IDialogConstants.CANCEL_ID,
            "Cancel current pallet", false);
        createButton(parent, IDialogConstants.PROCEED_ID, "Accept aliquots",
            false);
        createButton(parent, IDialogConstants.NEXT_ID, "Start next Pallet",
            false);
        createButton(parent, IDialogConstants.FINISH_ID,
            IDialogConstants.FINISH_LABEL, false);
    }

    @Override
    protected void setOkButtonEnabled(boolean enabled) {
        Button processButton = getButton(IDialogConstants.PROCEED_ID);
        Button finishButton = getButton(IDialogConstants.FINISH_ID);
        Button nextButton = getButton(IDialogConstants.NEXT_ID);
        if (finishButton != null && !finishButton.isDisposed()) {
            finishButton.setEnabled(enabled);
            if (pendingAliquotsNumber == 0) {
                processButton.setEnabled(false);
                nextButton.setEnabled(enabled);
            } else {
                processButton.setEnabled(enabled);
                nextButton.setEnabled(false);
            }
        } else {
            okButtonEnabled = enabled;
        }
    }

    @Override
    protected void buttonPressed(int buttonId) {
        if (IDialogConstants.CANCEL_ID == buttonId)
            super.buttonPressed(buttonId);
        else if (IDialogConstants.PROCEED_ID == buttonId) {
            List<AliquotWrapper> aliquots = new ArrayList<AliquotWrapper>();
            for (PalletCell cell : palletScanManagement.getCells().values()) {
                aliquots.add(cell.getAliquot());
                cell.setStatus(CellStatus.IN_SHIPMENT_ACCEPTED);
            }
            try {
                currentShipment.receiveAliquots(aliquots);
                currentShipment.persist();
                spw.redraw();
                pendingAliquotsNumber = 0;
                setOkButtonEnabled(true);
            } catch (Exception e) {
                BioBankPlugin.openAsyncError("Error receiving aliquots", e);
            }
            Button cancelButton = getButton(IDialogConstants.CANCEL_ID);
            cancelButton.setEnabled(false);
        } else if (IDialogConstants.FINISH_ID == buttonId) {
            setReturnCode(OK);
            close();
        } else if (IDialogConstants.NEXT_ID == buttonId) {
            startNewPallet();
        }
    }

    private void startNewPallet() {
        spw.setCells(null);
        scanHasBeenLaunchedValue.setValue(false);
    }

    private void processScanResult(IProgressMonitor monitor) throws Exception {
        setScanNotLaunched(true);
        Map<RowColPos, PalletCell> cells = palletScanManagement.getCells();
        pendingAliquotsNumber = 0;
        if (cells != null) {
            boolean resOk = true;
            for (RowColPos rcp : cells.keySet()) {
                monitor.subTask("Processing position "
                    + LabelingScheme.rowColToSbs(rcp));
                PalletCell cell = cells.get(rcp);
                List<AliquotWrapper> aliquots = AliquotWrapper.getAliquots(
                    SessionManager.getAppService(), cell.getValue());
                if (aliquots == null || aliquots.size() == 0) {
                    cell.setStatus(CellStatus.ERROR);
                    cell.setInformation("Aliquot not found in database");
                    resOk = false;
                    continue;
                }
                if (aliquots.size() > 1) {
                    cell.setStatus(CellStatus.ERROR);
                    cell.setInformation("Found more than one aliquot with inventoryId "
                        + cell.getValue());
                    resOk = false;
                    continue;
                }
                AliquotWrapper aliquot = aliquots.get(0);
                cell.setAliquot(aliquot);
                cell.setTitle(aliquot.getPatientVisit().getPatient()
                    .getPnumber());
                if (currentShipment.getAliquotCollection().contains(aliquot)) {
                    if (aliquot.isActive()) {
                        cell.setStatus(CellStatus.IN_SHIPMENT_ACCEPTED);
                    } else {
                        cell.setStatus(CellStatus.IN_SHIPMENT_PENDING);
                        pendingAliquotsNumber++;
                    }
                } else {
                    cell.setStatus(CellStatus.NOT_IN_SHIPMENT);
                    cell.setInformation("Aliquot should not be in shipment");
                }
            }
            setScanOkValue(resOk);
        }
    }

    private void setScanOkValue(final boolean resOk) {
        Display.getDefault().asyncExec(new Runnable() {
            @Override
            public void run() {
                scanOkValue.setValue(resOk);
            }
        });
    }

    private void setScanNotLaunched(final boolean launched) {
        Display.getDefault().asyncExec(new Runnable() {
            @Override
            public void run() {
                scanHasBeenLaunchedValue.setValue(launched);
            }
        });
    }

}
