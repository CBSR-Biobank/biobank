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
import edu.ualberta.med.biobank.common.util.RowColPos;
import edu.ualberta.med.biobank.common.wrappers.CenterWrapper;
import edu.ualberta.med.biobank.common.wrappers.ContainerLabelingSchemeWrapper;
import edu.ualberta.med.biobank.common.wrappers.ContainerWrapper;
import edu.ualberta.med.biobank.common.wrappers.DispatchWrapper;
import edu.ualberta.med.biobank.common.wrappers.DispatchWrapper.CheckStatus;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;
import edu.ualberta.med.biobank.common.wrappers.SpecimenWrapper;
import edu.ualberta.med.biobank.forms.listener.EnterKeyToNextFieldListener;
import edu.ualberta.med.biobank.model.UICellStatus;
import edu.ualberta.med.biobank.model.PalletCell;
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
    protected void processScanResult(IProgressMonitor monitor,
        CenterWrapper<?> site) throws Exception {
        aliquotsAdded = false;
        boolean scanOk = true;
        currentPallet = null;
        if (isPalletWithPosition) {
            currentPallet = ContainerWrapper
                .getContainerWithProductBarcodeInSite(
                    SessionManager.getAppService(), (SiteWrapper) site,
                    currentProductBarcode);
            if (currentPallet != null) {
                // FIXME check it is a pallet ? Should we do it when enter
                // barcode ?
                Map<RowColPos, SpecimenWrapper> expectedAliquots = currentPallet
                    .getSpecimens();
                for (int row = 0; row < currentPallet.getRowCapacity(); row++) {
                    for (int col = 0; col < currentPallet.getColCapacity(); col++) {
                        RowColPos rcp = new RowColPos(row, col);
                        PalletCell cell = getCells().get(rcp);
                        processCell(monitor, rcp, cell, expectedAliquots);
                        scanOk = scanOk && cellOk(cell);
                    }
                }

            } else {
                BiobankPlugin
                    .openAsyncError(
                        Messages
                            .getString("DispatchCreateScanDialog.pallet.search.error.title"), //$NON-NLS-1$
                        Messages.getString(
                            "DispatchCreateScanDialog.pallet.search.error.msg", //$NON-NLS-1$
                            currentProductBarcode));
            }
        } else {
            for (PalletCell cell : getCells().values()) {
                processCell(monitor,
                    new RowColPos(cell.getRow(), cell.getCol()), cell, null);
                processCellStatus(cell);
                scanOk = scanOk && cellOk(cell);
            }
        }
        setScanOkValue(scanOk);
    }

    private boolean cellOk(PalletCell cell) {
        return cell == null || cell.getStatus() == UICellStatus.FILLED
            || cell.getStatus() == UICellStatus.IN_SHIPMENT_ADDED;
    }

    private void processCell(IProgressMonitor monitor, RowColPos rcp,
        PalletCell cell, Map<RowColPos, SpecimenWrapper> expectedAliquots)
        throws Exception {
        monitor.subTask(Messages.getString(
            "DispatchCreateScanDialog.processCell.task.position", //$NON-NLS-1$
            ContainerLabelingSchemeWrapper.rowColToSbs(rcp)));
        SpecimenWrapper expectedAliquot = null;
        if (expectedAliquots != null) {
            expectedAliquot = expectedAliquots.get(rcp);
            if (expectedAliquot != null) {
                if (cell == null) {
                    cell = new PalletCell(new ScanCell(rcp.row, rcp.col, null));
                    getCells().put(rcp, cell);
                }
                cell.setExpectedSpecimen(expectedAliquot);
            }
        }
        if (cell != null) {
            processCellStatus(cell);
        }
    }

    /**
     * set the status of the cell
     */
    protected void processCellStatus(PalletCell scanCell) throws Exception {
        SpecimenWrapper expectedAliquot = scanCell.getExpectedSpecimen();
        String value = scanCell.getValue();
        if (value == null) { // no aliquot scanned
            scanCell.setStatus(UICellStatus.MISSING);
            scanCell.setInformation(Messages.getString(
                "ScanAssign.scanStatus.aliquot.missing", //$NON-NLS-1$
                expectedAliquot.getInventoryId()));
            scanCell.setTitle("?"); //$NON-NLS-1$
        } else {
            SpecimenWrapper foundAliquot = SpecimenWrapper
                .getSpecimen(SessionManager.getAppService(), value,
                    SessionManager.getUser());
            if (foundAliquot == null) {
                // not in database
                scanCell.setStatus(UICellStatus.ERROR);
                scanCell.setInformation(Messages
                    .getString("ScanAssign.scanStatus.aliquot.notlinked")); //$NON-NLS-1$
            } else {
                if (expectedAliquot != null
                    && !foundAliquot.equals(expectedAliquot)) {
                    // Position taken
                    scanCell.setStatus(UICellStatus.ERROR);
                    scanCell
                        .setInformation(Messages
                            .getString("ScanAssign.scanStatus.aliquot.positionTakenError")); //$NON-NLS-1$
                    scanCell.setTitle("!"); //$NON-NLS-1$
                } else {
                    scanCell.setSpecimen(foundAliquot);
                    if (expectedAliquot != null || currentPallet == null) {
                        List<SpecimenWrapper> currentAliquots = (currentShipment)
                            .getSpecimenCollection(false);
                        CheckStatus check = (currentShipment)
                            .checkCanAddSpecimen(foundAliquot, false);
                        if (check.ok) {
                            // aliquot scanned is already registered at this
                            // position (everything is ok !)
                            scanCell.setStatus(UICellStatus.FILLED);
                            scanCell.setTitle(foundAliquot.getCollectionEvent()
                                .getPatient().getPnumber());
                            scanCell.setSpecimen(foundAliquot);
                            if (currentAliquots != null
                                && currentAliquots.contains(foundAliquot)) {
                                // was already added. Ok but just display the
                                // right color
                                scanCell
                                    .setStatus(UICellStatus.IN_SHIPMENT_ADDED);
                            }
                        } else {
                            scanCell.setStatus(UICellStatus.ERROR);
                            scanCell.setInformation(check.message);
                        }
                    } else {
                        // should not be there
                        scanCell.setStatus(UICellStatus.ERROR);
                        scanCell.setTitle(foundAliquot.getCollectionEvent()
                            .getPatient().getPnumber());
                        scanCell
                            .setInformation("This aliquot should be on another pallet"); //$NON-NLS-1$
                    }
                }
            }
        }
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
        ContainerWrapper currentPallet = ContainerWrapper
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
    protected void postprocessScanTubeAlone(PalletCell cell) throws Exception {
        processCellStatus(cell);
        if (cell.getStatus() == UICellStatus.ERROR) {
            Button okButton = getButton(IDialogConstants.PROCEED_ID);
            okButton.setEnabled(false);
        }
        super.postprocessScanTubeAlone(cell);
    }

    public void setCurrentProductBarcode(String currentProductBarcode) {
        this.currentProductBarcode = currentProductBarcode;
    }

    public String getCurrentProductBarcode() {
        return currentProductBarcode;
    }
}
