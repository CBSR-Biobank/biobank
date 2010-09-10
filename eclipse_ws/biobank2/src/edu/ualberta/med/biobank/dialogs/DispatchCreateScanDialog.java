package edu.ualberta.med.biobank.dialogs;

import java.util.List;
import java.util.Map;

import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.observable.value.WritableValue;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.util.LabelingScheme;
import edu.ualberta.med.biobank.common.util.RowColPos;
import edu.ualberta.med.biobank.common.wrappers.AliquotWrapper;
import edu.ualberta.med.biobank.common.wrappers.ContainerWrapper;
import edu.ualberta.med.biobank.common.wrappers.DispatchShipmentWrapper;
import edu.ualberta.med.biobank.forms.Messages;
import edu.ualberta.med.biobank.forms.listener.EnterKeyToNextFieldListener;
import edu.ualberta.med.biobank.model.CellStatus;
import edu.ualberta.med.biobank.model.PalletCell;
import edu.ualberta.med.biobank.validators.NonEmptyStringValidator;
import edu.ualberta.med.biobank.widgets.BiobankText;
import edu.ualberta.med.scannerconfig.dmscanlib.ScanCell;

public class DispatchCreateScanDialog extends AbstractDispatchScanDialog {

    private static final String TITLE = "Scanning aliquots";
    private BiobankText palletproductBarcodeText;
    private IObservableValue productBarcodeValue = new WritableValue("",
        String.class);
    private NonEmptyStringValidator productBarcodeValidator;
    private String currentProductBarcode;

    public DispatchCreateScanDialog(Shell parentShell,
        DispatchShipmentWrapper currentShipment) {
        super(parentShell, currentShipment);
    }

    @Override
    protected void createCustomDialogContents(Composite parent) {
        productBarcodeValidator = new NonEmptyStringValidator(
            Messages.getString("ScanAssign.productBarcode.validationMsg"));
        palletproductBarcodeText = (BiobankText) createBoundWidgetWithLabel(
            parent, BiobankText.class, SWT.NONE,
            Messages.getString("ScanAssign.productBarcode.label"), //$NON-NLS-1$
            null, productBarcodeValue, productBarcodeValidator); //$NON-NLS-1$
        palletproductBarcodeText
            .addKeyListener(new EnterKeyToNextFieldListener());
        GridData gd = new GridData();
        gd.horizontalAlignment = SWT.FILL;
        gd.horizontalSpan = 2;
        palletproductBarcodeText.setLayoutData(gd);

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
    }

    @Override
    protected void processScanResult(IProgressMonitor monitor) throws Exception {
        ContainerWrapper pallet = ContainerWrapper
            .getContainerWithProductBarcodeInSite(SessionManager
                .getAppService(),
                SessionManager.getInstance().getCurrentSite(),
                currentProductBarcode);
        if (pallet != null) {
            // FIXME verifier que c'est une palette. le faire quand rentre le
            // numero
            // FIXME et si prefere le label (pour ancienne pallets, pas de
            // product barcode

            Map<RowColPos, AliquotWrapper> expectedAliquots = pallet
                .getAliquots();
            for (int row = 0; row < pallet.getRowCapacity(); row++) {
                for (int col = 0; col < pallet.getColCapacity(); col++) {
                    RowColPos rcp = new RowColPos(row, col);
                    monitor.subTask("Processing position "
                        + LabelingScheme.rowColToSbs(rcp));
                    PalletCell cell = getCells().get(rcp);
                    AliquotWrapper expectedAliquot = null;
                    if (expectedAliquots != null) {
                        expectedAliquot = expectedAliquots.get(rcp);
                        if (expectedAliquot != null) {
                            if (cell == null) {
                                cell = new PalletCell(new ScanCell(rcp.row,
                                    rcp.col, null));
                                getCells().put(rcp, cell);
                            }
                            cell.setExpectedAliquot(expectedAliquot);
                        }
                    }
                    if (cell != null) {
                        processCellStatus(cell);
                    }
                }
            }
            // FIXME set correct boolean value
            setScanOkValue(true);
        }
    }

    /**
     * set the status of the cell
     */
    protected void processCellStatus(PalletCell scanCell) throws Exception {
        AliquotWrapper expectedAliquot = scanCell.getExpectedAliquot();
        String value = scanCell.getValue();
        if (value == null) { // no aliquot scanned
            scanCell.setStatus(CellStatus.MISSING);
        } else {
            List<AliquotWrapper> aliquots = AliquotWrapper.getAliquotsInSite(
                SessionManager.getAppService(), value, SessionManager
                    .getInstance().getCurrentSite());
            if (aliquots.size() == 0) {
                // not in database
                scanCell.setStatus(CellStatus.ERROR);
            } else if (aliquots.size() == 1) {
                AliquotWrapper foundAliquot = aliquots.get(0);
                if (expectedAliquot != null
                    && !foundAliquot.equals(expectedAliquot)) {
                    // Position prise
                    scanCell.setStatus(CellStatus.ERROR);
                } else {
                    scanCell.setAliquot(foundAliquot);
                    if (expectedAliquot != null) {
                        // aliquot scanned is already registered at this
                        // position (everything is ok !)
                        scanCell.setStatus(CellStatus.FILLED);
                        scanCell.setTitle(foundAliquot.getPatientVisit()
                            .getPatient().getPnumber());
                        scanCell.setAliquot(expectedAliquot);
                    } else {
                        // should not be there
                        scanCell.setStatus(CellStatus.ERROR);
                    }
                }
            } else {
                Assert.isTrue(false,
                    "InventoryId " + value + " should be unique !"); //$NON-NLS-1$ //$NON-NLS-2$
                scanCell.setStatus(CellStatus.ERROR);
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
    protected void doProceed() {
        // TODO Auto-generated method stub
        System.out.println("Add aliquots");
        // FIXME: Aliquots shoud be added to the shipment
    }

    @Override
    protected List<CellStatus> getPalletCellStatus() {
        return CellStatus.DEFAULT_PALLET_DISPATCH_CREATE_STATUS_LIST;
    }

    @Override
    protected Map<RowColPos, PalletCell> getFakeScanCells() throws Exception {
        return PalletCell
            .getRandomAliquotsAlreadyAssigned(SessionManager.getAppService(),
                currentShipment.getSender().getId());
    }

}
