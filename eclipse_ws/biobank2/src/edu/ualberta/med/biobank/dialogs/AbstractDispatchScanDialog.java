package edu.ualberta.med.biobank.dialogs;

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

public abstract class AbstractDispatchScanDialog extends BiobankDialog {

    private BiobankText plateToScanText;

    private IObservableValue plateToScanValue = new WritableValue("", //$NON-NLS-1$
        String.class);
    private PalletScanManagement palletScanManagement;
    private ScanPalletWidget spw;
    protected DispatchShipmentWrapper currentShipment;
    private IObservableValue scanHasBeenLaunchedValue = new WritableValue(
        Boolean.FALSE, Boolean.class);
    private IObservableValue scanOkValue = new WritableValue(Boolean.TRUE,
        Boolean.class);

    private Button scanButton;

    public AbstractDispatchScanDialog(Shell parentShell,
        final DispatchShipmentWrapper currentShipment) {
        super(parentShell);
        this.currentShipment = currentShipment;
        palletScanManagement = new PalletScanManagement() {
            @Override
            protected void processScanResult(IProgressMonitor monitor)
                throws Exception {
                AbstractDispatchScanDialog.this.processScanResult(monitor);
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

    protected abstract void processScanResult(IProgressMonitor monitor)
        throws Exception;

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

    protected void setScanNotLaunched(final boolean launched) {
        Display.getDefault().asyncExec(new Runnable() {
            @Override
            public void run() {
                scanHasBeenLaunchedValue.setValue(launched);
            }
        });
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
            finishButton.setEnabled(enabled);
            if (canActivateProceedButton()) {
                proceedButton.setEnabled(false);
                nextButton.setEnabled(enabled);
            }
            if (canActivateNextButton()) {
                proceedButton.setEnabled(enabled);
                nextButton.setEnabled(false);
            }
        } else {
            okButtonEnabled = enabled;
        }
    }

    protected boolean canActivateNextButton() {
        return true;
    }

    protected boolean canActivateProceedButton() {
        return true;
    }

    @Override
    protected void buttonPressed(int buttonId) {
        if (IDialogConstants.CANCEL_ID == buttonId)
            super.buttonPressed(buttonId);
        else if (IDialogConstants.PROCEED_ID == buttonId) {
            doProceed();
        } else if (IDialogConstants.FINISH_ID == buttonId) {
            setReturnCode(OK);
            close();
        } else if (IDialogConstants.NEXT_ID == buttonId) {
            startNewPallet();
        }
    }

    protected abstract void doProceed();
}
