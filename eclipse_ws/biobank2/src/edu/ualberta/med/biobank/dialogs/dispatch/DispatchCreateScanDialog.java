package edu.ualberta.med.biobank.dialogs.dispatch;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import edu.ualberta.med.biobank.BiobankPlugin;
import edu.ualberta.med.biobank.Messages;
import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.scanprocess.data.DispatchProcessData;
import edu.ualberta.med.biobank.common.scanprocess.data.ProcessData;
import edu.ualberta.med.biobank.common.scanprocess.result.ScanProcessResult;
import edu.ualberta.med.biobank.common.util.RowColPos;
import edu.ualberta.med.biobank.common.wrappers.CenterWrapper;
import edu.ualberta.med.biobank.common.wrappers.ContainerWrapper;
import edu.ualberta.med.biobank.common.wrappers.DispatchWrapper;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;
import edu.ualberta.med.biobank.common.wrappers.SpecimenWrapper;
import edu.ualberta.med.biobank.forms.listener.EnterKeyToNextFieldListener;
import edu.ualberta.med.biobank.model.PalletCell;
import edu.ualberta.med.biobank.model.UICellStatus;
import edu.ualberta.med.biobank.validators.NonEmptyStringValidator;
import edu.ualberta.med.biobank.widgets.BiobankText;
import edu.ualberta.med.scannerconfig.dmscanlib.ScanCell;

