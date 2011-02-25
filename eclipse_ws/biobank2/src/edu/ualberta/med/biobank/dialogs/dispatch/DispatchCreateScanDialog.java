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
import org.eclipse.swt.widgets.Shell;

import edu.ualberta.med.biobank.BioBankPlugin;
import edu.ualberta.med.biobank.Messages;
import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.util.RowColPos;
import edu.ualberta.med.biobank.common.wrappers.SpecimenWrapper;
import edu.ualberta.med.biobank.common.wrappers.ContainerLabelingSchemeWrapper;
import edu.ualberta.med.biobank.common.wrappers.ContainerWrapper;
import edu.ualberta.med.biobank.common.wrappers.DispatchWrapper;
import edu.ualberta.med.biobank.common.wrappers.DispatchWrapper.CheckStatus;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;
import edu.ualberta.med.biobank.forms.listener.EnterKeyToNextFieldListener;
import edu.ualberta.med.biobank.model.CellStatus;
import edu.ualberta.med.biobank.model.PalletCell;
import edu.ualberta.med.biobank.validators.NonEmptyStringValidator;
import edu.ualberta.med.biobank.widgets.BiobankText;
import edu.ualberta.med.scannerconfig.dmscanlib.ScanCell;

public class DispatchCreateScanDialog extends
    AbstractScanDialog<DispatchWrapper> {

    private class ProductBarcodeValue {
        private String value;

        @SuppressWarnings("unused")
        public void setValue(String value) {
            this.value = value;
        }

        @SuppressWarnings("unused")
        public String getValue() {
            return value;
        }
    }

    private static final String TITLE = "Scanning aliquots";
    private BiobankText palletproductBarcodeText;
    private NonEmptyStringValidator productBarcodeValidator;
    private String currentProductBarcode;
    private Button newPalletCheckBox;
    private boolean isNewPallet;
    private boolean aliquotsAdded = false;
    private ContainerWrapper currentPallet;
    private List<ContainerWrapper> removedPallets = new ArrayList<ContainerWrapper>();

    private ProductBarcodeValue productBarcodeValue;

    public DispatchCreateScanDialog(Shell parentShell,
        DispatchWrapper currentShipment, SiteWrapper site) {
        super(parentShell, currentShipment, site);
    }

    @Override
    protected void createCustomDialogContents(Composite parent) {
        productBarcodeValidator = new NonEmptyStringValidator(
            Messages.getString("ScanAssign.productBarcode.validationMsg"));
        palletproductBarcodeText = (BiobankText) createBoundWidgetWithLabel(
            parent, BiobankText.class, SWT.NONE,
            Messages.getString("ScanAssign.productBarcode.label"), //$NON-NLS-1$
            null, productBarcodeValue, "value", productBarcodeValidator); //$NON-NLS-1$
        palletproductBarcodeText
            .addKeyListener(new EnterKeyToNextFieldListener());

        newPalletCheckBox = new Button(parent, SWT.CHECK);
        newPalletCheckBox.setText("new pallet");
        newPalletCheckBox.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                resetScan();
                if (newPalletCheckBox.getSelection()) {
                    palletproductBarcodeText.setEnabled(false);
                    palletproductBarcodeText.setText("no product barcode");
                } else {
                    palletproductBarcodeText.setEnabled(true);
                    palletproductBarcodeText.setText("");
                }

            }
        });
    }

    @Override
    protected boolean fieldsValid() {
        return super.fieldsValid()
            && productBarcodeValidator.validate(
                palletproductBarcodeText.getText()).equals(Status.OK_STATUS);
    }

    @Override
    protected void beforeScanThreadStart() {
        currentProductBarcode = palletproductBarcodeText.getText();
        isNewPallet = newPalletCheckBox.getSelection();
    }

    @Override
    protected void processScanResult(IProgressMonitor monitor, SiteWrapper site)
        throws Exception {
        aliquotsAdded = false;
        boolean scanOk = true;
        currentPallet = null;
        if (isNewPallet) {
            for (PalletCell cell : getCells().values()) {
                processCell(monitor,
                    new RowColPos(cell.getRow(), cell.getCol()), cell, null);
                processCellStatus(cell);
                scanOk = scanOk && cellOk(cell);
            }
        } else {
            currentPallet = ContainerWrapper
                .getContainerWithProductBarcodeInSite(
                    SessionManager.getAppService(), site, currentProductBarcode);
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
                BioBankPlugin.openAsyncError("Pallet error",
                    "Can't find pallet with barcode '" + currentProductBarcode
                        + "'");
            }
        }
        setScanOkValue(scanOk);
    }

    private boolean cellOk(PalletCell cell) {
        return cell == null || cell.getStatus() == CellStatus.FILLED
            || cell.getStatus() == CellStatus.IN_SHIPMENT_ADDED;
    }

    private void processCell(IProgressMonitor monitor, RowColPos rcp,
        PalletCell cell, Map<RowColPos, SpecimenWrapper> expectedAliquots)
        throws Exception {
        monitor.subTask("Processing position "
            + ContainerLabelingSchemeWrapper.rowColToSbs(rcp));
        SpecimenWrapper expectedAliquot = null;
        if (expectedAliquots != null) {
            expectedAliquot = expectedAliquots.get(rcp);
            if (expectedAliquot != null) {
                if (cell == null) {
                    cell = new PalletCell(new ScanCell(rcp.row, rcp.col, null));
                    getCells().put(rcp, cell);
                }
                cell.setExpectedAliquot(expectedAliquot);
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
        SpecimenWrapper expectedAliquot = scanCell.getExpectedAliquot();
        String value = scanCell.getValue();
        if (value == null) { // no aliquot scanned
            scanCell.setStatus(CellStatus.MISSING);
            scanCell
                .setInformation(Messages
                    .getFormattedString(
                        "ScanAssign.scanStatus.aliquot.missing", expectedAliquot.getInventoryId())); //$NON-NLS-1$
            scanCell.setTitle("?"); //$NON-NLS-1$
        } else {
            SpecimenWrapper foundAliquot = SpecimenWrapper
                .getSpecimen(SessionManager.getAppService(), value,
                    SessionManager.getUser());
            if (foundAliquot == null) {
                // not in database
                scanCell.setStatus(CellStatus.ERROR);
                scanCell.setInformation(Messages
                    .getString("ScanAssign.scanStatus.aliquot.notlinked")); //$NON-NLS-1$
            } else {
                if (expectedAliquot != null
                    && !foundAliquot.equals(expectedAliquot)) {
                    // Position taken
                    scanCell.setStatus(CellStatus.ERROR);
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
                            .checkCanAddAliquot(foundAliquot, false);
                        if (check.ok) {
                            // aliquot scanned is already registered at this
                            // position (everything is ok !)
                            scanCell.setStatus(CellStatus.FILLED);
                            scanCell.setTitle(foundAliquot.getProcessingEvent()
                                .getPatient().getPnumber());
                            scanCell.setSpecimen(foundAliquot);
                            if (currentAliquots != null
                                && currentAliquots.contains(foundAliquot)) {
                                // was already added. Ok but just display the
                                // right color
                                scanCell
                                    .setStatus(CellStatus.IN_SHIPMENT_ADDED);
                            }
                        } else {
                            scanCell.setStatus(CellStatus.ERROR);
                            scanCell.setInformation(check.message);
                        }
                    } else {
                        // should not be there
                        scanCell.setStatus(CellStatus.ERROR);
                        scanCell.setTitle(foundAliquot.getProcessingEvent()
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
        return "Scan aliquots to dispatch";
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
        return "Add aliquots";
    }

    @Override
    protected void doProceed() throws Exception {
        List<SpecimenWrapper> aliquots = new ArrayList<SpecimenWrapper>();
        for (PalletCell cell : getCells().values()) {
            if (cell.getStatus() != CellStatus.MISSING) {
                aliquots.add(cell.getSpecimen());
                cell.setStatus(CellStatus.IN_SHIPMENT_ADDED);
            }
        }
        (currentShipment).addNewAliquots(aliquots, false);
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
    protected List<CellStatus> getPalletCellStatus() {
        return CellStatus.DEFAULT_PALLET_DISPATCH_CREATE_STATUS_LIST;
    }

    @Override
    protected Map<RowColPos, PalletCell> getFakeScanCells() throws Exception {
        ContainerWrapper currentPallet = ContainerWrapper
            .getContainerWithProductBarcodeInSite(
                SessionManager.getAppService(), currentSite,
                currentProductBarcode);
        Map<RowColPos, PalletCell> map = new HashMap<RowColPos, PalletCell>();
        if (currentPallet == null) {
            Map<RowColPos, PalletCell> cells = PalletCell
                .getRandomAssignedAliquots(SessionManager
                    .getAppService(), (currentShipment).getSender().getId(),
                    (currentShipment).getStudy().getId());
            return cells;
        } else {
            for (SpecimenWrapper aliquot : currentPallet.getSpecimens().values()) {
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
        if (cell.getStatus() == CellStatus.ERROR) {
            Button okButton = getButton(IDialogConstants.PROCEED_ID);
            okButton.setEnabled(false);
        }
        super.postprocessScanTubeAlone(cell);
    }
}