public class DispatchCreateScanDialog extends
    AbstractScanDialog<DispatchWrapper> {

    private static final String TITLE = Messages
        .getString("DispatchCreateScanDialog.title"); //$NON-NLS-1$
    private BiobankText palletproductBarcodeText;
    private NonEmptyStringValidator productBarcodeValidator;
    private String currentProductBarcode;
    private boolean isPalletWithPosition;
    private boolean aliquotsAdded = false;
    private ContainerWrapper currentPallet;
    private List<ContainerWrapper> removedPallets = new ArrayList<ContainerWrapper>();

    public DispatchCreateScanDialog(Shell parentShell,
        DispatchWrapper currentShipment, CenterWrapper<?> site) {
        super(parentShell, currentShipment, site);
    }

    @Override
    protected void createCustomDialogPreContents(final Composite parent) {
        Button palletWithoutPositionRadio = new Button(parent, SWT.RADIO);
        palletWithoutPositionRadio.setText(Messages
            .getString("DispatchCreateScanDialog.without.position.radio.text")); //$NON-NLS-1$
        final Button palletWithPositionRadio = new Button(parent, SWT.RADIO);
        palletWithPositionRadio.setText(Messages
            .getString("DispatchCreateScanDialog.with.position.radio.text")); //$NON-NLS-1$

        palletWithPositionRadio.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                isPalletWithPosition = palletWithPositionRadio.getSelection();
                showProductBarcodeField(palletWithPositionRadio.getSelection());
            }
        });

        productBarcodeValidator = new NonEmptyStringValidator(
            Messages.getString("ScanAssign.productBarcode.validationMsg"));//$NON-NLS-1$
        Label palletproductBarcodeLabel = widgetCreator.createLabel(parent,
            Messages.getString("ScanAssign.productBarcode.label"));//$NON-NLS-1$
        palletproductBarcodeText = (BiobankText) createBoundWidget(parent,
            BiobankText.class, SWT.NONE, palletproductBarcodeLabel,
            new String[0], this,
            "currentProductBarcode", productBarcodeValidator); //$NON-NLS-1$
        palletproductBarcodeText
            .addKeyListener(new EnterKeyToNextFieldListener());
        showProductBarcodeField(false);
        palletWithoutPositionRadio.setSelection(true);
    }

    private void showProductBarcodeField(boolean show) {
        resetScan();
        palletproductBarcodeText.setEnabled(show);
        if (show) {
            palletproductBarcodeText.setText(""); //$NON-NLS-1$
        } else {
            palletproductBarcodeText.setText(Messages
                .getString("DispatchCreateScanDialog.noposision.text")); //$NON-NLS-1$
        }
    }

    @Override
    protected boolean fieldsValid() {
        return super.fieldsValid()
            && productBarcodeValidator.validate(
                palletproductBarcodeText.getText()).equals(Status.OK_STATUS);
    }

    @Override
    protected ScanProcessResult internalProcessScanResult(
        IProgressMonitor monitor,
        Map<RowColPos, edu.ualberta.med.biobank.common.scanprocess.Cell> serverCells,
        CenterWrapper<?> center) throws Exception {
        aliquotsAdded = false;
        currentPallet = null;
        if (isPalletWithPosition) {
            if (center instanceof SiteWrapper)
                currentPallet = ContainerWrapper
                    .getContainerWithProductBarcodeInSite(
                        SessionManager.getAppService(), (SiteWrapper) center,
                        currentProductBarcode);
            if (currentPallet == null) {
                BiobankPlugin
                    .openAsyncError(
                        Messages
                            .getString("DispatchCreateScanDialog.pallet.search.error.title"), //$NON-NLS-1$
                        Messages.getString(
                            "DispatchCreateScanDialog.pallet.search.error.msg", //$NON-NLS-1$
                            currentProductBarcode));
                return null;
            }
        }
        // server side call
        return SessionManager.getAppService().processScanResult(serverCells,
            getProcessData(), isRescanMode(), SessionManager.getUser());
    }

    @Override
    protected String getTitleAreaMessage() {
        return Messages.getString("DispatchCreateScanDialog.description"); //$NON-NLS-1$
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
    protected String getProceedButtonlabel() {
        return Messages
            .getString("DispatchCreateScanDialog.proceed.button.label"); //$NON-NLS-1$
    }

    @Override
    protected void doProceed() throws Exception {
        List<SpecimenWrapper> aliquots = new ArrayList<SpecimenWrapper>();
        for (PalletCell cell : getCells().values()) {
            if (cell.getStatus() != UICellStatus.MISSING) {
                aliquots.add(cell.getSpecimen());
                cell.setStatus(UICellStatus.IN_SHIPMENT_ADDED);
            }
        }
        (currentShipment).addAliquots(aliquots);
        if (currentPallet != null) {
            removedPallets.add(currentPallet);
        }
        redrawPallet();
        aliquotsAdded = true;
        setOkButtonEnabled(true);
        Button cancelButton = getButton(IDialogConstants.CANCEL_ID);
        cancelButton.setEnabled(false);
    }

    @Override
    protected void startNewPallet() {
        setRescanMode(false);
        Button cancelButton = getButton(IDialogConstants.CANCEL_ID);
        cancelButton.setEnabled(true);
        super.startNewPallet();
    }

    @Override
    protected boolean canActivateProceedButton() {
        return !aliquotsAdded;
    }

    @Override
    protected boolean canActivateNextAndFinishButton() {
        return aliquotsAdded;
    }

    @Override
    protected List<UICellStatus> getPalletCellStatus() {
        return UICellStatus.DEFAULT_PALLET_DISPATCH_CREATE_STATUS_LIST;
    }

    @Override
    protected Map<RowColPos, PalletCell> getFakeScanCells() throws Exception {
        ContainerWrapper currentPallet = null;
        if (isPalletWithPosition)
            currentPallet = ContainerWrapper
                .getContainerWithProductBarcodeInSite(
                    SessionManager.getAppService(), (SiteWrapper) currentSite,
                    currentProductBarcode);
        Map<RowColPos, PalletCell> map = new HashMap<RowColPos, PalletCell>();
        if (currentPallet == null) {
            Map<RowColPos, PalletCell> cells = PalletCell
                .getRandomNonDispatchedSpecimens(
                    SessionManager.getAppService(), (currentShipment)
                        .getSenderCenter().getId());
            return cells;
        } else {
            for (SpecimenWrapper aliquot : currentPallet.getSpecimens()
                .values()) {
                PalletCell cell = new PalletCell(new ScanCell(
                    aliquot.getPosition().row, aliquot.getPosition().col,
                    aliquot.getInventoryId()));
                map.put(aliquot.getPosition(), cell);
            }
        }
        return map;
    }

    public List<ContainerWrapper> getRemovedPallets() {
        return removedPallets;
    }

    @Override
    protected ProcessData getProcessData() {
        return new DispatchProcessData(currentPallet, currentShipment, true,
            false);
    }

    public void setCurrentProductBarcode(String currentProductBarcode) {
        this.currentProductBarcode = currentProductBarcode;
    }

    public String getCurrentProductBarcode() {
        return currentProductBarcode;
    }
}
